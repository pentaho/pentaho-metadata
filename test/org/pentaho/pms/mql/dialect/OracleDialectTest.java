package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class OracleDialectTest extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect(
        "SELECT * FROM (SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC ) WHERE ROWNUM <= 10",
        new OracleDialect(), createLimitedQuery());
  }

  public void testNoLimitSQL() {
    assertSelect("SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC ",
        new OracleDialect(), createUnlimitedQuery());
  }
}