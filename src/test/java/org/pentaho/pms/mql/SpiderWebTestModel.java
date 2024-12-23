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

package org.pentaho.pms.mql;

import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

public class SpiderWebTestModel {

  public BusinessModel getSpiderModel() throws Exception {
    // Create Business Model
    final BusinessModel model = new BusinessModel();
    model.setId( "enjoi" );
    // Create Only one Business View
    BusinessCategory rootCat = new BusinessCategory();
    rootCat.setRootCategory( true );
    BusinessCategory mainCat = new BusinessCategory();
    mainCat.setId( "buscat" );
    rootCat.addBusinessCategory( mainCat );
    model.setRootCategory( rootCat );
    // Create 18 Business Tables
    BusinessTable bt01 = createBusinessTable( "01", model, mainCat );
    BusinessTable bt02 = createBusinessTable( "02", model, mainCat );
    BusinessTable bt03 = createBusinessTable( "03", model, mainCat );
    BusinessTable bt04 = createBusinessTable( "04", model, mainCat );
    BusinessTable bt05 = createBusinessTable( "05", model, mainCat );
    BusinessTable bt06 = createBusinessTable( "06", model, mainCat );
    BusinessTable bt07 = createBusinessTable( "07", model, mainCat );
    BusinessTable bt08 = createBusinessTable( "08", model, mainCat );
    BusinessTable bt09 = createBusinessTable( "09", model, mainCat );
    BusinessTable bt10 = createBusinessTable( "10", model, mainCat );
    BusinessTable bt11 = createBusinessTable( "11", model, mainCat );
    BusinessTable bt12 = createBusinessTable( "12", model, mainCat );
    BusinessTable bt13 = createBusinessTable( "13", model, mainCat );
    BusinessTable bt14 = createBusinessTable( "14", model, mainCat );
    BusinessTable bt15 = createBusinessTable( "15", model, mainCat );
    BusinessTable bt16 = createBusinessTable( "16", model, mainCat );
    BusinessTable bt17 = createBusinessTable( "17", model, mainCat );

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

  private BusinessTable createBusinessTable( String tblId, BusinessModel model, BusinessCategory mainCat )
    throws Exception {

    BusinessTable rtn = new BusinessTable();
    rtn.setId( "bt_" + tblId );
    rtn.setTargetTable( "pt_" + tblId );
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

    BusinessColumn bcs1 = new BusinessColumn();
    bcs1.setId( "bcs_" + tblId );
    bcs1.setFormula( "pc_" + tblId );
    bcs1.setAggregationType( AggregationSettings.SUM );
    bcs1.setBusinessTable( rtn );
    rtn.addBusinessColumn( bcs1 );
    mainCat.addBusinessColumn( bcs1 );
    model.addBusinessTable( rtn );
    return rtn;

  }

  private BusinessColumn
    createBusinessKeyColumn( String tblId, String columnId, BusinessTable tbl, BusinessCategory cat ) throws Exception {
    BusinessColumn rtn = new BusinessColumn();
    rtn.setId( "bc_" + columnId + "_" + tblId );
    rtn.setFormula( "pc_" + columnId + "_" + tblId );
    rtn.setBusinessTable( tbl );
    tbl.addBusinessColumn( rtn );
    cat.addBusinessColumn( rtn );
    return rtn;
  }

  private void
    createRelationship( BusinessTable from, BusinessTable to, int fromColId, int toColId, BusinessModel model )
      throws Exception {
    RelationshipMeta rel = new RelationshipMeta();
    rel.setTableFrom( from );
    rel.setTableTo( to );
    rel.setFieldFrom( from.getBusinessColumn( fromColId ) );
    rel.setFieldTo( to.getBusinessColumn( toColId ) );
    rel.setType( RelationshipMeta.TYPE_RELATIONSHIP_1_N );
    model.addRelationship( rel );
  }

}
