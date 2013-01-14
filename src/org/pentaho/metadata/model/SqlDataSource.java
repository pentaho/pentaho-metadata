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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
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