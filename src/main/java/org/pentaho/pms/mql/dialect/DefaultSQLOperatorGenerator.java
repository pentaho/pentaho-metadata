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
 * This is the default implementation of the SQL Operator Generator Interface
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class DefaultSQLOperatorGenerator implements SQLOperatorGeneratorInterface {

  /** sql to return to sql generator */
  private String sql;

  /**
   * constructor
   * 
   * @param sql
   */
  public DefaultSQLOperatorGenerator( String sql ) {
    this.sql = sql;
  }

  /**
   * return the sql specified in the constructor
   * 
   * @return sql
   */
  public String getOperatorSQL() {
    return sql;
  }
}
