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

package org.pentaho.di.core.database.mock;

import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;

/**
 * Mock database interface for Hive 2 so we don't have to depend on the Pentaho Big Data Plugin project at all. It is
 * purely a runtime dependency.
 * 
 */
@DatabaseMetaPlugin( type = "HIVE2", typeDescription = "Hadoop Hive 2" )
public class MockHive2DatabaseMeta extends MockHiveDatabaseMeta implements DatabaseInterface {

}
