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

import org.pentaho.pms.MetadataTestBase;

public class HypersonicDialectIT extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT TOP 10 DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new HypersonicDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new HypersonicDialect(), createUnlimitedQuery() );
  }

  public void testGetDateSQL() {
    String dateExpected = "'2015-08-28 00:00:00.0'";
    HypersonicDialect dialect = new HypersonicDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2015, 8, 28 ) );
  }
}
