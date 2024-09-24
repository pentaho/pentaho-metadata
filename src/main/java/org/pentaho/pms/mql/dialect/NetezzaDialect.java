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

public class NetezzaDialect extends DefaultSQLDialect {

  public NetezzaDialect() {
    super( "NETEZZA" ); //$NON-NLS-1$
  }

  @Override
  protected void generatePostOrderBy( SQLQueryModel query, StringBuilder sql ) {
    generateLimit( query, sql );
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }

}
