package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.MetadataTestBase;

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
}
