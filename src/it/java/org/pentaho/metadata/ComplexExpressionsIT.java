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
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.MetadataTestBase;

public class ComplexExpressionsIT {

  @BeforeClass
  public static void initKettle() throws KettleException {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testCombinedCalculationInSelection() throws Exception {
    LogicalModel model = createModel();
    DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta();
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, model.findLogicalColumn( "bce2" ), null ) );
    MappedQuery query = new SqlGenerator().generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt2.pc2 * bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )", //$NON-NLS-1$ 
        query.getQuery() );
  }

  @Test
  public void testCombinedCalculationInWhereClause() throws Exception {
    LogicalModel model = createModel();
    DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta();
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, model.findLogicalColumn( "bc1" ), null ) );
    myTest.getConstraints().add( new Constraint( CombinationType.AND, "[bt2.bce2] > 5" ) );
    MappedQuery query = new SqlGenerator().generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) AND (( bt2.pc2 * bt1.pc1 > 5 ))", //$NON-NLS-1$ 
            query.getQuery() );
  }

  @Test
  public void testCombinedCalculationInOrderBy() throws Exception {
    LogicalModel model = createModel();
    DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta();
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, model.findLogicalColumn( "bc1" ), null ) );
    myTest.getOrders()
        .add( new Order( new Selection( null, model.findLogicalColumn( "bce2" ), null ), Order.Type.DESC ) ); // Sort on
                                                                                                              // calculated
                                                                                                              // column
                                                                                                              // descending
    MappedQuery query = new SqlGenerator().generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper
        .assertEqualsIgnoreWhitespaces(
            "SELECT DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) ORDER BY bt2.pc2 * bt1.pc1 DESC", //$NON-NLS-1$ 
            query.getQuery() );
  }

  /*
   * WG: I've disabled this test, the SUM(SUM( behavior is expected in this scenario.
   * 
   * public void testCombinedCalculationInHaving() throws Exception { LogicalModel model = createModel();
   * 
   * model.findLogicalColumn("bce2").setAggregationType(AggregationType.SUM);
   * 
   * DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta(); Query myTest = new Query(null, model);
   * //$NON-NLS-1$ myTest.getSelections().add(new Selection(null, model.findLogicalColumn("bc1"), null));
   * myTest.getConstraints().add(new Constraint(CombinationType.AND, "SUM( [bt2.bce2] ) > 5")); MappedQuery query = new
   * SqlGenerator().generateSql(myTest, "en_US", null, databaseMeta); TestHelper.assertEqualsIgnoreWhitespaces(
   * "SELECT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM( bt2.pc2 * bt1.pc1 ) > 5 )"
   * , //$NON-NLS-1$ query.getQuery()); }
   */
  @Test
  public void testOracleDatabaseMeta() {
    Assert.assertEquals( TestHelper.createOracleDatabaseMeta().getPluginId(), "ORACLE" ); //$NON-NLS-1$
  }

  private LogicalModel createModel() {

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
    bc2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );
    model.getLogicalTables().add( bt2 );

    final LogicalColumn bce2 = new LogicalColumn();
    bce2.setId( "bce2" ); //$NON-NLS-1$
    bce2.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    bce2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "[bt2.bc2] * [bt1.bc1]" ); //$NON-NLS-1$
    bce2.setLogicalTable( bt2 );
    bt2.addLogicalColumn( bce2 );
    mainCat.addLogicalColumn( bce2 );

    final LogicalRelationship rl1 = new LogicalRelationship();

    rl1.setFromTable( bt1 );
    rl1.setFromColumn( bc1 );
    rl1.setToTable( bt2 );
    rl1.setToColumn( bc2 );

    model.getLogicalRelationships().add( rl1 );

    return model;
  }
}
