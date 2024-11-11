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
 * PostgreSQL Implementation of Metadata SQL Dialect
 *
 * @author Will Gorman (wgorman@pentaho.org)
 */
public class PostgreSQLDialect extends DefaultSQLDialect {

  public PostgreSQLDialect( String dialectName ) {
    super( dialectName );
    // oracle specific date functions
    supportedFunctions.put(
      "NOW", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "now",
        0 ) { //$NON-NLS-1$ //$NON-NLS-2$
        public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
                                         FormulaFunction f ) throws PentahoMetadataException {
          sb.append( sql );
        }
      } );

  }

  public PostgreSQLDialect() {
    this( "POSTGRESQL" ); //$NON-NLS-1$
  }

  /**
   * return PostgreSQL formatted date, date 'YYYY-MM-DD'
   *
   * @param year
   * @param month
   * @param day
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return "date " + //$NON-NLS-1$
      quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits(
        day ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public String getDateSQL( int year, int month, int day, int hour, int minute, int second, int milli ) {
    return "timestamp " //$NON-NLS-1$
      + quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) //$NON-NLS-1$ //$NON-NLS-2$
      + " " + displayAsTwoOrMoreDigits( hour ) //$NON-NLS-1$
      + ":" + displayAsTwoOrMoreDigits( minute ) + ":" + displayAsTwoOrMoreDigits( second ) + "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + milli );
  }

  /**
   * PostgreSQL has a 63 character limit on table name length
   *
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 63;
  }

  @Override
  protected String getStringConcatOperator() {
    return "||"; //$NON-NLS-1$
  }

  // PostgreSQL the N'xxxx' notation for Unicode strings.
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
