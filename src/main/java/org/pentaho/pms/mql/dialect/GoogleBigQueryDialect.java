/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.mql.dialect;

/**
 * Google BigQuery Implementation of Metadata SQL Dialect
 *
 * @author lucboudreau
 *
 */
public class GoogleBigQueryDialect extends DefaultSQLDialect {

  public GoogleBigQueryDialect() {
    super( "GOOGLEBIGQUERY" ); //$NON-NLS-1$
  }

  protected GoogleBigQueryDialect( String compatibleDialect ) {
    super( compatibleDialect ); //$NON-NLS-1$
  }

  /**
   * CAST('YYYY-MM-DD' AS DATE)
   */
  @Override
  public String getDateSQL( int year, int month, int day ) {
    return "CAST(" + super.getDateSQL( year, month, day ) + " AS DATE)";
  }

  @Override
  public String getDateSQL( int year, int month, int day, int hour, int minute,
    int second, int milli ) {
    return "CAST(" + //$NON-NLS-1$
      super.getDateSQL( year, month, day, hour, minute, second, milli )
        + " AS TIMESTAMP)"; //$NON-NLS-1$
  }

  public int getMaxTableNameLength() {
    return 1024;
  }

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

  @Override
  protected void generatePostOrderBy( SQLQueryModel query, StringBuilder sql ) {
    generateLimit( query, sql );
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }
}
