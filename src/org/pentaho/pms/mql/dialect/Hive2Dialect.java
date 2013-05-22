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
package org.pentaho.pms.mql.dialect;


/**
 * Apache Hadoop Hive Server 2 Implementation of Metadata SQL Dialect
 * 
 */
public class Hive2Dialect extends BaseHiveDialect {

  protected final static String HIVE_DIALECT_TYPE = "HIVE2";
  
  protected final static String DRIVER_CLASS_NAME = "org.apache.hive.jdbc.HiveDriver";
  
  public Hive2Dialect() {
    super(HIVE_DIALECT_TYPE); //$NON-NLS-1$
  }
  
  @Override
  protected String getDriverClassName() {
    return DRIVER_CLASS_NAME;
  }
  
  protected static String getHiveDialectType() {
    return HIVE_DIALECT_TYPE;
  }
}
