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
import java.util.HashMap;
import java.util.Map;

/**
 * This class is temporary until we have a thin DatabaseMeta implementation.  The thin 
 * implementation will not be available until Kettle 4.0, so this class will need to 
 * survive for a while.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlDataSource implements Serializable {

  private static final long serialVersionUID = -911638128486994514L;

  public enum DataSourceType {
    NATIVE,
    ODBC,
    OCI,
    PLUGIN,
    JNDI,
    CUSTOM
  }
  
  private DataSourceType type = DataSourceType.JNDI;
  private String databaseName;
  private String username;
  private String password;
  private String hostname;
  private String port;
  private String dialectType;
  private Map<String, String> attributes = new HashMap<String, String>();
  
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
  public void setDialectType(String dialectType) {
    this.dialectType = dialectType;
  }
  public String getDialectType() {
    return dialectType;
  }
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }
  public String getHostname() {
    return hostname;
  }
  public void setPort(String port) {
    this.port = port;
  }
  public String getPort() {
    return port;
  }
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
  public Map<String, String> getAttributes() {
    return attributes;
  }
}