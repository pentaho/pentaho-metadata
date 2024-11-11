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
 * MS Access implementation of Metadata SQL Dialect
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class MSAccessDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$

  /**
   * constructor
   */
  public MSAccessDialect() {
    super( "MSACCESS" ); //$NON-NLS-1$
  }

  /**
   * return MS Access formatsted date, #MM/DD/YYYY#
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return "#" + displayAsTwoOrMoreDigits( month ) + "/" + displayAsTwoOrMoreDigits( day ) + "/" + year + "#"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  /**
   * MSAccess has a 64 character limit on table name length
   * 
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 64;
  }

  protected String getStringConcatOperator() {
    return "+";
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateTopAfterDistinct( query, sql, TOP_KEYWORD );
  }

}
