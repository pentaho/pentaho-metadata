package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class MSSQLDialectIT extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT DISTINCT TOP 10 t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new MSSQLDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC", new MSSQLDialect(),
        createUnlimitedQuery() );
  }

  public void testGetDateSQL() {
    String dateExpected = "'20000101'";
    MSSQLDialect dialect = new MSSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1 ) );
  }

  public void testGetDateSQL_withTime() {
    String dateExpected = "'2000-01-01 12:00:00.0'";
    MSSQLDialect dialect = new MSSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1, 12, 0, 0, 0 ) );
  }
}
