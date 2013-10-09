package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class H2DialectTest extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT TOP 10 DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new H2Dialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC", new H2Dialect(),
        createUnlimitedQuery() );
  }
}
