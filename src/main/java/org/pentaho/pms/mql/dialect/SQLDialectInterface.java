/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.pms.mql.dialect;

/**
 * This interface defines how specific SQL dialects interact with the metadata system's sql generation.
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
   * This method quotes a string literal. Note that for the time being we just use the ANSI standard
   * 
   * @param databaseMeta
   *          passed in for potential future use
   * @param str
   *          string to quote
   * @return quoted string
   */
  public String quoteStringLiteral( Object str );

  /**
   * returns true if a function is supported by PMSFormulaContext
   * 
   * @param functionName
   *          name of function
   * @return true if function is supported
   */
  public boolean isSupportedFunction( String functionName );

  /**
   * returns true if a function is an aggregate function. This is used because certain contexts do not allow aggregates.
   * 
   * @param functionName
   *          name of function
   * @return true if aggregate
   */
  public boolean isAggregateFunction( String functionName );

  /**
   * returns true if infix operator is supported
   * 
   * @param operator
   *          operator to validate
   * @return true if supported
   */
  public boolean isSupportedInfixOperator( String operator );

  /**
   * return a reference to the sql generator for a given function
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLFunctionGeneratorInterface getFunctionSQLGenerator( String functionName );

  /**
   * return a reference to the sql generator for a given infix operator
   * 
   * @param functionName
   * @return sqlgenerator object
   */
  public SQLOperatorGeneratorInterface getInfixOperatorSQLGenerator( String operatorName );

  /**
   * this method renders a date into dialect specific SQL
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return appropriate sql
   */
  public String getDateSQL( int year, int month, int day );

  /**
   * this method renders a date into dialect specific SQL
   * 
   * @param year
   * @param month
   * @param day
   * @param hour
   * @param minute
   * @param second
   * @param milli
   * @return
   */
  public String getDateSQL( int year, int month, int day, int hour, int minute, int second, int milli );

  /**
   * this method renders a SQLQueryModel to a string
   * 
   * @param model
   *          the model to generate sql from
   * 
   * @return dialect specific sql
   */
  public String generateSelectStatement( SQLQueryModel model );

  /**
   * Maximum Table Name Length
   */
  public int getMaxTableNameLength();

  /**
   * @return True if the dialect supports N'xxxx' notation on string literals.
   */
  boolean supportsNLSLiteral();
}
