/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

public class FirebirdDialectIT extends MetadataTestBase {
  public void testLimitSQL() {
    assertSelect( "SELECT FIRST 10 DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new FirebirdDialect(), createLimitedQuery() );
  }

  public void testNoLimitSQL() {
    assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new FirebirdDialect(), createUnlimitedQuery() );
  }
}
