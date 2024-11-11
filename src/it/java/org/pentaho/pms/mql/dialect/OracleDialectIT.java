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

import org.pentaho.pms.MetadataTestBase;

public class OracleDialectIT extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect(
        "SELECT * FROM (SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC ) WHERE ROWNUM <= 10",
        new OracleDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC ",
        new OracleDialect(), createUnlimitedQuery() );
  }

  public void testGetDateSQL() {
    String dateExpected = "TO_DATE('2000-01-01','YYYY-MM-DD')";
    DefaultSQLDialect dialect = new OracleDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1 ) );
  }

  public void testGetDateSQL_withTime() {
    String dateExpected = "TO_DATE('2000-01-01 12:00:00','YYYY-MM-DD HH24:MI:SS')";
    DefaultSQLDialect dialect = new OracleDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1, 12, 0, 0, 0 ) );
  }

}
