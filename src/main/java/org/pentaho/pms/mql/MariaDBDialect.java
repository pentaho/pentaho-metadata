/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.pms.mql;

import org.pentaho.pms.mql.dialect.MySQLDialect;

public class MariaDBDialect extends MySQLDialect {

  public MariaDBDialect() {
    super( "MARIADB" );
  }

}
