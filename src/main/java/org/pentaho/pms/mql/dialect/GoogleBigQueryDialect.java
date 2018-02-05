/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2018 - 2018 Hitachi Vantara. All rights reserved.
 */
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
