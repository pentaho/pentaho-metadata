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

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;

/**
 * Oracle Implementation of Metadata SQL Dialect
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class OracleDialect extends DefaultSQLDialect {

  public OracleDialect() {
    super( "ORACLE" ); //$NON-NLS-1$

    // oracle specific date functions
    supportedFunctions.put(
        "NOW", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "SYSDATE", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( sql );
          }
        } );

    // oracle specific boolean data type

    supportedFunctions.put(
        "TRUE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "TRUE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "1=1" );
          }
        } );

    supportedFunctions.put(
        "FALSE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "FALSE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "1=0" );
          }
        } );
  }

  /**
   * return Oracle formatted date, TO_DATE('YYYY-MM-DD','YYYY-MM-DD')
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return "TO_DATE(" + //$NON-NLS-1$
        quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) ) + //$NON-NLS-1$ //$NON-NLS-2$
        "," + quoteStringLiteral( "YYYY-MM-DD" ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  /**
   * return Oracle formatted date, TO_DATE('YYYY-MM-DD','YYYY-MM-DD')
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL( int year, int month, int day, int hour, int minute, int second, int milli ) {
    return "TO_DATE(" + //$NON-NLS-1$
        quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) + //$NON-NLS-1$ //$NON-NLS-2$
            " " + displayAsTwoOrMoreDigits( hour )
            + ":" + displayAsTwoOrMoreDigits( minute ) + ":" + displayAsTwoOrMoreDigits( second ) ) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "," + quoteStringLiteral( "YYYY-MM-DD HH24:MI:SS" ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  /**
   * Oracle has a 30 character limit on table name length
   * 
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 30;
  }

  @Override
  protected String getStringConcatOperator() {
    return "||"; //$NON-NLS-1$
  }

  // Oracle supports the N'xxxx' notation for Unicode strings.
  public boolean supportsNLSLiteral() {
    return true;
  }

  @Override
  public String generateSelectStatement( SQLQueryModel query ) {
    if ( query.getLimit() >= 0 ) {
      String origSelect = super.generateSelectStatement( query );
      StringBuilder sql = new StringBuilder();
      sql.append( "SELECT * FROM (" ); //$NON-NLS-1$
      sql.append( origSelect );
      sql.append( ") WHERE ROWNUM <= " ); //$NON-NLS-1$
      sql.append( query.getLimit() );
      return sql.toString();
    } else {
      return super.generateSelectStatement( query );
    }
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }

}
