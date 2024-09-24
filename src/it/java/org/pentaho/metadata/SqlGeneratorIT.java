/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.RelationshipType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.Path;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryModelMetaData;
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.dialect.SQLQueryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings( "nls" )
public class SqlGeneratorIT {

  @BeforeClass
  public static void initKettle() throws KettleException {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testGetShortestPathBetween() throws Exception {

    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt2 );
    Path path = sqlGenerator.getShortestPathBetween( model, tbls );

    assertEquals( path.size(), 1 );
    assertEquals( path.getRelationship( 0 ), rl1 );
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add( bt1 );
    tbls2.add( bt3 );
    path = sqlGenerator.getShortestPathBetween( model, tbls2 );
    assertEquals( path.size(), 2 );
    assertEquals( path.getRelationship( 0 ), rl1 );
    assertEquals( path.getRelationship( 1 ), rl2 );
  }

  public static class TestSqlGenerator extends SqlGenerator {

    @Override
    public Path getShortestPathBetween( LogicalModel model, List<LogicalTable> tables ) {
      return super.getShortestPathBetween( model, tables );
    }

    @Override
    public String getJoin( LogicalModel LogicalModel, LogicalRelationship relation,
        Map<LogicalTable, String> tableAliases, Map<String, Object> parameters, boolean genAsPreparedStatement,
        DatabaseMeta databaseMeta, String locale ) throws PentahoMetadataException {
      return super.getJoin( LogicalModel, relation, tableAliases, parameters, genAsPreparedStatement, databaseMeta,
          locale );
    }

    @Override
    public <T> List<List<T>> getSubsetsOfSize( int size, List<T> list ) {
      return super.getSubsetsOfSize( size, list );
    }

    @Override
    public String generateUniqueAlias( String alias, int maxLength, Collection<String> existingAliases ) {
      return super.generateUniqueAlias( alias, maxLength, existingAliases );
    }
  }

  @Test
  public void testThreeSibJoin() throws Exception {

    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    final LogicalTable bt6 = new LogicalTable();
    bt6.setId( "bt6" ); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setToTable( bt3 );
    rl3.setFromTable( bt5 );

    final LogicalRelationship rl4 = new LogicalRelationship();
    rl4.setToTable( bt5 );
    rl4.setFromTable( bt6 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt5 );
    model.getLogicalTables().add( bt6 );
    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt6 );
    Path path = sqlGenerator.getShortestPathBetween( model, tbls );

    // this should return a path, but it is returning null instead

    // MB - Fixed - should be expects, is not is, expects
    assertEquals( 4, path.size() );

    // Note - path is unordered - this test was invalid. Joins will
    // be ordered by join-order keys. This should simply be relationships
    // that will get us from point to point.
    // Now, test that all joins are returned, and no dupes are found
    ArrayList<LogicalRelationship> rtns = new ArrayList<LogicalRelationship>( 4 );
    rtns.add( rl1 );
    rtns.add( rl2 );
    rtns.add( rl3 );
    rtns.add( rl4 );
    for ( int i = 0; i < path.size(); i++ ) {
      LogicalRelationship rel = path.getRelationship( i );
      int idx = rtns.indexOf( rel );
      if ( idx < 0 ) {
        fail( "Relationship returned twice - " + rel );
      } else {
        rtns.remove( idx );
      }
    }

  }

  @Test
  public void testCircularJoin() throws Exception {

    final LogicalModel model = new LogicalModel();
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    final LogicalTable bt6 = new LogicalTable();
    bt6.setId( "bt6" ); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setToTable( bt2 );
    rl3.setFromTable( bt4 );

    final LogicalRelationship rl4 = new LogicalRelationship();
    rl4.setToTable( bt3 );
    rl4.setFromTable( bt5 );

    final LogicalRelationship rl5 = new LogicalRelationship();
    rl5.setToTable( bt4 );
    rl5.setFromTable( bt5 );

    final LogicalRelationship rl6 = new LogicalRelationship();
    rl6.setToTable( bt5 );
    rl6.setFromTable( bt6 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );
    model.getLogicalTables().add( bt6 );
    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    model.getLogicalRelationships().add( rl5 );
    model.getLogicalRelationships().add( rl6 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt6 );
    Path path = sqlGenerator.getShortestPathBetween( model, tbls );

    // As of 9/23, it looks like this:
    // [bt5-bt4], [bt6-bt5], [bt4-bt2], [bt1-bt2]
    //
    // Real problem is that there's no hard-and-fast rule
    // about what should happen in a graph cycle. when there
    // is nothing compelling (like a higher-score item in the
    // graph. When you look at the "found paths" when there
    // is a cycle, a bug is evident - there is no consideration
    // for the path 1->2, 2->3, 3->5, 5->6

    assertEquals( 4, path.size() );

    // Note - path is unordered - the old test was invalid. Joins will
    // be ordered by join-order keys. This should simply be relationships
    // that will get us from point to point.
    // Now, test that all joins are returned, and no dupes are found
    ArrayList<LogicalRelationship> rtns = new ArrayList<LogicalRelationship>( 4 );
    rtns.add( rl1 );
    rtns.add( rl3 );
    rtns.add( rl5 );
    rtns.add( rl6 );
    for ( int i = 0; i < path.size(); i++ ) {
      LogicalRelationship rel = path.getRelationship( i );
      int idx = rtns.indexOf( rel );
      if ( idx < 0 ) {
        fail( "Relationship returned twice - " + rel );
      } else {
        rtns.remove( idx );
      }
    }

  }

  @Test
  public void testGetShortestPathBetween2() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setToTable( bt4 );
    rl3.setFromTable( bt5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt2 );
    Path path = sqlGenerator.getShortestPathBetween( model, tbls );

    assertEquals( path.size(), 1 );
    assertEquals( path.getRelationship( 0 ), rl1 );
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add( bt1 );
    tbls2.add( bt3 );
    path = sqlGenerator.getShortestPathBetween( model, tbls2 );
    assertEquals( path.size(), 2 );
    assertEquals( path.getRelationship( 0 ), rl1 );
    assertEquals( path.getRelationship( 1 ), rl2 );
  }

  @Test
  public void testGetShortestPathBetween3() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setToTable( bt1 );
    rl3.setFromTable( bt3 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt2 );
    Path path = sqlGenerator.getShortestPathBetween( model, tbls );

    assertEquals( path.size(), 1 );
    assertEquals( path.getRelationship( 0 ), rl1 );
    List<LogicalTable> tbls2 = new ArrayList<LogicalTable>();
    tbls2.add( bt1 );
    tbls2.add( bt3 );
    path = sqlGenerator.getShortestPathBetween( model, tbls2 );
    assertEquals( path.size(), 1 );
    assertEquals( path.getRelationship( 0 ), rl3 );
  }

  @Test
  public void testComplexJoinMQL() throws Exception {

    String locale = "en_US"; //$NON-NLS-1$

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );
    rl1.setComplexJoin( "[bt1.bc1] = [bt2.bc2]" ); //$NON-NLS-1$
    rl1.setComplex( true );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setToTable( bt2 );
    rl2.setFromTable( bt3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setToTable( bt1 );
    rl3.setFromTable( bt3 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );

    TestSqlGenerator sqlGenerator = new TestSqlGenerator();
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    String joinSQL = sqlGenerator.getJoin( model, rl1, null, null, false, databaseMeta, locale );

    TestHelper.assertEqualsIgnoreWhitespaces( joinSQL, " bt1.pc1  =  bt2.pc2 " ); //$NON-NLS-1$
  }

  @Test
  public void testGroupBySQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      bc1.setProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, AggregationType.SUM );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );
      query.getSelections().add( new Selection( null, bce2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1] > 25" ) ); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "GROUP BY \n" //$NON-NLS-1$
          + "          bt2.pc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n" //$NON-NLS-1$
          + "HAVING \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          mquery.getQuery() );
      Map map = mquery.getMap();
      Assert.assertNotNull( map );
      assertEquals( map.size(), 3 );
      assertEquals( map.get( "COL0" ), "bc1" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( map.get( "COL1" ), "bc2" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( map.get( "COL2" ), "bce2" ); //$NON-NLS-1$ //$NON-NLS-2$

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS bc1\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS bc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS bce2\n"//$NON-NLS-1$        
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "GROUP BY \n" //$NON-NLS-1$
          + "          bt2.pc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n" //$NON-NLS-1$
          + "HAVING \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$        
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          mquery.getDisplayQuery() );

      MemoryMetaData mmd = new MemoryMetaData( new Object[][] { { "COL0", "COL1" } }, //$NON-NLS-1$  //$NON-NLS-2$
          null );

      QueryModelMetaData emd = (QueryModelMetaData) mquery.generateMetadata( mmd );

      assertEquals( "pc1", emd.getAttribute( 0, 0, SqlPhysicalColumn.TARGET_COLUMN ).toString() ); //$NON-NLS-1$  //$NON-NLS-2$

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testOrderByQuotedSQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      bc1.setProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, AggregationType.SUM );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields( true );
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );
      query.getSelections().add( new Selection( null, bce2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1] > 25" ) ); //$NON-NLS-1$

      query.getOrders().add( new Order( new Selection( null, bc1, null ), Type.ASC ) );

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );
      // TestHelper.printOutJava(mquery.getQuery());

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" + "          SUM(\"bt1\".\"pc1\") AS \"COL0\"\n"
          + "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + "         , \"bt2\".\"pc2\"  * 2 AS \"COL2\"\n" + "FROM \n"
          + "          \"pt1\" \"bt1\"\n" + "         ,\"pt2\" \"bt2\"\n" + "WHERE \n"
          + "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n" + "GROUP BY \n" + "          \"bt2\".\"pc2\"\n"
          + "         , \"bt2\".\"pc2\"  * 2\n" + "HAVING \n" + "          (\n"
          + "              SUM(\"bt1\".\"pc1\")  > 25\n" + "          )\n" + "ORDER BY \n" + "          \"COL0\"\n",
          mquery.getQuery() );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testEscapeQuotes() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields( true );
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "CONTAINS([bt1.bc1];\"a'b\")" ) ); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          \"bt1\".\"pc1\" AS \"COL0\"\n"
          + "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + "FROM \n" + "          \"pt1\" \"bt1\"\n"
          + "         ,\"pt2\" \"bt2\"\n" + "WHERE \n" + "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n"
          + "      AND \n" + "        (\n" + "          (\n"
          + "              \"bt1\".\"pc1\"  LIKE '%' || 'a''b' || '%'\n" + "          )\n" + "        )\n", mquery
          .getQuery() );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testFirstConstraintNOT() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields( true );
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] > 1" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] < 1" ) ); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );

      // TestHelper.printOutJava(mquery.getQuery());

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          \"bt1\".\"pc1\" AS \"COL0\"\n"
          + "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + "FROM \n" + "          \"pt1\" \"bt1\"\n"
          + "         ,\"pt2\" \"bt2\"\n" + "WHERE \n" + "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n"
          + "      AND \n" + "        (\n" + "      NOT (\n" + "              \"bt1\".\"pc1\"  > 1\n" + "          )\n"
          + "      AND NOT (\n" + "              \"bt1\".\"pc1\"  < 1\n" + "          )\n" + "        )\n", mquery
          .getQuery() );

      query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.OR_NOT, "[bt1.bc1] > 1" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.OR_NOT, "[bt1.bc1] < 1" ) ); //$NON-NLS-1$

      mquery = generator.generateSql( query, "en_US", null, databaseMeta );

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          \"bt1\".\"pc1\" AS \"COL0\"\n"
          + "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + "FROM \n" + "          \"pt1\" \"bt1\"\n"
          + "         ,\"pt2\" \"bt2\"\n" + "WHERE \n" + "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n"
          + "      AND \n" + "        (\n" + "      NOT (\n" + "              \"bt1\".\"pc1\"  > 1\n" + "          )\n"
          + "      OR NOT (\n" + "              \"bt1\".\"pc1\"  < 1\n" + "          )\n" + "        )\n", mquery
          .getQuery() );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testParameterSqlGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query( null, model );

      query.getParameters().add( new Parameter( "test1", DataType.BOOLEAN, true ) );
      query.getParameters().add( new Parameter( "test2", DataType.NUMERIC, 1.2 ) );
      query.getParameters().add( new Parameter( "test3", DataType.STRING, "value" ) );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "[param:test1]" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1] > [param:test2]" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "[param:test3] = [bt2.bc2]" ) ); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta, null, false );
      // TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "             1=1\n" + "          )\n" + "      AND (\n" + "              bt1.pc1  > 1.2\n"
          + "          )\n" + "      AND (\n" + "             'value' =  bt2.pc2 \n" + "          )\n" + "        )\n",
          mquery.getQuery() );

      Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put( "test1", false );
      parameters.put( "test2", 2.1 );
      parameters.put( "test3", "eulav" );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, false );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "             1=0\n" + "          )\n" + "      AND (\n" + "              bt1.pc1  > 2.1\n"
          + "          )\n" + "      AND (\n" + "             'eulav' =  bt2.pc2 \n" + "          )\n" + "        )\n",
          mquery.getQuery() );

      Assert.assertNull( mquery.getParamList() );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, true );
      // TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n" + "             ?\n"
          + "          )\n" + "      AND (\n" + "              bt1.pc1  > ?\n" + "          )\n" + "      AND (\n"
          + "             ? =  bt2.pc2 \n" + "          )\n" + "        )\n", mquery.getQuery() );
      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 3, mquery.getParamList().size() );
      assertEquals( "test1", mquery.getParamList().get( 0 ) );
      assertEquals( "test2", mquery.getParamList().get( 1 ) );
      assertEquals( "test3", mquery.getParamList().get( 2 ) );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testMultiParameterSqlGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query( null, model );

      query.getParameters().add( new Parameter( "test1", DataType.NUMERIC, new Double[] { 1.2, 1.3 } ) );
      query.getParameters().add( new Parameter( "test2", DataType.STRING, new String[] { "value", "value2" } ) );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "EQUALS([bt1.bc1];[param:test1])" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "EQUALS([bt2.bc2];[param:test2])" ) ); //$NON-NLS-1$

      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta, null, false );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1  IN ( 1.2 , 1.3 ) \n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  IN ( 'value' , 'value2' ) \n" + "          )\n" + "        )\n", mquery.getQuery() );

      Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put( "test1", "value1" );
      parameters.put( "test2", 2.1 );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, false );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1  = 'value1'\n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  = 2.1\n" + "          )\n" + "        )\n", mquery.getQuery() );

      Assert.assertNull( mquery.getParamList() );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, true );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1  = ?\n" + "          )\n" + "      AND (\n" + "              bt2.pc2  = ?\n"
          + "          )\n" + "        )\n", mquery.getQuery() );
      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 2, mquery.getParamList().size() );
      assertEquals( "test1", mquery.getParamList().get( 0 ) );
      assertEquals( "test2", mquery.getParamList().get( 1 ) );

      parameters = new HashMap<String, Object>();
      parameters.put( "test1", new String[] { "value1", "value2" } );
      parameters.put( "test2", new Double[] { 2.1, 2.2, 2.3 } );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, true );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1  IN ( ?, ? ) \n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  IN ( ?, ?, ? ) \n" + "          )\n" + "        )\n", mquery.getQuery() );
      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 2, mquery.getParamList().size() );
      assertEquals( "test1", mquery.getParamList().get( 0 ) );
      assertEquals( "test2", mquery.getParamList().get( 1 ) );

      // test that a single-value array translates into an '=' operation, not an IN
      parameters = new HashMap<String, Object>();
      parameters.put( "test1", new String[] { "value1" } );
      parameters.put( "test2", new Double[] { 2.1, 2.2, 2.3 } );

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, true );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1 = ? \n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2 IN ( ?, ?, ? ) \n" + "          )\n" + "        )\n", mquery.getQuery() );
      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 2, mquery.getParamList().size() );
      assertEquals( "test1", mquery.getParamList().get( 0 ) );
      assertEquals( "test2", mquery.getParamList().get( 1 ) );

      // Test multiple params in a single constraint
      query.getConstraints()
          .add(
              new Constraint( CombinationType.AND,
                  "AND(EQUALS([bt1.bc1];[param:test1]);EQUALS([bt2.bc2];[param:test2]))" ) ); //$NON-NLS-1$

      mquery = generator.generateSql( query, "en_US", null, databaseMeta, parameters, true );
      // TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt1.pc1  = ?\n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  IN ( ?, ?, ? ) \n" + "          )\n" + "      AND (\n"
          + "              bt1.pc1  = ? AND  bt2.pc2  IN ( ?, ?, ? ) \n" + "          )\n" + "        )\n", mquery
          .getQuery() );

      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 4, mquery.getParamList().size() );
      assertEquals( "test1", mquery.getParamList().get( 0 ) );
      assertEquals( "test2", mquery.getParamList().get( 1 ) );
      assertEquals( "test1", mquery.getParamList().get( 2 ) );
      assertEquals( "test2", mquery.getParamList().get( 3 ) );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testMultiParameterSqlGeneration_IN_operator() throws Exception {

    LogicalModel model = TestHelper.buildDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
    LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
    LogicalColumn bc3 = model.findLogicalColumn( "bc3" );
    LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query query = new Query( null, model );

    query.getParameters().add( new Parameter( "test1", DataType.NUMERIC, new Double[] { 1.2, 1.3 } ) );
    query.getParameters().add( new Parameter( "test2", DataType.STRING, new String[] { "value", "value2" } ) ); //$NON-NLS-1$ //$NON-NLS-2$
    query.getParameters().add( new Parameter( "test3", DataType.STRING, "single" ) ); //$NON-NLS-1$

    query.getSelections().add( new Selection( null, bc1, null ) );
    query.getSelections().add( new Selection( null, bc2, null ) );
    query.getSelections().add( new Selection( null, bc3, null ) );

    query.getConstraints().add( new Constraint( CombinationType.AND, "IN([bt1.bc1];[param:test1])" ) ); //$NON-NLS-1$
    query.getConstraints().add( new Constraint( CombinationType.AND, "IN([bt2.bc2];[param:test2])" ) ); //$NON-NLS-1$
    query.getConstraints().add( new Constraint( CombinationType.AND, "IN([bt3.bc3];[param:test3])" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();

    MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta, null, false );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
        + "         ,bt2.pc2 AS COL1\n" + "         ,bt3.pc3 AS COL2\n" + "FROM \n" + "          pt1 bt1\n"
        + "         ,pt2 bt2\n" + "         ,pt3 bt3\n" + "WHERE \n" + "          ( bt1.pc1 = bt2.pc2 )\n"
        + "      AND ( bt2.pc2 = bt3.pc3 )\n" + "      AND \n" + "        (\n" + "          (\n"
        + "              bt1.pc1  IN ( 1.2 , 1.3 ) \n" + "          )\n" + "      AND (\n"
        + "              bt2.pc2  IN ( 'value' , 'value2' ) \n" + "          )\n" + "      AND (\n"
        + "              bt3.pc3  IN ( 'single' ) \n" + "          )\n" + "        )\n", mquery.getQuery() );
  }

  @Test
  public void testAggListSQLGeneration() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      bc1.setAggregationType( AggregationType.SUM );
      List<AggregationType> aggregationList = new ArrayList<AggregationType>();
      aggregationList.add( AggregationType.SUM );
      aggregationList.add( AggregationType.COUNT );
      aggregationList.add( AggregationType.NONE );
      bc1.setAggregationList( aggregationList );

      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query myTest = new Query( null, model ); //$NON-NLS-1$
      myTest.getSelections().add( new Selection( null, bc1, null ) );
      myTest.getSelections().add( new Selection( null, bc2, null ) );
      myTest.getSelections().add( new Selection( null, bce2, null ) );
      myTest.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1] > 25" ) ); //$NON-NLS-1$

      // databaseMeta, "en_US"
      SqlGenerator generator = new SqlGenerator();

      MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "GROUP BY \n" //$NON-NLS-1$
          + "          bt2.pc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n" //$NON-NLS-1$
          + "HAVING \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          query.getQuery() );
      Map map = query.getMap();
      Assert.assertNotNull( map );
      assertEquals( map.size(), 3 );
      assertEquals( map.get( "COL0" ), "bc1" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( map.get( "COL1" ), "bc2" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( map.get( "COL2" ), "bce2" ); //$NON-NLS-1$ //$NON-NLS-2$

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" //$NON-NLS-1$
          + "          SUM(bt1.pc1) AS bc1\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS bc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS bce2\n"//$NON-NLS-1$        
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "GROUP BY \n" //$NON-NLS-1$
          + "          bt2.pc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n" //$NON-NLS-1$
          + "HAVING \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$        
          + "              SUM(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          query.getDisplayQuery() );

      MemoryMetaData mmd = new MemoryMetaData( new Object[][] { { "COL0", "COL1" } }, //$NON-NLS-1$  //$NON-NLS-2$
          null );

      QueryModelMetaData emd = (QueryModelMetaData) query.generateMetadata( mmd );

      assertEquals( "pc1", emd.getAttribute( 0, 0, SqlPhysicalColumn.TARGET_COLUMN ).toString() ); //$NON-NLS-1$  //$NON-NLS-2$

      // select none aggregate

      Query myTest2 = new Query( null, model ); //$NON-NLS-1$
      myTest2.getSelections().add( new Selection( null, bc1, AggregationType.NONE ) );
      myTest2.getSelections().add( new Selection( null, bc2, null ) );
      myTest2.getSelections().add( new Selection( null, bce2, null ) );

      myTest2.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1.none] > 25" ) ); //$NON-NLS-1$

      MappedQuery query2 = generator.generateSql( myTest2, "en_US", null, databaseMeta );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
          + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "AND       ((\n" //$NON-NLS-1$
          + "              bt1.pc1 > 25\n" //$NON-NLS-1$
          + "          ))\n", //$NON-NLS-1$
          query2.getQuery() );

      // select count aggregate
      Query myTest3 = new Query( null, model ); //$NON-NLS-1$
      myTest3.getSelections().add( new Selection( null, bc1, AggregationType.COUNT ) );
      myTest3.getSelections().add( new Selection( null, bc2, null ) );
      myTest3.getSelections().add( new Selection( null, bce2, null ) );

      myTest3.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1.count] > 25" ) ); //$NON-NLS-1$

      MappedQuery query3 = generator.generateSql( myTest3, "en_US", null, databaseMeta );
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n" //$NON-NLS-1$
          + "          COUNT(bt1.pc1) AS COL0\n" //$NON-NLS-1$
          + "         ,bt2.pc2 AS COL1\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2 AS COL2\n"//$NON-NLS-1$        
          + "FROM \n" //$NON-NLS-1$
          + "          pt1 bt1\n" //$NON-NLS-1$
          + "         ,pt2 bt2\n" //$NON-NLS-1$
          + "WHERE \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$
          + "             bt1.pc1 = bt2.pc2\n" //$NON-NLS-1$
          + "          )\n" //$NON-NLS-1$
          + "GROUP BY \n" //$NON-NLS-1$
          + "          bt2.pc2\n" //$NON-NLS-1$
          + "         , bt2.pc2  * 2\n" //$NON-NLS-1$
          + "HAVING \n" //$NON-NLS-1$
          + "          (\n" //$NON-NLS-1$        
          + "              COUNT(bt1.pc1)  > 25\n" //$NON-NLS-1$
          + "          )\n", //$NON-NLS-1$
          query3.getQuery() );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testGetShortestPathBetween4() throws Exception {
    // This test makes sure that inner-join join-orders are respected

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );
    rl1.setJoinOrderKey( "A" );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );
    rl2.setJoinOrderKey( "C" );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );
    rl3.setJoinOrderKey( "B" );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );
    rl4.setJoinOrderKey( "D" );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n" + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt1.pc1 = bt2.pc2\n" + "          )\n", query.getQuery() ); //$NON-NLS-1$
  }

  @Test
  public void testGetShortestPathBetween5() throws Exception {
    // This test makes sure that inner-join join-orders are respected

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    String actualQuery = query.getQuery();
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n" + "             bt1.pc1 = bt2.pc2\n" + "          )\n"
        + "      AND (\n"
        + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n", actualQuery ); //$NON-NLS-1$
  }

  @Test
  public void testClassicGetShortestPathBetween() throws Exception {

    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "CLASSIC" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n" + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt1.pc1 = bt2.pc2\n" + "          )\n", query.getQuery() ); //$NON-NLS-1$
  }

  @Test
  public void testClassicGetShortestPathBetweenNoPathPossible() throws Exception {

    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "CLASSIC" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt3 );
    rl2.setFromColumn( bc3 );
    rl2.setToTable( bt4 );
    rl2.setToColumn( bc4 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt4 );
    rl3.setFromColumn( bc4 );
    rl3.setToTable( bt5 );
    rl3.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    SqlGenerator generator = new SqlGenerator();

    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt5 );

    // There is no path between table 1 and table 5...
    Path path = generator.getShortestPathBetween( model, tbls );
    Assert.assertNull( path );
  }

  @Test
  public void testNewAlgorithmGetShortestPathBetweenNoPathPossible() throws Exception {

    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "SHORTEST" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt3 );
    rl2.setFromColumn( bc3 );
    rl2.setToTable( bt4 );
    rl2.setToColumn( bc4 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt4 );
    rl3.setFromColumn( bc4 );
    rl3.setToTable( bt5 );
    rl3.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    SqlGenerator generator = new SqlGenerator();

    List<LogicalTable> tbls = new ArrayList<LogicalTable>();
    tbls.add( bt1 );
    tbls.add( bt5 );

    // There is no path between table 1 and table 5...
    Path path = generator.getShortestPathBetween( model, tbls );
    Assert.assertNull( path );
  }

  @Test
  public void testAnyRelevantGetShortestPathBetween() throws Exception {
    // Note - this returns all possible joins between the tables. In this
    // example, the following two paths are possible:
    // [1-2, 2-5] and [1-3, 3-5]
    // This path provides both sets of joins, ignoring the spurious
    // relationship [4-5] because you can't get from table 1 trough to
    // table 4.
    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "ANY_RELEVANT" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt3 );
    rl1.setToColumn( bc3 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt1 );
    rl2.setFromColumn( bc1 );
    rl2.setToTable( bt2 );
    rl2.setToColumn( bc2 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt2 );
    rl3.setFromColumn( bc2 );
    rl3.setToTable( bt5 );
    rl3.setToColumn( bc5 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    final LogicalRelationship rl5 = new LogicalRelationship();

    rl5.setFromTable( bt3 );
    rl5.setFromColumn( bc3 );
    rl5.setToTable( bt5 );
    rl5.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    model.getLogicalRelationships().add( rl5 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc5, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    String queryString = query.getQuery();
    // System.out.println("*********");
    // System.out.println(queryString);
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT  " + "          bt1.pc1 AS COL0 "
        + "         ,bt5.pc5 AS COL1 " + "FROM  " + "          pt1 bt1 " + "         ,pt2 bt2 " + "         ,pt3 bt3 "
        + "         ,pt5 bt5 " + "WHERE  " + "          ( bt1.pc1 = bt3.pc3 ) " + "      AND ( bt1.pc1 = bt2.pc2 ) "
        + "      AND ( bt2.pc2 = bt5.pc5 ) " + "      AND ( bt3.pc3 = bt5.pc5 ) ", queryString ); //$NON-NLS-1$
  }

  @Test
  public void testfirstShortGetShortestPathBetween() throws Exception {
    // This one is testing FIRST_SHORT
    // It can logically choose [1-2, 2-5] or [1-3, 3-5]
    // Note that the relative size of '3' is 100 - this algorithm
    // doesn't consider this relative size, - it stops on the first one it finds
    // based on hops.

    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "FIRST_SHORT" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 100 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt5 );
    rl2.setToColumn( bc5 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt1 );
    rl3.setFromColumn( bc1 );
    rl3.setToTable( bt3 );
    rl3.setToColumn( bc3 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt3 );
    rl4.setFromColumn( bc3 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc5, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    String queryString = query.getQuery();
    // System.out.println("*************");
    // System.out.println(queryString);

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT  " + "          bt1.pc1 AS COL0 "
        + "         ,bt5.pc5 AS COL1 " + "FROM  " + "          pt1 bt1 " + "         ,pt3 bt3 " + "         ,pt5 bt5 "
        + "WHERE  " + "          ( bt1.pc1 = bt3.pc3 ) " + "      AND ( bt3.pc3 = bt5.pc5 ) ", queryString ); //$NON-NLS-1$
  }

  @Test
  public void testlowestScoreGetShortestPathBetween() throws Exception {
    // Note - the relative size of table 2 is huge (100) compared with the others.
    // so - path should favor [1->3, 3->5] and avoid joins through table 2.
    final LogicalModel model = new LogicalModel();
    model.setProperty( "path_build_method", "LOWEST_SCORE" );
    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 100 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt5 );
    rl2.setToColumn( bc5 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt1 );
    rl3.setFromColumn( bc1 );
    rl3.setToTable( bt3 );
    rl3.setToColumn( bc3 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt3 );
    rl4.setFromColumn( bc3 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc5, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    String queryString = query.getQuery();
    // System.out.println("**************");
    // System.out.println(queryString);
    // Should be 1->3 3->5 ... table 2 is relatively huge
    TestHelper.assertEqualsIgnoreWhitespaces( " SELECT DISTINCT " + " bt1.pc1 AS COL0 " + " ,bt5.pc5 AS COL1 "
        + " FROM  " + " pt1 bt1 " + " ,pt3 bt3 " + " ,pt5 bt5 " + " WHERE  " + " ( bt1.pc1 = bt3.pc3 ) "
        + "  AND ( bt3.pc3 = bt5.pc5 ) ", queryString ); //$NON-NLS-1$
  }

  public static String subsetsToString( List subsets ) {
    StringBuffer sb = new StringBuffer();
    for ( int i = 0; i < subsets.size(); i++ ) {
      List subset = (List) subsets.get( i );
      if ( i != 0 ) {
        sb.append( "," ); //$NON-NLS-1$
      }
      sb.append( "[" ); //$NON-NLS-1$
      for ( int j = 0; j < subset.size(); j++ ) {
        if ( j != 0 ) {
          sb.append( "," ); //$NON-NLS-1$
        }
        sb.append( subset.get( j ) );
      }
      sb.append( "]" ); //$NON-NLS-1$
    }
    return sb.toString();
  }

  @Test
  public void testSubsets() {
    TestSqlGenerator myTest = new TestSqlGenerator();
    ArrayList<String> testList = new ArrayList<String>();
    testList.add( "A" ); //$NON-NLS-1$
    testList.add( "B" ); //$NON-NLS-1$
    testList.add( "C" ); //$NON-NLS-1$
    testList.add( "D" ); //$NON-NLS-1$
    testList.add( "E" ); //$NON-NLS-1$

    List subsets = myTest.getSubsetsOfSize( 0, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 0, subsets.size() );

    subsets = myTest.getSubsetsOfSize( 1, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 5, subsets.size() );
    String subsetStr = subsetsToString( subsets );
    assertEquals( "[A],[B],[C],[D],[E]", subsetStr ); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize( 2, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 10, subsets.size() );
    subsetStr = subsetsToString( subsets );
    assertEquals( "[A,B],[A,C],[A,D],[A,E],[B,C],[B,D],[B,E],[C,D],[C,E],[D,E]", subsetStr ); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize( 3, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 10, subsets.size() );
    subsetStr = subsetsToString( subsets );
    assertEquals( "[A,B,C],[A,B,D],[A,B,E],[A,C,D],[A,C,E],[A,D,E],[B,C,D],[B,C,E],[B,D,E],[C,D,E]", subsetStr ); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize( 4, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 5, subsets.size() );
    subsetStr = subsetsToString( subsets );
    assertEquals( "[A,B,C,D],[A,B,C,E],[A,B,D,E],[A,C,D,E],[B,C,D,E]", subsetStr ); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize( 5, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 1, subsets.size() );
    subsetStr = subsetsToString( subsets );
    assertEquals( "[A,B,C,D,E]", subsetStr ); //$NON-NLS-1$

    subsets = myTest.getSubsetsOfSize( 6, testList );
    Assert.assertNotNull( subsets );
    assertEquals( 0, subsets.size() );
  }

  @Test
  public void testLogicalColumnFormulaUsingTwoBT() throws Exception {

    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalColumn bce2 = new LogicalColumn();
    bce2.setId( "bce2" ); //$NON-NLS-1$
    bce2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bce2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "[cat_01.bc2] * [cat_01.bc1]" ); //$NON-NLS-1$
    bce2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bce2 );
    mainCat.addLogicalColumn( bce2 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bce2, null ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt2.pc2 * bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )", query
                .getQuery() ); //$NON-NLS-1$

  }

  /**
   * Scenario 1: Two Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario1() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1a: Two Tables are outer joined with a constraint
   */
  @Test
  public void testOuterJoinScenario1a() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[cat_01.bc2] > 1" ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1b: Two Tables are outer joined with an aggregate
   */
  @Test
  public void testOuterJoinScenario1b() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setAggregationType( AggregationType.SUM );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1c: Two Tables are outer joined with an aggregate constraint
   */
  @Test
  public void testOuterJoinScenario1c() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setAggregationType( AggregationType.SUM );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[cat_01.bc2] > 1" ) );
    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT bt1.pc1 AS COL0 ,SUM(bt2.pc2) AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM(bt2.pc2) > 1 )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 1d: Two Tables are outer joined both with constraints
   */
  @Test
  public void testOuterJoinScenario1d() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );
    model.getLogicalTables().add( bt1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );
    model.getLogicalTables().add( bt2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[bt1.bc1] > 1" ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[bt2.bc2] > 1" ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) ) WHERE (( bt1.pc1 > 1 ))", //$NON-NLS-1$
            query.getQuery() );
  }

  /**
   * Scenario 1d: Two Tables are outer joined both with constraints This scenario uses nots, verifying NOT syntax
   */
  @Test
  public void testOuterJoinScenario1dNOT() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );
    model.getLogicalTables().add( bt1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );
    model.getLogicalTables().add( bt2 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] > 1" ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] < 1" ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[bt2.bc2] > 1" ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 ,bt2.pc2 AS COL1 FROM pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON ( bt1.pc1 = bt2.pc2 AND ( bt2.pc2 > 1 ) ) WHERE ( NOT ( bt1.pc1 > 1 ) AND NOT (bt1.pc1 < 1) )", //$NON-NLS-1$
            query.getQuery() );
  }

  /**
   * Scenario 2: Two Joined Tables are outer joined to a single table
   */
  @Test
  public void testOuterJoinScenario2() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._1_N );
    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0,bt2.pc2 AS COL1,bt3.pc3 AS COL2 FROM pt3 bt3 JOIN(pt1 bt1 LEFT OUTER JOIN pt2 bt2 ON(bt1.pc1 = bt2.pc2))ON(bt2.pc2 = bt3.pc3)",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 2a: Two Joined Tables are outer joined to two other tables
   */
  @Test
  public void testOuterJoinScenario2a() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "t4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    mainCat.addLogicalColumn( bc4 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._1_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._0_N );
    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setRelationshipType( RelationshipType._1_N );
    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    model.getLogicalRelationships().add( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0,bt2.k AS COL1,bt3.k AS COL2,bt4.k AS COL3 FROM t1 bt1 JOIN(t4 bt4 JOIN(t2 bt2 LEFT OUTER JOIN t3 bt3 ON(bt2.k = bt3.k))ON(bt3.k = bt4.k))ON(bt1.k = bt2.k)",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 3: Three Tables are outer joined
   */
  @Test
  public void testOuterJoinScenario3() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._0_N );
    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t1 bt1 LEFT OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t3 bt3 ON ( bt2.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 4: Two outer joins on a single table
   */
  @Test
  public void testOuterJoinScenario4() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._0_N );
    rl2.setFromTable( bt1 );
    rl2.setFromColumn( bc1 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 RIGHT OUTER JOIN ( t1 bt1 LEFT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  @Test
  public void testOuterJoinScenario5a() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._N_0 );
    rl1.setJoinOrderKey( "A" );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._N_0 );
    rl2.setJoinOrderKey( "B" );
    rl2.setFromTable( bt1 );
    rl2.setFromColumn( bc1 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t3 bt3 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t2 bt2 ON ( bt1.k = bt2.k ) ) ON ( bt1.k = bt3.k )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 5: Two outer joins in the opposite direction
   */
  @Test
  public void testOuterJoinScenario5b() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._N_0 );
    rl1.setJoinOrderKey( "B" );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._N_0 );
    rl2.setJoinOrderKey( "A" );
    rl2.setFromTable( bt1 );
    rl2.setFromColumn( bc1 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 FROM t2 bt2 LEFT OUTER JOIN ( t1 bt1 RIGHT OUTER JOIN t3 bt3 ON ( bt1.k = bt3.k ) ) ON ( bt1.k = bt2.k )",
            query.getQuery() ); //$NON-NLS-1$
  }

  /**
   * Scenario 6: 4 tables outer joined
   * 
   * NOTE: This does not work on MYSQL, because FULL OUTER JOIN is not supported.
   */
  @Test
  public void testOuterJoinScenario6() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    mainCat.addLogicalColumn( bc1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "t2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "t3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    mainCat.addLogicalColumn( bc3 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "t4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "k" ); //$NON-NLS-1$
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    mainCat.addLogicalColumn( bc4 );

    final LogicalRelationship rl1 = new LogicalRelationship();
    rl1.setRelationshipType( RelationshipType._0_0 );
    rl1.setJoinOrderKey( "A" );
    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    final LogicalRelationship rl2 = new LogicalRelationship();
    rl2.setRelationshipType( RelationshipType._0_0 );
    rl2.setJoinOrderKey( "B" );
    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    model.getLogicalRelationships().add( rl2 );

    final LogicalRelationship rl3 = new LogicalRelationship();
    rl3.setRelationshipType( RelationshipType._0_N );
    rl3.setJoinOrderKey( "A" );
    rl3.setFromTable( bt2 );
    rl3.setFromColumn( bc2 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    model.getLogicalRelationships().add( rl3 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );
    myTest.getSelections().add( new Selection( null, bc3, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.k AS COL0 ,bt2.k AS COL1 ,bt3.k AS COL2 ,bt4.k AS COL3 FROM t3 bt3 FULL OUTER JOIN ( t1 bt1 FULL OUTER JOIN ( t2 bt2 LEFT OUTER JOIN t4 bt4 ON ( bt2.k = bt4.k ) ) ON ( bt1.k = bt2.k ) ) ON ( bt2.k = bt3.k )",
            query.getQuery() ); //$NON-NLS-1$
  }

  @Test
  public void testGenerateUniqueAlias() {
    List<String> existingAliases = new ArrayList<String>();
    existingAliases.add( "test" );
    TestSqlGenerator generator = new TestSqlGenerator();
    assertEquals( "tes01", generator.generateUniqueAlias( "test", 5, existingAliases ) );
    assertEquals( "tes01", generator.generateUniqueAlias( "testing", 5, existingAliases ) );
    assertEquals( "test1", generator.generateUniqueAlias( "test1", 5, existingAliases ) );

    existingAliases.add( "tes01" );
    assertEquals( "tes02", generator.generateUniqueAlias( "test", 5, existingAliases ) );
    assertEquals( "tes02", generator.generateUniqueAlias( "testing", 5, existingAliases ) );
    assertEquals( "test1", generator.generateUniqueAlias( "test1", 5, existingAliases ) );

  }

  @Test
  public void testAliasGeneration() throws Exception {
    // Testing alias generation with Join Order Keys
    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "metadata_business_table_very_long_name_1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "metadata_business_table_very_long_name_2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "metadata_business_table_very_long_name_3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "metadata_business_table_very_long_name_4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "metadata_business_table_very_long_name_5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" );
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId( "bc6" ); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "SUM([pc5]*2)" );
    bc6.setAggregationType( AggregationType.SUM );

    // bc6.setAggregationList(list);
    bc6.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc6 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );
    rl1.setComplex( true );
    rl1.setComplexJoin( "[metadata_business_table_very_long_name_1.bc1] = [metadata_business_table_very_long_name_2.bc2]" );
    rl1.setJoinOrderKey( "A" );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );
    rl2.setJoinOrderKey( "D" );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );
    rl3.setJoinOrderKey( "B" );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );
    rl4.setJoinOrderKey( "E" );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    myTest.getConstraints().add(
        new Constraint( CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
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
        + "      AND (\n" + "          (\n" + "             metadata_business_table_very01.pc1 > 25"
        + "          )"
        + "          )", query.getQuery() ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //

    myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    myTest.getSelections().add( new Selection( null, bc6, null ) );

    query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n"
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n" + "FROM \n"
        + "             pt4 metadata_business_table_very01 \n" + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n" + "             (\n"
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" + "GROUP BY \n" + "             metadata_business_table_very01.pc4 \n", query.getQuery() );
  }

  @Test
  public void testAliasGenerationWithoutJoinOrderKeys() throws Exception {
    // Testing alias generation without Join Order Keys
    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "metadata_business_table_very_long_name_1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "metadata_business_table_very_long_name_2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "metadata_business_table_very_long_name_3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "metadata_business_table_very_long_name_4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "metadata_business_table_very_long_name_5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" );
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId( "bc6" ); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "SUM([pc5]*2)" );
    bc6.setAggregationType( AggregationType.SUM );

    // bc6.setAggregationList(list);
    bc6.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc6 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setToTable( bt2 );
    rl1.setComplex( true );
    rl1.setComplexJoin( "[metadata_business_table_very_long_name_1.bc1] = [metadata_business_table_very_long_name_2.bc2]" );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    myTest.getConstraints().add(
        new Constraint( CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          metadata_business_table_very01.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,metadata_business_table_very04.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 metadata_business_table_very01\n" //$NON-NLS-1$
        + "         ,pt2 metadata_business_table_very02\n" //$NON-NLS-1$
        + "         ,pt3 metadata_business_table_very03\n" //$NON-NLS-1$
        + "         ,pt4 metadata_business_table_very04\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2\n"
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very02.pc2 = metadata_business_table_very03.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very03.pc3 = metadata_business_table_very04.pc4\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "          (\n"
        + "             metadata_business_table_very01.pc1 > 25"
        + "          )" + "          )", query.getQuery() ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //

    myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    myTest.getSelections().add( new Selection( null, bc6, null ) );

    query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n"
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n" + "FROM \n"
        + "             pt4 metadata_business_table_very01 \n" + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n" + "             (\n"
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" + "GROUP BY \n" + "             metadata_business_table_very01.pc4 \n", query.getQuery() );
  }

  @Test
  public void testAliasOuterJoinGeneration() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "metadata_business_table_very_long_name_1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "metadata_business_table_very_long_name_2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "metadata_business_table_very_long_name_3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "metadata_business_table_very_long_name_4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "metadata_business_table_very_long_name_5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" );
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId( "bc6" ); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "SUM([pc5]*2)" );
    bc6.setAggregationType( AggregationType.SUM );

    // bc6.setAggregationList(list);
    bc6.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc6 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setRelationshipType( RelationshipType._0_N );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc2, null ) );

    myTest.getConstraints().add(
        new Constraint( CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    // TestHelper.printOutJava(query.getQuery());

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n"
        + "          metadata_business_table_very01.pc1 AS COL0\n"
        + "         ,metadata_business_table_very02.pc2 AS COL1\n" + "\n"
        + "FROM pt1 metadata_business_table_very01 LEFT OUTER JOIN pt2 metadata_business_table_very02\n"
        + "     ON ( metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2 )\n" + "\n" + "WHERE \n"
        + "        (\n" + "          (\n" + "              metadata_business_table_very01.pc1  > 25\n"
        + "          )\n" + "        )\n", query.getQuery() ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //

    myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    myTest.getSelections().add( new Selection( null, bc6, null ) );

    query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n"
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n" + "FROM \n"
        + "             pt4 metadata_business_table_very01 \n" + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n" + "             (\n"
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" + "GROUP BY \n" + "             metadata_business_table_very01.pc4 \n", query.getQuery() );
  }

  @Test
  public void testSumFormula() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "metadata_business_table_very_long_name_1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "metadata_business_table_very_long_name_2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "metadata_business_table_very_long_name_3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "metadata_business_table_very_long_name_4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "metadata_business_table_very_long_name_5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" );
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId( "bc6" ); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "[pc5]*2" );
    bc6.setAggregationType( AggregationType.SUM );

    // bc6.setAggregationList(list);
    bc6.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc6 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );
    rl1.setJoinOrderKey( "A" );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );
    rl2.setJoinOrderKey( "D" );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );
    rl3.setJoinOrderKey( "B" );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );
    rl4.setJoinOrderKey( "E" );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    myTest.getConstraints().add(
        new Constraint( CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
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
        + "      AND (\n" + "          (\n" + "             metadata_business_table_very01.pc1 > 25"
        + "          )"
        + "          )", query.getQuery() ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //

    myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    myTest.getSelections().add( new Selection( null, bc6, null ) );

    query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n"
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n" + "FROM \n"
        + "             pt4 metadata_business_table_very01 \n" + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n" + "             (\n"
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" + "GROUP BY \n" + "             metadata_business_table_very01.pc4 \n", query.getQuery() );
  }

  @Test
  public void testSumFormulaWithoutJoinKeys() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "metadata_business_table_very_long_name_1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "metadata_business_table_very_long_name_2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "metadata_business_table_very_long_name_3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "metadata_business_table_very_long_name_4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "metadata_business_table_very_long_name_5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" );
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalColumn bc6 = new LogicalColumn();
    bc6.setId( "bc6" ); //$NON-NLS-1$
    // bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bc6.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "[pc5]*2" );
    bc6.setAggregationType( AggregationType.SUM );

    // bc6.setAggregationList(list);
    bc6.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc6 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    myTest.getConstraints().add(
        new Constraint( CombinationType.AND, "[metadata_business_table_very_long_name_1.bc1] > 25" ) ); //$NON-NLS-1$

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          metadata_business_table_very01.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,metadata_business_table_very04.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          pt1 metadata_business_table_very01\n" //$NON-NLS-1$
        + "         ,pt2 metadata_business_table_very02\n" //$NON-NLS-1$
        + "         ,pt3 metadata_business_table_very03\n" //$NON-NLS-1$
        + "         ,pt4 metadata_business_table_very04\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n"
        + "             metadata_business_table_very01.pc1 = metadata_business_table_very02.pc2\n"
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very02.pc2 = metadata_business_table_very03.pc3\n" //$NON-NLS-1$
        + "          )\n"
        + "      AND (\n"
        + "             metadata_business_table_very03.pc3 = metadata_business_table_very04.pc4\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "          (\n"
        + "             metadata_business_table_very01.pc1 > 25"
        + "          )" + "          )", query.getQuery() ); //$NON-NLS-1$

    //
    // This tests the physical column aliasing
    //

    myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    myTest.getSelections().add( new Selection( null, bc6, null ) );

    query = generator.generateSql( myTest, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT \n"
        + "             metadata_business_table_very01.pc4 AS COL0 \n"
        + "           , SUM( metadata_business_table_very02.pc5  * 2) AS COL1 \n" + "FROM \n"
        + "             pt4 metadata_business_table_very01 \n" + "            ,pt5 metadata_business_table_very02 \n"
        + "WHERE \n" + "             (\n"
        + "                metadata_business_table_very01.pc4 = metadata_business_table_very02.pc5 "
        + "             )\n" + "GROUP BY \n" + "             metadata_business_table_very01.pc4 \n", query.getQuery() );
  }

  @Test
  public void testInlineTable() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE_TYPE, TargetTableType.INLINE_SQL );
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "select * from mytable" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );
    rl1.setJoinOrderKey( "A" );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );
    rl2.setJoinOrderKey( "D" );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );
    rl3.setJoinOrderKey( "B" );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );
    rl4.setJoinOrderKey( "E" );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          (select * from mytable) bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n" + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt1.pc1 = bt2.pc2\n" + "          )\n", query.getQuery() ); //$NON-NLS-1$
  }

  @Test
  public void testInlineTableWithoutJoinOrderKeys() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE_TYPE, TargetTableType.INLINE_SQL );
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "select * from mytable" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    rl2.setFromColumn( bc2 );
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );

    SqlGenerator generator = new SqlGenerator();
    MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" //$NON-NLS-1$
        + "          bt1.pc1 AS COL0\n" //$NON-NLS-1$
        + "         ,bt4.pc4 AS COL1\n" //$NON-NLS-1$
        + "FROM \n" //$NON-NLS-1$
        + "          (select * from mytable) bt1\n" //$NON-NLS-1$
        + "         ,pt2 bt2\n" //$NON-NLS-1$
        + "         ,pt3 bt3\n" //$NON-NLS-1$
        + "         ,pt4 bt4\n" //$NON-NLS-1$
        + "WHERE \n" //$NON-NLS-1$
        + "          (\n" + "             bt1.pc1 = bt2.pc2\n" + "          )\n"
        + "      AND (\n"
        + "             bt2.pc2 = bt3.pc3\n" //$NON-NLS-1$
        + "          )\n" + "      AND (\n" + "             bt3.pc3 = bt4.pc4\n" //$NON-NLS-1$
        + "          )\n", query.getQuery() ); //$NON-NLS-1$
  }

  @Test
  public void testParameterSqlGenerationWithFunctions() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      LogicalColumn bce2 = model.findLogicalColumn( "bce2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      Query query = new Query( null, model );

      query.getParameters().add( new Parameter( "test3", DataType.STRING, "value" ) );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND, "LIKE([bt2.bc2]; [param:test3])" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "CONTAINS([bt2.bc2]; [param:test3])" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "BEGINSWITH([bt2.bc2]; [param:test3])" ) ); //$NON-NLS-1$
      query.getConstraints().add( new Constraint( CombinationType.AND, "ENDSWITH([bt2.bc2]; [param:test3])" ) ); //$NON-NLS-1$
      SqlGenerator generator = new SqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta, null, true );
      // TestHelper.printOutJava(mquery.getQuery());
      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          bt1.pc1 AS COL0\n"
          + "         ,bt2.pc2 AS COL1\n" + "FROM \n" + "          pt1 bt1\n" + "         ,pt2 bt2\n" + "WHERE \n"
          + "          ( bt1.pc1 = bt2.pc2 )\n" + "      AND \n" + "        (\n" + "          (\n"
          + "              bt2.pc2  LIKE ?\n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  LIKE '%' || ? || '%'\n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  LIKE ? || '%'\n" + "          )\n" + "      AND (\n"
          + "              bt2.pc2  LIKE '%' || ?\n" + "          )\n" + "        )\n", mquery.getQuery() );

      Assert.assertNotNull( mquery.getParamList() );
      assertEquals( 4, mquery.getParamList().size() );
      assertEquals( "test3", mquery.getParamList().get( 0 ) );
      assertEquals( "test3", mquery.getParamList().get( 0 ) );
      assertEquals( "test3", mquery.getParamList().get( 0 ) );
      assertEquals( "test3", mquery.getParamList().get( 0 ) );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testInvalidRelationException() throws Exception {

    final LogicalModel model = new LogicalModel();

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt1" ); //$NON-NLS-1$
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc1.setLogicalTable( bt1 );
    bt1.addLogicalColumn( bc1 );
    bt1.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt2 = new LogicalTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt2" ); //$NON-NLS-1$
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc2.setLogicalTable( bt2 );
    // bt2.addLogicalColumn(bc2);

    bt2.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt3 = new LogicalTable();
    bt3.setId( "bt3" ); //$NON-NLS-1$
    bt3.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt3" ); //$NON-NLS-1$
    final LogicalColumn bc3 = new LogicalColumn();
    bc3.setId( "bc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc3" ); //$NON-NLS-1$
    bc3.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc3.setLogicalTable( bt3 );
    bt3.addLogicalColumn( bc3 );
    bt3.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt4 = new LogicalTable();
    bt4.setId( "bt4" ); //$NON-NLS-1$
    bt4.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt4" ); //$NON-NLS-1$
    final LogicalColumn bc4 = new LogicalColumn();
    bc4.setId( "bc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc4" ); //$NON-NLS-1$
    bc4.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc4.setLogicalTable( bt4 );
    bt4.addLogicalColumn( bc4 );
    bt4.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );

    final LogicalTable bt5 = new LogicalTable();
    bt5.setId( "bt5" ); //$NON-NLS-1$
    bt5.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt5" ); //$NON-NLS-1$
    final LogicalColumn bc5 = new LogicalColumn();
    bc5.setId( "bc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc5" ); //$NON-NLS-1$
    bc5.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.COLUMN_NAME );
    bc5.setLogicalTable( bt5 );
    bt5.addLogicalColumn( bc5 );
    bt5.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 1 );
    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    // rl1.setToColumn(bc2);

    final LogicalRelationship rl2 = new LogicalRelationship();

    rl2.setFromTable( bt2 );
    // rl2.setFromColumn(bc2);
    rl2.setToTable( bt3 );
    rl2.setToColumn( bc3 );

    final LogicalRelationship rl3 = new LogicalRelationship();

    rl3.setFromTable( bt3 );
    rl3.setFromColumn( bc3 );
    rl3.setToTable( bt4 );
    rl3.setToColumn( bc4 );

    final LogicalRelationship rl4 = new LogicalRelationship();

    rl4.setFromTable( bt4 );
    rl4.setFromColumn( bc4 );
    rl4.setToTable( bt5 );
    rl4.setToColumn( bc5 );

    model.getLogicalTables().add( bt1 );
    model.getLogicalTables().add( bt2 );
    model.getLogicalTables().add( bt3 );
    model.getLogicalTables().add( bt4 );
    model.getLogicalTables().add( bt5 );

    model.getLogicalRelationships().add( rl1 );
    model.getLogicalRelationships().add( rl2 );
    model.getLogicalRelationships().add( rl3 );
    model.getLogicalRelationships().add( rl4 );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, bc1, null ) );
    myTest.getSelections().add( new Selection( null, bc4, null ) );
    SqlGenerator generator = new SqlGenerator();
    try {
      MappedQuery query = generator.generateSql( myTest, "en_US", null, databaseMeta );
      fail();
    } catch ( PentahoMetadataException e ) {
      Assert.assertTrue( e.getMessage().indexOf( "ERROR_0003" ) >= 0 );
    }
  }

  @Test
  public void testPreProcessedQuery() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields( true );
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] > 1" ) ); //$NON-NLS-1$

      SqlGenerator generator = new TestPreSqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );

      // TestHelper.printOutJava(mquery.getQuery());

      TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          \"bt1\".\"pc1\" AS \"COL0\"\n"
          + "         ,\"bt2\".\"pc2\" AS \"COL1\"\n" + "FROM \n" + "          \"pt1\" \"bt1\"\n"
          + "         ,\"pt2\" \"bt2\"\n" + "WHERE \n" + "          ( \"bt1\".\"pc1\" = \"bt2\".\"pc2\" )\n"
          + "      AND \n" + "        (\n" + "      NOT (\n" + "              \"bt1\".\"pc1\"  > 1\n" + "          )\n"
          + "      AND NOT (\n" + "              \"bt1\".\"pc1\"  < 1\n" + "          )\n" + "        )\n", mquery
          .getQuery() );

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testPostProcessedQuery() {
    try {

      LogicalModel model = TestHelper.buildDefaultModel();
      LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
      LogicalColumn bc2 = model.findLogicalColumn( "bc2" );
      DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
      databaseMeta.setQuoteAllFields( true );
      Query query = new Query( null, model );

      query.getSelections().add( new Selection( null, bc1, null ) );
      query.getSelections().add( new Selection( null, bc2, null ) );

      query.getConstraints().add( new Constraint( CombinationType.AND_NOT, "[bt1.bc1] > 1" ) ); //$NON-NLS-1$

      SqlGenerator generator = new TestPostSqlGenerator();

      MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );

      assertEquals( "Totally bogus", mquery.getQuery() );

      // TestHelper.printOutJava(mquery.getQuery());

    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testLimitedQuery() throws Exception {
    LogicalModel model = TestHelper.buildDefaultModel();
    LogicalColumn bc1 = model.findLogicalColumn( "bc1" );
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    Query query = new Query( null, model );
    query.setLimit( 10 );

    query.getSelections().add( new Selection( null, bc1, null ) );

    SqlGenerator generator = new SqlGenerator();

    MappedQuery mquery = generator.generateSql( query, "en_US", null, databaseMeta );

    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT TOP 10 DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1", mquery.getQuery() );
  }

  public static class TestPreSqlGenerator extends SqlGenerator {

    @Override
    protected void preprocessQueryModel( SQLQueryModel query, List<Selection> selections,
        Map<LogicalTable, String> tableAliases, DatabaseMeta databaseMeta ) {

      query.addWhereFormula( " \"bt1\".\"pc1\"  < 1", "AND NOT" );

      super.preprocessQueryModel( query, selections, tableAliases, databaseMeta );
    }

  }

  public static class TestPostSqlGenerator extends SqlGenerator {

    @Override
    protected String processGeneratedSql( String sql ) {
      sql = "Totally bogus";
      return super.processGeneratedSql( sql );
    }

  }

  @Test
  public void testSpiderModelQuery1() throws Exception {
    SpiderWebTestModel spiderweb = new SpiderWebTestModel();
    LogicalModel model = spiderweb.getSpiderModel();
    Category bcat = model.getCategories().get( 0 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    // Get all business tables for future processing
    LogicalTable bt01 = model.getLogicalTables().get( 0 ); // zero-based index
    LogicalTable bt02 = model.getLogicalTables().get( 1 ); // zero-based index
    LogicalTable bt03 = model.getLogicalTables().get( 2 ); // zero-based index
    LogicalTable bt04 = model.getLogicalTables().get( 3 ); // zero-based index
    LogicalTable bt05 = model.getLogicalTables().get( 4 ); // zero-based index
    LogicalTable bt06 = model.getLogicalTables().get( 5 ); // zero-based index
    LogicalTable bt07 = model.getLogicalTables().get( 6 ); // zero-based index
    LogicalTable bt08 = model.getLogicalTables().get( 7 ); // zero-based index
    LogicalTable bt09 = model.getLogicalTables().get( 8 ); // zero-based index
    LogicalTable bt10 = model.getLogicalTables().get( 9 ); // zero-based index
    LogicalTable bt11 = model.getLogicalTables().get( 10 ); // zero-based index
    LogicalTable bt12 = model.getLogicalTables().get( 11 ); // zero-based index
    LogicalTable bt13 = model.getLogicalTables().get( 12 ); // zero-based index
    LogicalTable bt14 = model.getLogicalTables().get( 13 ); // zero-based index
    LogicalTable bt15 = model.getLogicalTables().get( 14 ); // zero-based index
    LogicalTable bt16 = model.getLogicalTables().get( 15 ); // zero-based index
    LogicalTable bt17 = model.getLogicalTables().get( 16 ); // zero-based index

    LogicalColumn bcs03 = bt03.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.
    LogicalColumn bcs05 = bt05.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.

    Query test1 = new Query( null, model ); //$NON-NLS-1$
    test1.getSelections().add( new Selection( null, bcs03, null ) );
    test1.getSelections().add( new Selection( null, bcs05, null ) );
    SqlGenerator generator1 = new SqlGenerator();
    MappedQuery query1 = generator1.generateSql( test1, "en_US", null, databaseMeta );
    String queryString1 = query1.getQuery();
    // Valid "from" answers (because there's no weighting and the route is shortest) include:
    // pt_03, pt_05, pt_13
    // pt_03, pt_05, pt_12
    // pt_03, pt_05, pt_11
    // pt_03, pt_05, pt_10
    // pt_03, pt_05, pt_09
    // pt_03, pt_05, pt_06
    // pt_03, pt_05, pt_04

    // Current algorithm (SHORTEST) favors last relationship added - so this check
    // looks for pt_13 - the code in SpiderWebTestModel adds the bt03->bt13 rel
    // as the last bt03 rel
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT " + " SUM(bt_03.pc_03) AS COL0 " + " ,SUM(bt_05.pc_05) AS COL1 "
        + " FROM  " + " pt_03 bt_03 " + " ,pt_05 bt_05 " + " ,pt_13 bt_13 " + " WHERE  "
        + " ( bt_03.pc_keya_03 = bt_13.pc_keyk_13 ) " + " AND ( bt_05.pc_keyb_05 = bt_13.pc_keyi_13 )", query1
        .getQuery() );

    // Now, do the same query, but with "LOWEST_SCORE"
    model.setProperty( "path_build_method", "LOWEST_SCORE" );
    // Set relative sizes to favor the bt03->bt06->bt05 relationship...
    bt01.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 58 );
    bt02.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 177 );
    bt03.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 11 );
    bt04.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 43 );
    bt05.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 7 );
    bt06.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 17 );
    bt07.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 91 );
    bt08.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 113 );
    bt09.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 57 );
    bt10.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 35 );
    bt11.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 65 );
    bt12.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 25 );
    bt13.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 99 );
    bt14.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 97 );
    bt15.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 96 );
    bt16.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 95 );
    bt17.setProperty( SqlPhysicalTable.RELATIVE_SIZE, 94 );

    Query test2 = new Query( null, model ); //$NON-NLS-1$
    SqlGenerator generator2 = new SqlGenerator();
    test2.getSelections().add( new Selection( null, bcs03, null ) );
    test2.getSelections().add( new Selection( null, bcs05, null ) );
    MappedQuery query2 = generator2.generateSql( test1, "en_US", null, databaseMeta );
    String queryString2 = query2.getQuery();
    TestHelper.assertEqualsIgnoreWhitespaces( " SELECT  " + "           SUM(bt_03.pc_03) AS COL0 "
        + "          ,SUM(bt_05.pc_05) AS COL1 " + " FROM  " + "           pt_03 bt_03 " + "          ,pt_05 bt_05 "
        + "          ,pt_06 bt_06 " + " WHERE  " + "           ( bt_03.pc_keya_03 = bt_06.pc_keyd_06 ) "
        + "       AND ( bt_05.pc_keyb_05 = bt_06.pc_keyc_06 ) ", query2.getQuery() );

  }

  @Test
  public void testSpiderModelQuery2() throws Exception {
    SpiderWebTestModel spiderweb = new SpiderWebTestModel();
    LogicalModel model = spiderweb.getSpiderModel();
    Category bcat = model.getCategories().get( 0 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    // Get all business tables for future processing
    LogicalTable bt05 = model.getLogicalTables().get( 4 ); // zero-based index
    LogicalTable bt14 = model.getLogicalTables().get( 13 ); // zero-based index
    LogicalTable bt17 = model.getLogicalTables().get( 16 ); // zero-based index

    LogicalColumn bcs05 = bt05.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.
    LogicalColumn bcs14 = bt14.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.
    LogicalColumn bcs17 = bt17.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.

    Query test1 = new Query( null, model ); //$NON-NLS-1$
    test1.getSelections().add( new Selection( null, bcs05, null ) );
    test1.getSelections().add( new Selection( null, bcs14, null ) );
    test1.getSelections().add( new Selection( null, bcs17, null ) );
    SqlGenerator generator1 = new SqlGenerator();
    MappedQuery query1 = generator1.generateSql( test1, "en_US", null, databaseMeta );
    String queryString1 = query1.getQuery();

    // Valid "from" answers (because there's no weighting and the route is shortest) include:
    // 05,13,14,16,17 (we're testing for this one)
    // 05,12,14,16,17
    // 05,11,14,16,17
    // 05,07,14,16,17
    // 05,13,14,15,17
    // 05,12,14,15,17
    // 05,11,14,15,17
    // 05,07,14,15,17

    TestHelper.assertEqualsIgnoreWhitespaces( " SELECT  " + "           SUM(bt_05.pc_05) AS COL0 "
        + "          ,SUM(bt_14.pc_14) AS COL1 " + "          ,SUM(bt_17.pc_17) AS COL2 " + " FROM  "
        + "           pt_05 bt_05 " + "          ,pt_13 bt_13 " + "          ,pt_14 bt_14 " + "          ,pt_16 bt_16 "
        + "          ,pt_17 bt_17 " + " WHERE  " + "           ( bt_05.pc_keyb_05 = bt_13.pc_keyi_13 ) "
        + "       AND ( bt_14.pc_keyc_14 = bt_16.pc_keye_16 ) " + "       AND ( bt_17.pc_keyd_17 = bt_13.pc_keye_13 ) "
        + "       AND ( bt_17.pc_keyd_17 = bt_16.pc_keyg_16 ) ", query1.getQuery() );

  }

  @Test
  public void testSpiderModelQuery3() throws Exception {
    SpiderWebTestModel spiderweb = new SpiderWebTestModel();
    LogicalModel model = spiderweb.getSpiderModel();
    Category bcat = model.getCategories().get( 0 );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "HYPERSONIC", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    // Get all business tables for future processing
    LogicalTable bt05 = model.getLogicalTables().get( 4 ); // zero-based index
    LogicalTable bt07 = model.getLogicalTables().get( 6 ); // zero-based index
    LogicalTable bt17 = model.getLogicalTables().get( 16 ); // zero-based index

    LogicalColumn bcs07 = bt07.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.
    LogicalColumn bcs05 = bt05.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.
    LogicalColumn bcs17 = bt17.getLogicalColumns().get( 12 ); // bcs_xx columns are all the last column in the table.

    Query test1 = new Query( null, model ); //$NON-NLS-1$
    test1.getSelections().add( new Selection( null, bcs07, null ) );
    test1.getSelections().add( new Selection( null, bcs05, null ) );
    test1.getSelections().add( new Selection( null, bcs17, null ) );
    SqlGenerator generator1 = new SqlGenerator();
    MappedQuery query1 = generator1.generateSql( test1, "en_US", null, databaseMeta );
    String queryString1 = query1.getQuery();

    // In this case, the correct answer is 07/05/17 - There are direct joins (if you can ignore all the noise)

    TestHelper.assertEqualsIgnoreWhitespaces(
        " SELECT  " + "           SUM(bt_07.pc_07) AS COL0 " + "          ,SUM(bt_05.pc_05) AS COL1 "
            + "          ,SUM(bt_17.pc_17) AS COL2 " + " FROM  " + "           pt_05 bt_05 "
            + "          ,pt_07 bt_07 " + "          ,pt_17 bt_17 " + " WHERE  "
            + "           ( bt_05.pc_keyb_05 = bt_07.pc_keyd_07 ) "
            + "       AND ( bt_17.pc_keyd_17 = bt_07.pc_keyb_07 ) ", query1.getQuery() );

  }

}
