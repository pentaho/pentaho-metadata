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

/**
 * This interface defines how specific SQL dialects interact with the metadata 
 * system's sql generation.
 *  
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public interface SQLDialectInterface {
  
  /**
   * return the database type for this dialect
   * 
   * @return database type
   */
  public String getDatabaseType();
  
  /**
   * This method quotes a string literal.  Note that for the time being we just
   * use the ANSI standard
   * 
   * @param databaseMeta passed in for potential future use
   * @param str string to quote
   * @return quoted string
   */
  public String quoteStringLiteral(Object str);
  
  /**
   * returns true if a function is supported by PMSFormulaContext
   * 
   * @param functionName name of function
   * @return true if function is supported
   */
  public boolean isSupportedFunction(String functionName);
  
  /**
   * returns true if a function is an aggregate function.  This 
   * is used because certain contexts do not allow aggregates.
   * 
   * @param functionName name of function
   * @return true if aggregate
   */
  public boolean isAggregateFunction(String functionName);
  
  /**
   * returns true if infix operator is supported
   * 
   * @param operator operator to validate
   * @return true if supported
   */
  public boolean isSupportedInfixOperator(String operator);
  
  /**
   * return a reference to the sql generator for a given function
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLFunctionGeneratorInterface getFunctionSQLGenerator(String functionName);
  
  /**
   * return a reference to the sql generator for a given infix operator
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLOperatorGeneratorInterface getInfixOperatorSQLGenerator(String operatorName);
  
  /**
   * this method renders a date into dialect specific SQL
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return appropriate sql
   */
  public String getDateSQL(int year, int month, int day);
  
  /**
   * this method renders a SQLQueryModel to a string
   * 
   * @param model the model to generate sql from
   * 
   * @return dialect specific sql 
   */
  public String generateSelectStatement(SQLQueryModel model);
}
