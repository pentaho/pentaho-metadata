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
