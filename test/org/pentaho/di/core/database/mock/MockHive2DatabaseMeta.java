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
 * Copyright (c) 20011 Pentaho Corporation.  All rights reserved.
 */
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
