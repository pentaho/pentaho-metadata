/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.example.AdvancedMQLQuery;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

@SuppressWarnings( "deprecation" )
public class AdvancedMQLQueryImplIT extends MetadataTestBase {

  public BusinessModel getDefaultModel() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" );
    bt1.setTargetTable( "pt1" );

    BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" );
    bc1.setFormula( "pc1" );
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    mainCat.addBusinessColumn( bc1 );

    BusinessColumn bcs1 = new BusinessColumn();
    bcs1.setId( "bcs1" );
    bcs1.setFormula( "pc1" );
    bcs1.setAggregationType( AggregationSettings.SUM );
    bcs1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bcs1 );
    mainCat.addBusinessColumn( bcs1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" );
    bt2.setTargetTable( "pt2" );

    BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" );
    bc2.setFormula( "pc2" );
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessColumn bce2 = new BusinessColumn();
    bce2.setId( "bce2" ); //$NON-NLS-1$
    bce2.setExact( true );
    bce2.setFormula( "[bt2.bc2] * 2" ); //$NON-NLS-1$
    bce2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bce2 );
    mainCat.addBusinessColumn( bce2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" );
    bt3.setTargetTable( "pt3" );

    BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" );
    bc3.setFormula( "pc3" );
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();

    rl1.setTableFrom( bt1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldFrom( bc1 );
    rl1.setFieldTo( bc2 );

    final RelationshipMeta rl2 = new RelationshipMeta();

    rl2.setTableTo( bt2 );
    rl2.setTableFrom( bt3 );
    rl2.setFieldFrom( bc3 );
    rl2.setFieldTo( bc2 );

    model.addBusinessTable( bt1 );
    model.addBusinessTable( bt2 );
    model.addBusinessTable( bt3 );
    model.addRelationship( rl1 );
    model.addRelationship( rl2 );

    return model;
  }

  public void testAliasedJoin() throws Exception {

    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn( "bc1" ); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn( "bc3" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( "[alias1.bc1] * 3" ) );

    myTest.addConstraint( "AND", "[alias1.bc1] > 10" );
    myTest.addConstraint( "AND", "[bt3.bc3] > 10" );

    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEqualsIgnoreWhitespaces( "SELECT DISTINCT " + "bt1.pc1 AS COL0 ," + "bt1_alias1.pc1 AS COL1 ,"
        + "bt3.pc3 AS COL2 , " + "bt1_alias1.pc1 * 3 AS COL3 " + "FROM " + "pt1 bt1 ," + "pt2 bt2 ," + "pt3 bt3 ,"
        + "pt1 bt1_alias1 ," + "pt2 bt2_alias1 " + "WHERE " + "( bt1.pc1 = bt2.pc2 ) " + "AND ( bt3.pc3 = bt2.pc2 ) "
        + "AND ( bt1_alias1.pc1 = bt2_alias1.pc2 ) " + "AND ( bt3.pc3 = bt2_alias1.pc2 ) "
        + "AND (( bt1_alias1.pc1 > 10 ) " + "AND ( bt3.pc3 > 10 ))", myTest.getQuery().getQuery() );
  }

  public void testAliasedJoin2() throws Exception {

    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn( "bc1" ); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn( "bc3" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    String qry = myTest.getQuery().getQuery();
    // System.out.println("Generated query: " + qry);
    assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1_alias1.pc1 AS COL0\n"
        + "         ,bt3.pc3 AS COL1\n" + "FROM \n" + "          pt3 bt3\n" + "         ,pt1 bt1_alias1\n"
        + "         ,pt2 bt2_alias1\n" + "WHERE \n" + "          (\n"
        + "             bt1_alias1.pc1 = bt2_alias1.pc2\n" + "          )\n" + "      AND (\n"
        + "             bt3.pc3 = bt2_alias1.pc2\n" + "          )\n", qry );

  }

  // test aliased join with aggregate functions defined in query

  public void testQueryAggFunctions() throws Exception {

    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn( "bc1" ); //$NON-NLS-1$
    BusinessColumn bc3 = model.findBusinessColumn( "bc3" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( "SUM([bt3.bc3])" ) );

    myTest.addConstraint( "AND", "SUM([bt3.bc3]) > 30" );

    myTest.addOrderBy( new AdvancedMQLQuery.AliasedSelection( "SUM([bt3.bc3])" ), true );

    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());

    assertEqualsIgnoreWhitespaces( "SELECT \n" + "          bt1_alias1.pc1 AS COL0\n"
        + "         , SUM( bt3.pc3 ) AS COL1\n" + "FROM \n" + "          pt3 bt3\n" + "         ,pt1 bt1_alias1\n"
        + "         ,pt2 bt2_alias1\n" + "WHERE \n" + "          (\n"
        + "             bt1_alias1.pc1 = bt2_alias1.pc2\n" + "          )\n" + "      AND (\n"
        + "             bt3.pc3 = bt2_alias1.pc2\n" + "          )\n" + "GROUP BY \n" + "          bt1_alias1.pc1\n"
        + "HAVING \n" + "          (\n" + "              SUM( bt3.pc3 ) > 30\n" + "          )\n" + "ORDER BY \n"
        + "           SUM( bt3.pc3 )\n", myTest.getQuery().getQuery() );
  }

  public void testModelAggFunctions() throws Exception {
    BusinessModel model = getDefaultModel();
    BusinessColumn bc2 = model.findBusinessColumn( "bc2" ); //$NON-NLS-1$
    BusinessColumn bcs1 = model.findBusinessColumn( "bcs1" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bcs1, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bcs1, null ) );

    myTest.addConstraint( "AND", "[bt2.bc2] > 10" );
    myTest.addConstraint( "AND", "[alias1.bcs1] >= 30" );
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEqualsIgnoreWhitespaces( "SELECT \n" + "          SUM(bt1_alias1.pc1) AS COL0\n"
        + "         ,bt2.pc2 AS COL1\n" + "         ,SUM(bt1.pc1) AS COL2\n" + "FROM \n" + "          pt1 bt1\n"
        + "         ,pt2 bt2\n" + "         ,pt1 bt1_alias1\n" + "WHERE \n" + "          (\n"
        + "             bt1.pc1 = bt2.pc2\n" + "          )\n" + "      AND (\n"
        + "             bt1_alias1.pc1 = bt2.pc2\n" + "          )\n" + "      AND ((\n"
        + "              bt2.pc2  > 10\n" + "          ))\n" + "GROUP BY \n" + "          bt2.pc2\n" + "HAVING \n"
        + "          (\n" + "              SUM(bt1_alias1.pc1)  >= 30\n" + "          )\n", myTest.getQuery()
        .getQuery() );
  }

  public void testModelFunctions() throws Exception {
    BusinessModel model = getDefaultModel();
    BusinessColumn bc1 = model.findBusinessColumn( "bc1" ); //$NON-NLS-1$
    BusinessColumn bce2 = model.findBusinessColumn( "bce2" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bce2, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bce2, null ) );

    myTest.addOrderBy( new AdvancedMQLQuery.AliasedSelection( bc1, null ), true );
    myTest.addOrderBy( new AdvancedMQLQuery.AliasedSelection( bce2, "alias1" ), true );

    myTest.addConstraint( "OR", "[alias1.bce2] > 10" );
    // SQLQueryTest.printOutJava(myTest.getQuery().getQuery());
    assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "           bt2_alias1.pc2  * 2 AS COL0\n"
        + "         ,bt1.pc1 AS COL1\n" + "         , bt2.pc2  * 2 AS COL2\n" + "FROM \n" + "          pt1 bt1\n"
        + "         ,pt2 bt2\n" + "         ,pt2 bt2_alias1\n" + "WHERE \n" + "          (\n"
        + "             bt1.pc1 = bt2.pc2\n" + "          )\n" + "      AND (\n"
        + "             bt1.pc1 = bt2_alias1.pc2\n" + "          )\n" + "      AND ((\n"
        + "               bt2_alias1.pc2  * 2  > 10\n" + "          ))\n" + "ORDER BY \n" + "          bt1.pc1\n"
        + "         , bt2_alias1.pc2  * 2\n", myTest.getQuery().getQuery() );
  }

  /**
   * this test generates an advanced mqlquery, generates xml, re-reads the xml and then compares the sql of both to
   * verify the serialization / deserialization code works.
   */
  public void testXmlReadingWriting() throws Exception {
    BusinessModel model = getDefaultModel();
    SchemaMeta schemaMeta = new SchemaMeta();
    schemaMeta.setDomainName( "test_domain" );
    schemaMeta.addModel( model );
    BusinessColumn bc1 = model.findBusinessColumn( "bc1" ); //$NON-NLS-1$
    BusinessColumn bce2 = model.findBusinessColumn( "bce2" ); //$NON-NLS-1$

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( schemaMeta, model, databaseMeta, "en_US" );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bce2, "alias1" ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bce2, null ) );

    // myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bc1, null), true);
    // myTest.addOrderBy(new AdvancedMQLQuery.AliasedSelection(bce2, "alias1"), true);

    myTest.addConstraint( "OR", "[alias1.bce2] > 10" );

    // System.out.println(myTest.getXML().replaceAll(">", ">\n"));

    AdvancedMQLQuery myReadTest = new AdvancedMQLQuery( schemaMeta, model, databaseMeta, "en_US" );
    myReadTest.fromXML( myTest.getXML(), schemaMeta );

    assertEquals( myTest.getQuery().getQuery(), myReadTest.getQuery().getQuery() );

    AdvancedMQLQuery myReadTest2 = new AdvancedMQLQuery( schemaMeta, model, databaseMeta, "en_US" );
    String xml =
        "<mql><domain_type>relational</domain_type><domain_id>test_domain</domain_id>"
            + "<model_id>model_01</model_id><model_name>Model 1</model_name>"
            + "<options><disable_distinct>false</disable_distinct></options>"
            + "<selections><selection><view>cat_01</view><column>bc1</column></selection>"
            + "<selection><alias>Alias1</alias><view>cat_01</view><column>bc1</column></selection></selections>"
            + "<constraints>   <constraint><operator/> <condition>[cat_01.bc1] =\"1539006\"</condition> </constraint> </constraints></mql>";
    myReadTest2.fromXML( xml, schemaMeta );

    assertEqualsIgnoreWhitespaces( "SELECT DISTINCT " + "   bt1.pc1 AS COL0," + "   bt1_Alias1.pc1 AS COL1 " + "FROM "
        + "   pt1 bt1," + "   pt1 bt1_Alias1 " + "WHERE" + "   ((bt1.pc1 = '1539006'))", myReadTest2.getQuery()
        .getQuery() );

    AdvancedMQLQuery myReadTest3 = new AdvancedMQLQuery( schemaMeta, model, databaseMeta, "en_US" );
    xml =
        "<mql>" + "  <domain_type>relational</domain_type>" + "  <domain_id>test_domain</domain_id>"
            + "  <model_id>model_01</model_id>" + "  <model_name>Model 1</model_name>" + "  <options>"
            + "    <disable_distinct>false</disable_distinct>" + "  </options>" + "  <selections>"
            + "    <selection><view>cat_01</view><column>bc1</column></selection>"
            + "    <selection><view>cat_01</view><column>bc2</column></selection>"
            + "    <selection><view>cat_01</view><column>bc3</column></selection>"
            + "    <selection><formula>[cat_01.bc1] * [cat_01.bc2]</formula></selection>"
            + "    <selection><formula>[cat_01.bc1] / [cat_01.bc2]</formula></selection>"
            + "    <selection><formula>[cat_01.bc1] + [cat_01.bc2]</formula></selection>"
            + "    <selection><formula>[cat_01.bc1] - [cat_01.bc2]</formula></selection>" + "  </selections>"
            + "  <constraints>"
            + "    <constraint><operator/> <condition>[cat_01.bc1] =\"1539006\"</condition> </constraint> "
            + "  </constraints>" + "</mql>";
    myReadTest3.fromXML( xml, schemaMeta );
    // System.out.println(myReadTest3.getQuery().getQuery());
    assertEqualsIgnoreWhitespaces( "SELECT DISTINCT " + "  bt1.pc1 AS COL0," + "  bt2.pc2 AS COL1,"
        + "  bt3.pc3 AS COL2," + "  bt1.pc1*bt2.pc2 AS COL3," + "  bt1.pc1/bt2.pc2 AS COL4,"
        + "  bt1.pc1+bt2.pc2 AS COL5," + "  bt1.pc1-bt2.pc2 AS COL6 " + "FROM " + "  pt1 bt1," + "  pt2 bt2,"
        + "  pt3 bt3 " + "WHERE" + "  (bt1.pc1 = bt2.pc2)" + "  AND (bt3.pc3 = bt2.pc2)"
        + "  AND ((bt1.pc1 = '1539006'))", myReadTest3.getQuery().getQuery() );

  }

  /**
   * Scenario 1: Two Tables are outer joined
   */
  public void testOuterJoinScenario1WithAddlAlias() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    model.addBusinessTable( bt1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    model.addBusinessTable( bt2 );
    mainCat.addBusinessColumn( bc2 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, "alias" ) );

    myTest.addConstraint( "AND", "[cat_01.bc2] = 1" );
    myTest.addConstraint( "AND", "[alias.bc2] = 2" );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt2_alias.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t2 bt2_alias ON ( bt1.k = bt2_alias.k AND ( bt2_alias.k = 2 ) ) ) ON ( bt1.k = bt2.k AND ( bt2.k = 1 ) )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1: Two Tables are outer joined
   */
  public void testOuterJoinScenario1() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1a: Two Tables are outer joined with a constraint
   */
  public void testOuterJoinScenario1a() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addConstraint( "AND", "[cat_01.bc2] > 1" );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1b: Two Tables are outer joined with an aggregate
   */
  public void testOuterJoinScenario1b() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setAggregationType( AggregationSettings.SUM );
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1c: Two Tables are outer joined with an aggregate constraint
   */
  public void testOuterJoinScenario1c() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setAggregationType( AggregationSettings.SUM );
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addConstraint( "AND", "[cat_01.bc2] > 1" );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM(bt2.pc2) > 1 )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 2: Two Joined Tables are outer joined to a single table
   */
  public void testOuterJoinScenario2() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "pt3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "pc3" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_1_N );
    rl2.setTableFrom( bt2 );
    rl2.setFieldFrom( bc2 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0,bt2.pc2 AS COL1,bt3.pc3 AS COL2 FROM pt3 bt3 JOIN(pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON(bt1.pc1 = bt2.pc2))ON(bt2.pc2 = bt3.pc3)",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 2a: Two Joined Tables are outer joined to two other tables
   */
  public void testOuterJoinScenario2a() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final BusinessTable bt4 = new BusinessTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setTargetTable( "t4" ); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setFormula( "k" ); //$NON-NLS-1$
    bc4.setBusinessTable( bt4 );
    bt4.addBusinessColumn( bc4 );
    mainCat.addBusinessColumn( bc4 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_1_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl2.setTableFrom( bt2 );
    rl2.setFieldFrom( bc2 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    final RelationshipMeta rl3 = new RelationshipMeta();
    rl3.setType( RelationshipMeta.TYPE_RELATIONSHIP_1_N );
    rl3.setTableFrom( bt3 );
    rl3.setFieldFrom( bc3 );
    rl3.setTableTo( bt4 );
    rl3.setFieldTo( bc4 );

    model.addRelationship( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc4, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0,bt2.k AS COL1,bt3.k AS COL2,bt4.k AS COL3 FROM t1 bt1 JOIN(t4 bt4 JOIN(t2 bt2 LEFT OUTER JOIN t3 bt3 ON(bt2.k = bt3.k))ON(bt3.k = bt4.k))ON(bt1.k = bt2.k)",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 3: Three Tables are outer joined
   */
  public void testOuterJoinScenario3() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl2.setTableFrom( bt2 );
    rl2.setFieldFrom( bc2 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t1 bt1 LEFT OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t3 bt3 ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 4: Two outer joins on a single table
   */
  public void testOuterJoinScenario4() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl2.setTableFrom( bt1 );
    rl2.setFieldFrom( bc1 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  public void testOuterJoinScenario5a() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_N_0 );
    rl1.setJoinOrderKey( "A" );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_N_0 );
    rl2.setJoinOrderKey( "B" );
    rl2.setTableFrom( bt1 );
    rl2.setFieldFrom( bc1 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t3 bt3 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t2 bt2 ON ( bt1.k = bt2.k ) ) ON ( bt1.k = bt3.k )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  public void testOuterJoinScenario5b() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_N_0 );
    rl1.setJoinOrderKey( "B" );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_N_0 );
    rl2.setJoinOrderKey( "A" );
    rl2.setTableFrom( bt1 );
    rl2.setFieldFrom( bc1 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
        query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 6: 4 tables outer joined
   * 
   * NOTE: This does not work on MYSQL, because FULL OUTER JOIN is not supported.
   */
  public void testOuterJoinScenario6() throws Exception {
    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "t1" ); //$NON-NLS-1$
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "k" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    bt1.setRelativeSize( 1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "t2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "k" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "t3" ); //$NON-NLS-1$
    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "k" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final BusinessTable bt4 = new BusinessTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setTargetTable( "t4" ); //$NON-NLS-1$
    final BusinessColumn bc4 = new BusinessColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setFormula( "k" ); //$NON-NLS-1$
    bc4.setBusinessTable( bt4 );
    bt4.addBusinessColumn( bc4 );
    mainCat.addBusinessColumn( bc4 );

    final RelationshipMeta rl1 = new RelationshipMeta();
    rl1.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_0 );
    rl1.setJoinOrderKey( "A" );
    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    final RelationshipMeta rl2 = new RelationshipMeta();
    rl2.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_0 );
    rl2.setJoinOrderKey( "B" );
    rl2.setTableFrom( bt2 );
    rl2.setFieldFrom( bc2 );
    rl2.setTableTo( bt3 );
    rl2.setFieldTo( bc3 );

    model.addRelationship( rl2 );

    final RelationshipMeta rl3 = new RelationshipMeta();
    rl3.setType( RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    rl3.setJoinOrderKey( "A" );
    rl3.setTableFrom( bt2 );
    rl3.setFieldFrom( bc2 );
    rl3.setTableTo( bt4 );
    rl3.setFieldTo( bc4 );

    model.addRelationship( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc3, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc4, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t3 bt3 FULL OUTER JOIN ( t1 bt1 FULL OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t4 bt4 ON ( bt2.k = bt4.k ) ) ON ( bt1.k = bt2.k ) ) ON ( bt2.k = bt3.k )",
        query.getQuery() ); //$NON-NLS-1$
  }

  public void testComplexJoinMQL() throws Exception {
    // System.out.println("******************* testComplexJoinMQL *******************");
    String locale = "en_US"; //$NON-NLS-1$

    final BusinessModel model = new BusinessModel();
    model.setId( "model_01" );
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "cat_01" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );

    final BusinessTable bt1 = new BusinessTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setTargetTable( "pt1" );
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "pc1" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bt1.addBusinessColumn( bc1 );
    mainCat.addBusinessColumn( bc1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" );
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    final BusinessTable bt3 = new BusinessTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setTargetTable( "pt3" );

    final BusinessColumn bc3 = new BusinessColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setFormula( "pc3" ); //$NON-NLS-1$
    bc3.setBusinessTable( bt3 );
    bt3.addBusinessColumn( bc3 );
    mainCat.addBusinessColumn( bc3 );

    final RelationshipMeta rl1 = new RelationshipMeta();

    rl1.setTableFrom( bt1 );
    rl1.setTableTo( bt2 );
    rl1.setComplexJoin( "[bt1.bc1] = [bt2.bc2]" ); //$NON-NLS-1$
    rl1.setComplex( true );

    final RelationshipMeta rl2 = new RelationshipMeta();

    rl2.setTableTo( bt2 );
    rl2.setTableFrom( bt3 );

    final RelationshipMeta rl3 = new RelationshipMeta();

    rl3.setTableTo( bt1 );
    rl3.setTableFrom( bt3 );

    model.addBusinessTable( bt1 );
    model.addBusinessTable( bt2 );
    model.addBusinessTable( bt3 );

    model.addRelationship( rl1 );
    model.addRelationship( rl2 );
    model.addRelationship( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    AdvancedMQLQuery myTest = new AdvancedMQLQuery( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc1, null ) );
    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, null ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )", //$NON-NLS-1$
        query.getQuery() );

    myTest.addSelection( new AdvancedMQLQuery.AliasedSelection( bc2, "alias" ) );
    query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 ,bt2_alias.pc2 AS COL2 FROM pt1 bt1 ,pt2 bt2 ,pt2 bt2_alias WHERE ( bt1.pc1 = bt2.pc2 ) AND ( bt1.pc1 = bt2_alias.pc2 )", //$NON-NLS-1$
        query.getQuery() );
  }

}
