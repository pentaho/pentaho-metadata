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
 * MySQL Implementation of Metadata SQL Dialect
 *
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class MySQLDialect extends DefaultSQLDialect {

  public MySQLDialect() {
    super( "MYSQL" ); //$NON-NLS-1$
  }

  protected MySQLDialect( String compatibleDialect ) {
    super( compatibleDialect ); //$NON-NLS-1$
  }

  /**
   * return MySQL formatted date, DATE('YYYY-MM-DD')
   *
   * @param year
   * @param month
   * @param day
   *
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return "DATE(" + //$NON-NLS-1$
        quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) ) + //$NON-NLS-1$ //$NON-NLS-2$
        ")"; //$NON-NLS-1$
  }

  /**
   * MYSQL has a 64 character limit on table name length
   *
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 64;
  }

  // there is no string concat operator in MySQL
  @Override
  protected String getStringConcatOperator() {
    return null; //$NON-NLS-1$
  }

  @Override
  protected String generateStringConcat( String... vals ) {
    StringBuilder sb = new StringBuilder();
    sb.append( "CONCAT(" );
    for ( int i = 0; i < vals.length; i++ ) {
      if ( i != 0 ) {
        sb.append( ", " ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      sb.append( vals[i] );
    }
    sb.append( ")" );
    return sb.toString();
  }

  // MySql supports the N'xxxx' notation for Unicode strings.
  public boolean supportsNLSLiteral() {
    return true;
  }

  @Override
  protected void generatePostOrderBy( SQLQueryModel query, StringBuilder sql ) {
    generateLimit( query, sql );
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }
}
