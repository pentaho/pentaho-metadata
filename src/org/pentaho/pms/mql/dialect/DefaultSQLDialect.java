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
package org.pentaho.pms.mql.dialect;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.formula.lvalues.FormulaFunction;
import org.jfree.formula.lvalues.StaticValue;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;

/**
 * This is the Default SQL Dialect Class that implements SQLDialectInterface.
 * Specific Database Dialect Classes should extend this class.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class DefaultSQLDialect implements SQLDialectInterface {
  
  protected Map<String,SQLFunctionGeneratorInterface>  supportedFunctions = new HashMap<String,SQLFunctionGeneratorInterface> ();
  protected Map<String,SQLOperatorGeneratorInterface> supportedInfixOperators = new HashMap<String,SQLOperatorGeneratorInterface>();
  String databaseType;
  DatabaseMeta databaseMeta;
  
  public DefaultSQLDialect() {
    this("GENERIC"); //$NON-NLS-1$
  }
  
  public DefaultSQLDialect(String databaseType) {
    this.databaseType = databaseType;
    this.databaseMeta = new DatabaseMeta("", databaseType, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    // logical functions
    supportedFunctions.put("AND", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.INLINE_FUNCTION, "AND")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put("OR",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.INLINE_FUNCTION, "OR"));  //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put("NOT",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "NOT", 1));  //$NON-NLS-1$ //$NON-NLS-2$

    // infix operators
    supportedInfixOperators.put("+",  new DefaultSQLOperatorGenerator("+")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("-",  new DefaultSQLOperatorGenerator("-")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("*",  new DefaultSQLOperatorGenerator("*")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("/",  new DefaultSQLOperatorGenerator("/")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("=",  new DefaultSQLOperatorGenerator("=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<",  new DefaultSQLOperatorGenerator("<")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put(">",  new DefaultSQLOperatorGenerator(">")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<=", new DefaultSQLOperatorGenerator("<=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put(">=", new DefaultSQLOperatorGenerator(">=")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("<>", new DefaultSQLOperatorGenerator("<>")); //$NON-NLS-1$ //$NON-NLS-2$
    supportedInfixOperators.put("LIKE", new DefaultSQLOperatorGenerator("LIKE")); //$NON-NLS-1$ //$NON-NLS-2$

    //
    // comparison functions
    //
    supportedFunctions.put("LIKE",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.INLINE_FUNCTION, "LIKE", 2, false)); //$NON-NLS-1$ //$NON-NLS-2$
    supportedFunctions.put("IN",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "IN", 2) { //$NON-NLS-1$ //$NON-NLS-2$
      
      /**
       * make sure there are at least two params
       */
      public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
          if (f.getChildValues() == null || f.getChildValues().length < 2) {
            throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "" + paramCount)); //$NON-NLS-1$ //$NON-NLS-2$
          }
      }
      
      /**
       * render the necessary sql
       */
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        formula.generateSQL(f, f.getChildValues()[0], sb, locale);
        sb.append(" IN ( "); //$NON-NLS-1$
        formula.generateSQL(f, f.getChildValues()[1], sb, locale);
        for (int i = 2; i < f.getChildValues().length; i++) {
          sb.append(" , "); //$NON-NLS-1$
          formula.generateSQL(f, f.getChildValues()[i], sb, locale);
        }
        sb.append(" ) "); //$NON-NLS-1$
      }
    });
    
    //
    // aggregator functions
    //
    
    supportedFunctions.put("COUNT", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL() { 
          return databaseMeta.getFunctionCount();
      }
    });   

    supportedFunctions.put("SUM", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL() { 
          return databaseMeta.getFunctionSum();
      }
    });

    supportedFunctions.put("AVG", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL() { 
          return databaseMeta.getFunctionAverage();
      }
    });
    
    supportedFunctions.put("MIN", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL() { 
          return databaseMeta.getFunctionMinimum();
      }
    });
    
    supportedFunctions.put("MAX", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION) { //$NON-NLS-1$
      public String getSQL() { 
          return databaseMeta.getFunctionMaximum();
      }
    });
    
    //
    // date functions
    //
    
    supportedFunctions.put("NOW", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "NOW()", 0) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        sb.append(sql);
      }
    });
    
    // note, by using the "getDateSQL" method, inheriting classes can alter how the date is displayed
    
    supportedFunctions.put("DATE", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "DATE", 3) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        BigDecimal year = (BigDecimal)((StaticValue)f.getChildValues()[0]).getValue();
        BigDecimal month = (BigDecimal)((StaticValue)f.getChildValues()[1]).getValue();
        BigDecimal day = (BigDecimal)((StaticValue)f.getChildValues()[2]).getValue();
        sb.append(getDateSQL(year.intValue(), month.intValue(), day.intValue()));
        
      }
      
      public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
        super.validateFunction(f);
        // check to make sure all three params are of static number type
        verifyAllStaticNumbers(f);
      }
    });
    
    // note, by using the "getDateSQL" method, inheriting classes can alter how the date is displayed
    
    supportedFunctions.put("DATEVALUE", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "DATE", 1) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        Pattern p = Pattern.compile("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)"); //$NON-NLS-1$
        Matcher m = p.matcher((String)((StaticValue)f.getChildValues()[0]).getValue());
        if (!m.matches()) {
          throw new PentahoMetadataException(Messages.getErrorString("DefaultSQLDialect.ERROR_0001_DATE_STRING_SYNTAX_INVALID", (String)((StaticValue)f.getChildValues()[0]).getValue())); //$NON-NLS-1$
        }
        int year = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2));
        int day = Integer.parseInt(m.group(3));
       sb.append(getDateSQL(year, month, day));
      }
      
      public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
        super.validateFunction(f);
        // check to make sure all three params are of static number type
        verifyAllStaticStrings(f);
      }
    });
    
    
    // case function
    
    supportedFunctions.put("CASE",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "CASE") { //$NON-NLS-1$ //$NON-NLS-2$
      
      /**
       * make sure there are at least two params
       */
      public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
          if (f.getChildValues() == null || f.getChildValues().length < 2) {
            throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "2")); //$NON-NLS-1$ //$NON-NLS-2$
          }
      }
      
      /**
       * render the necessary sql
       */
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        sb.append(" CASE "); //$NON-NLS-1$

        for (int i = 1; i < f.getChildValues().length; i+=2) {
          sb.append(" WHEN "); //$NON-NLS-1$
          formula.generateSQL(f, f.getChildValues()[i-1], sb, locale);
          sb.append(" THEN "); //$NON-NLS-1$
          formula.generateSQL(f, f.getChildValues()[i], sb, locale);
        }
        
        if (f.getChildValues().length % 2 == 1) {
          sb.append(" ELSE "); //$NON-NLS-1$
          formula.generateSQL(f, f.getChildValues()[f.getChildValues().length - 1], sb, locale);
        }
        
        sb.append(" END "); //$NON-NLS-1$
      }
    });

    
    // coalesce
    supportedFunctions.put("COALESCE",  new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "COALESCE") { //$NON-NLS-1$ //$NON-NLS-2$
      
      /**
       * make sure there are at least two params
       */
      public void validateFunction(FormulaFunction f) throws PentahoMetadataException {
          if (f.getChildValues() == null || f.getChildValues().length < 1) {
            throw new PentahoMetadataException(Messages.getErrorString("PMSFormulaContext.ERROR_0002_INVALID_NUMBER_PARAMS", f.getFunctionName(), "1")); //$NON-NLS-1$ //$NON-NLS-2$
          }
      }
    });
    
  }
  
  /**
   * utility function to format integer correctly for dates
   * 
   * @param number number to format
   * @return correctly formatted string
   */
  protected String displayAsTwoOrMoreDigits(int number) {
    if (number >= 0 && number < 10) {
      return "0" + number; //$NON-NLS-1$
    } else {
      return "" + number; //$NON-NLS-1$
    }
  }
  
  /**
   * returns the correct dialect string for date representations in SQL
   * 
   * @param year the year 
   * @param month the month
   * @param day the day
   * @return string representation for date SQL
   */
  public String getDateSQL(int year, int month, int day) {
    return quoteStringLiteral(year + "-" + displayAsTwoOrMoreDigits(month) + "-" + displayAsTwoOrMoreDigits(day)); //$NON-NLS-1$ //$NON-NLS-2$
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
    SQLFunctionGeneratorInterface gen = getFunctionSQLGenerator(functionName);
    if (gen != null) {
      return gen.getType() == SQLFunctionGeneratorInterface.PARAM_AGG_FUNCTION;
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
  public SQLFunctionGeneratorInterface getFunctionSQLGenerator(String functionName) {
    return (SQLFunctionGeneratorInterface)supportedFunctions.get(functionName);
  }
  
  /**
   * return a reference to the sql generator for a given infix operator
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLOperatorGeneratorInterface getInfixOperatorSQLGenerator(String operatorName) {
    return (SQLOperatorGeneratorInterface)supportedInfixOperators.get(operatorName);
  }
  
  
  /**
   * This method quotes a string literal.  Note that for the time being we just
   * use the ANSI standard
   * 
   * @param databaseMeta passed in for potential future use
   * @param str string to quote
   * @return quoted string
   */
  public String quoteStringLiteral(Object str) {
    return "'" + str + "'"; //$NON-NLS-1$  //$NON-NLS-2$
  }
}
