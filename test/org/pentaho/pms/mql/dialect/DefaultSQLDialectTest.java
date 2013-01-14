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
 * Copyright (c) 2010 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultSQLDialectTest extends MetadataTestBase {

  /**
   * Verify the WHERE condition is generated properly for a "simple" single table query
   */
  public void testSimpleWhereCondition_single_table() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("t.id is null", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) )"; //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces(expected, result);
  }

  /**
   * Verify the WHERE condition is generated properly when model contains an INNER JOIN
   */
  public void testSimpleWhereCondition_with_inner_join() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable("TABLE2", "two"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addJoin("TABLE", "t", "TABLE2", "two", JoinType.INNER_JOIN, "t.id = two.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    query.addWhereFormula("t.id is null", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT t.id FROM TABLE t, TABLE2 two WHERE ( t.id = two.id) AND ( ( t.id is null ) )"; //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces(expected, result);
  }

  /**
   * Verify the WHERE condition is generated properly when model contains an OUTER JOIN
   */
  public void testSimpleWhereCondition_with_outer_joins() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable("TABLE2", "two"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addJoin("TABLE", "t", "TABLE2", "two", JoinType.FULL_OUTER_JOIN, "t.id = two.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    query.addWhereFormula("t.id is null", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT t.id FROM TABLE t FULL OUTER JOIN TABLE2 two ON ( t.id = two.id) WHERE ( ( t.id is null ) )"; //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces(expected, result);
  }
  
  public void testLimitSQL() {
    assertSelect("SELECT DISTINCT TOP 10 t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new DefaultSQLDialect(), createLimitedQuery());
  }

  public void testNoLimitSQL() {
    assertSelect("SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new DefaultSQLDialect(), createUnlimitedQuery());
  }

  public void testGetDateSQL() {
    String dateExpected = "'2000-01-01'";
    DefaultSQLDialect dialect = new DefaultSQLDialect();
    assertEquals(dateExpected, dialect.getDateSQL(2000, 1, 1));
  }
  public void testGetDateSQL_withTime() {
    String dateExpected = "'2000-01-01 12:00:00.0'";
    DefaultSQLDialect dialect = new DefaultSQLDialect();
    assertEquals(dateExpected, dialect.getDateSQL(2000, 1, 1, 12, 0, 0, 0));
  }

}
