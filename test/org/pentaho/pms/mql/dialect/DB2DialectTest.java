package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class DB2DialectTest extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect(
        "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC FETCH FIRST 10 ROWS ONLY",
        new DB2Dialect(), createLimitedQuery());
  }

  public void testNoLimitSQL() {
    assertSelect("SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC", new DB2Dialect(),
        createUnlimitedQuery());
  }
}
