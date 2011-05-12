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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.model.*;
import org.pentaho.metadata.model.concept.types.*;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.Path;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.*;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.metadata.query.model.util.QueryModelMetaData;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.dialect.SQLQueryModel;

import java.util.*;

@SuppressWarnings("nls")
public class SqlGeneratorTest {
  
  @BeforeClass
  public static void initKettle() throws KettleException {
    KettleEnvironment.init(false);
  }
  
  @Test
  public void testGetShortestPathBetween() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1");  //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    Assert.assertEquals(path.size(), 1);
    Assert.assertEquals(path.getRelationship(0), rl1);
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    Assert.assertEquals(path.size(), 2);
    Assert.assertEquals(path.getRelationship(0), rl1);
    Assert.assertEquals(path.getRelationship(1), rl2);
  }
  
  public static class TestSqlGenerator extends SqlGenerator {
    
    @Override
    public Path getShortestPathBetween(LogicalModel model, List<LogicalTable> tables) {
      return super.getShortestPathBetween(model, tables);
    }
    
    @Override
    public String getJoin(LogicalModel LogicalModel, LogicalRelationship relation, Map<LogicalTable, String> tableAliases, Map<String, Object> parameters, boolean genAsPreparedStatement, DatabaseMeta databaseMeta, String locale) throws PentahoMetadataException {
      return super.getJoin(LogicalModel, relation, tableAliases, parameters, genAsPreparedStatement, databaseMeta, locale);
    }
    
    @Override
    public <T> List<List<T>> getSubsetsOfSize(int size, List<T> list) {
      return super.getSubsetsOfSize(size, list);
    }
    
    @Override
    public String generateUniqueAlias(String alias, int maxLength, Collection<String> existingAliases) {
      return super.generateUniqueAlias(alias, maxLength, existingAliases);
    }
  }
  
  @Test
  public void testThreeSibJoin() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1");  //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    final LogicalTable bt6 = new LogicalTable();
    bt6.setId("bt6"); //$NON-NLS-1$
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setToTable(bt3);
    rl3.setFromTable(bt5);

    final LogicalRelationship rl4 = new LogicalRelationship();
    rl4.setToTable(bt5);
    rl4.setFromTable(bt6);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt5);
    model.getLogicalTables().add(bt6);
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add(bt1); tbls.add(bt6);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    // this should return a path, but it is returning null instead
    
    Assert.assertEquals(path.size(), 4);
    Assert.assertEquals(path.getRelationship(0), rl3);
    Assert.assertEquals(path.getRelationship(1), rl4); // may be rl3 
    Assert.assertEquals(path.getRelationship(2), rl2); // may be rl5
    Assert.assertEquals(path.getRelationship(3), rl1);
  }
  
  @Test
  public void testCircularJoin() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1");  //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    final LogicalTable bt6 = new LogicalTable();
    bt6.setId("bt6"); //$NON-NLS-1$
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setToTable(bt2);
    rl3.setFromTable(bt4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    rl4.setToTable(bt3);
    rl4.setFromTable(bt5);

    final LogicalRelationship rl5 = new LogicalRelationship();
    rl5.setToTable(bt4);
    rl5.setFromTable(bt5);

    final LogicalRelationship rl6 = new LogicalRelationship();
    rl6.setToTable(bt5);
    rl6.setFromTable(bt6);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    model.getLogicalTables().add(bt6);
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    model.getLogicalRelationships().add(rl5);
    model.getLogicalRelationships().add(rl6);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add(bt1); tbls.add(bt6);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    // As of 9/23, it looks like this:
    // [bt5-bt4], [bt6-bt5], [bt4-bt2], [bt1-bt2]
    //
    // Real problem is that there's no hard-and-fast rule
    // about what should happen in a graph cycle. when there
    // is nothing compelling (like a higher-score item in the
    // graph. When you look at the "found paths" when there
    // is a cycle, a bug is evident - there is no consideration
    // for the path 1->2, 2->3, 3->5, 5->6
    
    Assert.assertEquals(path.size(), 4);
    Assert.assertEquals(path.getRelationship(0), rl5);
    Assert.assertEquals(path.getRelationship(1), rl6);  
    Assert.assertEquals(path.getRelationship(2), rl3); 
    Assert.assertEquals(path.getRelationship(3), rl1);
  }

  @Test
  public void testGetShortestPathBetween2() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);
    
    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setToTable(bt4);
    rl3.setFromTable(bt5);
    
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    Assert.assertEquals(path.size(), 1);
    Assert.assertEquals(path.getRelationship(0), rl1);
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    Assert.assertEquals(path.size(), 2);
    Assert.assertEquals(path.getRelationship(0), rl1);
    Assert.assertEquals(path.getRelationship(1), rl2);
  }

  @Test
  public void testGetShortestPathBetween3() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);
    
    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setToTable(bt1);
    rl3.setFromTable(bt3);
    
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    Assert.assertEquals(path.size(), 1);
    Assert.assertEquals(path.getRelationship(0), rl1);
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    Assert.assertEquals(path.size(), 1);
    Assert.assertEquals(path.getRelationship(0), rl3);
  }

  @Test
  public void testComplexJoinMQL() throws Exception {
    
    String locale = "en_US"; //$NON-NLS-1$
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    rl1.setComplexJoin("[bt1.bc1] = [bt2.bc2]"); //$NON-NLS-1$
    rl1.setComplex(true);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);
    
    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setToTable(bt1);
    rl3.setFromTable(bt3);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    
    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    String joinSQL = sqlGenerator.getJoin(model, rl1, null, null, false, databaseMeta, locale);

    TestHelper.assertEqualsIgnoreWhitespaces(joinSQL, " bt1.pc1  =  bt2.pc2 ");//$NON-NLS-1$
  } 
  
  @Test
  public void testGroupBySQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      bc1.setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, AggregationType.SUM);
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      query.getSelections().add(new Selection(null, bce2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1] > 25")); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n"                          //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n"      //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n"                          //$NON-NLS-1$
          + "          pt1 bt1\n"              //$NON-NLS-1$
          + "         ,pt2 bt2\n"              //$NON-NLS-1$
          + "WHERE \n"                         //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n"                    //$NON-NLS-1$
          + "GROUP BY \n"                      //$NON-NLS-1$
          + "          bt2.pc2\n"              //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
          + "HAVING \n"                        //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          mquery.getQuery()
          );
      Map map = mquery.getMap();
      Assert.assertNotNull(map);
      Assert.assertEquals(map.size(), 3);
      Assert.assertEquals(map.get("COL0"), "bc1"); //$NON-NLS-1$ //$NON-NLS-2$
      Assert.assertEquals(map.get("COL1"), "bc2");  //$NON-NLS-1$ //$NON-NLS-2$
      Assert.assertEquals(map.get("COL2"), "bce2"); //$NON-NLS-1$ //$NON-NLS-2$
      
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n"                        //$NON-NLS-1$
        + "          SUM(bt1.pc1) AS bc1\n"  //$NON-NLS-1$
        + "         ,bt2.pc2 AS bc2\n"       //$NON-NLS-1$
        + "         , bt2.pc2  * 2 AS bce2\n"//$NON-NLS-1$        
        + "FROM \n"                          //$NON-NLS-1$
        + "          pt1 bt1\n"              //$NON-NLS-1$
        + "         ,pt2 bt2\n"              //$NON-NLS-1$
        + "WHERE \n"                         //$NON-NLS-1$
        + "          (\n"                    //$NON-NLS-1$
        + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
        + "          )\n"                    //$NON-NLS-1$
        + "GROUP BY \n"                      //$NON-NLS-1$
        + "          bt2.pc2\n"              //$NON-NLS-1$
        + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
        + "HAVING \n"                        //$NON-NLS-1$
        + "          (\n"                    //$NON-NLS-1$        
        + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
        + "          )\n",                   //$NON-NLS-1$
        mquery.getDisplayQuery()
      );

      MemoryMetaData mmd = new MemoryMetaData(
          new Object[][] {{"COL0", "COL1"}}, //$NON-NLS-1$  //$NON-NLS-2$
          null
          );
      
      QueryModelMetaData emd = (QueryModelMetaData)mquery.generateMetadata(mmd);
      
      Assert.assertEquals("pc1", emd.getAttribute(0, 0, SqlPhysicalColumn.TARGET_COLUMN).toString()); //$NON-NLS-1$  //$NON-NLS-2$
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testOrderByQuotedSQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      bc1.setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, AggregationType.SUM);
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields(true);
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      query.getSelections().add(new Selection(null, bce2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1] > 25")); //$NON-NLS-1$

      query.getOrders().add(new Order(new Selection(null, bc1, null), Type.ASC));
      
      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);
      TestHelper.printOutJava(mquery.getQuery());
      
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n" + 
          "          SUM(\"bt1\".\"pc1\") AS \"COL0\"\n" + 
          "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + 
          "         , \"bt2\".\"pc2\"  * 2 AS \"COL2\"\n" + 
          "FROM \n" + 
          "          \"pt1\" \"bt1\"\n" + 
          "         ,\"pt2\" \"bt2\"\n" + 
          "WHERE \n" + 
          "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + 
          "GROUP BY \n" + 
          "          \"bt2\".\"pc2\"\n" + 
          "         , \"bt2\".\"pc2\"  * 2\n" + 
          "HAVING \n" + 
          "          (\n" + 
          "              SUM(\"bt1\".\"pc1\")  > 25\n" + 
          "          )\n" + 
          "ORDER BY \n" + 
          "          \"COL0\"\n"
          ,
          mquery.getQuery()
          );

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  
  @Test
  public void testEscapeQuotes() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields(true);
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND, "CONTAINS([bt1.bc1];\"a'b\")")); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);
     
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          \"bt1\".\"pc1\" AS \"COL0\"\n" + 
          "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + 
          "FROM \n" + 
          "          \"pt1\" \"bt1\"\n" + 
          "         ,\"pt2\" \"bt2\"\n" + 
          "WHERE \n" + 
          "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              \"bt1\".\"pc1\"  LIKE '%' || 'a''b' || '%'\n" + 
          "          )\n" + 
          "        )\n"
          ,
          mquery.getQuery()
          );

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  
  @Test
  public void testFirstConstraintNOT() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields(true);
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] > 1")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] < 1")); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);

      TestHelper.printOutJava(mquery.getQuery());
      
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          \"bt1\".\"pc1\" AS \"COL0\"\n" + 
          "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + 
          "FROM \n" + 
          "          \"pt1\" \"bt1\"\n" + 
          "         ,\"pt2\" \"bt2\"\n" + 
          "WHERE \n" + 
          "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "      NOT (\n" + 
          "              \"bt1\".\"pc1\"  > 1\n" + 
          "          )\n" + 
          "      AND NOT (\n" + 
          "              \"bt1\".\"pc1\"  < 1\n" + 
          "          )\n" + 
          "        )\n"
          ,
          mquery.getQuery()
          );

      query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.OR_NOT, "[bt1.bc1] > 1")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.OR_NOT, "[bt1.bc1] < 1")); //$NON-NLS-1$

      mquery = generator.generateSql(query, "en_US", null, databaseMeta);

      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          \"bt1\".\"pc1\" AS \"COL0\"\n" + 
          "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + 
          "FROM \n" + 
          "          \"pt1\" \"bt1\"\n" + 
          "         ,\"pt2\" \"bt2\"\n" + 
          "WHERE \n" + 
          "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "      NOT (\n" + 
          "              \"bt1\".\"pc1\"  > 1\n" + 
          "          )\n" + 
          "      OR NOT (\n" + 
          "              \"bt1\".\"pc1\"  < 1\n" + 
          "          )\n" + 
          "        )\n"
          ,
          mquery.getQuery()
          );

      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testParameterSqlGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query(null, model);

      query.getParameters().add(new Parameter("test1", DataType.BOOLEAN, true));
      query.getParameters().add(new Parameter("test2", DataType.NUMERIC, 1.2));
      query.getParameters().add(new Parameter("test3", DataType.STRING, "value"));
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));

      query.getConstraints().add(new Constraint(CombinationType.AND, "[param:test1]")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1] > [param:test2]")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "[param:test3] = [bt2.bc2]")); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta, null, false);
      TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "             1\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt1.pc1  > 1.2\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "             'value' =  bt2.pc2 \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
          );

      Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("test1", false);
      parameters.put("test2", 2.1);
      parameters.put("test3", "eulav");
      
      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, false);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "             0\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt1.pc1  > 2.1\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "             'eulav' =  bt2.pc2 \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
          );

      Assert.assertNull(mquery.getParamList());
      
      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, true);
      TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "             ?\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt1.pc1  > ?\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "             ? =  bt2.pc2 \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
        );
      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(3, mquery.getParamList().size());
      Assert.assertEquals("test1", mquery.getParamList().get(0));
      Assert.assertEquals("test2", mquery.getParamList().get(1));
      Assert.assertEquals("test3", mquery.getParamList().get(2));

      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testMultiParameterSqlGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query(null, model);

      query.getParameters().add(new Parameter("test1", DataType.NUMERIC, new Double[] {1.2, 1.3}));
      query.getParameters().add(new Parameter("test2", DataType.STRING, new String[] {"value", "value2"}));
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));

      query.getConstraints().add(new Constraint(CombinationType.AND, "EQUALS([bt1.bc1];[param:test1])")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "EQUALS([bt2.bc2];[param:test2])")); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta, null, false);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt1.pc1  IN ( 1.2 , 1.3 ) \n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  IN ( 'value' , 'value2' ) \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
          );

      Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("test1", "value1");
      parameters.put("test2", 2.1);
      
      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, false);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt1.pc1  = 'value1'\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  = 2.1\n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
          );

      Assert.assertNull(mquery.getParamList());
      
      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, true);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt1.pc1  = ?\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  = ?\n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
        );
      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(2, mquery.getParamList().size());
      Assert.assertEquals("test1", mquery.getParamList().get(0));
      Assert.assertEquals("test2", mquery.getParamList().get(1));
      
      parameters = new HashMap<String, Object>();
      parameters.put("test1", new String[] {"value1", "value2"});
      parameters.put("test2", new Double[] {2.1, 2.2, 2.3});
       
      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, true);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt1.pc1  IN ( ?, ? ) \n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  IN ( ?, ?, ? ) \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
        );
      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(2, mquery.getParamList().size());
      Assert.assertEquals("test1", mquery.getParamList().get(0));
      Assert.assertEquals("test2", mquery.getParamList().get(1));


      // test that a single-value array translates into an '=' operation, not an IN
      parameters = new HashMap<String, Object>();
      parameters.put("test1", new String[] {"value1"});
      parameters.put("test2", new Double[] {2.1, 2.2, 2.3});

      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, true);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" +
          "          bt1.pc1 AS COL0\n" +
          "         ,bt2.pc2 AS COL1\n" +
          "FROM \n" +
          "          pt1 bt1\n" +
          "         ,pt2 bt2\n" +
          "WHERE \n" +
          "          ( bt1.pc1 = bt2.pc2 )\n" +
          "      AND \n" +
          "        (\n" +
          "          (\n" +
          "              bt1.pc1 = ? \n" +
          "          )\n" +
          "      AND (\n" +
          "              bt2.pc2 IN ( ?, ?, ? ) \n" +
          "          )\n" +
          "        )\n",
          mquery.getQuery()
        );
      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(2, mquery.getParamList().size());
      Assert.assertEquals("test1", mquery.getParamList().get(0));
      Assert.assertEquals("test2", mquery.getParamList().get(1));

      // Test multiple params in a single constraint
      query.getConstraints().add(new Constraint(CombinationType.AND, "AND(EQUALS([bt1.bc1];[param:test1]);EQUALS([bt2.bc2];[param:test2]))")); //$NON-NLS-1$

      mquery = generator.generateSql(query, "en_US", null, databaseMeta, parameters, true);
      TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" +
          "          bt1.pc1 AS COL0\n" +
          "         ,bt2.pc2 AS COL1\n" +
          "FROM \n" +
          "          pt1 bt1\n" +
          "         ,pt2 bt2\n" +
          "WHERE \n" +
          "          ( bt1.pc1 = bt2.pc2 )\n" +
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt1.pc1  = ?\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  IN ( ?, ?, ? ) \n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt1.pc1  = ? AND  bt2.pc2  IN ( ?, ?, ? ) \n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
        );
      
      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(4, mquery.getParamList().size());
      Assert.assertEquals("test1", mquery.getParamList().get(0));
      Assert.assertEquals("test2", mquery.getParamList().get(1));
      Assert.assertEquals("test1", mquery.getParamList().get(2));
      Assert.assertEquals("test2", mquery.getParamList().get(3));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  
  @Test
  public void testMultiParameterSqlGeneration_IN_operator() throws Exception {
    
    LogicalModel model = TestHelper.buildDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1");
    LogicalColumn bc2 = model.findLogicalColumn("bc2");
    LogicalColumn bc3 = model.findLogicalColumn("bc3");
    LogicalColumn bce2 = model.findLogicalColumn("bce2");
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query query = new Query(null, model);

    query.getParameters().add(new Parameter("test1", DataType.NUMERIC, new Double[] {1.2, 1.3}));
    query.getParameters().add(new Parameter("test2", DataType.STRING, new String[] {"value", "value2"})); //$NON-NLS-1$ //$NON-NLS-2$
    query.getParameters().add(new Parameter("test3", DataType.STRING, "single")); //$NON-NLS-1$
    
    query.getSelections().add(new Selection(null, bc1, null));
    query.getSelections().add(new Selection(null, bc2, null));
    query.getSelections().add(new Selection(null, bc3, null));

    query.getConstraints().add(new Constraint(CombinationType.AND, "IN([bt1.bc1];[param:test1])")); //$NON-NLS-1$
    query.getConstraints().add(new Constraint(CombinationType.AND, "IN([bt2.bc2];[param:test2])")); //$NON-NLS-1$
    query.getConstraints().add(new Constraint(CombinationType.AND, "IN([bt3.bc3];[param:test3])")); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    
    MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta, null, false);
    TestHelper.assertEqualsIgnoreWhitespaces(
      "SELECT DISTINCT \n" + 
      "          bt1.pc1 AS COL0\n" + 
      "         ,bt2.pc2 AS COL1\n" + 
      "         ,bt3.pc3 AS COL2\n" + 
      "FROM \n" + 
      "          pt1 bt1\n" + 
      "         ,pt2 bt2\n" +
      "         ,pt3 bt3\n" + 
      "WHERE \n" + 
      "          ( bt1.pc1 = bt2.pc2 )\n" +
      "      AND ( bt2.pc2 = bt3.pc3 )\n" + 
      "      AND \n" + 
      "        (\n" + 
      "          (\n" + 
      "              bt1.pc1  IN ( 1.2 , 1.3 ) \n" + 
      "          )\n" + 
      "      AND (\n" + 
      "              bt2.pc2  IN ( 'value' , 'value2' ) \n" + 
      "          )\n" + 
      "      AND (\n" + 
      "              bt3.pc3  IN ( 'single' ) \n" + 
      "          )\n" + 
      "        )\n",      
      mquery.getQuery()
      );
  }

  
  @Test
  public void testAggListSQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      bc1.setAggregationType(AggregationType.SUM);
      List<AggregationType> aggregationList = new ArrayList<AggregationType>();
      aggregationList.add(AggregationType.SUM);
      aggregationList.add(AggregationType.COUNT);
      aggregationList.add(AggregationType.NONE);
      bc1.setAggregationList(aggregationList);
      
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query myTest = new Query(null, model);  //$NON-NLS-1$
      myTest.getSelections().add(new Selection(null, bc1, null));
      myTest.getSelections().add(new Selection(null, bc2, null));
      myTest.getSelections().add(new Selection(null, bce2, null));
      myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1] > 25")); //$NON-NLS-1$

      // databaseMeta, "en_US"
      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n"                          //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n"      //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n"                          //$NON-NLS-1$
          + "          pt1 bt1\n"              //$NON-NLS-1$
          + "         ,pt2 bt2\n"              //$NON-NLS-1$
          + "WHERE \n"                         //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n"                    //$NON-NLS-1$
          + "GROUP BY \n"                      //$NON-NLS-1$
          + "          bt2.pc2\n"              //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
          + "HAVING \n"                        //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          query.getQuery()
          );
      Map map = query.getMap();
      Assert.assertNotNull(map);
      Assert.assertEquals(map.size(), 3);
      Assert.assertEquals(map.get("COL0"), "bc1"); //$NON-NLS-1$ //$NON-NLS-2$
      Assert.assertEquals(map.get("COL1"), "bc2");  //$NON-NLS-1$ //$NON-NLS-2$
      Assert.assertEquals(map.get("COL2"), "bce2"); //$NON-NLS-1$ //$NON-NLS-2$
      
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n"                        //$NON-NLS-1$
        + "          SUM(bt1.pc1) AS bc1\n"  //$NON-NLS-1$
        + "         ,bt2.pc2 AS bc2\n"       //$NON-NLS-1$
        + "         , bt2.pc2  * 2 AS bce2\n"//$NON-NLS-1$        
        + "FROM \n"                          //$NON-NLS-1$
        + "          pt1 bt1\n"              //$NON-NLS-1$
        + "         ,pt2 bt2\n"              //$NON-NLS-1$
        + "WHERE \n"                         //$NON-NLS-1$
        + "          (\n"                    //$NON-NLS-1$
        + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
        + "          )\n"                    //$NON-NLS-1$
        + "GROUP BY \n"                      //$NON-NLS-1$
        + "          bt2.pc2\n"              //$NON-NLS-1$
        + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
        + "HAVING \n"                        //$NON-NLS-1$
        + "          (\n"                    //$NON-NLS-1$        
        + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
        + "          )\n",                   //$NON-NLS-1$
        query.getDisplayQuery()
      );

      MemoryMetaData mmd = new MemoryMetaData(
          new Object[][] {{"COL0", "COL1"}}, //$NON-NLS-1$  //$NON-NLS-2$
          null
          );
      
      QueryModelMetaData emd = (QueryModelMetaData)query.generateMetadata(mmd);
      
     Assert.assertEquals("pc1", emd.getAttribute(0, 0, SqlPhysicalColumn.TARGET_COLUMN).toString()); //$NON-NLS-1$  //$NON-NLS-2$
      
      // select none aggregate
      
      Query myTest2 = new Query(null, model);  //$NON-NLS-1$
      myTest2.getSelections().add(new Selection(null, bc1, AggregationType.NONE));
      myTest2.getSelections().add(new Selection(null, bc2, null));
      myTest2.getSelections().add(new Selection(null, bce2, null));
      
      myTest2.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1.none] > 25")); //$NON-NLS-1$

      MappedQuery query2 = generator.generateSql(myTest2, "en_US", null, databaseMeta);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n"                 //$NON-NLS-1$
          + "          bt1.pc1 AS COL0\n"      //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n"      //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n"                          //$NON-NLS-1$
          + "          pt1 bt1\n"              //$NON-NLS-1$
          + "         ,pt2 bt2\n"              //$NON-NLS-1$
          + "WHERE \n"                         //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n"                    //$NON-NLS-1$
          + "AND       ((\n"                    //$NON-NLS-1$
          + "              bt1.pc1 > 25\n" //$NON-NLS-1$
          + "          ))\n", //$NON-NLS-1$
          query2.getQuery()
        );
      
      // select count aggregate
      Query myTest3 = new Query(null, model);  //$NON-NLS-1$
      myTest3.getSelections().add(new Selection(null, bc1, AggregationType.COUNT));
      myTest3.getSelections().add(new Selection(null, bc2, null));
      myTest3.getSelections().add(new Selection(null, bce2, null));
      
      myTest3.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1.count] > 25")); //$NON-NLS-1$

      MappedQuery query3 = generator.generateSql(myTest3, "en_US", null, databaseMeta);
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT \n"                        //$NON-NLS-1$
          + "          COUNT(bt1.pc1) AS COL0\n"  //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n"       //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$        
          + "FROM \n"                          //$NON-NLS-1$
          + "          pt1 bt1\n"              //$NON-NLS-1$
          + "         ,pt2 bt2\n"              //$NON-NLS-1$
          + "WHERE \n"                         //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n"                    //$NON-NLS-1$
          + "GROUP BY \n"                      //$NON-NLS-1$
          + "          bt2.pc2\n"              //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
          + "HAVING \n"                        //$NON-NLS-1$
          + "          (\n"                    //$NON-NLS-1$        
          + "              COUNT(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          query3.getQuery()
        );
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  
  @Test
  public void testGetShortestPathBetween4() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc4, null));
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n" 
        + "             bt1.pc1 = bt2.pc2\n"
        + "          )\n",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  public static String subsetsToString(List subsets) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < subsets.size(); i++) {
      List subset = (List)subsets.get(i);
      if (i != 0) sb.append(","); //$NON-NLS-1$
      sb.append("["); //$NON-NLS-1$
      for (int j = 0; j < subset.size(); j++) {
        if (j != 0) sb.append(","); //$NON-NLS-1$
        sb.append(subset.get(j));
      }
      sb.append("]"); //$NON-NLS-1$
    }
    return sb.toString();
  }
  
  @Test
  public void testSubsets() {
    TestSqlGenerator myTest = new TestSqlGenerator();
    ArrayList<String> testList = new ArrayList<String>();
    testList.add("A"); //$NON-NLS-1$
    testList.add("B"); //$NON-NLS-1$
    testList.add("C"); //$NON-NLS-1$
    testList.add("D"); //$NON-NLS-1$
    testList.add("E"); //$NON-NLS-1$
  
    List subsets = myTest.getSubsetsOfSize(0, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(0, subsets.size());
    
    subsets = myTest.getSubsetsOfSize(1, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(5, subsets.size());
    String subsetStr = subsetsToString(subsets);
    Assert.assertEquals("[A],[B],[C],[D],[E]", subsetStr);  //$NON-NLS-1$

    
    subsets = myTest.getSubsetsOfSize(2, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(10, subsets.size());
    subsetStr = subsetsToString(subsets);
    Assert.assertEquals("[A,B],[A,C],[A,D],[A,E],[B,C],[B,D],[B,E],[C,D],[C,E],[D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(3, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(10, subsets.size());
    subsetStr = subsetsToString(subsets);
    Assert.assertEquals("[A,B,C],[A,B,D],[A,B,E],[A,C,D],[A,C,E],[A,D,E],[B,C,D],[B,C,E],[B,D,E],[C,D,E]", subsetStr);  //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(4, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(5, subsets.size());
    subsetStr = subsetsToString(subsets);
    Assert.assertEquals("[A,B,C,D],[A,B,C,E],[A,B,D,E],[A,C,D,E],[B,C,D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(5, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(1, subsets.size());
    subsetStr = subsetsToString(subsets);
    Assert.assertEquals("[A,B,C,D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(6, testList);
    Assert.assertNotNull(subsets);
    Assert.assertEquals(0, subsets.size());
  }
  
  @Test
  public void testLogicalColumnFormulaUsingTwoBT() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalColumn bce2 = new LogicalColumn();
    bce2.setId("bce2"); //$NON-NLS-1$
    bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
    bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "[cat_01.bc2] * [cat_01.bc1]"); //$NON-NLS-1$
    bce2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bce2);
    mainCat.addLogicalColumn(bce2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bce2, null));
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt2.pc2 * bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
    
  }
  
  /**
   * Scenario 1: Two Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario1() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 1a: Two Tables are outer joined with a constraint
   */
  @Test
  public void testOuterJoinScenario1a() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[cat_01.bc2] > 1"));
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 1b: Two Tables are outer joined with an aggregate
   */
  @Test
  public void testOuterJoinScenario1b() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setAggregationType(AggregationType.SUM);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  
  /**
   * Scenario 1c: Two Tables are outer joined with an aggregate constraint
   */
  @Test
  public void testOuterJoinScenario1c() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setAggregationType(AggregationType.SUM);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[cat_01.bc2] > 1"));
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM(bt2.pc2) > 1 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 1d: Two Tables are outer joined both with constraints
   */
  @Test
  public void testOuterJoinScenario1d() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    model.getLogicalTables().add(bt1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    model.getLogicalTables().add(bt2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt1.bc1] > 1"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt2.bc2] > 1"));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) ) WHERE (( bt1.pc1 > 1 ))",  //$NON-NLS-1$
        query.getQuery()    
    );
  }
  
  /**
   * Scenario 1d: Two Tables are outer joined both with constraints
   * This scenario uses nots, verifying NOT syntax
   */
  @Test
  public void testOuterJoinScenario1dNOT() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    model.getLogicalTables().add(bt1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    model.getLogicalTables().add(bt2);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] > 1"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] < 1"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt2.bc2] > 1"));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) ) WHERE ( NOT ( bt1.pc1 > 1 ) AND NOT (bt1.pc1 < 1) )",  //$NON-NLS-1$
        query.getQuery()    
    );
  }
  
  
  /**
   * Scenario 2: Two Joined Tables are outer joined to a single table
   */
  @Test
  public void testOuterJoinScenario2()throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._1_N);
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 ,bt3.pc3 AS COL2 FROM pt1 bt1 LEFT OUTER JOIN ( pt2 bt2 JOIN pt3 bt3 ON ( bt2.pc2 = bt3.pc3 ) ) ON ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 2a: Two Joined Tables are outer joined to two other tables
   */
  @Test
  public void testOuterJoinScenario2a()throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "t4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    mainCat.addLogicalColumn(bc4);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._1_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._0_N);
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setRelationshipType(RelationshipType._1_N);
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);
    
    model.getLogicalRelationships().add(rl3);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    myTest.getSelections().add(new Selection(null, bc4, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t1 bt1 JOIN ( t2 bt2 LEFT OUTER JOIN ( t3 bt3 JOIN t4 bt4 ON ( bt3.k = bt4.k ) ) ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 3: Three Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario3() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._0_N);
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t1 bt1 LEFT OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t3 bt3 ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 4: Two outer joins on a single table
   */
  @Test
  public void testOuterJoinScenario4() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._0_N);
    rl2.setFromTable(bt1);
    rl2.setFromColumn(bc1);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  @Test
  public void testOuterJoinScenario5a() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._N_0);
    rl1.setJoinOrderKey("A");
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._N_0);
    rl2.setJoinOrderKey("B");
    rl2.setFromTable(bt1);
    rl2.setFromColumn(bc1);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t3 bt3 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t2 bt2 ON ( bt1.k = bt2.k ) ) ON ( bt1.k = bt3.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  @Test
  public void testOuterJoinScenario5b() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._N_0);
    rl1.setJoinOrderKey("B");
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._N_0);
    rl2.setJoinOrderKey("A");
    rl2.setFromTable(bt1);
    rl2.setFromColumn(bc1);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 6: 4 tables outer joined
   * 
   * NOTE: This does not work on MYSQL, because FULL OUTER JOIN is not supported.
   */
  @Test
  public void testOuterJoinScenario6() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "t1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "t3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "t4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    mainCat.addLogicalColumn(bc4);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType(RelationshipType._0_0);
    rl1.setJoinOrderKey("A");
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    model.getLogicalRelationships().add(rl1);    

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType(RelationshipType._0_0);
    rl2.setJoinOrderKey("B");
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);
    
    model.getLogicalRelationships().add(rl2);
    
    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setRelationshipType(RelationshipType._0_N);
    rl3.setJoinOrderKey("A");
    rl3.setFromTable(bt2);
    rl3.setFromColumn(bc2);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);
    
    model.getLogicalRelationships().add(rl3);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));
    myTest.getSelections().add(new Selection(null, bc3, null));
    myTest.getSelections().add(new Selection(null, bc4, null));    
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t3 bt3 FULL OUTER JOIN ( t1 bt1 FULL OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t4 bt4 ON ( bt2.k = bt4.k ) ) ON ( bt1.k = bt2.k ) ) ON ( bt2.k = bt3.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  @Test
  public void testGenerateUniqueAlias() {
    List<String> existingAliases = new ArrayList<String>();
    existingAliases.add("test");
    TestSqlGenerator generator = new TestSqlGenerator();
    Assert.assertEquals("tes01", generator.generateUniqueAlias("test", 5, existingAliases));
    Assert.assertEquals("tes01", generator.generateUniqueAlias("testing", 5, existingAliases));
    Assert.assertEquals("test1", generator.generateUniqueAlias("test1", 5, existingAliases));
    
    existingAliases.add("tes01");
    Assert.assertEquals("tes02", generator.generateUniqueAlias("test", 5, existingAliases));
    Assert.assertEquals("tes02", generator.generateUniqueAlias("testing", 5, existingAliases));
    Assert.assertEquals("test1", generator.generateUniqueAlias("test1", 5, existingAliases));
    
  }
  
  @Test
  public void testAliasGeneration() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("metadata_business_table_very_long_name_1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("metadata_business_table_very_long_name_2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("metadata_business_table_very_long_name_3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("metadata_business_table_very_long_name_4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("metadata_business_table_very_long_name_5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5");
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId("bc6"); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "SUM([pc5]*2)");
    bc6.setAggregationType(AggregationType.SUM);
    
    
    //bc6.setAggregationList(list);
    bc6.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc6);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    rl1.setComplex(true);
    rl1.setComplexJoin("[metadata_business_table_very_long_name_1.bc1] = [metadata_business_table_very_long_name_2.bc2]");
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc4, null));

    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25")); //$NON-NLS-1$
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          metadata_business_table_very01.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,metadata_business_table_very04.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 metadata_business_table_very01\n" //$NON-NLS-1$
        + "         ,pt2 metadata_business_table_very02\n" //$NON-NLS-1$
        + "         ,pt3 metadata_business_table_very03\n" //$NON-NLS-1$
        + "         ,pt4 metadata_business_table_very04\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             metadata_business_table_very02.pc2 = metadata_business_table_very03.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very03.pc3 = metadata_business_table_very04.pc4\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n" 
        + "             metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2\n"
        + "          )\n"
        + "      AND (\n"
        + "          (\n"
        + "             metadata_business_table_very01.pc1 > 25"
        + "          )"
        + "          )",
        query.getQuery()    
    ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //
    
    myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc4, null));
    myTest.getSelections().add(new Selection(null, bc6, null));
    
    query = generator.generateSql(myTest, "en_US", null, databaseMeta);

    TestHelper.assertEqualsIgnoreWhitespaces( 
          "SELECT \n" 
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n"
        + "FROM \n" 
        + "             pt4 metadata_business_table_very01 \n" 
        + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n"
        + "             (\n" 
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" 
        + "GROUP BY \n"  
        + "             metadata_business_table_very01.pc4 \n",
        query.getQuery()    
    );
  }

  @Test
  public void testAliasOuterJoinGeneration() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("metadata_business_table_very_long_name_1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("metadata_business_table_very_long_name_2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("metadata_business_table_very_long_name_3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("metadata_business_table_very_long_name_4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("metadata_business_table_very_long_name_5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5");
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId("bc6"); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "SUM([pc5]*2)");
    bc6.setAggregationType(AggregationType.SUM);
    
    
    //bc6.setAggregationList(list);
    bc6.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc6);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setRelationshipType(RelationshipType._0_N);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc2, null));

    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25")); //$NON-NLS-1$
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.printOutJava(query.getQuery());
    
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT \n" + 
        "          metadata_business_table_very01.pc1 AS COL0\n" + 
        "         ,metadata_business_table_very02.pc2 AS COL1\n" + 
        "\n" + 
        "FROM pt1 metadata_business_table_very01 LEFT OUTER JOIN pt2 metadata_business_table_very02\n" + 
        "     ON ( metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2 )\n" + 
        "\n" + 
        "WHERE \n" + 
        "        (\n" + 
        "          (\n" + 
        "              metadata_business_table_very01.pc1  > 25\n" + 
        "          )\n" + 
        "        )\n",
        query.getQuery()    
    ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //
    
    myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc4, null));
    myTest.getSelections().add(new Selection(null, bc6, null));
    
    query = generator.generateSql(myTest, "en_US", null, databaseMeta);

    TestHelper.assertEqualsIgnoreWhitespaces( 
          "SELECT \n" 
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n"
        + "FROM \n" 
        + "             pt4 metadata_business_table_very01 \n" 
        + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n"
        + "             (\n" 
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" 
        + "GROUP BY \n"  
        + "             metadata_business_table_very01.pc4 \n",
        query.getQuery()    
    );
  }

  
  @Test
  public void testSumFormula() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("metadata_business_table_very_long_name_1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("metadata_business_table_very_long_name_2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("metadata_business_table_very_long_name_3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("metadata_business_table_very_long_name_4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("metadata_business_table_very_long_name_5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5");
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId("bc6"); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
    bc6.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "[pc5]*2");
    bc6.setAggregationType(AggregationType.SUM);
    
    
    //bc6.setAggregationList(list);
    bc6.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc6);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);

    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc4, null));

    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25")); //$NON-NLS-1$
    
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          metadata_business_table_very01.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,metadata_business_table_very04.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 metadata_business_table_very01\n" //$NON-NLS-1$
        + "         ,pt2 metadata_business_table_very02\n" //$NON-NLS-1$
        + "         ,pt3 metadata_business_table_very03\n" //$NON-NLS-1$
        + "         ,pt4 metadata_business_table_very04\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             metadata_business_table_very02.pc2 = metadata_business_table_very03.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very03.pc3 = metadata_business_table_very04.pc4\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n" 
        + "             metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2\n"
        + "          )\n"
        + "      AND (\n"
        + "          (\n"
        + "             metadata_business_table_very01.pc1 > 25"
        + "          )"
        + "          )",
        query.getQuery()    
    ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //
    
    myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc4, null));
    myTest.getSelections().add(new Selection(null, bc6, null));

    query = generator.generateSql(myTest, "en_US", null, databaseMeta);

    TestHelper.assertEqualsIgnoreWhitespaces( 
          "SELECT \n" 
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n"
        + "FROM \n" 
        + "             pt4 metadata_business_table_very01 \n" 
        + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n"
        + "             (\n" 
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" 
        + "GROUP BY \n"  
        + "             metadata_business_table_very01.pc4 \n",
        query.getQuery()    
    );
  }

  @Test
  public void testInlineTable() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE_TYPE, TargetTableType.INLINE_SQL);
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "select * from mytable"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);    
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc4, null));

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          (select * from mytable) bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n" 
        + "             bt1.pc1 = bt2.pc2\n"
        + "          )\n",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  @Test
  public void testParameterSqlGenerationWithFunctions() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      LogicalColumn bce2 = model.findLogicalColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query(null, model);

      query.getParameters().add(new Parameter("test3", DataType.STRING, "value"));
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));

      query.getConstraints().add(new Constraint(CombinationType.AND, "LIKE([bt2.bc2]; [param:test3])")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "CONTAINS([bt2.bc2]; [param:test3])")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "BEGINSWITH([bt2.bc2]; [param:test3])")); //$NON-NLS-1$
      query.getConstraints().add(new Constraint(CombinationType.AND, "ENDSWITH([bt2.bc2]; [param:test3])")); //$NON-NLS-1$
      SqlGenerator generator = new SqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta, null, true);
      TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          bt1.pc1 AS COL0\n" + 
          "         ,bt2.pc2 AS COL1\n" + 
          "FROM \n" + 
          "          pt1 bt1\n" + 
          "         ,pt2 bt2\n" + 
          "WHERE \n" + 
          "          ( bt1.pc1 = bt2.pc2 )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              bt2.pc2  LIKE ?\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  LIKE '%' || ? || '%'\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  LIKE ? || '%'\n" + 
          "          )\n" + 
          "      AND (\n" + 
          "              bt2.pc2  LIKE '%' || ?\n" + 
          "          )\n" + 
          "        )\n",
          mquery.getQuery()
          );

      Assert.assertNotNull(mquery.getParamList());
      Assert.assertEquals(4, mquery.getParamList().size());
      Assert.assertEquals("test3", mquery.getParamList().get(0));
      Assert.assertEquals("test3", mquery.getParamList().get(0));
      Assert.assertEquals("test3", mquery.getParamList().get(0));
      Assert.assertEquals("test3", mquery.getParamList().get(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    
    
    
  }

  @Test
  public void testInvalidRelationException() throws Exception {
    
    final LogicalModel model = new LogicalModel();
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc2.setLogicalTable(bt2);
    // bt2.addLogicalColumn(bc2);

    bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
    bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc4.setLogicalTable(bt4);
    bt4.addLogicalColumn(bc4);
    bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME);
    bc5.setLogicalTable(bt5);
    bt5.addLogicalColumn(bc5);
    bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setFromColumn(bc1);
    rl1.setToTable(bt2);
    // rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setFromTable(bt2);
    // rl2.setFromColumn(bc2);
    rl2.setToTable(bt3);
    rl2.setToColumn(bc3);

    final LogicalRelationship rl3 = new LogicalRelationship();
    
    rl3.setFromTable(bt3);
    rl3.setFromColumn(bc3);
    rl3.setToTable(bt4);
    rl3.setToColumn(bc4);

    final LogicalRelationship rl4 = new LogicalRelationship();
    
    rl4.setFromTable(bt4);
    rl4.setFromColumn(bc4);
    rl4.setToTable(bt5);
    rl4.setToColumn(bc5);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalTables().add(bt4);
    model.getLogicalTables().add(bt5);
    
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    model.getLogicalRelationships().add(rl3);
    model.getLogicalRelationships().add(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new Selection(null, bc1, null));
    myTest.getSelections().add(new Selection(null, bc4, null));
    SqlGenerator generator = new SqlGenerator();
    try {
      MappedQuery query = generator.generateSql(myTest, "en_US", null, databaseMeta);
      Assert.fail();
    } catch (PentahoMetadataException e) {
      Assert.assertTrue(e.getMessage().indexOf("ERROR_0003") >= 0);
    }
  }

  @Test
  public void testPreProcessedQuery() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields(true);
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] > 1")); //$NON-NLS-1$

      SqlGenerator generator = new TestPreSqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);

      TestHelper.printOutJava(mquery.getQuery());
      
      TestHelper.assertEqualsIgnoreWhitespaces(
          "SELECT DISTINCT \n" + 
          "          \"bt1\".\"pc1\" AS \"COL0\"\n" + 
          "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + 
          "FROM \n" + 
          "          \"pt1\" \"bt1\"\n" + 
          "         ,\"pt2\" \"bt2\"\n" + 
          "WHERE \n" + 
          "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "      NOT (\n" + 
          "              \"bt1\".\"pc1\"  > 1\n" + 
          "          )\n" + 
          "      AND NOT (\n" + 
          "              \"bt1\".\"pc1\"  < 1\n" + 
          "          )\n" + 
          "        )\n"
          ,
          mquery.getQuery()
          );

      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testPostProcessedQuery() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn("bc1");
      LogicalColumn bc2 = model.findLogicalColumn("bc2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields(true);
      Query query = new Query(null, model);
      
      query.getSelections().add(new Selection(null, bc1, null));
      query.getSelections().add(new Selection(null, bc2, null));
      
      query.getConstraints().add(new Constraint(CombinationType.AND_NOT, "[bt1.bc1] > 1")); //$NON-NLS-1$

      SqlGenerator generator = new TestPostSqlGenerator();
      
      MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);

      Assert.assertEquals("Totally bogus", mquery.getQuery());

      TestHelper.printOutJava(mquery.getQuery());
      
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  
  @Test
  public void testLimitedQuery() throws Exception {
    LogicalModel model = TestHelper.buildDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1");
    DatabaseMeta databaseMeta = new DatabaseMeta("", "HYPERSONIC", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query query = new Query(null, model);
    query.setLimit(10);
    
    query.getSelections().add(new Selection(null, bc1, null));
    
    SqlGenerator generator = new SqlGenerator();
    
    MappedQuery mquery = generator.generateSql(query, "en_US", null, databaseMeta);

    TestHelper.assertEqualsIgnoreWhitespaces("SELECT TOP 10 DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1", mquery.getQuery());
  }

  public static class TestPreSqlGenerator extends SqlGenerator {
    
    @Override
    protected void preprocessQueryModel(SQLQueryModel query, List<Selection> selections,
        Map<LogicalTable, String> tableAliases, DatabaseMeta databaseMeta) {
      
      query.addWhereFormula(" \"bt1\".\"pc1\"  < 1", "AND NOT");

      super.preprocessQueryModel(query, selections, tableAliases, databaseMeta);
    }


  }

  public static class TestPostSqlGenerator extends SqlGenerator {

    @Override
    protected String processGeneratedSql(String sql) {
      sql = "Totally bogus";
      return super.processGeneratedSql(sql);
    }
    

  }









}
