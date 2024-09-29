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

import org.junit.Assert;
import org.pentaho.pms.MetadataTestBase;

public class GoogleBigQueryDialectIT extends MetadataTestBase {

  public void testLimitSQL() {
    assertSelect(
      "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC LIMIT 10",
      new GoogleBigQueryDialect(),
      createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect(
      "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
      new GoogleBigQueryDialect(),
      createUnlimitedQuery() );
  }

  public void testGetDateSQL() {
    String dateExpected = "CAST('2000-01-01' AS DATE)";
    DefaultSQLDialect dialect = new GoogleBigQueryDialect();
    Assert.assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1 ) );
  }

  public void testGetDateSQL_withTime() {
    String dateExpected = "CAST('2000-01-01 12:00:00.0' AS TIMESTAMP)";
    DefaultSQLDialect dialect = new GoogleBigQueryDialect();
    Assert.assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1, 12, 0, 0, 0 ) );
  }
}
