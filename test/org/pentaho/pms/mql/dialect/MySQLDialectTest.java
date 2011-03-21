package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class MySQLDialectTest extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect("SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC LIMIT 10",
        new MySQLDialect(), createLimitedQuery());
  }

  public void testNoLimitSQL() {
    assertSelect("SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC", new MySQLDialect(),
        createUnlimitedQuery());
  }
}
