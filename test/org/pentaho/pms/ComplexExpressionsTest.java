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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.pms;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.OrderBy;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

@SuppressWarnings( "deprecation" )
public class ComplexExpressionsTest extends MetadataTestBase {

  public void testCombinedCalculationInSelection() throws Exception {
    BusinessModel model = createModel();
    DatabaseMeta databaseMeta = createOracleDatabaseMeta();
    MQLQueryImpl myTest = new MQLQueryImpl( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new Selection( model.findBusinessColumn( "bce2" ) ) );
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt2.pc2 * bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 )", //$NON-NLS-1$ 
        query.getQuery() );
  }

  public void testCombinedCalculationInWhereClause() throws Exception {
    BusinessModel model = createModel();
    DatabaseMeta databaseMeta = createOracleDatabaseMeta();
    MQLQueryImpl myTest = new MQLQueryImpl( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new Selection( model.findBusinessColumn( "bc1" ) ) );
    myTest.addConstraint( "AND", "[bt2.bce2] > 5" );
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) AND (( bt2.pc2 * bt1.pc1 > 5 ))", //$NON-NLS-1$ 
        query.getQuery() );
  }

  public void testCombinedCalculationInOrderBy() throws Exception {
    BusinessModel model = createModel();
    DatabaseMeta databaseMeta = createOracleDatabaseMeta();
    MQLQueryImpl myTest = new MQLQueryImpl( null, model, databaseMeta, "en_US" ); //$NON-NLS-1$
    myTest.addSelection( new Selection( model.findBusinessColumn( "bc1" ) ) );
    myTest.getOrder().add( new OrderBy( new Selection( model.findBusinessColumn( "bce2" ) ), false ) ); // Sort on
                                                                                                        // calculated
                                                                                                        // column
                                                                                                        // descending
    MappedQuery query = myTest.getQuery();
    assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) ORDER BY bt2.pc2 * bt1.pc1 DESC", //$NON-NLS-1$ 
        query.getQuery() );
  }

  /*
   * WG: This test assumes a behavior that no longer exists
   * 
   * public void testCombinedCalculationInHaving() throws Exception { BusinessModel model = createModel();
   * 
   * model.findBusinessColumn("bce2").setAggregationType(AggregationSettings.SUM);
   * 
   * DatabaseMeta databaseMeta = createOracleDatabaseMeta(); MQLQueryImpl myTest = new MQLQueryImpl(null, model,
   * databaseMeta, "en_US"); //$NON-NLS-1$ myTest.addSelection(new Selection(model.findBusinessColumn("bc1")));
   * myTest.addConstraint("AND", "SUM( [bt2.bce2] ) > 5"); MappedQuery query = myTest.getQuery();
   * assertEqualsIgnoreWhitespaces(
   * "SELECT bt1.pc1 AS COL0 FROM pt1 bt1 ,pt2 bt2 WHERE ( bt1.pc1 = bt2.pc2 ) GROUP BY bt1.pc1 HAVING ( SUM( bt2.pc2 * bt1.pc1 ) > 5 )"
   * , //$NON-NLS-1$ query.getQuery()); }
   */
  private BusinessModel createModel() throws ObjectAlreadyExistsException {

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
    model.addBusinessTable( bt1 );

    final BusinessTable bt2 = new BusinessTable();
    bt2.setId( "bt2" ); //$NON-NLS-1$
    bt2.setTargetTable( "pt2" ); //$NON-NLS-1$
    final BusinessColumn bc2 = new BusinessColumn();
    bc2.setId( "bc2" ); //$NON-NLS-1$
    bc2.setFormula( "pc2" ); //$NON-NLS-1$
    bc2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bc2 );
    mainCat.addBusinessColumn( bc2 );
    model.addBusinessTable( bt2 );

    final BusinessColumn bce2 = new BusinessColumn();
    bce2.setId( "bce2" ); //$NON-NLS-1$
    bce2.setExact( true );
    bce2.setFormula( "[bt2.bc2] * [bt1.bc1]" ); //$NON-NLS-1$
    bce2.setBusinessTable( bt2 );
    bt2.addBusinessColumn( bce2 );
    mainCat.addBusinessColumn( bce2 );

    final RelationshipMeta rl1 = new RelationshipMeta();

    rl1.setTableFrom( bt1 );
    rl1.setFieldFrom( bc1 );
    rl1.setTableTo( bt2 );
    rl1.setFieldTo( bc2 );

    model.addRelationship( rl1 );

    return model;
  }
}
