/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.mql.dialect;

public class RedshiftDialect extends PostgreSQLDialect {

  public RedshiftDialect() {
    super( "REDSHIFT" );
  }

  @Override
  public boolean supportsNLSLiteral() {
    return false;
  }
}
