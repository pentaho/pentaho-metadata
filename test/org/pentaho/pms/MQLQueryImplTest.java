package org.pentaho.pms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.ExtendedMetaData;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.Path;
import org.pentaho.pms.mql.SQLGenerator;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

public class MQLQueryImplTest  extends TestCase {
  
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
    String joinSQL = sqlGenerator.getJoin(model, rl1, databaseMeta, locale);

    assertEquals(joinSQL, " bt1.pc1  =  bt2.pc2 ");//$NON-NLS-1$
  } 
  
  public void testGroupBySQLGeneration() {
    try {

      BusinessModel model = buildDefaultModel();
      BusinessColumn bc0 = model.getBusinessTable(0).getBusinessColumn(0);
      bc0.setAggregationType(AggregationSettings.SUM);
      BusinessColumn bc1 = model.getBusinessTable(1).getBusinessColumn(0);
      BusinessColumn bc2 = model.getBusinessTable(1).getBusinessColumn(1);
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      MQLQueryImpl myTest = new MQLQueryImpl(null, model, databaseMeta, "en_US");  //$NON-NLS-1$
      myTest.addSelection(new Selection(bc0));
      myTest.addSelection(new Selection(bc1));
      myTest.addSelection(new Selection(bc2));
      
      myTest.addConstraint(WhereCondition.operators[0], "[bt1.bc1] > 25"); //$NON-NLS-1$

      MappedQuery query = myTest.getQuery();
      assertEquals(query.getQuery(), 
          "SELECT \n"                          //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n"      //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n"                          //$NON-NLS-1$
          + "          pt1 bt1\n"              //$NON-NLS-1$
          + "         ,pt2 bt2\n"              //$NON-NLS-1$
          + "WHERE \n"                         //$NON-NLS-1$
          + "          bt1.pc1 = bt2.pc2\n"    //$NON-NLS-1$
          + "GROUP BY \n"                      //$NON-NLS-1$
          + "          bt2.pc2\n"              //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n"              //$NON-NLS-1$
          + "HAVING \n"                        //$NON-NLS-1$
          + "           (  SUM(bt1.pc1)  > 25 ) \n" //$NON-NLS-1$
          );
      Map map = query.getMap();
      assertNotNull(map);
      assertEquals(map.size(), 3);
      assertEquals(map.get("COL0"), "bc1"); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals(map.get("COL1"), "bc2");  //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals(map.get("COL2"), "bce2"); //$NON-NLS-1$ //$NON-NLS-2$
      
      assertEquals(query.getDisplayQuery(),
          "SELECT \n"                        //$NON-NLS-1$
        + "          SUM(bt1.pc1) AS bc1\n"  //$NON-NLS-1$
        + "         ,bt2.pc2 AS bc2\n"       //$NON-NLS-1$
        + "         , bt2.pc2  * 2 AS bce2\n"//$NON-NLS-1$        
        + "FROM \n"                          //$NON-NLS-1$
        + "          pt1 bt1\n"              //$NON-NLS-1$
        + "         ,pt2 bt2\n"              //$NON-NLS-1$
        + "WHERE \n"                         //$NON-NLS-1$
        + "          bt1.pc1 = bt2.pc2\n"    //$NON-NLS-1$
        + "GROUP BY \n"                      //$NON-NLS-1$
        + "          bt2.pc2\n"              //$NON-NLS-1$
        + "         , bt2.pc2  * 2\n"        //$NON-NLS-1$
        + "HAVING \n"                        //$NON-NLS-1$
        + "           (  SUM(bt1.pc1)  > 25 ) \n" //$NON-NLS-1$
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
    assertEquals(query.getQuery(), 
        "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "      AND bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "      AND bt1.pc1 = bt2.pc2\n"); //$NON-NLS-1$
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
}
