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
package org.pentaho.metadata.util;

import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;

/**
 * This utility class handles DatabaseMeta related tasks needed by metadata layer users, used today in the BI Platform
 * metadata query component.
 */
public class DatabaseMetaUtil {

  public static DatabaseInterface getDatabaseInterface( String productName ) {

    if ( productName == null ) {
      return null;
    }

    productName = productName.toLowerCase();

    // special case to map hsql to hypersonic
    if ( productName.indexOf( "hsql" ) >= 0 ) { //$NON-NLS-1$
      productName = "hypersonic"; //$NON-NLS-1$
    }

    // look through all available database dialects for a match
    DatabaseInterface[] interfaces = DatabaseMeta.getDatabaseInterfaces();
    for ( int i = 0; i < interfaces.length; i++ ) {
      String typeDesc = interfaces[i].getPluginId().toLowerCase();
      if ( productName.equals( typeDesc ) ) {
        return DatabaseMeta.getDatabaseInterfaces()[i];
      }
    }
    for ( int i = 0; i < interfaces.length; i++ ) {
      String typeDesc = interfaces[i].getPluginId().toLowerCase();
      if ( productName.indexOf( typeDesc ) >= 0 ) {
        return DatabaseMeta.getDatabaseInterfaces()[i];
      }
    }

    return null;
  }

  public static DatabaseInterface getDatabaseInterface( String productName, DatabaseMeta databaseMeta ) {

    if ( productName == null ) {
      return null;
    }

    // special case to separate hive1 and hive2
    if ( productName.indexOf( "Apache Hive" ) >= 0 ) { //$NON-NLS-1$
      String hivePluginId = databaseMeta.getDatabaseInterface().getPluginId();
      switch ( hivePluginId ) {
        case "HIVE": //$NON-NLS-1$
        case "HIVE2": //$NON-NLS-1$
          productName = hivePluginId;
          break;
      }
    }
    return getDatabaseInterface( productName );
  }
}
