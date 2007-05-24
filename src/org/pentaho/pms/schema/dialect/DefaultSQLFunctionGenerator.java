/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.schema.dialect;

import org.jfree.formula.lvalues.FormulaFunction;
import org.jfree.formula.lvalues.StaticValue;
import org.jfree.formula.typing.coretypes.NumberType;
import org.jfree.formula.typing.coretypes.TextType;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;

/**
 * This is the default implementation of the SQLFunctionGeneratorInterface.
 * Each MQL / libformula function must convert itself to SQL based on the dialect.
 * Extend this class in the various DB dialects to implement a new MQL function.
 *  
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class DefaultSQLFunctionGenerator implements SQLFunctionGeneratorInterface {
  
  protected int type;
  protected int paramCount = -1;
  protected String sql;
  
  /**
   * constructor
   * 
   * @param type the type of function
   */
  public DefaultSQLFunctionGenerator(int type) {
    this.type = type;
  }
  
  /**
   * constructor 
   * 
   * @param type the type of function
   * @param sql sql to return
   */
  public DefaultSQLFunctionGenerator(int type, String sql) {
    this(type);
    this.sql = sql;
  }
  
  /**
   * constructor
   * 
   * @param type the type of function
   * @param sql sql to return
   * @param paramCount expected number of parameters
   */
  public DefaultSQLFunctionGenerator(int type, String sql, int paramCount) {
    this(type, sql);
    this.paramCount = paramCount;
  }
  
  /**
   * default validation function verifies parameter count if necessary
   */
  public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
    if (paramCount != -1) {
      if (f.getChildValues() == null || f.getChildValues().length != paramCount) {
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "" + paramCount)); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }
  
  /**
   * This is a utility function that may be used by child classes to verify 
   * all params are static numbers.
   * 
   * @param f function to verify
   * @throws PMSFormulaException if params are not numbers
   */
  protected void verifyAllStaticNumbers(FormulaFunction f) throws PentahoMetadataException {
  
    for (int i = 0; i < f.getChildValues().length; i++) {
      if (!(f.getChildValues()[i] instanceof StaticValue) ||
          !(((StaticValue)f.getChildValues()[i]).getValueType() == NumberType.GENERIC_NUMBER)) {
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0003_INVALID_PARAM_TYPE_NOT_STATIC_NUMBER", f.getFunctionName())); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }
  
  /**
   * This method may be used by child classes to verify all params are static numbers.
   * 
   * @param f function to verify
   * @throws PMSFormulaException if params are not numbers
   */
  protected void verifyAllStaticStrings(FormulaFunction f) throws PentahoMetadataException {
  
    for (int i = 0; i < f.getChildValues().length; i++) {
      if (!(f.getChildValues()[i] instanceof StaticValue) ||
          !(((StaticValue)f.getChildValues()[i]).getValueType() instanceof TextType)) {
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0003_INVALID_PARAM_TYPE_NOT_STATIC_NUMBER", f.getFunctionName())); //$NON-NLS-1$ //$NON-NLS-2$
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
  
  /**
   * this is the default implementation of generateFunctionSQL.
   * 
   * Note that this function is part of the formula traversal process,
   * which is executed in PMSFormula
   * 
   * @see PMSFormula
   * 
   * @param formula the traversal instance
   * @param sb the string to append sql to
   * @param f libformula function object 
   */
  public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
    if (type == INLINE_FUNCTION) {
      if (f.getChildValues() != null && f.getChildValues().length > 0) {
        formula.generateSQL(f.getChildValues()[0], sb, locale);
        for (int i = 1; i < f.getChildValues().length; i++) {
          sb.append(" " + sql + " "); //$NON-NLS-1$ //$NON-NLS-2$
          formula.generateSQL(f.getChildValues()[i], sb, locale);
        }
      }
    } else if (type == PARAM_FUNCTION || type == PARAM_AGG_FUNCTION) {
      sb.append(" " + sql); //$NON-NLS-1$
      if (f.getChildValues() != null && f.getChildValues().length > 0) {
        formula.generateSQL(f.getChildValues()[0], sb, locale);
        for (int i = 1; i < f.getChildValues().length; i++) {
          sb.append(" , "); //$NON-NLS-1$
          formula.generateSQL(f.getChildValues()[i], sb, locale);
        }
      }
    }
  }
}
