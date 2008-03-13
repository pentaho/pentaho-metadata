/*
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
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
public class OuterJoinSQLQueryTest extends MetadataTestBase {
  
  /*****************************************************************************
  
   Test Outer Join scenarios with 2 tables.
   
   *****************************************************************************/
	
	
  /**
   * Test a simple LEFT OUTER JOIN scenario with 2 tables
   */
  public void test2TablesLeftOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "A");
    query.addOrderBy(null, "t1_pk", OrderType.ASCENDING);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase(
    	"SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " +
    	"FROM t1 LEFT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " +
    	"ORDER BY t1_pk ASC",
    	sql);
  }  

  /**
   * Test a LEFT OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesLeftOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addSelection("sum(t1.pk)", "Total");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "A");
    query.addOrderBy(null, "t1_pk", OrderType.ASCENDING);
    query.addGroupBy("t1.pk", "t1_pk");
    query.addGroupBy("t2.pk", "t2_pk");
    query.setDistinct(false);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces(
    	"SELECT t1.pk AS t1_pk ,t2.pk AS t2_pk ,sum(t1.pk) AS Total " +
    	"FROM t1 LEFT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " +
    	"GROUP BY t1_pk ,t2_pk " +
    	"ORDER BY t1_pk ASC",
    	sql);
  }  

  /**
   * Test a simple RIGHT OUTER JOIN scenario with 2 tables
   */
  public void test2TablesRightOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.RIGHT_OUTER_JOIN, "t1.pk = t2.fk", "A");
    query.addOrderBy(null, "t1_pk", OrderType.DESCENDING);
    
    DatabaseMeta databaseMeta = MetadataTestBase.createOracleDatabaseMeta();
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase(
        	"SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " +
        	"FROM t1 RIGHT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " +
        	"ORDER BY t1_pk DESC",
        	sql);
  }  
  
  /**
   * Test a RIGHT OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesRightOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addSelection("sum(t2.pk)", "Total");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.RIGHT_OUTER_JOIN, "t1.pk = t2.fk", "A");
    query.addOrderBy(null, "t1_pk", OrderType.ASCENDING);
    query.addGroupBy("t1.pk", "t1_pk");
    query.addGroupBy("t2.pk", "t2_pk");
    query.setDistinct(false);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces(
    	"SELECT t1.pk AS t1_pk ,t2.pk AS t2_pk ,sum(t2.pk) AS Total " +
    	"FROM t1 RIGHT OUTER JOIN t2 ON ( t1.pk = t2.fk ) " +
    	"GROUP BY t1_pk ,t2_pk " +
    	"ORDER BY t1_pk ASC", 
    	sql);
  }  
  /**
   * Test a simple FULL OUTER JOIN scenario with 2 tables
   */
  public void test2TablesFullOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.FULL_OUTER_JOIN, "t1.pk = t2.fk", null);
    
    DatabaseMeta databaseMeta = MetadataTestBase.createOracleDatabaseMeta();
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase( 
        	"SELECT DISTINCT t1.pk AS t1_pk ,t2.pk AS t2_pk " +
        	"FROM t1 FULL OUTER JOIN t2 ON ( t1.pk = t2.fk )",
        	sql);
  }  

  /**
   * Test a FULL OUTER JOIN scenario with 2 tables and a grouping
   */
  public void test2TablesFullOuterJoinGrouping() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t2.pk", "t2_pk");
    query.addSelection("sum(t1.pk)", "Total");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addJoin("t1", "t2", JoinType.FULL_OUTER_JOIN, "t1.pk = t2.fk", "A");
    query.addOrderBy(null, "t2_pk", OrderType.DESCENDING);
    query.addGroupBy("t2.pk", "t2_pk");
    query.setDistinct(false);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespaces( 
    	"SELECT t2.pk AS t2_pk ,sum(t1.pk) AS Total " +
    	"FROM t1 FULL OUTER JOIN t2 ON ( t1.pk = t2.fk ) " +
    	"GROUP BY t2_pk " +
    	"ORDER BY t2_pk DESC", 
    	sql);
  }  
  
  /*****************************************************************************
  
  Test Outer Join scenarios with 3 tables.
  
  *****************************************************************************/

  /**
   * Test an OUTER JOIN scenario with 3 tables.<br>
   * - T1-T2 : a left outer join (to be executed second)<br> 
   * - T2-T3 : an inner join (to be executed first)<br>
   * <br>
   */
  public void test3TablesLeftOuterJoin() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addSelection("t3.pk", "t3_pk");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addTable("t3", null);
    query.addJoin("t1", "t2", JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", "B");
    query.addJoin("t2", "t3", JoinType.INNER_JOIN, "t2.pk = t3.fk", "A");
    query.addOrderBy(null, "t1_pk", OrderType.ASCENDING);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase(
    	"SELECT DISTINCT T1.PK AS T1_PK ,T2.PK AS T2_PK ,T3.PK AS T3_PK " +
    	"FROM T1 LEFT OUTER JOIN ( T2 JOIN T3 ON ( T2.PK = T3.FK ) ) ON ( T1.PK = T2.FK ) " +
    	"ORDER BY T1_PK ASC",
    	sql);
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
    query.addSelection("t1.pk", "t1_pk");
    query.addSelection("t2.pk", "t2_pk");
    query.addSelection("t3.pk", "t3_pk");
    query.addTable("t1", null);
    query.addTable("t2", null);
    query.addTable("t3", null);
    query.addJoin("t1", "t2", JoinType.LEFT_OUTER_JOIN, "t1.pk = t2.fk", null);
    query.addJoin("t2", "t3", JoinType.INNER_JOIN, "t2.pk = t3.fk", null);
    query.addOrderBy(null, "t3_pk", OrderType.DESCENDING);
    
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect(createOracleDatabaseMeta());
    
    String sql = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase(
    	"SELECT DISTINCT T1.PK AS T1_PK ,T2.PK AS T2_PK ,T3.PK AS T3_PK " +
    	"FROM T1 LEFT OUTER JOIN ( T2 JOIN T3 ON ( T2.PK = T3.FK ) ) ON ( T1.PK = T2.FK ) " +
    	"ORDER BY T3_PK DESC",
    	sql);
  }  
  
}
