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

/**
 * Apache Hadoop Hive Server 2 Implementation of Metadata SQL Dialect
 * 
 */
public class Hive2Dialect extends BaseHiveDialect {

  protected static final String HIVE_DIALECT_TYPE = "HIVE2";

  protected static final String DRIVER_CLASS_NAME = "org.apache.hive.jdbc.HiveDriver";

  public Hive2Dialect() {
    super( HIVE_DIALECT_TYPE ); //$NON-NLS-1$
  }

  @Override
  protected String getDriverClassName() {
    return DRIVER_CLASS_NAME;
  }

  protected static String getHiveDialectType() {
    return HIVE_DIALECT_TYPE;
  }
}
