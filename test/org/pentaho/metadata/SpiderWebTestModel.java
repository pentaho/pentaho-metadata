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
 * Copyright (c) 20012 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.RelationshipType;

public class SpiderWebTestModel {

  public LogicalModel getSpiderModel() throws Exception {
    // Create Business Model
    final LogicalModel model = new LogicalModel();
    model.setId( "enjoi" );
    // Create Only one Business View
    Category mainCat = new Category();
    mainCat.setId( "buscat" );
    model.getCategories().add( mainCat );
    // Create 18 Business Tables
    LogicalTable bt01 = createLogicalTable( "01", model, mainCat );
    LogicalTable bt02 = createLogicalTable( "02", model, mainCat );
    LogicalTable bt03 = createLogicalTable( "03", model, mainCat );
    LogicalTable bt04 = createLogicalTable( "04", model, mainCat );
    LogicalTable bt05 = createLogicalTable( "05", model, mainCat );
    LogicalTable bt06 = createLogicalTable( "06", model, mainCat );
    LogicalTable bt07 = createLogicalTable( "07", model, mainCat );
    LogicalTable bt08 = createLogicalTable( "08", model, mainCat );
    LogicalTable bt09 = createLogicalTable( "09", model, mainCat );
    LogicalTable bt10 = createLogicalTable( "10", model, mainCat );
    LogicalTable bt11 = createLogicalTable( "11", model, mainCat );
    LogicalTable bt12 = createLogicalTable( "12", model, mainCat );
    LogicalTable bt13 = createLogicalTable( "13", model, mainCat );
    LogicalTable bt14 = createLogicalTable( "14", model, mainCat );
    LogicalTable bt15 = createLogicalTable( "15", model, mainCat );
    LogicalTable bt16 = createLogicalTable( "16", model, mainCat );
    LogicalTable bt17 = createLogicalTable( "17", model, mainCat );

    createRelationship( bt02, bt01, 0, 0, model );
    createRelationship( bt03, bt01, 0, 1, model );
    createRelationship( bt03, bt04, 0, 2, model );
    createRelationship( bt03, bt06, 0, 3, model );
    createRelationship( bt03, bt07, 0, 4, model );
    createRelationship( bt03, bt08, 0, 5, model );
    createRelationship( bt03, bt09, 0, 6, model );
    createRelationship( bt03, bt10, 0, 7, model );
    createRelationship( bt03, bt11, 0, 8, model );
    createRelationship( bt03, bt12, 0, 9, model );
    createRelationship( bt03, bt13, 0, 10, model );

    createRelationship( bt05, bt02, 1, 1, model );
    createRelationship( bt05, bt04, 1, 1, model );
    createRelationship( bt05, bt06, 1, 2, model );
    createRelationship( bt05, bt07, 1, 3, model );
    createRelationship( bt05, bt09, 1, 4, model );
    createRelationship( bt05, bt10, 1, 5, model );
    createRelationship( bt05, bt11, 1, 6, model );
    createRelationship( bt05, bt12, 1, 7, model );
    createRelationship( bt05, bt13, 1, 8, model );

    createRelationship( bt14, bt08, 2, 2, model );
    createRelationship( bt14, bt15, 2, 3, model );
    createRelationship( bt14, bt16, 2, 4, model );

    createRelationship( bt17, bt07, 3, 1, model );
    createRelationship( bt17, bt11, 3, 4, model );
    createRelationship( bt17, bt12, 3, 3, model );
    createRelationship( bt17, bt13, 3, 4, model );
    createRelationship( bt17, bt15, 3, 5, model );
    createRelationship( bt17, bt16, 3, 6, model );

    //
    // Regression queries to try:
    //
    // bcs_03, bcs_05
    // bcs_05, bcs_14, bcs_17
    // bcs_07, bcs_05, bcs_17
    // bcs_16, bcs_02
    //

    return model;
  }

  private LogicalTable createLogicalTable( String tblId, LogicalModel model, Category mainCat ) throws Exception {

    LogicalTable rtn = new LogicalTable();
    rtn.setId( "bt_" + tblId );
    rtn.setProperty( SqlPhysicalTable.TARGET_TABLE, "pt_" + tblId ); //$NON-NLS-1$
    createBusinessKeyColumn( tblId, "keya", rtn, mainCat ); // 0
    createBusinessKeyColumn( tblId, "keyb", rtn, mainCat ); // 1
    createBusinessKeyColumn( tblId, "keyc", rtn, mainCat ); // 2
    createBusinessKeyColumn( tblId, "keyd", rtn, mainCat ); // 3
    createBusinessKeyColumn( tblId, "keye", rtn, mainCat ); // 4
    createBusinessKeyColumn( tblId, "keyf", rtn, mainCat ); // 5
    createBusinessKeyColumn( tblId, "keyg", rtn, mainCat ); // 6
    createBusinessKeyColumn( tblId, "keyh", rtn, mainCat ); // 7
    createBusinessKeyColumn( tblId, "keyi", rtn, mainCat ); // 8
    createBusinessKeyColumn( tblId, "keyj", rtn, mainCat ); // 9
    createBusinessKeyColumn( tblId, "keyk", rtn, mainCat ); // 10
    createBusinessKeyColumn( tblId, "keyl", rtn, mainCat ); // 11

    LogicalColumn bcs1 = new LogicalColumn();
    bcs1.setId( "bcs_" + tblId );
    bcs1.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc_" + tblId ); //$NON-NLS-1$
    bcs1.setProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, AggregationType.SUM );
    bcs1.setLogicalTable( rtn );
    rtn.addLogicalColumn( bcs1 );
    mainCat.addLogicalColumn( bcs1 );
    model.addLogicalTable( rtn );
    return rtn;

  }

  private LogicalColumn createBusinessKeyColumn( String tblId, String columnId, LogicalTable tbl, Category cat )
    throws Exception {
    LogicalColumn rtn = new LogicalColumn();
    rtn.setId( "bc_" + columnId + "_" + tblId );
    rtn.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "pc_" + columnId + "_" + tblId ); //$NON-NLS-1$
    rtn.setLogicalTable( tbl );
    tbl.addLogicalColumn( rtn );
    cat.addLogicalColumn( rtn );
    return rtn;
  }

  private void createRelationship( LogicalTable from, LogicalTable to, int fromColId, int toColId, LogicalModel model )
    throws Exception {
    LogicalRelationship rel = new LogicalRelationship();
    rel.setFromTable( from );
    rel.setToTable( to );
    rel.setFromColumn( from.getLogicalColumns().get( fromColId ) );
    rel.setToColumn( to.getLogicalColumns().get( toColId ) );
    rel.setRelationshipType( RelationshipType._1_N );
    model.getLogicalRelationships().add( rel );
  }

}
