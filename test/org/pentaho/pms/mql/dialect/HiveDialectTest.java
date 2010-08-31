package org.pentaho.pms.mql.dialect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.HiveDatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.util.DatabaseMetaUtil;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

/**
 * Tests the Hive Dialect to ensure joins are handled appropriately.
 * 
 * @author Jordan Ganoff (jganoff@pentaho.com)
 * 
 */
public class HiveDialectTest {

  @BeforeClass
  public static void initKettle() throws KettleException {
    KettleEnvironment.init(false);
  }

  @Test
  public void isValidJoinFormula_valid() {
    String formula = "t.a = v.b"; //$NON-NLS-1$
    assertTrue(new HiveDialect().isValidJoinFormula(formula));
  }

  @Test
  public void isValidJoinFormula_invalid() {
    String ne = "t.a <> v.b"; //$NON-NLS-1$
    String lt = "t.a < v.b"; //$NON-NLS-1$
    String gt = "t.a > v.b"; //$NON-NLS-1$
    String lte = "t.a <= v.b"; //$NON-NLS-1$
    String gte = "t.a >= v.b"; //$NON-NLS-1$
    String nullCheck = "t.a is null"; //$NON-NLS-1$
    String notNullCheck = "t.a is not null"; //$NON-NLS-1$

    final HiveDialect dialect = new HiveDialect();

    assertFalse(dialect.isValidJoinFormula(ne));
    assertFalse(dialect.isValidJoinFormula(lt));
    assertFalse(dialect.isValidJoinFormula(gt));
    assertFalse(dialect.isValidJoinFormula(lte));
    assertFalse(dialect.isValidJoinFormula(gte));
    assertFalse(dialect.isValidJoinFormula(nullCheck));
    assertFalse(dialect.isValidJoinFormula(notNullCheck));
  }

  @Test
  public void simpleQuery() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("a", null); //$NON-NLS-1$
    query.addTable("TABLE", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT \n          a\nFROM \n          TABLE\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void simpleQuery_not_distinct() {
    SQLQueryModel query = new SQLQueryModel();
    query.setDistinct(false);
    query.addSelection("a", null); //$NON-NLS-1$
    query.addTable("TABLE", null); //$NON-NLS-1$

    String expected = "SELECT \n          a\nFROM \n          TABLE\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void simpleQuery_multiple_selects() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("a", null); //$NON-NLS-1$
    query.addSelection("b", null); //$NON-NLS-1$
    query.addTable("TABLE", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT \n          a\n         ,b\nFROM \n          TABLE\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void simpleQuery_with_aliases() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("a", "alias"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    // Column aliases are currently not supported
    String expected = "SELECT DISTINCT \n          a\nFROM \n          TABLE t\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void simpleQuery_multiple_tables() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("a", null); //$NON-NLS-1$
    query.addSelection("b", null); //$NON-NLS-1$
    query.addTable("TABLE", null); //$NON-NLS-1$
    query.addTable("TABLE2", null); //$NON-NLS-1$

    String expected = "SELECT DISTINCT \n          a\n         ,b\nFROM \n          TABLE\n     JOIN TABLE2\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void outerJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addJoin("A", null, "B", null, JoinType.LEFT_OUTER_JOIN, "A.id = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    SQLDialectInterface dialect = new HiveDialect();
    try {
      dialect.generateSelectStatement(query);
      fail("Should not be able to generate SQL for outer joins with Hive"); //$NON-NLS-1$
    } catch (Exception ex) {
      assertTrue("Expected exception [HiveDialect.ERROR_0001] but received: " + ex.getMessage(), //$NON-NLS-1$
          ex.getMessage().contains("HiveDialect.ERROR_0001")); //$NON-NLS-1$
    }
  }

  @Test
  public void innerJoin_single_equality() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B ON ( A.b = B.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_single_inequality() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b > B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B\nWHERE\n          ( A.b > B.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_double_unordered() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c = C.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B ON ( A.b = B.id )\n          JOIN C ON ( B.c = C.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_double_inequalities() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b > B.id", "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c > C.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B\n          JOIN C\nWHERE\n          ( A.b > B.id )\n      AND ( B.c > C.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_double_ordered_equality() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", "2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c = C.id", "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          B\n          JOIN C ON ( B.c = C.id )\n          JOIN A ON ( A.b = B.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_double_ordered_mixed_equalities() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", "2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c > C.id", "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          B\n          JOIN C\n          JOIN A ON ( A.b = B.id )\nWHERE\n          ( B.c > C.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_triple_out_of_order() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("C", null, "D", null, JoinType.INNER_JOIN, "C.d = D.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c = C.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B ON ( A.b = B.id )\n          JOIN C ON ( B.c = C.id )\n          JOIN D ON ( C.d = D.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_triple_out_of_order_inequality() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("C", null, "D", null, JoinType.INNER_JOIN, "C.d = D.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c > C.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          A\n          JOIN B ON ( A.b = B.id )\n          JOIN C\n          JOIN D ON ( C.d = D.id )\nWHERE\n          ( B.c > C.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_triple_out_of_order_inequality_ordered() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("C", null, "D", null, JoinType.INNER_JOIN, "C.d = D.id", "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    query.addJoin("B", null, "C", null, JoinType.INNER_JOIN, "B.c > C.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          C\n          JOIN D ON ( C.d = D.id )\n          JOIN B\n          JOIN A ON ( A.b = B.id )\nWHERE\n          ( B.c > C.id )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void innerJoin_orphaned_join() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("C", null, "D", null, JoinType.INNER_JOIN, "C.d = D.id", "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    SQLDialectInterface dialect = new HiveDialect();
    try {
      dialect.generateSelectStatement(query);
    } catch (Exception ex) {
      assertTrue("Expected exception [HiveDialect.ERROR_0002] but received: " + ex.getMessage(), //$NON-NLS-1$
          ex.getMessage().contains("HiveDialect.ERROR_0002")); //$NON-NLS-1$
    }
  }

  @Test
  public void innerJoin_orphaned_join2() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("C", null, "D", null, JoinType.INNER_JOIN, "C.d = D.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("E", null, "F", null, JoinType.INNER_JOIN, "E.f = F.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    SQLDialectInterface dialect = new HiveDialect();
    try {
      dialect.generateSelectStatement(query);
    } catch (Exception ex) {
      assertTrue("Expected exception [HiveDialect.ERROR_0002] but received: " + ex.getMessage(), //$NON-NLS-1$
          ex.getMessage().contains("HiveDialect.ERROR_0002")); //$NON-NLS-1$
    }
  }

  @Test
  public void lookupViaDatabaseMetaUtil() {
    // Verify the Database Product Name from the Hive JDBC Driver resolves to the correct database interface
    DatabaseInterface di = DatabaseMetaUtil.getDatabaseInterface("Hive"); //$NON-NLS-1$
    assertNotNull(di);
    assertTrue(di instanceof HiveDatabaseMeta);
  }

  @Test
  public void repeatedlyJoinedTables() {
    SQLQueryModel query = new SQLQueryModel();
    query.addJoin("A", null, "B", null, JoinType.INNER_JOIN, "A.b = B.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    query.addJoin("B", null, "A", null, JoinType.INNER_JOIN, "B.a = A.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    SQLDialectInterface dialect = new HiveDialect();
    try {
      dialect.generateSelectStatement(query);
    } catch (Exception ex) {
      assertTrue("Expected exception [HiveDialect.ERROR_0003] but received: " + ex.getMessage(), //$NON-NLS-1$
          ex.getMessage().contains("HiveDialect.ERROR_0003")); //$NON-NLS-1$
    }
  }

  @Test
  public void removeAliases_orderBy() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("a.a_column", "alias"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable("A", "a"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addOrderBy("a.a_column", "alias", OrderType.ASCENDING); //$NON-NLS-1$ //$NON-NLS-2$

    String expected = "SELECT DISTINCT \n          a.a_column\nFROM \n          A a\nORDER BY \n          a_column ASC\n"; //$NON-NLS-1$

    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);

    assertEquals(expected, result);
  }

  @Test
  public void havingClause() {
    SQLQueryModel query = new SQLQueryModel();
    query.addTable("TABLE", null); //$NON-NLS-1$
    query.addHavingFormula("a", null); //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    try {
      dialect.generateSelectStatement(query);
    } catch (Exception ex) {
      assertTrue("Expected exception [HiveDialect.ERROR_0004] but received: " + ex.getMessage(), //$NON-NLS-1$
          ex.getMessage().contains("HiveDialect.ERROR_0004")); //$NON-NLS-1$
    }
  }

  @Test
  public void stripTableAliasesFromFormula() {
    String formula = "a.id"; //$NON-NLS-1$
    String expected = "id"; //$NON-NLS-1$
    String result = new HiveDialect().stripTableAliasesFromFormula(formula);
    assertEquals(expected, result);
  }

  @Test
  public void stripTableAliasesFromFormula_no_aliases() {
    String formula = "id"; //$NON-NLS-1$
    String result = new HiveDialect().stripTableAliasesFromFormula(formula);
    assertEquals(formula, result);
  }

  @Test
  public void stripTableAliasesFromFormula_aggregate() {
    String formula = "count(a.id)"; //$NON-NLS-1$
    String expected = "count(id)"; //$NON-NLS-1$
    String result = new HiveDialect().stripTableAliasesFromFormula(formula);
    assertEquals(expected, result);
  }

  @Test
  public void stripTableAliasesFromFormula_with_spaces() {
    String formula = "count( a .id)"; //$NON-NLS-1$
    String expected = "count( id)"; //$NON-NLS-1$
    String result = new HiveDialect().stripTableAliasesFromFormula(formula);
    assertEquals(expected, result);
  }

  @Test
  public void where_simple() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("name <> 'test'", null); //$NON-NLS-1$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          TABLE t\nWHERE \n        (\n          (\n             name <> 'test'\n          )\n        )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void where_multiple() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("name <> 'test'", null); //$NON-NLS-1$
    query.addWhereFormula("age = 10", null); //$NON-NLS-1$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          TABLE t\nWHERE \n        (\n          (\n             name <> 'test'\n          )\n      AND (\n             age = 10\n          )\n        )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }

  @Test
  public void where_with_join_inequality() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addJoin("TABLE2", "two", "TABLE", "t", JoinType.INNER_JOIN, "two.id > t.id", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    query.addWhereFormula("name <> 'test'", null); //$NON-NLS-1$
    String expected = "SELECT DISTINCT \n          id\nFROM \n          TABLE2 two\n          JOIN TABLE t\nWHERE\n          ( two.id > t.id )\n      AND \n        (\n          (\n             name <> 'test'\n          )\n        )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
  }
  
  @Test
  public void where_with_WHERE() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.WHERES_WALDO", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("name <> 'test'", null); //$NON-NLS-1$
    String expected = "SELECT DISTINCT \n          t.WHERES_WALDO\nFROM \n          TABLE t\nWHERE \n        (\n          (\n             name <> 'test'\n          )\n        )\n"; //$NON-NLS-1$
    SQLDialectInterface dialect = new HiveDialect();
    String result = dialect.generateSelectStatement(query);
    assertEquals(expected, result);
    
  }

  @Test
  public void generateStringConcat_single() {
    String[] vals = new String[] { "'string'" }; //$NON-NLS-1$
    String expected = "CONCAT('string')"; //$NON-NLS-1$
    HiveDialect dialect = new HiveDialect();
    String result = dialect.generateStringConcat(vals);
    assertEquals(expected, result);
  }

  @Test
  public void generateStringConcat_multiple() {
    String[] vals = new String[] { "'%'", "value", "'%'" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String expected = "CONCAT('%',value,'%')"; //$NON-NLS-1$
    HiveDialect dialect = new HiveDialect();
    String result = dialect.generateStringConcat(vals);
    assertEquals(expected, result);
  }
}