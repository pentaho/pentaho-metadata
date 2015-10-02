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
 * Copyright (c) 2008 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.dialect.JoinType;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

/**
 * Tests the Outer Join SQLQueryModel and sql query generation system.
 * 
 * @author Matt Casters (mcasters@pentaho.org)
 * 
 */
public class OuterJoinSQLQueryIT extends MetadataTestBase {

  /*****************************************************************************
   * Test Outer Join scenarios with 2 tables.
   *****************************************************************************/

  /**
   * Test a simple LEFT OUTER JOIN scenario with 2 tables
   */
  public void test2TablesLeftOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "A" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$
    query.addOrderBy( null, "t1_pk", OrderType.ASCENDING ); // $NON-NLS-1$

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " + // $NON-NLS-1$
        "FROM t1 LEFT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " + // $NON-NLS-1$
        "ORDER BY t1_pk ASC", // $NON-NLS-1$
        sql );
  }

  /**
   * Test a LEFT OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesLeftOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "sum(t1.pk)", "Total" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "A" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$ $NON-NLS-4$
    query.addOrderBy( null, "t1_pk", OrderType.ASCENDING ); // $NON-NLS-1$ $NON-NLS-2$
    query.addGroupBy( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addGroupBy( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.setDistinct( false );

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespaces( "SELECT t1.pk AS t1_pk ,t2.pk AS t2_pk ,sum(t1.pk) AS Total " + // $NON-NLS-1$
        "FROM t1 LEFT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " + // $NON-NLS-1$
        "GROUP BY t1_pk ,t2_pk " + // $NON-NLS-1$
        "ORDER BY t1_pk ASC", // $NON-NLS-1$
        sql );
  }

  /**
   * Test a simple RIGHT OUTER JOIN scenario with 2 tables
   */
  public void test2TablesRightOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.RIGHT_OUTER_JOIN, "t1.pk = t2.fk", "A" );// $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$ $NON-NLS-4$
    query.addOrderBy( null, "t1_pk", OrderType.DESCENDING ); // $NON-NLS-1$

    DatabaseMeta databaseMeta = MetadataTestBase.createOracleDatabaseMeta();
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " + // $NON-NLS-1$
        "FROM t1 RIGHT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " + // $NON-NLS-1$
        "ORDER BY t1_pk DESC", // $NON-NLS-1$
        sql );
  }

  /**
   * Test a RIGHT OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesRightOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "sum(t2.pk)", "Total" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.RIGHT_OUTER_JOIN, "t1.pk = t2.fk", "A" );// $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$ $NON-NLS-4$
    query.addOrderBy( null, "t1_pk", OrderType.ASCENDING ); // $NON-NLS-1$
    query.addGroupBy( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addGroupBy( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.setDistinct( false );

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespaces( "SELECT t1.pk AS t1_pk ,t2.pk AS t2_pk ,sum(t2.pk) AS Total " + // $NON-NLS-1$
        "FROM t1 RIGHT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " + // $NON-NLS-1$
        "GROUP BY t1_pk ,t2_pk " + // $NON-NLS-1$
        "ORDER BY t1_pk ASC", // $NON-NLS-1$
        sql );
  }

  /**
   * Test a simple FULL OUTER JOIN scenario with 2 tables
   */
  public void test2TablesFullOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.FULL_OUTER_JOIN, "t1.pk = t2.fk", null ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                              // $NON-NLS-3$

    DatabaseMeta databaseMeta = MetadataTestBase.createOracleDatabaseMeta();
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " + // $NON-NLS-1$
        "FROM t1 FULL OUTER JOIN t2 ON ( t1.pk = t2.fk )", // $NON-NLS-1$
        sql );
  }

  /**
   * Test a FULL OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesFullOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "sum(t1.pk)", "Total" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.FULL_OUTER_JOIN, "t1.pk = t2.fk", "A" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$ $NON-NLS-4$
    query.addOrderBy( null, "t2_pk", OrderType.DESCENDING ); // $NON-NLS-1$
    query.addGroupBy( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.setDistinct( false );

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespaces( "SELECT t2.pk AS t2_pk ,sum(t1.pk) AS Total " + // $NON-NLS-1$
        "FROM t1 FULL OUTER JOIN t2 ON ( t1.pk = t2.fk ) " + // $NON-NLS-1$
        "GROUP BY t2_pk " + // $NON-NLS-1$
        "ORDER BY t2_pk DESC", // $NON-NLS-1$
        sql );
  }

  /*****************************************************************************
   * Test Outer Join scenarios with 3 tables.
   *****************************************************************************/

  /**
   * Test an OUTER JOIN scenario with 3 tables.<br>
   * - T1-T2 : a left outer join (to be executed second)<br>
   * - T2-T3 : an inner join (to be executed first)<br>
   * <br>
   */
  public void test3TablesLeftOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t3.pk", "t3_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addTable( "t3", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "B" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$ $NON-NLS-4$
    query.addJoin( "t2", null, "t3", null, JoinType.INNER_JOIN, "t2.pk = t3.fk", "A" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                        // $NON-NLS-3$ $NON-NLS-4$
    query.addOrderBy( null, "t1_pk", OrderType.ASCENDING ); // $NON-NLS-1$

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT T1.PK AS T1_PK ,T2.PK AS T2_PK ,T3.PK AS T3_PK " + // $NON-NLS-1$
        "FROM T1 LEFT OUTER JOIN ( T2 JOIN T3 ON ( T2.PK = T3.FK ) ) ON ( T1.PK = T2.FK ) " + // $NON-NLS-1$
        "ORDER BY T1_PK ASC", // $NON-NLS-1$
        sql );
  }

  /**
   * Test an OUTER JOIN scenario with 3 tables.<br>
   * - T1-T2 : a left outer join (to be executed second)<br>
   * - T2-T3 : an inner join (to be executed first)<br>
   * <br>
   * It's the same test as test3TablesLeftOuterJoin() but <b>we didn't specify an order</b>.<br>
   * As such, <u>we expect the inner join to be executed first</u><br>
   */
  public void test3TablesLeftOuterJoinNoOrder() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t3.pk", "t3_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addTable( "t3", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", null ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                              // $NON-NLS-3$
    query.addJoin( "t2", null, "t3", null, JoinType.INNER_JOIN, "t2.pk = t3.fk", null ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                         // $NON-NLS-3$
    query.addOrderBy( null, "t3_pk", OrderType.DESCENDING ); // $NON-NLS-1$

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT T1.PK AS T1_PK,T2.PK AS T2_PK,T3.PK AS T3_PK FROM "
        + "T3 JOIN(T1 LEFT OUTER JOIN T2 ON(T1.PK = T2.FK))ON(T2.PK = T3.FK)ORDER BY T3_PK DESC", sql );
  }

  /**
   * Test an OUTER JOIN scenario with 4 tables.<br>
   * - T1-T2 : a left outer join (to be executed second)<br>
   * - T2-T3 : an inner join (to be executed first)<br>
   * - T2-T4 : an cross join (to be executed last) (==full outer join)<br>
   * <br>
   */
  public void test4TablesWithOuterJoins() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.pk", "t1_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t2.pk", "t2_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t3.pk", "t3_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addSelection( "t4.pk", "t4_pk" ); // $NON-NLS-1$ $NON-NLS-2$
    query.addTable( "t1", null ); // $NON-NLS-1$
    query.addTable( "t2", null ); // $NON-NLS-1$
    query.addTable( "t3", null ); // $NON-NLS-1$
    query.addTable( "t4", null ); // $NON-NLS-1$
    query.addJoin( "t1", null, "t2", null, JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "A" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$
    query.addJoin( "t2", null, "t3", null, JoinType.INNER_JOIN, "t2.pk = t3.fk", "B" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                        // $NON-NLS-3$
    query.addJoin( "t2", null, "t4", null, JoinType.FULL_OUTER_JOIN, "t2.pk = t4.fk", "C" ); // $NON-NLS-1$ $NON-NLS-2$
                                                                                             // $NON-NLS-3$
    query.addOrderBy( null, "t2_pk", OrderType.ASCENDING ); // $NON-NLS-1$

    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( createOracleDatabaseMeta() );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase(
        "SELECT DISTINCT T1.PK AS T1_PK ,T2.PK AS T2_PK ,T3.PK AS T3_PK ,T4.PK AS T4_PK " + // $NON-NLS-1$
            "FROM T4 FULL OUTER JOIN ( " + // $NON-NLS-1$
            "	T3 JOIN ( " + // $NON-NLS-1$
            "		T1 LEFT OUTER JOIN T2 ON ( T1.PK = T2.FK ) " + // $NON-NLS-1$
            "		) ON ( T2.PK = T3.FK ) " + // $NON-NLS-1$
            "	) ON ( T2.PK = T4.FK ) " + // $NON-NLS-1$
            "ORDER BY T2_PK ASC", // $NON-NLS-1$
        sql );
  }

}
