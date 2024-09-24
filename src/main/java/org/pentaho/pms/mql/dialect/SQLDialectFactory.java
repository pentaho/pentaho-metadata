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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.pms.mql.MariaDBDialect;

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
    logger = LogFactory.getLog( SQLDialectFactory.class );
    registerCoreDialects();
    loadDialectPlugins();
  }

  /**
   * Register all dialects explicitly here as a stopgap solution so the core dialects are guaranteed to load before any
   * Dialect Plugins.
   */
  private void registerCoreDialects() {
    addDialect( new DefaultSQLDialect() );
    addDialect( new OracleDialect() );
    addDialect( new MSSQLDialect() );
    addDialect( new MSSQLNativeDialect() );
    addDialect( new DB2Dialect() );
    addDialect( new PostgreSQLDialect() );
    addDialect( new MariaDBDialect() );
    addDialect( new MySQLDialect() );
    addDialect( new GoogleBigQueryDialect() );
    addDialect( new MSAccessDialect() );
    addDialect( new NetezzaDialect() );
    addDialect( new H2Dialect() );
    addDialect( new HypersonicDialect() );
    addDialect( new SybaseDialect() );
    addDialect( new IngresDialect() );
    addDialect( new FirebirdDialect() );
    addDialect( new LucidDbDialect() );
    addDialect( new RedshiftDialect() );
    // Conditionally load the Hive Dialect if it can be loaded
    if ( HiveDialect.canLoad() ) {
      addDialect( new HiveDialect() );
    }
    // Conditionally load the Hive 2 Dialect if it can be loaded
    if ( Hive2Dialect.canLoad() ) {
      addDialect( new Hive2Dialect() );
    }
    // Conditionally load the Impala Dialect if it can be loaded
    if ( ImpalaDialect.canLoad() ) {
      addDialect( new ImpalaDialect() );
    }
  }

  /**
   * Load and register dialects defined as service providers implementing {@link SQLDialectInterface} (via Java's
   * ServiceLoader mechanism).
   */
  private void loadDialectPlugins() {
    ServiceLoader<SQLDialectInterface> dialects = ServiceLoader.load( SQLDialectInterface.class );
    Iterator<SQLDialectInterface> dialectIter = dialects.iterator();
    while ( dialectIter.hasNext() ) {
      SQLDialectInterface dialect = null;
      try {
        dialect = dialectIter.next(); // Try to instantiate the next dialect
      } catch ( ServiceConfigurationError err ) {
        // Log an error if dialect instantiation/registration fails for any other reason. We don't know the dialect
        // we attempted to load here so log it as a generic error with stack trace.
        logger.warn( Messages.getErrorString(
            "SQLDialectFactory.WARN_0001_DIALECT_COULD_NOT_BE_LOADED", err.getMessage() ) ); //$NON-NLS-1$
        if ( logger.isDebugEnabled() ) {
          logger.debug( Messages.getErrorString(
              "SQLDialectFactory.WARN_0001_DIALECT_COULD_NOT_BE_LOADED", err.getMessage() ), err ); //$NON-NLS-1$
        }
      }
      if ( dialect != null ) {
        addDialect( dialect );
      }
    }
  }

  private void addDialect( SQLDialectInterface dialect ) {
    // Don't check for existing dialects for this database type, just overwrite the old one.
    // Possibly change this to prevent conflicting dialects?
    supportedDialects.put( dialect.getDatabaseType(), dialect );
    logger.info( Messages.getString( "SQLDialectFactory.INFO_0001_DIALECT_REGISTERED", dialect.getDatabaseType() ) ); //$NON-NLS-1$
  }

  public static SQLDialectInterface getSQLDialect( DatabaseMeta databaseMeta ) {
    SQLDialectInterface sqlDialect =
        singleton.supportedDialects.get( databaseMeta.getDatabaseTypeDesc() );
    if ( sqlDialect == null ) {
      sqlDialect = singleton.supportedDialects.get( "GENERIC" ); //$NON-NLS-1$
    }
    return sqlDialect;
  }
}
