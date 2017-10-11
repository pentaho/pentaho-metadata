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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
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
