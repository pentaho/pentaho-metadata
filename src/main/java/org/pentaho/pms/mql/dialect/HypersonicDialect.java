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

public class HypersonicDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$

  public HypersonicDialect() {
    super( "HYPERSONIC" ); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateTopBeforeDistinct( query, sql, TOP_KEYWORD );
  }

  /**
   * return HSQL formatted date, 'YYYY-MM-DD hh:mm:ss'
   *
   * @param year
   * @param month
   * @param day
   *
   * @return date string
   */
  @Override
  public String getDateSQL( int year, int month, int day ) {
    return getDateSQL( year, month, day, 0, 0, 0, 0 );
  }
}
