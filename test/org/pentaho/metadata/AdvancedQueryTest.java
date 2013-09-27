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
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.mock.MockHiveDatabaseMeta;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.example.AdvancedQueryXmlHelper;
import org.pentaho.metadata.query.example.AdvancedSqlGenerator;
import org.pentaho.metadata.query.example.AliasedSelection;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.pms.MetadataTestBase;

public class AdvancedQueryTest {
  
  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }
  
  @Test
  public void testQueryXmlSerialization() {
    Domain domain = TestHelper.getBasicDomain();
    LogicalModel model = domain.findLogicalModel("MODEL");
    Query query = new Query(domain, model);
    
    Category category = model.findCategory("CATEGORY");
    LogicalColumn column = category.findLogicalColumn("LC_CUSTOMERNAME");
    query.getSelections().add(new AliasedSelection(category, column, null, null));
    query.getSelections().add(new AliasedSelection(category, column, null, "alias"));
    
    query.getConstraints().add(new Constraint(CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\""));

    query.getOrders().add(new Order(new AliasedSelection(category, column, null, null), Order.Type.ASC));
    
    AdvancedQueryXmlHelper helper = new AdvancedQueryXmlHelper();
    String xml = helper.toXML(query);
    
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    try {
      repo.storeDomain(domain, true);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Query newQuery = null;
    try {
      newQuery = helper.fromXML(repo, xml);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    // verify that when we serialize and deserialize, the xml stays the same. 
    Assert.assertEquals(xml, helper.toXML(newQuery));
  }
  
  public LogicalModel getDefaultModel() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId("model_01");
    Category mainCat = new Category();
    mainCat.setId("cat_01");
    model.getCategories().add(mainCat);
    
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId("bt1");
    bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1");
    
    LogicalColumn bc1 = new LogicalColumn();
    bc1.setId("bc1");
    bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1");
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    mainCat.addLogicalColumn(bc1);
    
    LogicalColumn bcs1 = new LogicalColumn();
    bcs1.setId("bcs1");
    bcs1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1");
    bcs1.setAggregationType(AggregationType.SUM);
    bcs1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bcs1);
    mainCat.addLogicalColumn(bcs1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2");
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2");
    
    LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2");
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2");
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalColumn bce2 = new LogicalColumn();
    bce2.setId("bce2"); //$NON-NLS-1$
    bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
    bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "[bt2.bc2] * 2"); //$NON-NLS-1$
    bce2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bce2);
    mainCat.addLogicalColumn(bce2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3");
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3");
    
    LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3");
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3");
    bc3.setLogicalTable(bt3);
    bt3.addLogicalColumn(bc3);
    mainCat.addLogicalColumn(bc3);
    
    final LogicalRelationship rl1 = new LogicalRelationship();
    
    rl1.setFromTable(bt1);
    rl1.setToTable(bt2);
    rl1.setFromColumn(bc1);
    rl1.setToColumn(bc2);
    
    final LogicalRelationship rl2 = new LogicalRelationship();
    
    rl2.setToTable(bt2);
    rl2.setFromTable(bt3);
    rl2.setFromColumn(bc3);
    rl2.setToColumn(bc2);
    
    model.getLogicalTables().add(bt1);
    model.getLogicalTables().add(bt2);
    model.getLogicalTables().add(bt3);
    model.getLogicalRelationships().add(rl1);
    model.getLogicalRelationships().add(rl2);
    
    return model;
  }
  
  @Test
  public void testAliasedJoin() throws Exception {
    
    LogicalModel model = getDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1"); //$NON-NLS-1$
    LogicalColumn bc3 = model.findLogicalColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(null, model);

    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, "alias1"));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    myTest.getSelections().add(new AliasedSelection("[alias1.bc1] * 3"));
    
    
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[alias1.bc1] > 10"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt3.bc3] > 10"));
    
    // SQLQueryTest.printOutJava(new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT " + 
        "bt1.pc1 AS COL0 ," + 
        "bt1_alias1.pc1 AS COL1 ," + 
        "bt3.pc3 AS COL2 , " + 
        "bt1_alias1.pc1 * 3 AS COL3 " +
        "FROM " +
        "pt1 bt1 ," +
        "pt2 bt2 ," +
        "pt3 bt3 ," +
        "pt1 bt1_alias1 ," +
        "pt2 bt2_alias1 " +
        "WHERE " +
        "( bt1.pc1 = bt2.pc2 ) " +
        "AND ( bt3.pc3 = bt2.pc2 ) " +
        "AND ( bt1_alias1.pc1 = bt2_alias1.pc2 ) " +
        "AND ( bt3.pc3 = bt2_alias1.pc2 ) " +
        "AND (( bt1_alias1.pc1 > 10 ) " +
        "AND ( bt3.pc3 > 10 ))",
        new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
  }

  @Test
  public void testAliasedJoin2() throws Exception {

    LogicalModel model = getDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1"); //$NON-NLS-1$
    LogicalColumn bc3 = model.findLogicalColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(null, model);

    myTest.getSelections().add(new AliasedSelection(null, bc1, null, "alias1"));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));

    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT \n" + 
        "          bt1_alias1.pc1 AS COL0\n" + 
        "         ,bt3.pc3 AS COL1\n" + 
        "FROM \n" + 
        "          pt3 bt3\n" + 
        "         ,pt1 bt1_alias1\n" + 
        "         ,pt2 bt2_alias1\n" + 
        "WHERE \n" + 
        "          (\n" + 
        "             bt1_alias1.pc1 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt3.pc3 = bt2_alias1.pc2\n" + 
        "          )\n",
        new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
  }
  
  
  // test aliased join with aggregate functions defined in query
  @Test
  public void testQueryAggFunctions() throws Exception {

    LogicalModel model = getDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1"); //$NON-NLS-1$
    LogicalColumn bc3 = model.findLogicalColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(null, model);

    myTest.getSelections().add(new AliasedSelection(null, bc1, null, "alias1"));
    myTest.getSelections().add(new AliasedSelection("SUM([bt3.bc3])"));
    
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "SUM([bt3.bc3]) > 30"));
    
    myTest.getOrders().add(new Order(new AliasedSelection("SUM([bt3.bc3])"), Type.ASC));
    
    // SQLQueryTest.printOutJava(new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
    
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT \n" + 
        "          bt1_alias1.pc1 AS COL0\n" + 
        "         , SUM( bt3.pc3 ) AS COL1\n" + 
        "FROM \n" + 
        "          pt3 bt3\n" + 
        "         ,pt1 bt1_alias1\n" + 
        "         ,pt2 bt2_alias1\n" + 
        "WHERE \n" + 
        "          (\n" + 
        "             bt1_alias1.pc1 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt3.pc3 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "GROUP BY \n" + 
        "          bt1_alias1.pc1\n" +
        "HAVING \n" + 
        "          (\n" + 
        "              SUM( bt3.pc3 ) > 30\n" + 
        "          )\n" + 
        "ORDER BY \n" + 
        "           SUM( bt3.pc3 )\n",
        new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
  }
  
  @Test
  public void testModelAggFunctions() throws Exception {
    LogicalModel model = getDefaultModel();
    LogicalColumn bc2 = model.findLogicalColumn("bc2"); //$NON-NLS-1$
    LogicalColumn bcs1 = model.findLogicalColumn("bcs1"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(null, model);
    
    myTest.getSelections().add(new AliasedSelection(null, bcs1, null,  "alias1"));    
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bcs1, null, null));
    
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[bt2.bc2] > 10"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[alias1.bcs1] >= 30"));
    // SQLQueryTest.printOutJava(new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT \n" + 
        "          SUM(bt1_alias1.pc1) AS COL0\n" + 
        "         ,bt2.pc2 AS COL1\n" + 
        "         ,SUM(bt1.pc1) AS COL2\n" + 
        "FROM \n" + 
        "          pt1 bt1\n" + 
        "         ,pt2 bt2\n" + 
        "         ,pt1 bt1_alias1\n" + 
        "WHERE \n" + 
        "          (\n" + 
        "             bt1.pc1 = bt2.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt1_alias1.pc1 = bt2.pc2\n" + 
        "          )\n" + 
        "      AND ((\n" + 
        "              bt2.pc2  > 10\n" + 
        "          ))\n" + 
        "GROUP BY \n" + 
        "          bt2.pc2\n" + 
        "HAVING \n" + 
        "          (\n" + 
        "              SUM(bt1_alias1.pc1)  >= 30\n" + 
        "          )\n",
        new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
  }
  
  @Test
  public void testModelFunctions() throws Exception {
    LogicalModel model = getDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn("bc1"); //$NON-NLS-1$
    LogicalColumn bce2 = model.findLogicalColumn("bce2"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(null, model);
    
    myTest.getSelections().add(new AliasedSelection(null, bce2, null, "alias1"));    
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bce2, null, null));
    
    myTest.getOrders().add(new Order(new AliasedSelection(null, bc1, null, null), Type.ASC));
    myTest.getOrders().add(new Order(new AliasedSelection(null, bce2, null, "alias1"), Type.ASC));
    
    myTest.getConstraints().add(new Constraint(CombinationType.OR, "[alias1.bce2] > 10"));
    // SQLQueryTest.printOutJava(new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT \n" + 
        "           bt2_alias1.pc2  * 2 AS COL0\n" + 
        "         ,bt1.pc1 AS COL1\n" + 
        "         , bt2.pc2  * 2 AS COL2\n" + 
        "FROM \n" + 
        "          pt1 bt1\n" + 
        "         ,pt2 bt2\n" + 
        "         ,pt2 bt2_alias1\n" + 
        "WHERE \n" + 
        "          (\n" + 
        "             bt1.pc1 = bt2.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt1.pc1 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "      AND ((\n" + 
        "               bt2_alias1.pc2  * 2  > 10\n" + 
        "          ))\n" +
        "ORDER BY \n" + 
        "          bt1.pc1\n" + 
        "         , bt2_alias1.pc2  * 2\n",
        new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery());
  }
  
  /**
   * this test generates an advanced mqlquery, generates xml, re-reads the xml
   * and then compares the sql of both to verify the serialization / deserialization
   * code works.
   */
  @Test
  public void testXmlReadingWriting() throws Exception {
    LogicalModel model = getDefaultModel();
    Domain domain = new Domain();
    domain.setId("test_domain");
    domain.getLogicalModels().add(model);
    LogicalColumn bc1 = model.findLogicalColumn("bc1"); //$NON-NLS-1$
    LogicalColumn bce2 = model.findLogicalColumn("bce2"); //$NON-NLS-1$
    Category mainCat = model.findCategory("cat_01");
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    Query myTest = new Query(domain, model);
    
    myTest.getSelections().add(new AliasedSelection(mainCat, bce2, null, "alias1"));    
    myTest.getSelections().add(new AliasedSelection(mainCat, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(mainCat, bce2, null, null));
    
    // myTest.getOrders().add(new Order((new AliasedSelection(null, bc1, null, null), true);
    // myTest.getOrders().add(new Order((new AliasedSelection(null, bce2, null, "alias1"), true);
    
    myTest.getConstraints().add(new Constraint(CombinationType.OR, "[alias1.bce2] > 10"));
    
    // System.out.println(myTest.getXML().replaceAll(">", ">\n"));
    AdvancedQueryXmlHelper xmlhelper = new AdvancedQueryXmlHelper();
    String xml = xmlhelper.toXML(myTest);
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    repo.storeDomain(domain, false);
    Query myReadTest = xmlhelper.fromXML(repo, xml);
    
    Assert.assertEquals(new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta).getQuery(), 
    new AdvancedSqlGenerator().generateSql(myReadTest, "en_US", null, databaseMeta).getQuery());

    
    xml = "<mql><domain_type>relational</domain_type><domain_id>test_domain</domain_id>" +
      "<model_id>model_01</model_id><model_name>Model 1</model_name>"+
      "<options><disable_distinct>false</disable_distinct></options>"+
      "<selections><selection><view>cat_01</view><column>bc1</column></selection>" +
      "<selection><alias>Alias1</alias><view>cat_01</view><column>bc1</column></selection></selections>" +
      "<constraints>   <constraint><operator/> <condition>[cat_01.bc1] =\"1539006\"</condition> </constraint> </constraints></mql>";
    Query myReadTest2 = xmlhelper.fromXML(repo, xml);

    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT " +
        "   bt1.pc1 AS COL0," +
        "   bt1_Alias1.pc1 AS COL1 " +
        "FROM " +
        "   pt1 bt1," +
        "   pt1 bt1_Alias1 " +
        "WHERE" +
        "   ((bt1.pc1 = '1539006'))", 
        new AdvancedSqlGenerator().generateSql(myReadTest2, "en_US", null, databaseMeta).getQuery());

    xml = 
      "<mql>" +
      "  <domain_type>relational</domain_type>" +
      "  <domain_id>test_domain</domain_id>" +
      "  <model_id>model_01</model_id>" +
      "  <model_name>Model 1</model_name>"+
      "  <options>" +
      "    <disable_distinct>false</disable_distinct>" +
      "  </options>"+
      "  <selections>" +
      "    <selection><view>cat_01</view><column>bc1</column></selection>" +
      "    <selection><view>cat_01</view><column>bc2</column></selection>" +
      "    <selection><view>cat_01</view><column>bc3</column></selection>" +      
      "    <selection><formula>[cat_01.bc1] * [cat_01.bc2]</formula></selection>" +
      "    <selection><formula>[cat_01.bc1] / [cat_01.bc2]</formula></selection>" +
      "    <selection><formula>[cat_01.bc1] + [cat_01.bc2]</formula></selection>" +
      "    <selection><formula>[cat_01.bc1] - [cat_01.bc2]</formula></selection>" +      
      "  </selections>" +
      "  <constraints>" +
      "    <constraint><operator/> <condition>[cat_01.bc1] =\"1539006\"</condition> </constraint> " +
      "  </constraints>" +
      "</mql>";
    
    Query myReadTest3 = xmlhelper.fromXML(repo, xml);
    
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT " +
        "  bt1.pc1 AS COL0," +
        "  bt2.pc2 AS COL1," +
        "  bt3.pc3 AS COL2," +
        "  bt1.pc1*bt2.pc2 AS COL3," +
        "  bt1.pc1/bt2.pc2 AS COL4," +
        "  bt1.pc1+bt2.pc2 AS COL5," +
        "  bt1.pc1-bt2.pc2 AS COL6 " +
        "FROM " +
        "  pt1 bt1," +
        "  pt2 bt2," +
        "  pt3 bt3 " +
        "WHERE" +
        "  (bt1.pc1 = bt2.pc2)" +
        "  AND (bt3.pc3 = bt2.pc2)" +
        "  AND ((bt1.pc1 = '1539006'))",
        new AdvancedSqlGenerator().generateSql(myReadTest3, "en_US", null, databaseMeta).getQuery());
  
  }
  
  
  /**
   * Scenario 1: Two Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario1WithAddlAlias()throws Exception {
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
    model.getLogicalTables().add(bt1);
    mainCat.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "t2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "k"); //$NON-NLS-1$
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    model.getLogicalTables().add(bt2);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, "alias"));
    
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[cat_01.bc2] = 1"));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[alias.bc2] = 2"));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt2_alias.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t2 bt2_alias ON ( bt1.k = bt2_alias.k AND ( bt2_alias.k = 2 ) ) ) ON ( bt1.k = bt2.k AND ( bt2.k = 1 ) )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  

  /**
   * Scenario 1: Two Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario1()throws Exception {
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[cat_01.bc2] > 1"));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getConstraints().add(new Constraint(CombinationType.AND, "[cat_01.bc2] > 1"));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM(bt2.pc2) > 1 )",
        query.getQuery()    
    ); //$NON-NLS-1$
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
    bc2.setLogicalTable(bt2);
    bt2.addLogicalColumn(bc2);
    mainCat.addLogicalColumn(bc2);
    
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0,bt2.pc2 AS COL1,bt3.pc3 AS COL2 FROM pt3 bt3 JOIN(pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON(bt1.pc1 = bt2.pc2))ON(bt2.pc2 = bt3.pc3)",
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc4, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0,bt2.k AS COL1,bt3.k AS COL2,bt4.k AS COL3 FROM t1 bt1 JOIN(t4 bt4 JOIN(t2 bt2 LEFT OUTER JOIN t3 bt3 ON(bt2.k = bt3.k))ON(bt3.k = bt4.k))ON(bt1.k = bt2.k)",
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
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
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc3, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc4, null, null));    
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t3 bt3 FULL OUTER JOIN ( t1 bt1 FULL OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t4 bt4 ON ( bt2.k = bt4.k ) ) ON ( bt1.k = bt2.k ) ) ON ( bt2.k = bt3.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
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
    bc1.setLogicalTable(bt1);
    bt1.addLogicalColumn(bc1);
    
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
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
    
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query(null, model); //$NON-NLS-1$
    myTest.getSelections().add(new AliasedSelection(null, bc1, null, null));
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, null));
    
    MappedQuery query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM null bt1 ,null bt2 WHERE ( bt1.pc1 = bt2.pc2 )", //$NON-NLS-1$
        query.getQuery()    
    ); 
    
    myTest.getSelections().add(new AliasedSelection(null, bc2, null, "alias"));
    query = new AdvancedSqlGenerator().generateSql(myTest, "en_US", null, databaseMeta);
    TestHelper.assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 ,bt2_alias.pc2 AS COL2 FROM null bt1 ,null bt2 ,null bt2_alias WHERE ( bt1.pc1 = bt2.pc2 ) AND ( bt1.pc1 = bt2_alias.pc2 )", //$NON-NLS-1$
        query.getQuery()    
    );
  } 
  


}
