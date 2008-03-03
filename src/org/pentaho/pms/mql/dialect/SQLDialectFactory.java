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
package org.pentaho.pms.mql.dialect;

import java.util.HashMap;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;

/**
 * This class should eventually load additional plugin dialects
 * 
 * @author Will Gorman
 *
 */
public class SQLDialectFactory {
  /** singleton instance, one per classloader */
  private static SQLDialectFactory singleton = new SQLDialectFactory();
  
  private Map<String, SQLDialectInterface> supportedDialects = new HashMap<String, SQLDialectInterface>();
  
  /**
   * private constructor, for now supported functions and operators are hardcoded, in the future
   * this may be moved to a config file
   */
  private SQLDialectFactory() {
    addDialect(new DefaultSQLDialect());
    addDialect(new OracleDialect());
    addDialect(new MSSQLDialect());
    addDialect(new DB2Dialect());
    addDialect(new PostgreSQLDialect());
    addDialect(new MySQLDialect());
    addDialect(new MSAccessDialect());
  }
  
  private void addDialect(SQLDialectInterface dialect) {
     supportedDialects.put(dialect.getDatabaseType(), dialect);
  }
  
  public static SQLDialectInterface getSQLDialect(DatabaseMeta databaseMeta) {
    SQLDialectInterface sqlDialect = (SQLDialectInterface)singleton.supportedDialects.get(databaseMeta.getDatabaseTypeDesc());
    if (sqlDialect == null) {
      sqlDialect = (SQLDialectInterface)singleton.supportedDialects.get("GENERIC"); //$NON-NLS-1$
    }
    return sqlDialect;
  }
}
