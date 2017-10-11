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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.pms;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;

@SuppressWarnings( "deprecation" )
public class AggregationScenariosIT extends MetadataTestBase {

  /**
   * Scenario: we have 2 sums and we want to calculate a ratio.<br>
   * The aggregation on the ratio is obviously "none".<br>
   * However, the generator has to keep in mind that it still needs to generate a group by.<br>
   * <br>
   * This is a simple one-table example.<br>
   * 
   */
  public void testRatioOfSumsGroupBy() throws Exception {
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

    // dimension column d1
    //
    final BusinessColumn d1 = new BusinessColumn();
    d1.setId( "d1" ); //$NON-NLS-1$
    d1.setFormula( "d" ); //$NON-NLS-1$
    d1.setBusinessTable( bt1 );
    d1.setAggregationType( AggregationSettings.NONE );
    d1.setFieldType( FieldTypeSettings.DIMENSION );

    bt1.addBusinessColumn( d1 );
    mainCat.addBusinessColumn( d1 );

    // Sum column bc1
    //
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "a" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bc1.setAggregationType( AggregationSettings.SUM );
    bc1.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( bc1 );
    mainCat.addBusinessColumn( bc1 );

    // Sum column bc2
    //
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "b" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt1 );
    bc2.setAggregationType( AggregationSettings.SUM );
    bc2.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    // A calculated column: ratio
    //
    final BusinessColumn ratio = new BusinessColumn();
    ratio.setId( "ratio" ); //$NON-NLS-1$
    ratio.setFormula( "[bt1.bc1] / [bt1.bc2]" ); //$NON-NLS-1$
    ratio.setBusinessTable( bt1 );
    ratio.setAggregationType( AggregationSettings.NONE );
    ratio.setExact( true );
    ratio.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( ratio );
    mainCat.addBusinessColumn( ratio );

    DatabaseMeta databaseMeta = createOracleDatabaseMeta();
    MQLQueryImpl myTest = new MQLQueryImpl( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new Selection( d1 ) );
    myTest.addSelection( new Selection( ratio ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( "SELECT bt1.d AS COL0 , SUM(bt1.a) / SUM(bt1.b) AS COL1 FROM t1 bt1 GROUP BY bt1.d", //$NON-NLS-1$
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
  public void testSumOfRatioGroupBy() throws Exception {
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

    // dimension column d1
    //
    final BusinessColumn d1 = new BusinessColumn();
    d1.setId( "d1" ); //$NON-NLS-1$
    d1.setFormula( "d" ); //$NON-NLS-1$
    d1.setBusinessTable( bt1 );
    d1.setAggregationType( AggregationSettings.NONE );
    d1.setFieldType( FieldTypeSettings.DIMENSION );

    bt1.addBusinessColumn( d1 );
    mainCat.addBusinessColumn( d1 );

    // Sum column bc1
    //
    final BusinessColumn bc1 = new BusinessColumn();
    bc1.setId( "bc1" ); //$NON-NLS-1$
    bc1.setFormula( "a" ); //$NON-NLS-1$
    bc1.setBusinessTable( bt1 );
    bc1.setAggregationType( AggregationSettings.NONE );
    bc1.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( bc1 );
    mainCat.addBusinessColumn( bc1 );

    // Sum column bc2
    //
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "b" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt1 );
    bc2.setAggregationType( AggregationSettings.NONE );
    bc2.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );

    // A calculated column: ratio
    //
    final BusinessColumn ratio = new BusinessColumn();
    ratio.setId( "ratio" ); //$NON-NLS-1$
    ratio.setFormula( "SUM( [bt1.bc1] / [bt1.bc2] )" ); //$NON-NLS-1$
    ratio.setBusinessTable( bt1 );
    ratio.setAggregationType( AggregationSettings.SUM );
    ratio.setExact( true );
    ratio.setFieldType( FieldTypeSettings.FACT );

    bt1.addBusinessColumn( ratio );
    mainCat.addBusinessColumn( ratio );

    DatabaseMeta databaseMeta = createOracleDatabaseMeta();
    MQLQueryImpl myTest = new MQLQueryImpl( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new Selection( d1 ) );
    myTest.addSelection( new Selection( ratio ) );

    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces( "SELECT bt1.d AS COL0 , SUM( bt1.a / bt1.b ) AS COL1 FROM t1 bt1 GROUP BY bt1.d", //$NON-NLS-1$
        query.getQuery() );
  }
}
