/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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

  public void testQuoteStringLiteralNLS() {
    DefaultSQLDialect redshiftDialect = new PostgreSQLDialect();
    String quotedWord = redshiftDialect.quoteStringLiteral( "Instalação" );
    assertEquals( "N'Instalação'", quotedWord );
  }

}
