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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

/**
 * MySQL Implementation of Metadata SQL Dialect
 *  
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class MySQLDialect extends DefaultSQLDialect {
  
  public MySQLDialect() {
    super("MYSQL"); //$NON-NLS-1$
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
  public String getDateSQL(int year, int month, int day) {
    return "DATE(" + //$NON-NLS-1$
           quoteStringLiteral(year + "-" + displayAsTwoOrMoreDigits(month) + "-" + displayAsTwoOrMoreDigits(day)) + //$NON-NLS-1$ //$NON-NLS-2$
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
  protected String generateStringConcat(String... vals) {
    StringBuilder sb = new StringBuilder();
    sb.append("CONCAT(");
    for (int i = 0; i < vals.length; i++) {
      if (i != 0) {
        sb.append(", "); //$NON-NLS-1$ //$NON-NLS-2$
      }
      sb.append(vals[i]);
    }
    sb.append(")");
    return sb.toString();
  }
}
