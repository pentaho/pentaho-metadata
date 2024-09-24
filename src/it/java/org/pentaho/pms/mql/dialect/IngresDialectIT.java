package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class IngresDialectIT extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT FIRST 10 DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new IngresDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new IngresDialect(), createUnlimitedQuery() );
  }
}
