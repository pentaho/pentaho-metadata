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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.mql.DateMath;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLOrderBy;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLSelection;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLTable;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLWhereFormula;
import org.pentaho.pms.util.Const;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;

/**
 * This is the Default SQL Dialect Class that implements SQLDialectInterface. Specific Database Dialect Classes should
 * extend this class.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
@SuppressWarnings( "deprecation" )
public class DefaultSQLDialect implements SQLDialectInterface {
  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$
  protected Map<String, SQLFunctionGeneratorInterface> supportedFunctions =
      new HashMap<String, SQLFunctionGeneratorInterface>();
  protected Map<String, SQLOperatorGeneratorInterface> supportedInfixOperators =
      new HashMap<String, SQLOperatorGeneratorInterface>();
  String databaseType;
  DatabaseMeta databaseMeta;
  private String concatOperator; // will default to "||", can be overridden by -Ddefault.sql.dialect.concat.operator="+"

  public DefaultSQLDialect() {
    this( "GENERIC" ); //$NON-NLS-1$
  }

  public DefaultSQLDialect( String databaseType ) {
    concatOperator = System.getProperty( "default.sql.dialect.concat.operator", "||" ); //$NON-NLS-1$    
    this.databaseType = databaseType;
    this.databaseMeta = new DatabaseMeta( "", databaseType, "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    // logical functions
    supportedFunctions.put(
        "AND", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "AND" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions
        .put( "OR", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "OR" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put(
        "NOT", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "NOT", 1 ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put(
        "ISNA", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "IS NULL", 1 ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put(
        "NULL", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "NULL", false ) ); //$NON-NLS-1$ //$NON-NLS-2$

    // infix operators
    supportedInfixOperators.put( "+", new DefaultSQLOperatorGenerator( "+" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "-", new DefaultSQLOperatorGenerator( "-" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "*", new DefaultSQLOperatorGenerator( "*" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "/", new DefaultSQLOperatorGenerator( "/" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "=", new DefaultSQLOperatorGenerator( "=" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "<", new DefaultSQLOperatorGenerator( "<" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( ">", new DefaultSQLOperatorGenerator( ">" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "<=", new DefaultSQLOperatorGenerator( "<=" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( ">=", new DefaultSQLOperatorGenerator( ">=" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "<>", new DefaultSQLOperatorGenerator( "<>" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put( "LIKE", new DefaultSQLOperatorGenerator( "LIKE" ) ); //$NON-NLS-1$ //$NON-NLS-2$

    //
    // comparison functions
    //
    supportedFunctions.put(
        "LIKE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "LIKE", 2, false ) { //$NON-NLS-1$ //$NON-NLS-2$

        } );

    supportedFunctions.put(
        "CONTAINS", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "LIKE", 2, false ) { //$NON-NLS-1$ //$NON-NLS-2$
          /**
           * render the necessary sql
           */
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
              formula.generateSQL( f, f.getChildValues()[0], sb, locale );
              String quotedWildcard = quoteStringLiteral( getStringWildCard() );
              for ( int i = 1; i < f.getChildValues().length; i++ ) {
                sb.append( " " + getSQL() + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                StringBuffer tmpsb = new StringBuffer();
                formula.generateSQL( f, f.getChildValues()[i], tmpsb, locale );

                sb.append( generateStringConcat( quotedWildcard, tmpsb.toString(), quotedWildcard ) );

              }
            }
          }
        } );

    supportedFunctions
        .put(
            "BEGINSWITH", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "LIKE", 2, false ) { //$NON-NLS-1$ //$NON-NLS-2$
              /**
               * render the necessary sql
               */
              public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
                  FormulaFunction f ) throws PentahoMetadataException {
                if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
                  formula.generateSQL( f, f.getChildValues()[0], sb, locale );
                  String quotedWildcard = quoteStringLiteral( getStringWildCard() );
                  for ( int i = 1; i < f.getChildValues().length; i++ ) {
                    sb.append( " " + getSQL() + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                    StringBuffer tmpsb = new StringBuffer();
                    formula.generateSQL( f, f.getChildValues()[i], tmpsb, locale );

                    sb.append( generateStringConcat( tmpsb.toString(), quotedWildcard ) );

                  }
                }
              }
            } );

    supportedFunctions.put(
        "ENDSWITH", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.INLINE_FUNCTION, "LIKE", 2, false ) { //$NON-NLS-1$ //$NON-NLS-2$
          /**
           * render the necessary sql
           */
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
              formula.generateSQL( f, f.getChildValues()[0], sb, locale );
              String quotedWildcard = quoteStringLiteral( getStringWildCard() );
              for ( int i = 1; i < f.getChildValues().length; i++ ) {
                sb.append( " " + getSQL() + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                StringBuffer tmpsb = new StringBuffer();
                formula.generateSQL( f, f.getChildValues()[i], tmpsb, locale );

                sb.append( generateStringConcat( quotedWildcard, tmpsb.toString() ) );

              }
            }
          }
        } );

    //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put(
        "IN", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "IN", 2 ) { //$NON-NLS-1$ //$NON-NLS-2$

          /**
           * make sure there are at least two params
           */
          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            if ( f.getChildValues() == null || f.getChildValues().length < 2 ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "" + paramCount ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }

          /**
           * render the necessary sql
           */
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            formula.generateSQL( f, f.getChildValues()[0], sb, locale );
            sb.append( " IN ( " ); //$NON-NLS-1$
            formula.generateSQL( f, f.getChildValues()[1], sb, locale );
            for ( int i = 2; i < f.getChildValues().length; i++ ) {
              sb.append( " , " ); //$NON-NLS-1$
              formula.generateSQL( f, f.getChildValues()[i], sb, locale );
            }
            sb.append( " ) " ); //$NON-NLS-1$
          }

          public boolean isMultiValuedParamAware() {
            return true;
          }
        } );

    supportedFunctions.put(
        "EQUALS", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "EQUALS", 2 ) { //$NON-NLS-1$ //$NON-NLS-2$

          /**
           * render the necessary sql
           */
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            boolean multiVal = false;
            if ( f.getChildValues()[1] instanceof ContextLookup ) {
              Object val = formula.getParameterValue( (ContextLookup) f.getChildValues()[1] );
              multiVal = ( val instanceof Object[] ) && ( ( (Object[]) val ).length > 1 );
            }
            if ( multiVal ) {
              formula.generateSQL( f, f.getChildValues()[0], sb, locale );
              sb.append( " IN ( " ); //$NON-NLS-1$
              formula.generateSQL( f, f.getChildValues()[1], sb, locale );
              sb.append( " ) " ); //$NON-NLS-1$
            } else {
              formula.generateSQL( f, f.getChildValues()[0], sb, locale );
              sb.append( " = " );
              formula.generateSQL( f, f.getChildValues()[1], sb, locale );
            }
          }

          public boolean isMultiValuedParamAware() {
            return true;
          }

        } );

    //
    // aggregator functions
    //

    supportedFunctions.put(
        "COUNT", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION ) { //$NON-NLS-1$
          public String getSQL() {
            return databaseMeta.getFunctionCount();
          }
        } );

    supportedFunctions.put( "SUM", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION ) { //$NON-NLS-1$
          public String getSQL() {
            return databaseMeta.getFunctionSum();
          }
        } );

    supportedFunctions.put( "AVG", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION ) { //$NON-NLS-1$
          public String getSQL() {
            return databaseMeta.getFunctionAverage();
          }
        } );

    supportedFunctions.put( "MIN", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION ) { //$NON-NLS-1$
          public String getSQL() {
            return databaseMeta.getFunctionMinimum();
          }
        } );

    supportedFunctions.put( "MAX", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION ) { //$NON-NLS-1$
          public String getSQL() {
            return databaseMeta.getFunctionMaximum();
          }
        } );

    //
    // date functions
    //

    supportedFunctions.put(
        "NOW", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "NOW()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( sql );
          }
        } );

    // note, by using the "getDateSQL" method, inheriting classes can alter how the date is displayed

    supportedFunctions.put(
        "DATE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "DATE", 3 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            BigDecimal year = (BigDecimal) ( (StaticValue) f.getChildValues()[0] ).getValue();
            BigDecimal month = (BigDecimal) ( (StaticValue) f.getChildValues()[1] ).getValue();
            BigDecimal day = (BigDecimal) ( (StaticValue) f.getChildValues()[2] ).getValue();
            sb.append( getDateSQL( year.intValue(), month.intValue(), day.intValue() ) );

          }

          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            super.validateFunction( f );
            // check to make sure all three params are of static number type
            verifyAllStaticNumbers( f );
          }
        } );

    supportedFunctions.put(
        "DATEMATH", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "DATEMATH", 1 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            String exp = (String) ( (StaticValue) f.getChildValues()[0] ).getValue();
            // transform the date expression into an actual date
            try {
              Calendar cal = DateMath.calculateDate( exp );
              sb.append( getDateSQL( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ) + 1, cal
                  .get( Calendar.DAY_OF_MONTH ) ) );
            } catch ( IllegalArgumentException ex ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "DefaultSQLDialect.ERROR_0002_DATE_MATH_SYNTAX_INVALID", exp ), ex ); //$NON-NLS-1$
            }
          }

          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            super.validateFunction( f );
            // check to make sure all three params are of static number type
            verifyAllStaticStrings( f );
          }
        } );

    // note, by using the "getDateSQL" method, inheriting classes can alter how the date is displayed

    supportedFunctions.put(
        "DATEVALUE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "DATE", 1 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            Object dateValue = null;
            if ( f.getChildValues()[0] instanceof StaticValue ) {
              dateValue = ( (StaticValue) f.getChildValues()[0] ).getValue();
            } else if ( f.getChildValues()[0] instanceof ContextLookup ) {
              dateValue = formula.getParameterValue( (ContextLookup) f.getChildValues()[0] );
            }

            int year = 0;
            int month = 0;
            int day = 0;
            int hour = 0;
            int minute = 0;
            int second = 0;
            int milli = 0;
            boolean useTime = false;
            if ( dateValue instanceof String ) {
              Pattern p = Pattern.compile( "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d*)" ); //$NON-NLS-1$
              Matcher m = p.matcher( (String) dateValue );
              if ( m.matches() ) {
                useTime = true;
                hour = Integer.parseInt( m.group( 4 ) );
                minute = Integer.parseInt( m.group( 5 ) );
                second = Integer.parseInt( m.group( 6 ) );
                milli = Integer.parseInt( m.group( 7 ) );
              } else {
                p = Pattern.compile( "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)" ); //$NON-NLS-1$
                m = p.matcher( (String) dateValue );
                if ( !m.matches() ) {
                  throw new PentahoMetadataException( Messages.getErrorString(
                      "DefaultSQLDialect.ERROR_0001_DATE_STRING_SYNTAX_INVALID", (String) dateValue ) ); //$NON-NLS-1$
                }
              }
              year = Integer.parseInt( m.group( 1 ) );
              month = Integer.parseInt( m.group( 2 ) );
              day = Integer.parseInt( m.group( 3 ) );
            } else if ( dateValue instanceof java.util.Date ) {
              Calendar c = Calendar.getInstance();
              c.setTime( (java.util.Date) dateValue );
              year = c.get( Calendar.YEAR );
              month = c.get( Calendar.MONTH ) + 1;
              day = c.get( Calendar.DAY_OF_MONTH );
            } else {
              // The dateValue could be null here or some other data type we're not expecting
              String dateValueType = dateValue == null ? "null" : dateValue.getClass().getName(); //$NON-NLS-1$
              throw new PentahoMetadataException( Messages.getErrorString(
                  "DefaultSQLDialect.ERROR_0003_DATE_PARAMETER_UNRECOGNIZED", dateValueType ) ); //$NON-NLS-1$
            }
            if ( useTime ) {
              sb.append( getDateSQL( year, month, day, hour, minute, second, milli ) );
            } else {
              sb.append( getDateSQL( year, month, day ) );
            }
          }

          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            super.validateFunction( f );
            // check to make sure all three params are of static number type
            verifyAllStaticStrings( f );
          }
        } );

    // case function

    supportedFunctions.put(
        "CASE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "CASE" ) { //$NON-NLS-1$ //$NON-NLS-2$

          /**
           * make sure there are at least two params
           */
          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            if ( f.getChildValues() == null || f.getChildValues().length < 2 ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "2" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }

          /**
           * render the necessary sql
           */
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( " CASE " ); //$NON-NLS-1$

            for ( int i = 1; i < f.getChildValues().length; i += 2 ) {
              sb.append( " WHEN " ); //$NON-NLS-1$
              formula.generateSQL( f, f.getChildValues()[i - 1], sb, locale );
              sb.append( " THEN " ); //$NON-NLS-1$
              formula.generateSQL( f, f.getChildValues()[i], sb, locale );
            }

            if ( f.getChildValues().length % 2 == 1 ) {
              sb.append( " ELSE " ); //$NON-NLS-1$
              formula.generateSQL( f, f.getChildValues()[f.getChildValues().length - 1], sb, locale );
            }

            sb.append( " END " ); //$NON-NLS-1$
          }
        } );

    // coalesce
    supportedFunctions.put(
        "COALESCE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "COALESCE" ) { //$NON-NLS-1$ //$NON-NLS-2$

          /**
           * make sure there are at least two params
           */
          public void validateFunction( FormulaFunction f ) throws PentahoMetadataException {
            if ( f.getChildValues() == null || f.getChildValues().length < 1 ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "1" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        } );

    // boolean data type

    supportedFunctions.put(
        "TRUE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "TRUE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "TRUE" ); //$NON-NLS-1$
          }
        } );

    supportedFunctions.put(
        "FALSE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "FALSE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "FALSE" ); //$NON-NLS-1$
          }
        } );
  }

  /**
   * utility function to format integer correctly for dates
   * 
   * @param number
   *          number to format
   * @return correctly formatted string
   */
  protected String displayAsTwoOrMoreDigits( int number ) {
    if ( number >= 0 && number < 10 ) {
      return "0" + number; //$NON-NLS-1$
    } else {
      return "" + number; //$NON-NLS-1$
    }
  }

  /**
   * returns the correct dialect string for date representations in SQL
   * 
   * @param year
   *          the year
   * @param month
   *          the month
   * @param day
   *          the day
   * @return string representation for date SQL
   */
  public String getDateSQL( int year, int month, int day ) {
    return quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public String getDateSQL( int year, int month, int day, int hour, int minute, int second, int milli ) {
    return quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) + //$NON-NLS-1$ //$NON-NLS-2$
        " " + displayAsTwoOrMoreDigits( hour )
        + ":" + displayAsTwoOrMoreDigits( minute ) + ":" + displayAsTwoOrMoreDigits( second ) + "." + milli ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  /**
   * return the database type that this dialect implements
   * 
   * @return database type
   */
  public String getDatabaseType() {
    return databaseType;
  }

  /**
   * returns true if a function is supported by PMSFormulaContext
   * 
   * @param functionName
   *          name of function
   * @return true if function is supported
   */
  public boolean isSupportedFunction( String functionName ) {
    return supportedFunctions.containsKey( functionName );
  }

  /**
   * returns true if a function is an aggregate function. This is used because certain contexts do not allow aggregates.
   * 
   * @param functionName
   *          name of function
   * @return true if aggregate
   */
  public boolean isAggregateFunction( String functionName ) {
    SQLFunctionGeneratorInterface gen = getFunctionSQLGenerator( functionName );
    if ( gen != null ) {
      return gen.getType() == SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION;
    }
    return false;
  }

  /**
   * returns true if infix operator is supported
   * 
   * @param operator
   *          operator to validate
   * @return true if supported
   */
  public boolean isSupportedInfixOperator( String operator ) {
    return supportedInfixOperators.containsKey( operator );
  }

  /**
   * return a reference to the sql generator for a given function
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLFunctionGeneratorInterface getFunctionSQLGenerator( String functionName ) {
    return (SQLFunctionGeneratorInterface) supportedFunctions.get( functionName );
  }

  /**
   * return a reference to the sql generator for a given infix operator
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLOperatorGeneratorInterface getInfixOperatorSQLGenerator( String operatorName ) {
    return (SQLOperatorGeneratorInterface) supportedInfixOperators.get( operatorName );
  }

  /**
   * This method quotes a string literal. Note that for the time being we just use the ANSI standard
   * 
   * Note - if the input string is null, this (oddly) returns 'null'
   * 
   * @param databaseMeta
   *          passed in for potential future use
   * @param str
   *          string to quote
   * @return quoted string
   */
  public String quoteStringLiteral( Object str ) {
    String strval = null;
    if ( str != null ) {
      strval = str.toString();
      strval = strval.replaceAll( "'", "''" ); //$NON-NLS-1$  //$NON-NLS-2$
    }
    if ( !LocaleHelper.isAscii( strval ) && ( supportsNLSLiteral() ) ) {
      return "N'" + strval + "'"; //$NON-NLS-1$  //$NON-NLS-2$
    } else {
      return "'" + strval + "'"; //$NON-NLS-1$  //$NON-NLS-2$
    }
  }

  public boolean supportsNLSLiteral() {
    return false;
  }

  //
  // The following methods generate SQL based on a SQLQueryModel object.
  //

  /**
   * generates the SELECT portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateSelect( SQLQueryModel query, StringBuilder sql ) {
    sql.append( "SELECT " ); //$NON-NLS-1$
    generateSelectPredicate( query, sql );
    sql.append( Const.CR );
    boolean first = true;
    for ( SQLSelection selection : query.getSelections() ) {
      if ( first ) {
        first = false;
        sql.append( "          " ); //$NON-NLS-1$
      } else {
        sql.append( "         ," ); //$NON-NLS-1$
      }
      sql.append( selection.getFormula() );
      if ( selection.getAlias() != null ) {
        sql.append( " AS " ); //$NON-NLS-1$
        sql.append( selection.getAlias() );
      }
      sql.append( Const.CR );
    }
  }

  /**
   * Generates any predicates (e.g. DISTINCT or TOP)
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateTopAfterDistinct( query, sql, TOP_KEYWORD );
  }

  /**
   * Generates DISTINCT.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateDistinct( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getDistinct() ) {
      sql.append( "DISTINCT " ); //$NON-NLS-1$
    }
  }

  /**
   * Convenience method. Appends &quot; topKeyword n distinct &quot; where n is the query limit.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   * @param topKeyword
   *          top keyword (e.g. TOP or FIRST)
   */
  protected void generateTopBeforeDistinct( SQLQueryModel query, StringBuilder sql, String topKeyword ) {
    generateTop( query, sql, topKeyword );
    generateDistinct( query, sql );
  }

  /**
   * Convenience method. Appends &quot; distinct topKeyword n &quot; where n is the query limit.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   * @param topKeyword
   *          top keyword (e.g. TOP or FIRST)
   */
  protected void generateTopAfterDistinct( SQLQueryModel query, StringBuilder sql, String topKeyword ) {
    generateDistinct( query, sql );
    generateTop( query, sql, topKeyword );
  }

  /**
   * Appends &quot; topKeyword n &quot; where n is the query limit.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   * @param topKeyword
   *          top keyword (e.g. TOP or FIRST)
   */
  protected void generateTop( SQLQueryModel query, StringBuilder sql, String topKeyword ) {
    if ( query.getLimit() >= 0 ) {
      sql.append( " " );
      sql.append( topKeyword );
      sql.append( " " );
      sql.append( query.getLimit() );
      sql.append( " " );
    }
  }

  /**
   * generates the FROM portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateFrom( SQLQueryModel query, StringBuilder sql ) {
    sql.append( "FROM " ).append( Const.CR ); //$NON-NLS-1$
    boolean first = true;
    for ( SQLTable table : query.getTables() ) {
      if ( first ) {
        first = false;
        sql.append( "          " ); //$NON-NLS-1$
      } else {
        sql.append( "         ," ); //$NON-NLS-1$
      }
      sql.append( table.getTableName() );
      if ( table.getAlias() != null ) {
        sql.append( " " ); //$NON-NLS-1$
        sql.append( table.getAlias() );
      }
      sql.append( Const.CR );
    }
  }

  /**
   * generates the WHERE portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   * @param usedSQLWhereFormula
   *          the where formula that are already used by the outer join algorithm. (no need to list these again)
   */
  protected void generateWhere( SQLQueryModel query, StringBuilder sql, List<SQLWhereFormula> usedSQLWhereFormula ) {

    boolean addSecurityConstraint =
        query.getSecurityConstraint() != null && !query.getSecurityConstraint().isContainingAggregate();

    List<SQLWhereFormula> remainingFormulas = new ArrayList<SQLWhereFormula>();
    if ( query.getWhereFormulas().size() > 0 || addSecurityConstraint ) {
      boolean first = true;

      for ( SQLWhereFormula whereFormula : query.getWhereFormulas() ) {
        if ( !usedSQLWhereFormula.contains( whereFormula ) ) {
          remainingFormulas.add( whereFormula );
        }
      }
      boolean whereFormulasRemaining = remainingFormulas.size() > 0;

      if ( whereFormulasRemaining || addSecurityConstraint ) {
        if ( !containsWhereCondition( query, sql, usedSQLWhereFormula ) ) {
          sql.append( "WHERE " ).append( Const.CR ); //$NON-NLS-1$
        } else {
          sql.append( "      AND " ).append( Const.CR ); //$NON-NLS-1$
        }
      }

      if ( addSecurityConstraint ) {

        sql.append( "        (" ).append( Const.CR ); //$NON-NLS-1$
        sql.append( "          " ); //$NON-NLS-1$
        sql.append( query.getSecurityConstraint().getFormula() ).append( Const.CR );

        if ( whereFormulasRemaining ) {
          sql.append( "        ) AND " ).append( Const.CR ); //$NON-NLS-1$
        } else {
          sql.append( "        )" ).append( Const.CR ); //$NON-NLS-1$
        }
      }

      if ( whereFormulasRemaining ) {
        sql.append( "        (" ).append( Const.CR ); //$NON-NLS-1$

        for ( SQLWhereFormula whereFormula : remainingFormulas ) {
          if ( first ) {
            if ( whereFormula.getOperator().endsWith( "NOT" ) ) { //$NON-NLS-1$ 
              sql.append( "      NOT (" ); //$NON-NLS-1$
            } else {
              sql.append( "          (" ); //$NON-NLS-1$
            }
            first = false;
          } else {
            sql.append( "      " ); //$NON-NLS-1$
            sql.append( whereFormula.getOperator() );
            sql.append( " (" ); //$NON-NLS-1$
          }
          sql.append( Const.CR );
          sql.append( "             " ); //$NON-NLS-1$
          sql.append( whereFormula.getFormula() );
          sql.append( Const.CR );
          sql.append( "          )" ).append( Const.CR ); //$NON-NLS-1$
        }
        sql.append( "        )" ).append( Const.CR ); //$NON-NLS-1$
      }

    }
  }

  /**
   * Determines if there have been any WHERE conditions appended to the query already. The result of this determines if
   * "WHERE" or "AND" is used to concatenate the next WHERE condition on to {@code sql}.
   * 
   * @param query
   *          Query Model
   * @param sql
   *          In-progress query string being built
   * @return True if the query has already been appended with a WHERE keyword.
   */
  protected boolean containsWhereCondition( SQLQueryModel query, StringBuilder sql,
      List<SQLWhereFormula> usedSQLWhereFormula ) {
    return query.getJoins().size() != 0 && !query.containsOuterJoins();
  }

  /**
   * Generates the WHERE clause portion of the SQL statement.<br>
   * In this case, we generate the joins between the tables.<br>
   * <br>
   * Important: this method only applies to regular models, not outer-join scenarios!<br>
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateJoins( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getJoins().size() > 0 ) {
      boolean first = true;
      sql.append( "WHERE " ).append( Const.CR ); //$NON-NLS-1$
      List<SQLJoin> sortedJoins = new ArrayList<SQLJoin>( query.getJoins() );
      Collections.sort( sortedJoins );
      for ( SQLJoin join : sortedJoins ) {
        if ( first ) {
          first = false;
          sql.append( "          ( " ); //$NON-NLS-1$
        } else {
          // You always "AND" join conditions...
          //
          sql.append( "      AND ( " ); //$NON-NLS-1$
        }
        sql.append( join.getSqlWhereFormula().getFormula() );
        sql.append( " )" ).append( Const.CR ); //$NON-NLS-1$
      }
    }
  }

  /**
   * generates the GROUP BY portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateGroupBy( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getGroupBys().size() > 0 ) {
      sql.append( "GROUP BY " ).append( Const.CR ); //$NON-NLS-1$
      boolean first = true;
      for ( SQLSelection groupby : query.getGroupBys() ) {
        if ( first ) {
          first = false;
          sql.append( "          " ); //$NON-NLS-1$
        } else {
          sql.append( "         ," ); //$NON-NLS-1$
        }

        // only render the alias or the formula
        if ( groupby.getAlias() != null ) {
          sql.append( groupby.getAlias() );
        } else {
          sql.append( groupby.getFormula() );
        }
        sql.append( Const.CR );
      }
    }
  }

  /**
   * generates the HAVING portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateHaving( SQLQueryModel query, StringBuilder sql ) {

    boolean addSecurityConstraint =
        query.getSecurityConstraint() != null && query.getSecurityConstraint().isContainingAggregate();

    if ( query.getHavings().size() > 0 || addSecurityConstraint ) {

      sql.append( "HAVING " ).append( Const.CR ); //$NON-NLS-1$

      if ( addSecurityConstraint ) {
        sql.append( "        (" ).append( Const.CR ); //$NON-NLS-1$
        sql.append( "          " ); //$NON-NLS-1$
        sql.append( query.getSecurityConstraint().getFormula() ).append( Const.CR );
        if ( query.getHavings().size() > 0 ) {
          sql.append( "        ) AND (" ).append( Const.CR ); //$NON-NLS-1$
        }
      }

      boolean first = true;
      for ( SQLWhereFormula havingFormula : query.getHavings() ) {
        if ( first ) {
          first = false;
          if ( havingFormula.getOperator().endsWith( "NOT" ) ) { //$NON-NLS-1$
            sql.append( "      NOT (" ); //$NON-NLS-1$
          } else {
            sql.append( "          (" ); //$NON-NLS-1$
          }
        } else {
          sql.append( "      " ); //$NON-NLS-1$
          sql.append( havingFormula.getOperator() );
          sql.append( " (" ); //$NON-NLS-1$
        }
        sql.append( Const.CR );
        sql.append( "             " ); //$NON-NLS-1$
        sql.append( havingFormula.getFormula() );
        sql.append( Const.CR );
        sql.append( "          )" ).append( Const.CR ); //$NON-NLS-1$
      }

      if ( addSecurityConstraint ) {
        sql.append( "        )" ).append( Const.CR ); //$NON-NLS-1$
      }
    }
  }

  protected String getStringConcatOperator() {
    return concatOperator; //$NON-NLS-1$
  }

  protected String generateStringConcat( String... vals ) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < vals.length; i++ ) {
      if ( i != 0 ) {
        sb.append( " " ).append( getStringConcatOperator() ).append( " " ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      sb.append( vals[i] );
    }
    return sb.toString();
  }

  /**
   * generates the HAVING portion of the SQL statement
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateOrderBy( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getOrderBys().size() > 0 ) {
      sql.append( "ORDER BY " ).append( Const.CR ); //$NON-NLS-1$
      boolean first = true;
      for ( SQLOrderBy orderby : query.getOrderBys() ) {
        if ( first ) {
          first = false;
          sql.append( "          " ); //$NON-NLS-1$
        } else {
          sql.append( "         ," ); //$NON-NLS-1$
        }
        if ( orderby.getSelection().getAlias() != null ) {
          sql.append( orderby.getSelection().getAlias() );
        } else {
          sql.append( orderby.getSelection().getFormula() );
        }
        if ( orderby.getOrder() != null ) {
          sql.append( " " ); //$NON-NLS-1$
          switch ( orderby.getOrder() ) {
            case ASCENDING:
              sql.append( "ASC" ); //$NON-NLS-1$
              break;
            case DESCENDING:
              sql.append( "DESC" ); //$NON-NLS-1$
              break;
            default:
              throw new RuntimeException( "unsupported order type: " + orderby.getOrder() );
          }
        }
        sql.append( Const.CR );
      }
    }
  }

  /**
   * Generates anything after the ORDER BY clause of the SQL statement. Example: LIMIT.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generatePostOrderBy( SQLQueryModel query, StringBuilder sql ) {

  }

  /**
   * Convenience method. Calls {@code DatabaseMeta#getLimitClause(int)} if available.
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   */
  protected void generateLimit( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getLimit() >= 0 ) {
      sql.append( databaseMeta.getLimitClause( query.getLimit() ) );
    }
  }

  /**
   * Generates the outer joins portion of the query.<br>
   * <br>
   * We added the joins in a particular order that we simply unroll here.<br>
   * 
   * @param query
   *          query model
   * @param sql
   *          string buffer
   * @return
   */
  protected List<SQLWhereFormula> generateOuterJoin( SQLQueryModel query, StringBuilder sql ) {

    // Keep track of the SQL where formula we used in the joins
    //
    List<SQLWhereFormula> usedSQLWhereFormula = new ArrayList<SQLWhereFormula>();

    // If there are no joins, we just stop right here: return empty list.
    //
    if ( query.getJoins().size() == 0 ) {
      return usedSQLWhereFormula;
    }

    // Before this location we had the "SELECT x,y,z" part of the query in sql.
    // Now we're going to add the join syntax
    // It's important that we sort the joins to make sure the join order, intended by the model designer is used.
    // The rule is:
    // - If there is no sort order key specified and it's an inner join, we take the inner joins first
    // - If there is a sort order key specified, we sort on that.
    //
    // Obviously, it's possible to get hybrid situations, but there is little we can do about that.
    // It might be a good idea to include a model verification system module that checks this.
    //

    // First the sort: reverse ordered by join order key (or inner join capability)
    //
    List<SQLJoin> sortedJoins = new ArrayList<SQLJoin>( query.getJoins() );
    Collections.sort( sortedJoins );

    // OK, so we need to create a recursive call to add the nested Join statements...
    //
    String joinClause = getJoinClause( query, sortedJoins, 0, new ArrayList<String>(), usedSQLWhereFormula );

    sql.append( Const.CR ).append( "FROM " ).append( joinClause ).append( Const.CR ); //$NON-NLS-1$

    return usedSQLWhereFormula;
  }

  /**
   * Create join clause from back to front in the sorted joins list...
   * 
   * @param usedSQLWhereFormula
   * @return the nested join clause
   */
  private String getJoinClause( SQLQueryModel query, List<SQLJoin> sortedJoins, int index, List<String> usedTables,
      List<SQLWhereFormula> usedSQLWhereFormula ) {
    StringBuilder clause = new StringBuilder();
    String indent = Const.rightPad( " ", ( index + 1 ) + 3 ); //$NON-NLS-1$
    SQLJoin join = sortedJoins.get( index );
    String leftTableNameAndAlias = join.getLeftTablename();
    String leftTableNameOrAlias = join.getLeftTablename();
    if ( !Const.isEmpty( join.getLeftTableAlias() ) ) {
      leftTableNameAndAlias += " " + join.getLeftTableAlias(); //$NON-NLS-1$
      leftTableNameOrAlias = join.getLeftTableAlias();
    }

    String rightTableNameAndAlias = join.getRightTablename();
    String rightTableNameOrAlias = join.getRightTablename();
    if ( !Const.isEmpty( join.getRightTableAlias() ) ) {
      rightTableNameAndAlias += " " + join.getRightTableAlias(); //$NON-NLS-1$
      rightTableNameOrAlias = join.getRightTableAlias();
    }

    JoinType joinType = join.getJoinType();

    // We want to calculate this clause depth-first. That means we first add
    // the tables in the nested queries and then see which (left or right) fits with it.
    // If needed, we have to flip left-outer and right-outer join syntax.
    //
    String rightClause;

    if ( index < sortedJoins.size() - 1 ) {
      rightClause = getJoinClause( query, sortedJoins, index + 1, usedTables, usedSQLWhereFormula );
    } else {
      rightClause = rightTableNameAndAlias; // rightTableName; // TODO: OLD BUG?
    }

    // Now see if the left table name is already used in the nested right clause.
    // If so, we need to flip left and right, including the left/right outer join.
    //
    if ( usedTables.contains( leftTableNameOrAlias ) ) {

      leftTableNameAndAlias = join.getRightTablename();
      leftTableNameOrAlias = join.getRightTablename();
      if ( !Const.isEmpty( join.getRightTableAlias() ) ) {
        leftTableNameAndAlias += " " + join.getRightTableAlias(); //$NON-NLS-1$
        leftTableNameOrAlias = join.getRightTableAlias();
      }

      rightTableNameAndAlias = join.getLeftTablename();
      rightTableNameOrAlias = join.getLeftTablename();
      if ( !Const.isEmpty( join.getLeftTableAlias() ) ) {
        rightTableNameAndAlias += " " + join.getLeftTableAlias(); //$NON-NLS-1$
        rightTableNameOrAlias = join.getLeftTableAlias();
      }

      if ( join.getJoinType().equals( JoinType.LEFT_OUTER_JOIN ) ) {
        joinType = JoinType.RIGHT_OUTER_JOIN;
      } else if ( join.getJoinType().equals( JoinType.RIGHT_OUTER_JOIN ) ) {
        joinType = JoinType.LEFT_OUTER_JOIN;
      }
    }

    // The left hand side of the join clause...
    //
    clause.append( leftTableNameAndAlias );
    usedTables.add( leftTableNameOrAlias );

    // Now add the JOIN syntax
    //
    switch ( joinType ) {
      case INNER_JOIN:
        clause.append( " JOIN " );break; //$NON-NLS-1$
      case LEFT_OUTER_JOIN:
        clause.append( " LEFT OUTER JOIN " );break; //$NON-NLS-1$
      case RIGHT_OUTER_JOIN:
        clause.append( " RIGHT OUTER JOIN " );break; //$NON-NLS-1$
      case FULL_OUTER_JOIN:
        clause.append( " FULL OUTER JOIN " );break; //$NON-NLS-1$
    }

    // Now, we generate the clause in one go...
    //
    if ( index < sortedJoins.size() - 1 ) {
      clause.append( Const.CR ).append( indent ).append( " ( " ).append( Const.CR ).append( indent ).append( "  " ); //$NON-NLS-1$ //$NON-NLS-2$
      clause.append( rightClause );
      clause.append( indent ).append( " ) " ); //$NON-NLS-1$
    } else {
      clause.append( rightTableNameAndAlias );
      usedTables.add( rightTableNameOrAlias );
      // if (!Const.isEmpty(rightTableAlias)) clause.append(" ").append(rightTableAlias);
    }

    // finally add the ON () part
    //

    SQLWhereFormula joinFormula = join.getSqlWhereFormula();
    clause.append( Const.CR ).append( indent ).append( " ON ( " ); //$NON-NLS-1$
    clause.append( joinFormula.getFormula() );

    // Now see if there are any SQL where conditions that apply to either two tables...
    // NOTE: Don't even bother with this in the case of full outer joins. In that case we want
    // to delay the condition as long as possible (until outside this JOIN).
    //
    // You can also force it to delay the join conditions by setting a property on the business model:
    // 1- Edit the business model
    // 2- Add new boolean property called "delay_outer_join_conditions" (no quotes)
    // 3- Set the value to "true" (checked)
    //
    // This will be picked up by the SQLGenerator and set in the query model.
    //
    if ( ( !joinType.equals( JoinType.FULL_OUTER_JOIN ) ) && ( !query.getDelayOuterJoinConditions() ) ) {
      for ( SQLWhereFormula sqlWhereFormula : query.getWhereFormulas() ) {
        if ( !usedSQLWhereFormula.contains( sqlWhereFormula ) && !sqlWhereFormula.isContainingAggregate() ) {
          boolean allInvolvedAvailableHere = true;
          for ( String involvedTable : sqlWhereFormula.involvedTables ) {
            if ( !involvedTable.equalsIgnoreCase( leftTableNameOrAlias )
                && !involvedTable.equalsIgnoreCase( rightTableNameOrAlias ) ) {
              allInvolvedAvailableHere = false;
            }
          }

          // We can't place a constraint on the left table of a left outer join...
          //
          if ( joinType.equals( JoinType.LEFT_OUTER_JOIN )
              && Const.indexOfString( leftTableNameOrAlias, sqlWhereFormula.involvedTables ) >= 0 ) {
            allInvolvedAvailableHere = false;
          }

          // We can't place a constraint on the right table of a right outer join either...
          //
          if ( joinType.equals( JoinType.RIGHT_OUTER_JOIN )
              && Const.indexOfString( rightTableNameOrAlias, sqlWhereFormula.involvedTables ) >= 0 ) {
            allInvolvedAvailableHere = false;
          }

          // If all the involved tables are (usually 1) is part of this join, we specify the condition here...
          if ( allInvolvedAvailableHere ) {
            clause.append( " AND ( " ).append( sqlWhereFormula.getFormula() ).append( " ) " ); //$NON-NLS-1$ //$NON-NLS-2$
            // Remember that we did use it...
            usedSQLWhereFormula.add( sqlWhereFormula );
          }
        }
      }
    }
    clause.append( " )" ).append( Const.CR ); //$NON-NLS-1$

    return clause.toString();
  }

  public String getStringWildCard() {
    return "%"; //$NON-NLS-1$
  }

  public String getCharWildCard() {
    return "_"; //$NON-NLS-1$
  }

  /**
   * generates a sql query based on the SQLQueryModel object
   * 
   * @param query
   * @return
   */
  public String generateSelectStatement( SQLQueryModel query ) {
    StringBuilder sql = new StringBuilder();
    generateSelect( query, sql );

    List<SQLWhereFormula> usedSQLWhereFormula = new ArrayList<SQLWhereFormula>();

    if ( query.containsOuterJoins() ) {
      usedSQLWhereFormula = generateOuterJoin( query, sql );
    } else {
      // This is the "classic" join syntax
      generateFrom( query, sql );
      generateJoins( query, sql );
    }

    generateWhere( query, sql, usedSQLWhereFormula );
    generateGroupBy( query, sql );
    generateHaving( query, sql );
    generateOrderBy( query, sql );
    generatePostOrderBy( query, sql );

    return sql.toString();
  }

  /**
   * default the max table name length to a very large number.
   * 
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return Integer.MAX_VALUE;
  }
}
