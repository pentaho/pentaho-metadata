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
package org.pentaho.pms.schema;

import java.util.HashMap;
import java.util.Map;

import org.jfree.formula.DefaultFormulaContext;
import org.jfree.formula.typing.Type;
import org.pentaho.pms.messages.Messages;

import be.ibridge.kettle.core.database.DatabaseMeta;

/**
 * This is a singleton class that manages PMSFormula's context.
 * the PMSFormula uses this class to validate operators and
 * functions, and it also uses this class to convert Formula
 * terms to SQL.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class PMSFormulaContext extends DefaultFormulaContext {
  
  /** singleton instance, one per classloader */
  private static PMSFormulaContext singleton = new PMSFormulaContext();
  
  /**
   * static, thread safe singleton retrieval
   * 
   * @return PMSFormulaContext singleton object
   */
  public static PMSFormulaContext getInstance() {
    return singleton;
  }
  
  /** 
   * map lookup of supported functions
   * Note: we'll have to go with a more advanced lookup mechanism if we want to go case insensitive.
   */
  private Map supportedFunctions = new HashMap();
  
  /** map lookup of supported operators */
  private Map supportedInfixOperators = new HashMap();
  
  /**
   * private constructor, for now supported functions and operators are hardcoded, in the future
   * this may be moved to a config file
   * 
   * TODO: Move function and operator definitions into a config file
   */
  private PMSFormulaContext() {
    
    // logical functions
    supportedFunctions.put("AND", new DefaultSQLGenerator(SQLGeneratorInterface.INLINE_FUNCTION, "AND")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put("OR",  new DefaultSQLGenerator(SQLGeneratorInterface.INLINE_FUNCTION, "OR"));  //$NON-NLS-1$ //$NON-NLS-2$
    
    // infix operators
    supportedInfixOperators.put("+",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "+")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("-",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "-")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("*",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "*")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("/",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "/")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("=",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "<")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put(">",  new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, ">")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<=", new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "<=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put(">=", new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, ">=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<>", new DefaultSQLGenerator(SQLGeneratorInterface.INFIX_OPERATOR, "<>")); //$NON-NLS-1$ //$NON-NLS-2$
    
    // aggregator functions
    supportedFunctions.put("COUNT", new DefaultSQLGenerator(SQLGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL(DatabaseMeta databaseMeta) { 
          return databaseMeta.getFunctionCount();
      }
    });   

    supportedFunctions.put("SUM", new DefaultSQLGenerator(SQLGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL(DatabaseMeta databaseMeta) { 
          return databaseMeta.getFunctionSum();
      }
    });

    supportedFunctions.put("AVG", new DefaultSQLGenerator(SQLGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL(DatabaseMeta databaseMeta) { 
          return databaseMeta.getFunctionAverage();
      }
    });
    
    supportedFunctions.put("MIN", new DefaultSQLGenerator(SQLGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL(DatabaseMeta databaseMeta) { 
          return databaseMeta.getFunctionMinimum();
      }
    });
    
    supportedFunctions.put("MAX", new DefaultSQLGenerator(SQLGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL(DatabaseMeta databaseMeta) { 
          return databaseMeta.getFunctionMaximum();
      }
    });
  }
  
  /**
   * DefaultSQLGenerator implements SQLGenerator, which is used by
   * PMSFormula when rendering SQL.  ANSI standard SQL operators
   * and functions will use this class
   */
  private static class DefaultSQLGenerator implements SQLGeneratorInterface {
    
    /** the type of the sql to be generated, which is used by PMSFormula */
    int type;
    /** the specific SQL to be generated */
    String sql;

    /**
     * constructor
     * 
     * @param type type of sql to be generated
     * @param sql specific ANSI sql to be generated
     */
    public DefaultSQLGenerator(int type) {
      this.type = type;
    }
    
    /**
     * constructor
     * 
     * @param type type of sql to be generated
     * @param sql specific ANSI sql to be generated
     */
    public DefaultSQLGenerator(int type, String sql) {
      this.type = type;
      this.sql = sql;
    }
    
    /**
     * type accessor method
     * @return type of sql to generate
     */
    public int getType() { 
      return type; 
    }
    
    /**
     * generates necessary sql.  Note that databaseMeta
     * isn't used by the default sql generator.
     * 
     * @param databaseMeta not used
     * @return sql
     */
    public String getSQL(DatabaseMeta databaseMeta) { 
      return sql; 
    }
    
    /**
     * ANSI sql function param separator.  Note that databaseMeta
     * isn't used by the default sql generator.
     * @param databaseMeta not used
     * @return ANSI comma
     */
    public String getFunctionParamSeparator(DatabaseMeta databaseMeta) { 
      return ","; //$NON-NLS-1$
      }
  }
  
  /**
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional
   * manner of executing a formula.  Instead they generate the necessary SQL to be 
   * executed from an RDBMS.  
   */
  public Object resolveReference(Object name) {
    throw new UnsupportedOperationException(Messages.getErrorString("PMSFormulaContext.ERROR_0001_INVALID_USE")); //$NON-NLS-1$
  }

  /**
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional
   * manner of executing a formula.  Instead they generate the necessary SQL to be 
   * executed from an RDBMS.  
   */
  public Type resolveReferenceType(Object name) {
    throw new UnsupportedOperationException(Messages.getErrorString("PMSFormulaContext.ERROR_0001_INVALID_USE")); //$NON-NLS-1$
  }
  
  /**
   * returns true if a function is supported by PMSFormulaContext
   * 
   * @param functionName name of function
   * @return true if function is supported
   */
  public boolean isSupportedFunction(String functionName) {
    return supportedFunctions.containsKey(functionName);
  }
  
  /**
   * returns true if a function is an aggregate function.  This 
   * is used because certain contexts do not allow aggregates.
   * 
   * @param functionName name of function
   * @return true if aggregate
   */
  public boolean isAggregateFunction(String functionName) {
    SQLGeneratorInterface gen = getFunctionSQLGenerator(functionName);
    if (gen != null) {
      return gen.getType() == SQLGeneratorInterface.PARAM_AGG_FUNCTION;
    }
    return false;
  }
  
  /**
   * returns true if infix operator is supported
   * 
   * @param operator operator to validate
   * @return true if supported
   */
  public boolean isSupportedInfixOperator(String operator) {
    return supportedInfixOperators.containsKey(operator);
  }
  
  /**
   * return a reference to the sql generator for a given function
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLGeneratorInterface getFunctionSQLGenerator(String functionName) {
    return (SQLGeneratorInterface)supportedFunctions.get(functionName);
  }
  
  /**
   * return a reference to the sql generator for a given infix operator
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLGeneratorInterface getInfixOperatorSQLGenerator(String operatorName) {
    return (SQLGeneratorInterface)supportedInfixOperators.get(operatorName);
  }
  
  /**
   * This method quotes a string literal.  Note that for the time being we just
   * use the ANSI standard
   * 
   * @param databaseMeta passed in for potential future use
   * @param str string to quote
   * @return quoted string
   */
  public String quoteStringLiteral(DatabaseMeta databaseMeta, Object str) {
    return "'" + str + "'"; //$NON-NLS-1$  //$NON-NLS-2$
  }
}
