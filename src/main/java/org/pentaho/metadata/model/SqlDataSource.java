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
package org.pentaho.metadata.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is temporary until we have a thin DatabaseMeta implementation. The thin implementation will not be
 * available until Kettle 4.0, so this class will need to survive for a while.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class SqlDataSource implements Serializable {

  private static final long serialVersionUID = -911638128486994514L;

  public enum DataSourceType {
    NATIVE, ODBC, OCI, PLUGIN, JNDI, CUSTOM
  }

  private DataSourceType type = DataSourceType.JNDI;
  private String databaseName;
  private String username;
  private String password;
  private String hostname;
  private String port;
  private String dialectType;
  private String servername; //Stores Informix Server name for one
  private Map<String, String> attributes = new HashMap<String, String>();

  public void setType( DataSourceType type ) {
    this.type = type;
  }

  public DataSourceType getType() {
    return type;
  }

  public void setDatabaseName( String databaseName ) {
    this.databaseName = databaseName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setUsername( String username ) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setDialectType( String dialectType ) {
    this.dialectType = dialectType;
  }

  public String getDialectType() {
    return dialectType;
  }

  public void setHostname( String hostname ) {
    this.hostname = hostname;
  }

  public String getHostname() {
    return hostname;
  }

  public void setPort( String port ) {
    this.port = port;
  }

  public String getPort() {
    return port;
  }

  public void setAttributes( Map<String, String> attributes ) {
    this.attributes = attributes;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public String getServername() {
    return servername;
  }

  public void setServername( String serverName ) {
    this.servername = serverName;
  }

}
