package org.pentaho.pms;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.example.AdvancedMQLQuery;
import org.pentaho.pms.mql.Path;
import org.pentaho.pms.mql.SQLGenerator;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;

public class AdvancedMQLQueryImplTest  extends TestCase {
  
  public void test() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1");
    bt1.setTargetTable("pt1");
    
    BusinessColumn bc1 = new BusinessColumn();
    bc1.setBusinessTable(bt1);
    bc1.setId("bc1");
    bc1.setFormula("pc1");
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2");
    bt2.setTargetTable("pt2");
    BusinessColumn bc2 = new BusinessColumn();
    bc2.setBusinessTable(bt2);
    bc2.setId("bc2");
    bc2.setFormula("pc2");
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3");
    bt3.setTargetTable("pt3");
    
    BusinessColumn bc3 = new BusinessColumn();
    bc3.setBusinessTable(bt3);
    bc3.setId("bc3");
    bc3.setFormula("pc3");
    
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
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");

    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, "alias1"));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc3, null));
    
    myTest.getQuery();
  }

  public void test2() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1");
    bt1.setTargetTable("pt1");
    
    BusinessColumn bc1 = new BusinessColumn();
    bc1.setBusinessTable(bt1);
    bc1.setId("bc1");
    bc1.setFormula("pc1");
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2");
    bt2.setTargetTable("pt2");
    BusinessColumn bc2 = new BusinessColumn();
    bc2.setBusinessTable(bt2);
    bc2.setId("bc2");
    bc2.setFormula("pc2");
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3");
    bt3.setTargetTable("pt3");
    
    BusinessColumn bc3 = new BusinessColumn();
    bc3.setBusinessTable(bt3);
    bc3.setId("bc3");
    bc3.setFormula("pc3");
    
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
    
    System.out.println(bt1.getName("en_US"));
    
    DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery(null, model, databaseMeta, "en_US");

    //myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, null));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc1, "alias1"));
    myTest.addSelection(new AdvancedMQLQuery.AliasedSelection(bc3, null));
    
    myTest.getQuery();
  }
  
  
  public void testGetShortestPathBetween2() throws Exception {
    
    final BusinessModel model = new BusinessModel();
    
    final BusinessTable bt1 = new BusinessTable();
    bt1.setId("bt1");
    bt1.setTargetTable("pt1");
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2");
    bt2.setTargetTable("pt2");
    
    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3");
    bt3.setTargetTable("pt3");
    
    final BusinessTable bt4 = new BusinessTable();
    bt4.setId("bt4");
    bt4.setTargetTable("pt4");
    
    final BusinessTable bt5 = new BusinessTable();
    bt5.setId("bt5");
    
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
    
    List<BusinessTable> tbls = new ArrayList<BusinessTable>();
    tbls.add(bt1); tbls.add(bt2);
    SQLGenerator sqlGenerator = new SQLGenerator();
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
    bt1.setId("bt1");
    
    final BusinessTable bt2 = new BusinessTable();
    bt2.setId("bt2");

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId("bt3");

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
    
    System.out.println(bt1.getName("en_US"));
    
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

  
}
