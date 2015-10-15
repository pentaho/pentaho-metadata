package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class PostgreSQLDialectIT extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC LIMIT 10",
        new PostgreSQLDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new PostgreSQLDialect(), createUnlimitedQuery() );
  }

  public void testGetDateSQL() {
    String dateExpected = "date '2000-01-01'";
    DefaultSQLDialect dialect = new PostgreSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1 ) );
  }

  public void testGetDateSQL_withTime() {
    String dateExpected = "timestamp '2000-01-01 12:00:00.0'";
    DefaultSQLDialect dialect = new PostgreSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1, 12, 0, 0, 0 ) );
  }

}
