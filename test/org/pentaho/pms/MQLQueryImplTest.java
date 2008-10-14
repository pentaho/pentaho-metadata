package org.pentaho.pms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.ExtendedMetaData;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.Path;
import org.pentaho.pms.mql.SQLGenerator;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

public class MQLQueryImplTest extends MetadataTestBase {
  
  public void testGetShortestPathBetween() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1");  //$NON-NLS-1$
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setTableTo(bt2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableTo(bt2);
    rl2.setTableFrom(bt3);
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    
    SQLGenerator sqlGenerator = new SQLGenerator();
    List<BusinessTable> tbls = new ArrayList<BusinessTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    assertEquals(path.size(), 1);
    assertEquals(path.getRelationship(0), rl1);
    List<BusinessTable> tbls2 = new ArrayList<BusinessTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    assertEquals(path.size(), 2);
    assertEquals(path.getRelationship(0), rl1);
    assertEquals(path.getRelationship(1), rl2);
  }

  public void testGetShortestPathBetween2() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("bt4"); //$NON-NLS-1$

    final BusinessTable bt5 = new BusinessTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setTableTo(bt2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableTo(bt2);
    rl2.setTableFrom(bt3);
    
    final RelationshipMeta rl3 = new RelationshipMeta();
    
    rl3.setTableTo(bt4);
    rl3.setTableFrom(bt5);
    
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt4);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    model.addBusinessTable(bt5);
    
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    model.addRelationship(rl3);
    
    SQLGenerator sqlGenerator = new SQLGenerator();
    List<BusinessTable> tbls = new ArrayList<BusinessTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    assertEquals(path.size(), 1);
    assertEquals(path.getRelationship(0), rl1);
    List<BusinessTable> tbls2 = new ArrayList<BusinessTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    assertEquals(path.size(), 2);
    assertEquals(path.getRelationship(0), rl1);
    assertEquals(path.getRelationship(1), rl2);
  }


  public void testGetShortestPathBetween3() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setTableTo(bt2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableTo(bt2);
    rl2.setTableFrom(bt3);
    
    final RelationshipMeta rl3 = new RelationshipMeta();
    
    rl3.setTableTo(bt1);
    rl3.setTableFrom(bt3);
    
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    model.addRelationship(rl3);
    
    SQLGenerator sqlGenerator = new SQLGenerator();
    List<BusinessTable> tbls = new ArrayList<BusinessTable>();
    tbls.add(bt1); tbls.add(bt2);
    Path path = sqlGenerator.getShortestPathBetween(model, tbls);
    
    assertEquals(path.size(), 1);
    assertEquals(path.getRelationship(0), rl1);
    List<BusinessTable> tbls2 = new ArrayList<BusinessTable>();
    tbls2.add(bt1); tbls2.add(bt3);
    path = sqlGenerator.getShortestPathBetween(model, tbls2);
    assertEquals(path.size(), 1);
    assertEquals(path.getRelationship(0), rl3);
  }

  public void testComplexJoinMQL() throws Exception {
    
    String locale = "en_US"; //$NON-NLS-1$
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$

    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setTableTo(bt2);
    rl1.setComplexJoin("[bt1.bc1] = [bt2.bc2]"); //$NON-NLS-1$
    rl1.setComplex(true);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableTo(bt2);
    rl2.setTableFrom(bt3);
    
    final RelationshipMeta rl3 = new RelationshipMeta();
    
    rl3.setTableTo(bt1);
    rl3.setTableFrom(bt3);
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    model.addRelationship(rl3);
    
    SQLGenerator sqlGenerator = new SQLGenerator();
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    String joinSQL = sqlGenerator.getJoin(model, rl1, null, databaseMeta, locale);

    assertEqualsIgnoreWhitespaces(joinSQL, " bt1.pc1  =  bt2.pc2 ");//$NON-NLS-1$
  } 
  
  public void testGroupBySQLGeneration() {
    try {

      BusinessModel model = buildDefaultModel();
      BusinessColumn bc1 = model.findBusinessColumn("bc1");
      bc1.setAggregationType(AggregationSettings.SUM);
      BusinessColumn bc2 = model.findBusinessColumn("bc2");
      BusinessColumn bce2 = model.findBusinessColumn("bce2");
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US");  //$NON-NLS-1$
      myTest.addSelection(new Selection(bc1));
      myTest.addSelection(new Selection(bc2));
      myTest.addSelection(new Selection(bce2));
      
      myTest.addConstraint(WhereCondition.operators[0], "[bt1.bc1] > 25"); //$NON-NLS-1$

      MappedQuery query = myTest.getQuery();
      assertEqualsIgnoreWhitespaces(
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
      assertNotNull(map);
      assertEquals(map.size(), 3);
      assertEquals(map.get("COL0"), "bc1"); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals(map.get("COL1"), "bc2");  //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals(map.get("COL2"), "bce2"); //$NON-NLS-1$ //$NON-NLS-2$
      
      assertEqualsIgnoreWhitespaces(
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
      
      ExtendedMetaData emd = (ExtendedMetaData)query.generateMetadata(mmd);
      
      assertEquals("pc1", emd.getAttribute(0, 0, "formula").toString()); //$NON-NLS-1$  //$NON-NLS-2$
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  public void testLocale() {
    BusinessModel model = buildDefaultModel();
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    
    assertEquals("en_US", myTest.getLocale()); //$NON-NLS-1$
    myTest.setLocale("en"); //$NON-NLS-1$
    assertEquals("en", myTest.getLocale()); //$NON-NLS-1$
  }
  
  public void testSelections() {
    BusinessModel model = buildDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn("bc1"); //$NON-NLS-1$
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    
    // test adding of a selection twice to the list
    Selection selection = new Selection(bc1);
    myTest.addSelection(selection);
    assertEquals(1, myTest.getSelections().size());
    myTest.addSelection(selection);
    assertEquals(1, myTest.getSelections().size());
    
    // test the pulling of DatabaseMeta out of selection
    final PhysicalTable pt1 = new PhysicalTable();
    pt1.setDatabaseMeta(databaseMeta);
    
    final PhysicalColumn pc1 = new PhysicalColumn();
    pc1.setTable(pt1);
    bc1.setPhysicalColumn(pc1);
    
    myTest = new MQLQueryImpl(null, model, null, "en_US"); //$NON-NLS-1$
    myTest.addSelection(selection);
    DatabaseMeta dbMeta = myTest.getDatabaseMeta();
    assertNotNull(dbMeta);
    assertEquals(dbMeta, databaseMeta);
    
    // 
  }
  
  
  public static BusinessModel buildDefaultModel() {
    try {
      final BusinessModel model = new BusinessModel();
      
      final BusinessTable bt1 = new BusinessTable();
      bt1.setId("bt1"); //$NON-NLS-1$
      bt1.setTargetTable("pt1"); //$NON-NLS-1$
      final BusinessColumn bc1 = new BusinessColumn();
      bc1.setId("bc1"); //$NON-NLS-1$
      bc1.setFormula("pc1"); //$NON-NLS-1$
      bc1.setBusinessTable(bt1);
      bt1.addBusinessColumn(bc1);
      bt1.setRelativeSize(1);
      
      final BusinessTable bt2 = new BusinessTable();
      bt2.setId("bt2"); //$NON-NLS-1$
      bt2.setTargetTable("pt2"); //$NON-NLS-1$
      final BusinessColumn bc2 = new BusinessColumn();
      bc2.setId("bc2"); //$NON-NLS-1$
      bc2.setFormula("pc2"); //$NON-NLS-1$
      bc2.setBusinessTable(bt2);
      bt2.addBusinessColumn(bc2);

      final BusinessColumn bce2 = new BusinessColumn();
      bce2.setId("bce2"); //$NON-NLS-1$
      bce2.setExact(true);
      bce2.setFormula("[bt2.bc2] * 2"); //$NON-NLS-1$
      bce2.setBusinessTable(bt2);
      bt2.addBusinessColumn(bce2);

      
      bt2.setRelativeSize(1);
      
      final BusinessTable bt3 = new BusinessTable();
      bt3.setId("bt3"); //$NON-NLS-1$
      bt3.setTargetTable("pt3"); //$NON-NLS-1$
      final BusinessColumn bc3 = new BusinessColumn();
      bc3.setId("bc3"); //$NON-NLS-1$
      bc3.setFormula("pc3"); //$NON-NLS-1$
      bc3.setBusinessTable(bt3);
      bt3.addBusinessColumn(bc3);
      bt3.setRelativeSize(1);
      
      final BusinessTable bt4 = new BusinessTable();
      bt4.setId("bt4"); //$NON-NLS-1$
      bt4.setTargetTable("pt4"); //$NON-NLS-1$
      final BusinessColumn bc4 = new BusinessColumn();
      bc4.setId("bc4"); //$NON-NLS-1$
      bc4.setFormula("pc4"); //$NON-NLS-1$
      bc4.setBusinessTable(bt4);
      bt4.addBusinessColumn(bc4);
      bt4.setRelativeSize(1);
      
      final BusinessTable bt5 = new BusinessTable();
      bt5.setId("bt5"); //$NON-NLS-1$
      bt5.setTargetTable("pt5"); //$NON-NLS-1$
      final BusinessColumn bc5 = new BusinessColumn();
      bc5.setId("bc5"); //$NON-NLS-1$
      bc5.setFormula("pc5"); //$NON-NLS-1$
      bc5.setBusinessTable(bt5);
      bt5.addBusinessColumn(bc5);
      bt5.setRelativeSize(1);
      final RelationshipMeta rl1 = new RelationshipMeta();
      
      rl1.setTableFrom(bt1);
      rl1.setFieldFrom(bc1);
      rl1.setTableTo(bt2);
      rl1.setFieldTo(bc2);
      
      final RelationshipMeta rl2 = new RelationshipMeta();
      
      rl2.setTableFrom(bt2);
      rl2.setFieldFrom(bc2);
      rl2.setTableTo(bt3);
      rl2.setFieldTo(bc3);
  
      final RelationshipMeta rl3 = new RelationshipMeta();
      
      rl3.setTableFrom(bt3);
      rl3.setFieldFrom(bc3);
      rl3.setTableTo(bt4);
      rl3.setFieldTo(bc4);
  
      final RelationshipMeta rl4 = new RelationshipMeta();
      
      rl4.setTableFrom(bt4);
      rl4.setFieldFrom(bc4);
      rl4.setTableTo(bt5);
      rl4.setFieldTo(bc5);
      
      model.addBusinessTable(bt1);
      model.addBusinessTable(bt2);
      model.addBusinessTable(bt3);
      model.addBusinessTable(bt4);
      model.addBusinessTable(bt5);
      
      model.addRelationship(rl1);
      model.addRelationship(rl2);
      model.addRelationship(rl3);
      model.addRelationship(rl4);
      
      return model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public void testGetShortestPathBetween4() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);

    bt2.setRelativeSize(1);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("pt3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("pc3"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    bt3.setRelativeSize(1);
    
    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setTargetTable("pt4"); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setFormula("pc4"); //$NON-NLS-1$
    bc4.setBusinessTable(bt4);
    bt4.addBusinessColumn(bc4);
    bt4.setRelativeSize(1);
    
    final BusinessTable bt5 = new BusinessTable();
    bt5.setId("bt5"); //$NON-NLS-1$
    bt5.setTargetTable("pt5"); //$NON-NLS-1$
    final BusinessColumn bc5 = new BusinessColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    bc5.setFormula("pc5"); //$NON-NLS-1$
    bc5.setBusinessTable(bt5);
    bt5.addBusinessColumn(bc5);
    bt5.setRelativeSize(1);
    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);

    final RelationshipMeta rl3 = new RelationshipMeta();
    
    rl3.setTableFrom(bt3);
    rl3.setFieldFrom(bc3);
    rl3.setTableTo(bt4);
    rl3.setFieldTo(bc4);

    final RelationshipMeta rl4 = new RelationshipMeta();
    
    rl4.setTableFrom(bt4);
    rl4.setFieldFrom(bc4);
    rl4.setTableTo(bt5);
    rl4.setFieldTo(bc5);
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    model.addBusinessTable(bt4);
    model.addBusinessTable(bt5);
    
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    model.addRelationship(rl3);
    model.addRelationship(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc4));

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
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
  
  public void testSubsets() {
    SQLGenerator myTest = new SQLGenerator();
    ArrayList<String> testList = new ArrayList<String>();
    testList.add("A"); //$NON-NLS-1$
    testList.add("B"); //$NON-NLS-1$
    testList.add("C"); //$NON-NLS-1$
    testList.add("D"); //$NON-NLS-1$
    testList.add("E"); //$NON-NLS-1$
  
    List subsets = myTest.getSubsetsOfSize(0, testList);
    assertNotNull(subsets);
    assertEquals(0, subsets.size());
    
    subsets = myTest.getSubsetsOfSize(1, testList);
    assertNotNull(subsets);
    assertEquals(5, subsets.size());
    String subsetStr = subsetsToString(subsets);
    assertEquals("[A],[B],[C],[D],[E]", subsetStr);  //$NON-NLS-1$

    
    subsets = myTest.getSubsetsOfSize(2, testList);
    assertNotNull(subsets);
    assertEquals(10, subsets.size());
    subsetStr = subsetsToString(subsets);
    assertEquals("[A,B],[A,C],[A,D],[A,E],[B,C],[B,D],[B,E],[C,D],[C,E],[D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(3, testList);
    assertNotNull(subsets);
    assertEquals(10, subsets.size());
    subsetStr = subsetsToString(subsets);
    assertEquals("[A,B,C],[A,B,D],[A,B,E],[A,C,D],[A,C,E],[A,D,E],[B,C,D],[B,C,E],[B,D,E],[C,D,E]", subsetStr);  //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(4, testList);
    assertNotNull(subsets);
    assertEquals(5, subsets.size());
    subsetStr = subsetsToString(subsets);
    assertEquals("[A,B,C,D],[A,B,C,E],[A,B,D,E],[A,C,D,E],[B,C,D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(5, testList);
    assertNotNull(subsets);
    assertEquals(1, subsets.size());
    subsetStr = subsetsToString(subsets);
    assertEquals("[A,B,C,D,E]", subsetStr); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize(6, testList);
    assertNotNull(subsets);
    assertEquals(0, subsets.size());
  }
  
  public void testBusinessColumnFormulaUsingTwoBT() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessColumn bce2 = new BusinessColumn();
    bce2.setId("bce2"); //$NON-NLS-1$
    bce2.setExact(true);
    bce2.setFormula("[cat_01.bc2] * [cat_01.bc1]"); //$NON-NLS-1$
    bce2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bce2);
    mainCat.addBusinessColumn(bce2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bce2));

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt2.pc2 * bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
    
  }
  
  /**
   * Scenario 1: Two Tables are outer joined
   */
  public void testOuterJoinScenario1()throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 1a: Two Tables are outer joined with a constraint
   */
  public void testOuterJoinScenario1a() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addConstraint("AND", "[cat_01.bc2] > 1");
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 1b: Two Tables are outer joined with an aggregate
   */
  public void testOuterJoinScenario1b() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setAggregationType(AggregationSettings.SUM);
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  
  /**
   * Scenario 1c: Two Tables are outer joined with an aggregate constraint
   */
  public void testOuterJoinScenario1c() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setAggregationType(AggregationSettings.SUM);
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addConstraint("AND", "[cat_01.bc2] > 1");
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM(bt2.pc2) > 1 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 1d: Two Tables are outer joined both with constraints
   */
  
  public void testOuterJoinScenario1d() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    model.addBusinessTable(bt1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    model.addBusinessTable(bt2);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addConstraint("AND", "[bt1.bc1] > 1");
    myTest.addConstraint("AND", "[bt2.bc2] > 1");
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) ) WHERE ( bt1.pc1 > 1 )",  //$NON-NLS-1$
        query.getQuery()    
    ); 
  }
  
  
  /**
   * Scenario 2: Two Joined Tables are outer joined to a single table
   */
  public void testOuterJoinScenario2()throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("pt3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("pc3"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_1_N);
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 ,bt3.pc3 AS COL2 FROM pt1 bt1 LEFT OUTER JOIN ( pt2 bt2 JOIN pt3 bt3 ON ( bt2.pc2 = bt3.pc3 ) ) ON ( bt1.pc1 = bt2.pc2 )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }

  /**
   * Scenario 2a: Two Joined Tables are outer joined to two other tables
   */
  public void testOuterJoinScenario2a()throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setTargetTable("t4"); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setFormula("k"); //$NON-NLS-1$
    bc4.setBusinessTable(bt4);
    bt4.addBusinessColumn(bc4);
    mainCat.addBusinessColumn(bc4);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_1_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);

    final RelationshipMeta rl3 = new RelationshipMeta();
    rl3.setType(RelationshipMeta.TYPE_RELATIONSHIP_1_N);
    rl3.setTableFrom(bt3);
    rl3.setFieldFrom(bc3);
    rl3.setTableTo(bt4);
    rl3.setFieldTo(bc4);
    
    model.addRelationship(rl3);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    myTest.addSelection(new Selection(bc4));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t1 bt1 JOIN ( t2 bt2 LEFT OUTER JOIN ( t3 bt3 JOIN t4 bt4 ON ( bt3.k = bt4.k ) ) ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 3: Three Tables are outer joined
   */
  public void testOuterJoinScenario3() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t1 bt1 LEFT OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t3 bt3 ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 4: Two outer joins on a single table
   */
  public void testOuterJoinScenario4() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl2.setTableFrom(bt1);
    rl2.setFieldFrom(bc1);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  public void testOuterJoinScenario5a() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_N_0);
    rl1.setJoinOrderKey("A");
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_N_0);
    rl2.setJoinOrderKey("B");
    rl2.setTableFrom(bt1);
    rl2.setFieldFrom(bc1);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t3 bt3 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t2 bt2 ON ( bt1.k = bt2.k ) ) ON ( bt1.k = bt3.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  public void testOuterJoinScenario5b() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_N_0);
    rl1.setJoinOrderKey("B");
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_N_0);
    rl2.setJoinOrderKey("A");
    rl2.setTableFrom(bt1);
    rl2.setFieldFrom(bc1);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  /**
   * Scenario 6: 4 tables outer joined
   * 
   * NOTE: This does not work on MYSQL, because FULL OUTER JOIN is not supported.
   */
  public void testOuterJoinScenario6() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId("model_01");
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory(true);
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId("cat_01");
    rootCat.addBusinessCategory(mainCat);
    model.setRootCategory(rootCat);
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1"); //$NON-NLS-1$
    bt1.setTargetTable("t1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("k"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    mainCat.addBusinessColumn(bc1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2"); //$NON-NLS-1$
    bt2.setTargetTable("t2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("k"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);
    mainCat.addBusinessColumn(bc2);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3"); //$NON-NLS-1$
    bt3.setTargetTable("t3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("k"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    mainCat.addBusinessColumn(bc3);
    
    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("bt4"); //$NON-NLS-1$
    bt4.setTargetTable("t4"); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setFormula("k"); //$NON-NLS-1$
    bc4.setBusinessTable(bt4);
    bt4.addBusinessColumn(bc4);
    mainCat.addBusinessColumn(bc4);
    
    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_0);
    rl1.setJoinOrderKey("A");
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    model.addRelationship(rl1);    

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_0);
    rl2.setJoinOrderKey("B");
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);
    
    model.addRelationship(rl2);
    
    final RelationshipMeta rl3 = new RelationshipMeta();
    rl3.setType(RelationshipMeta.TYPE_RELATIONSHIP_0_N);
    rl3.setJoinOrderKey("A");
    rl3.setTableFrom(bt2);
    rl3.setFieldFrom(bc2);
    rl3.setTableTo(bt4);
    rl3.setFieldTo(bc4);
    
    model.addRelationship(rl3);
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc2));
    myTest.addSelection(new Selection(bc3));
    myTest.addSelection(new Selection(bc4));    
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t3 bt3 FULL OUTER JOIN ( t1 bt1 FULL OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t4 bt4 ON ( bt2.k = bt4.k ) ) ON ( bt1.k = bt2.k ) ) ON ( bt2.k = bt3.k )",
        query.getQuery()    
    ); //$NON-NLS-1$
  }
  
  public void testGenerateUniqueAlias() {
    List<String> existingAliases = new ArrayList<String>();
    existingAliases.add("test");
    assertEquals("tes01", SQLGenerator.generateUniqueAlias("test", 5, existingAliases));
    assertEquals("tes01", SQLGenerator.generateUniqueAlias("testing", 5, existingAliases));
    assertEquals("test1", SQLGenerator.generateUniqueAlias("test1", 5, existingAliases));
    
    existingAliases.add("tes01");
    assertEquals("tes02", SQLGenerator.generateUniqueAlias("test", 5, existingAliases));
    assertEquals("tes02", SQLGenerator.generateUniqueAlias("testing", 5, existingAliases));
    assertEquals("test1", SQLGenerator.generateUniqueAlias("test1", 5, existingAliases));
    
  }
  
  public void testAliasGeneration() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("metadata_business_table_very_long_name_1"); //$NON-NLS-1$
    bt1.setTargetTable("pt1"); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId("bc1"); //$NON-NLS-1$
    bc1.setFormula("pc1"); //$NON-NLS-1$
    bc1.setBusinessTable(bt1);
    bt1.addBusinessColumn(bc1);
    bt1.setRelativeSize(1);
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("metadata_business_table_very_long_name_2"); //$NON-NLS-1$
    bt2.setTargetTable("pt2"); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId("bc2"); //$NON-NLS-1$
    bc2.setFormula("pc2"); //$NON-NLS-1$
    bc2.setBusinessTable(bt2);
    bt2.addBusinessColumn(bc2);

    bt2.setRelativeSize(1);
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("metadata_business_table_very_long_name_3"); //$NON-NLS-1$
    bt3.setTargetTable("pt3"); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId("bc3"); //$NON-NLS-1$
    bc3.setFormula("pc3"); //$NON-NLS-1$
    bc3.setBusinessTable(bt3);
    bt3.addBusinessColumn(bc3);
    bt3.setRelativeSize(1);
    
    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("metadata_business_table_very_long_name_4"); //$NON-NLS-1$
    bt4.setTargetTable("pt4"); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId("bc4"); //$NON-NLS-1$
    bc4.setFormula("pc4"); //$NON-NLS-1$
    bc4.setBusinessTable(bt4);
    bt4.addBusinessColumn(bc4);
    bt4.setRelativeSize(1);
    
    final BusinessTable bt5 = new BusinessTable();
    bt5.setId("metadata_business_table_very_long_name_5"); //$NON-NLS-1$
    bt5.setTargetTable("pt5"); //$NON-NLS-1$
    final BusinessColumn bc5 = new BusinessColumn();
    bc5.setId("bc5"); //$NON-NLS-1$
    // bc5.setFormula("pc5"); //$NON-NLS-1$
    bc5.setFormula("pc5");
    bc5.setBusinessTable(bt5);
    bt5.addBusinessColumn(bc5);
    bt5.setRelativeSize(1);

    final BusinessColumn bc6 = new BusinessColumn();
    bc6.setId("bc6"); //$NON-NLS-1$
    // bc5.setFormula("pc5"); //$NON-NLS-1$
    bc6.setExact(true);
    bc6.setFormula("SUM([pc5]*2)");
    bc6.setAggregationType(AggregationSettings.SUM);
    bc6.setBusinessTable(bt5);
    bt5.addBusinessColumn(bc6);
    bt5.setRelativeSize(1);

    final RelationshipMeta rl1 = new RelationshipMeta();
    
    rl1.setTableFrom(bt1);
    rl1.setFieldFrom(bc1);
    rl1.setTableTo(bt2);
    rl1.setFieldTo(bc2);
    
    final RelationshipMeta rl2 = new RelationshipMeta();
    
    rl2.setTableFrom(bt2);
    rl2.setFieldFrom(bc2);
    rl2.setTableTo(bt3);
    rl2.setFieldTo(bc3);

    final RelationshipMeta rl3 = new RelationshipMeta();
    
    rl3.setTableFrom(bt3);
    rl3.setFieldFrom(bc3);
    rl3.setTableTo(bt4);
    rl3.setFieldTo(bc4);

    final RelationshipMeta rl4 = new RelationshipMeta();
    
    rl4.setTableFrom(bt4);
    rl4.setFieldFrom(bc4);
    rl4.setTableTo(bt5);
    rl4.setFieldTo(bc5);
    
    model.addBusinessTable(bt1);
    model.addBusinessTable(bt2);
    model.addBusinessTable(bt3);
    model.addBusinessTable(bt4);
    model.addBusinessTable(bt5);
    
    model.addRelationship(rl1);
    model.addRelationship(rl2);
    model.addRelationship(rl3);
    model.addRelationship(rl4);
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc1));
    myTest.addSelection(new Selection(bc4));

    myTest.addConstraint(WhereCondition.operators[0], "[metadata_business_table_very_long_name_1.bc1] > 25"); //$NON-NLS-1$
    
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( 
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
        + "             metadata_business_table_very01.pc1 > 25"
        + "          )",
        query.getQuery()    
    ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //
    
    myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US"); //$NON-NLS-1$
    myTest.addSelection(new Selection(bc4));
    myTest.addSelection(new Selection(bc6));
    
    query = myTest.getQuery();

    assertEqualsIgnoreWhitespaces( 
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
  
}
