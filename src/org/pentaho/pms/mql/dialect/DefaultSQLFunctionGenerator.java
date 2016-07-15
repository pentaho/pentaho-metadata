/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This is the default implementation of the SQLFunctionGeneratorInterface. Each MQL / libformula function must convert
 * itself to SQL based on the dialect. Extend this class in the various DB dialects to implement a new MQL function.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
@SuppressWarnings( "deprecation" )
public class DefaultSQLFunctionGenerator implements SQLFunctionGeneratorInterface {

  protected int type;
  protected int paramCount = -1;
  protected String sql;
  protected boolean parens = true;
  protected boolean multiValuedParamAware = false;

  /**
   * constructor
   * 
   * @param type
   *          the type of function
   */
  public DefaultSQLFunctionGenerator( int type ) {
    this.type = type;
  }

  /**
   * constructor
   * 
   * @param type
   *          the type of function
   * @param sql
   *          sql to return
   */
  public DefaultSQLFunctionGenerator( int type, String sql ) {
    this( type );
    this.sql = sql;
  }

  /**
   * constructor
   * 
   * @param type
   *          the type of function
   * @param sql
   *          sql to return
   * @param parens
   *          include parenthesis when rendering sql
   */
  public DefaultSQLFunctionGenerator( int type, String sql, boolean parens ) {
    this( type );
    this.sql = sql;
    this.parens = parens;
  }

  /**
   * constructor
   * 
   * @param type
   *          the type of function
   * @param sql
   *          sql to return
   * @param paramCount
   *          expected number of parameters
   * @param parens
   *          include parenthesis when rendering sql
   */
  public DefaultSQLFunctionGenerator( int type, String sql, int paramCount, boolean parens ) {
    this( type, sql );
    this.paramCount = paramCount;
    this.parens = parens;
  }

  /**
   * constructor
   * 
   * @param type
   *          the type of function
   * @param sql
   *          sql to return
   * @param paramCount
   *          expected number of parameters
   */
  public DefaultSQLFunctionGenerator( int type, String sql, int paramCount ) {
    this( type, sql );
    this.paramCount = paramCount;
  }

  /**
   * default validation function verifies parameter count if necessary
   */
  public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
    if ( paramCount != -1 ) {
      if ( f.getChildValues() == null || f.getChildValues().length != paramCount ) {
        throw new PentahoMetadataException( Messages.getErrorString(
            "PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "" + paramCount ) ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  /**
   * This is a utility function that may be used by child classes to verify all params are static numbers.
   * 
   * @param f
   *          function to verify
   * @throws PentahoMetadataException
   *           if params are not numbers
   */
  protected void verifyAllStaticNumbers( FormulaFunction f ) throws PentahoMetadataException {

    for ( int i = 0; i < f.getChildValues().length; i++ ) {
      if ( !( f.getChildValues()[i] instanceof StaticValue )
          || !( ( (StaticValue) f.getChildValues()[i] ).getValueType() == NumberType.GENERIC_NUMBER ) ) {
        throw new PentahoMetadataException(
            Messages
                .getErrorString(
                    "PMSFormulaContext.ERROR_0003_INVALID_PARAM_TYPE_NOT_STATIC_NUMBER", Integer.toString( i + 1 ), f.getFunctionName() ) ); //$NON-NLS-1$
      }
    }
  }

  /**
   * This method may be used by child classes to verify all params are static numbers.
   * 
   * @param f
   *          function to verify
   * @throws PentahoMetadataException
   *           if params are not numbers
   */
  protected void verifyAllStaticStrings( FormulaFunction f ) throws PentahoMetadataException {

    for ( int i = 0; i < f.getChildValues().length; i++ ) {
      // this checks to see if the strings are static or if they are available as parameters
      if ( ( !( f.getChildValues()[i] instanceof StaticValue ) || !( ( (StaticValue) f.getChildValues()[i] )
          .getValueType() instanceof TextType ) )
          && ( !( f.getChildValues()[i] instanceof ContextLookup ) || !( ( (ContextLookup) f.getChildValues()[i] )
              .getName().startsWith( "param:" ) ) ) ) {
        throw new PentahoMetadataException(
            Messages
                .getErrorString(
                    "PMSFormulaContext.ERROR_0004_INVALID_PARAM_TYPE_NOT_STRING", Integer.toString( i + 1 ), f.getFunctionName() ) ); //$NON-NLS-1$
      }
    }
  }

  /**
   * return the type of this specific term
   * 
   * @return type enumerator
   */
  public int getType() {
    return type;
  }

  protected String getSQL() {
    return sql;
  }

  /**
   * this is the default implementation of generateFunctionSQL.
   * 
   * Note that this function is part of the formula traversal process, which is executed in PMSFormula
   * 
   * @see PMSFormula
   * 
   * @param formula
   *          the traversal instance
   * @param sb
   *          the string to append sql to
   * @param f
   *          libformula function object
   */
  public void
    generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f )
      throws PentahoMetadataException {
    if ( type == INLINE_FUNCTION ) {
      if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
        formula.generateSQL( f, f.getChildValues()[0], sb, locale );
        if ( paramCount == 1 ) {
          sb.append( " " + getSQL() ); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          for ( int i = 1; i < f.getChildValues().length; i++ ) {
            sb.append( " " + getSQL() + " " ); //$NON-NLS-1$ //$NON-NLS-2$
            formula.generateSQL( f, f.getChildValues()[i], sb, locale );
          }
        }
      }
    } else if ( type == PARAM_FUNCTION || type == PARAM_AGG_FUNCTION ) {
      sb.append( " " + getSQL() ); //$NON-NLS-1$
      if ( parens ) {
        sb.append( "(" ); //$NON-NLS-1$
      }

      if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
        formula.generateSQL( f, f.getChildValues()[0], sb, locale );
        for ( int i = 1; i < f.getChildValues().length; i++ ) {
          sb.append( " , " ); //$NON-NLS-1$
          formula.generateSQL( f, f.getChildValues()[i], sb, locale );
        }
      }
      if ( parens ) {
        sb.append( ")" ); //$NON-NLS-1$
      }
    }
  }

  public boolean isMultiValuedParamAware() {
    return multiValuedParamAware;
  }

  public void setMultiValuedParamAware( boolean multiValuedParamAware ) {
    this.multiValuedParamAware = multiValuedParamAware;
  }
}
