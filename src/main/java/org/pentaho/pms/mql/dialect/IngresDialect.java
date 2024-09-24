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

public class IngresDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "FIRST"; //$NON-NLS-1$

  public IngresDialect() {
    super( "INGRES" ); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateTopBeforeDistinct( query, sql, TOP_KEYWORD );
  }

}
