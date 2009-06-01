/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.model;

import java.io.Serializable;

/**
 * This class is temporary until we have a thin DatabaseMeta implementation.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlDataSource implements Serializable {

  private static final long serialVersionUID = -911638128486994514L;

  public enum DataSourceType {
    JNDI,
    JDBC
  }
  
  private DataSourceType type = DataSourceType.JNDI;
  private String databaseName;
  private String driverClass;
  private String username;
  private String password;
  private String url;
  
  public void setType(DataSourceType type) {
    this.type = type;
  }
  public DataSourceType getType() {
    return type;
  }
  
  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }
  public String getDatabaseName() {
    return databaseName;
  }
  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }
  public String getDriverClass() {
    return driverClass;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getUsername() {
    return username;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPassword() {
    return password;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getUrl() {
    return url;
  }
}