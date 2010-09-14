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
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.Messages;

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

  private final Log logger;

  /**
   * private constructor
   */
  private SQLDialectFactory() {
    logger = LogFactory.getLog(SQLDialectFactory.class);
    loadDialects();
  }

  /**
   * Load and register dialects defined as service providers implementing {@link SQLDialectInterface}
   * (via Java's ServiceLoader mechanism).
   */
  private void loadDialects() {
    ServiceLoader<SQLDialectInterface> dialects = ServiceLoader.load(SQLDialectInterface.class);
    Iterator<SQLDialectInterface> dialectIter = dialects.iterator();
    while (dialectIter.hasNext()) {
      SQLDialectInterface dialect = null;
      try {
        dialect = dialectIter.next(); // Try to instantiate the next dialect
      } catch (Throwable t) {
        // Log an error if dialect instantiation/registration fails.  We don't know the dialect 
        // we attempted to load here so log it as a generic error with stack trace.  
        logger.error(Messages.getErrorString("SQLDialectFactory.ERROR_0001_ERROR_LOADING_DIALECT"), t); //$NON-NLS-1$
      }
      if (dialect != null) {
        addDialect(dialect);
        logger.info(Messages.getString("SQLDialectFactory.INFO_0001_DIALECT_REGISTERED", dialect.getDatabaseType())); //$NON-NLS-1$
      }
    }
  }

  private void addDialect(SQLDialectInterface dialect) {
    // Don't check for existing dialects for this database type, just overwrite the old one.
    // Possibly change this to prevent conflicting dialects?
    supportedDialects.put(dialect.getDatabaseType(), dialect);
  }

  public static SQLDialectInterface getSQLDialect(DatabaseMeta databaseMeta) {
    SQLDialectInterface sqlDialect = (SQLDialectInterface) singleton.supportedDialects.get(databaseMeta
        .getDatabaseTypeDesc());
    if (sqlDialect == null) {
      sqlDialect = (SQLDialectInterface) singleton.supportedDialects.get("GENERIC"); //$NON-NLS-1$
    }
    return sqlDialect;
  }
}
