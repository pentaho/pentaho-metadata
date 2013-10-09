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

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.MetadataTestBase;

public class AggregationScenariosTest {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  /**
   * Scenario: we have 2 sums and we want to calculate a ratio.<br>
   * The aggregation on the ratio is obviously "none".<br>
   * However, the generator has to keep in mind that it still needs to generate a group by.<br>
   * <br>
   * This is a simple one-table example.<br>
   * 
   */
  @Test
  public void testRatioOfSumsGroupBy() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$

    // dimension column d1
    //
    final LogicalColumn d1 = new LogicalColumn();
    d1.setId( "d1" ); //$NON-NLS-1$
    d1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "d" ); //$NON-NLS-1$
    d1.setLogicalTable( bt1 );
    d1.setAggregationType( AggregationType.NONE );
    d1.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.DIMENSION );

    bt1.addLogicalColumn( d1 );
    mainCat.addLogicalColumn( d1 );

    // Sum column bc1
    //
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "a" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bc1.setAggregationType( AggregationType.SUM );
    bc1.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( bc1 );
    mainCat.addLogicalColumn( bc1 );

    // Sum column bc2
    //
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "b" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt1 );
    bc2.setAggregationType( AggregationType.SUM );
    bc2.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    // A calculated column: ratio
    //
    final LogicalColumn ratio = new LogicalColumn();
    ratio.setId( "ratio" ); //$NON-NLS-1$
    ratio.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "[bt1.bc1] / [bt1.bc2]" ); //$NON-NLS-1$
    ratio.setLogicalTable( bt1 );
    ratio.setAggregationType( AggregationType.NONE );
    ratio.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    ratio.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( ratio );
    mainCat.addLogicalColumn( ratio );

    DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta();
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, d1, null ) );
    myTest.getSelections().add( new Selection( null, ratio, null ) );

    MappedQuery query = new SqlGenerator().generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT bt1.d AS COL0 , SUM(bt1.a) / SUM(bt1.b) AS COL1 FROM t1 bt1 GROUP BY bt1.d", //$NON-NLS-1$
        query.getQuery() );
  }

  /**
   * Scenario: we want to make a sum of a ratio<br>
   * The aggregation on the ratio is obviously "SUM".<br>
   * However, the aggregation on the used columns is none.<br>
   * <br>
   * This is a simple one-table example.<br>
   * 
   */
  @Test
  public void testSumOfRatioGroupBy() throws Exception {
    final LogicalModel model = new LogicalModel();
    model.setId( "model_01" );
    Category mainCat = new Category();
    mainCat.setId( "cat_01" );
    model.getCategories().add( mainCat );

    final LogicalTable bt1 = new LogicalTable();
    bt1.setId( "bt1" ); //$NON-NLS-1$
    bt1.setProperty( SqlPhysicalTable.TARGET_TABLE, "t1" ); //$NON-NLS-1$

    // dimension column d1
    //
    final LogicalColumn d1 = new LogicalColumn();
    d1.setId( "d1" ); //$NON-NLS-1$
    d1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "d" ); //$NON-NLS-1$
    d1.setLogicalTable( bt1 );
    d1.setAggregationType( AggregationType.NONE );
    d1.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.DIMENSION );

    bt1.addLogicalColumn( d1 );
    mainCat.addLogicalColumn( d1 );

    // Sum column bc1
    //
    final LogicalColumn bc1 = new LogicalColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "a" ); //$NON-NLS-1$
    bc1.setLogicalTable( bt1 );
    bc1.setAggregationType( AggregationType.NONE );
    bc1.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( bc1 );
    mainCat.addLogicalColumn( bc1 );

    // Sum column bc2
    //
    final LogicalColumn bc2 = new LogicalColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "b" ); //$NON-NLS-1$
    bc2.setLogicalTable( bt1 );
    bc2.setAggregationType( AggregationType.NONE );
    bc2.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( bc2 );
    mainCat.addLogicalColumn( bc2 );

    // A calculated column: ratio
    //
    final LogicalColumn ratio = new LogicalColumn();
    ratio.setId( "ratio" ); //$NON-NLS-1$
    ratio.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "SUM( [bt1.bc1] / [bt1.bc2] )" ); //$NON-NLS-1$
    ratio.setLogicalTable( bt1 );
    ratio.setAggregationType( AggregationType.SUM );
    ratio.setProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA );
    ratio.setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, FieldType.FACT );

    bt1.addLogicalColumn( ratio );
    mainCat.addLogicalColumn( ratio );

    DatabaseMeta databaseMeta = TestHelper.createOracleDatabaseMeta();
    Query myTest = new Query( null, model ); //$NON-NLS-1$
    myTest.getSelections().add( new Selection( null, d1, null ) );
    myTest.getSelections().add( new Selection( null, ratio, null ) );

    MappedQuery query = new SqlGenerator().generateSql( myTest, "en_US", null, databaseMeta );
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT bt1.d AS COL0 , SUM( bt1.a / bt1.b ) AS COL1 FROM t1 bt1 GROUP BY bt1.d", //$NON-NLS-1$
        query.getQuery() );
  }
}
