package org.pentaho.pms;


import junit.framework.TestCase;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.example.AdvancedMQLQuery;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

public class AdvancedMQLQueryImplTest  extends TestCase {

  public BusinessModel getDefaultModel() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1");
    bt1.setTargetTable("pt1");
    
    BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1");
    bc1.setFormula("pc1");
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    mainCat.addBusinessColumn(bc1);
    
    BusinessColumn bcs1 = new BusinessColumn();
    bcs1.setId("bcs1");
    bcs1.setFormula("pc1");
    bcs1.setAggregationType(AggregationSettings.SUM);
    bcs1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bcs1);
    mainCat.addBusinessColumn(bcs1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2");
    bt2.setTargetTable("pt2");
    
    BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2");
    bc2.setFormula("pc2");
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessColumn bce2 = new BusinessColumn();
    bce2.setId("bce2"); //$NON-NLS-1$
    bce2.setExact(true);
    bce2.setFormula("[bt2.bc2] * 2"); //$NON-NLS-1$
    bce2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bce2);
    mainCat.addBusinessColumn(bce2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3");
    bt3.setTargetTable("pt3");
    
    BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3");
    bc3.setFormula("pc3");
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setTableTo(bt2);
    rl1.setFieldFrom(bc1);
    rl1.setFieldTo(bc2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableTo(bt2);
    rl2.setTableFrom(bt3);
    rl2.setFieldFrom(bc3);
    rl2.setFieldTo(bc2);
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    
    return model;
  }
  
  public void testAliasedJoin() throws Exception {
    
    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");

    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, "alias1"));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc3, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection("[alias1.bc1] * 3"));
    
    
    myTest.addConstraint("AND", "[alias1.bc1] > 10");
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEquals(
        "SELECT DISTINCT \n" + 
        "          bt1.pc1 AS COL0\n" + 
        "         ,bt1_alias1.pc1 AS COL1\n" + 
        "         ,bt3.pc3 AS COL2\n" + 
        "         , bt1_alias1.pc1  * 3 AS COL3\n" + 
        "FROM \n" + 
        "          pt1 bt1\n" + 
        "         ,pt2 bt2\n" + 
        "         ,pt3 bt3\n" + 
        "         ,pt1 bt1_alias1\n" + 
        "         ,pt2 bt2_alias1\n" + 
        "WHERE \n" + 
        "          (\n" + 
        "             bt1.pc1 = bt2.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt3.pc3 = bt2.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt1_alias1.pc1 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "             bt3.pc3 = bt2_alias1.pc2\n" + 
        "          )\n" + 
        "      AND (\n" + 
        "              bt1_alias1.pc1  > 10\n" + 
        "          )\n",
        myTest.getQuery().getQuery());
  }

  public void testAliasedJoin2() throws Exception {

    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");

    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, "alias1"));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc3, null));

    assertEquals(
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
        myTest.getQuery().getQuery());
  }
  
  
  // test aliased join with aggregate functions defined in query
  
  public void testQueryAggFunctions() throws Exception {

    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn("bc3"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");

    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, "alias1"));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection("SUM([bt3.bc3])"));
    
    myTest.addConstraint("AND", "SUM([bt3.bc3]) > 30");
    
    myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection("SUM([bt3.bc3])"), true);
    
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    
    assertEquals(
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
        myTest.getQuery().getQuery());
  }
  
  public void testModelAggFunctions() throws Exception {
    BusinessModel model = getDefaultModel();
    BusinessColumn bc2 = model.findBusinessColumn("bc2"); //$NON-NLS-1$
    BusinessColumn bcs1 = model.findBusinessColumn("bcs1"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");
    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bcs1, "alias1"));    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc2, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bcs1, null));
    
    myTest.addConstraint("AND", "[bt2.bc2] > 10");
    myTest.addConstraint("AND", "[alias1.bcs1] >= 30");
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEquals(
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
        "      AND (\n" + 
        "              bt2.pc2  > 10\n" + 
        "          )\n" + 
        "GROUP BY \n" + 
        "          bt2.pc2\n" + 
        "HAVING \n" + 
        "          (\n" + 
        "              SUM(bt1_alias1.pc1)  >= 30\n" + 
        "          )\n",
        myTest.getQuery().getQuery());
  }
  
  public void testModelFunctions() throws Exception {
    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    BusinessColumn bce2 = model.findBusinessColumn("bce2"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");
    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bce2, "alias1"));    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bce2, null));
    
    myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bc1, null), true);
    myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bce2, "alias1"), true);
    
    myTest.addConstraint("OR", "[alias1.bce2] > 10");
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEquals(
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
        "      AND (\n" + 
        "               bt2_alias1.pc2  * 2  > 10\n" + 
        "          )\n" +
        "ORDER BY \n" + 
        "          bt1.pc1\n" + 
        "         , bt2_alias1.pc2  * 2\n",
        myTest.getQuery().getQuery());
  }
  
  /**
   * this test generates an advanced mqlquery, generates xml, re-reads the xml
   * and then compares the sql of both to verify the serialization / deserialization
   * code works.
   */
  public void testXmlReadingWriting() throws Exception {
    BusinessModel model = getDefaultModel();
    SchemaMeta schemaMeta = new SchemaMeta();
    schemaMeta.setDomainName("test_domain");
    schemaMeta.addModel(model);
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    BusinessColumn bce2 = model.findBusinessColumn("bce2"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(schemaMeta, model, databaseMeta, "en_US");
    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bce2, "alias1"));    
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bce2, null));
    
    // myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bc1, null), true);
    // myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bce2, "alias1"), true);
    
    myTest.addConstraint("OR", "[alias1.bce2] > 10");
    
    // System.out.println(myTest.getXML().replaceAll(">", ">\n"));
    
    AdvancedMQLQuery myReadTest = new AdvancedMQLQuery(schemaMeta, model, databaseMeta, "en_US");
    myReadTest.fromXML(myTest.getXML(), schemaMeta);
    
    assertEquals(myTest.getQuery().getQuery(), myReadTest.getQuery().getQuery());
  }
}
