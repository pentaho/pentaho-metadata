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
 * Microsoft SQL Server Implementation of Metadata SQL Dialect
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class MSSQLDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$

  /**
   * constructor
   */
  public MSSQLDialect() {
    this( "MSSQL" ); //$NON-NLS-1$
  }

  /**
   * constructor
   */
  protected MSSQLDialect( String databaseType ) {
    super( databaseType );
    // microsoft sql server specific date functions
    supportedFunctions.put(
        "NOW", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "GETDATE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( sql );
          }
        } );

    // mssql specific boolean data type

    supportedFunctions.put(
        "TRUE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "TRUE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "(1=1)" );
          }
        } );

    supportedFunctions.put(
        "FALSE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "FALSE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "(0=1)" );
          }
        } );
  }

  /**
   * return MSSQL formatted date, 'YYYYMMDD'
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return quoteStringLiteral( year + displayAsTwoOrMoreDigits( month ) + displayAsTwoOrMoreDigits( day ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * MSSQL has a 128 character limit on table name length
   * 
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 128;
  }

  // MS SQL Server supports the N'xxxx' notation for Unicode strings.
  public boolean supportsNLSLiteral() {
    return true;
  }

  protected String getStringConcatOperator() {
    return "+";
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateTopAfterDistinct( query, sql, TOP_KEYWORD );
  }

}
