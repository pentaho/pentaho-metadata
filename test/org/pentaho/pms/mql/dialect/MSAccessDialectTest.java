package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class MSAccessDialectTest extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT DISTINCT TOP 10 t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new MSAccessDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new MSAccessDialect(), createUnlimitedQuery() );
  }
}
