/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.schema.dialect;

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
  public DefaultSQLOperatorGenerator(String sql) {
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
