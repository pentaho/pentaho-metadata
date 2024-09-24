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
package org.pentaho.pms.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.metadata.util.Util;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmModelElement;
import org.pentaho.pms.cwm.pentaho.meta.instance.CwmExtent;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimension;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimensionedObject;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.pentaho.pms.core.CWM.TAG_TABLE_IS_DRAWN;
import static org.pentaho.pms.core.CWM.TAG_CONCEPT_PARENT_NAME;
import static org.pentaho.pms.core.CWM.TAG_BUSINESS_TABLE_PHYSICAL_TABLE_NAME;
import static org.pentaho.pms.core.CWM.TAG_BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME;

/**
 * @author Andrey Khayrutdinov
 */
@SuppressWarnings( "deprecation" )
public class CwmSchemaFactoryIT {

  private static final String TEST_DOMAIN = "Test Domain";

  private static final String PHYSICAL_COL = "physicalColumn";
  private static final String PHYSICAL_TBL = "PhysicalTable";
  private static final String BUSINESS_TBL = "BusinessTable";

  private CwmSchemaFactory factory;
  private CWM cwm;

  @BeforeClass
  public static void setUpEnv() throws Exception {
    KettleEnvironment.init( false );
  }

  @Before
  public void setUp() {
    factory = new CwmSchemaFactory();

    cwm = CWM.getInstance( TEST_DOMAIN );
    cwm = spy( cwm );

    doReturn( null ).when( cwm ).getFirstTaggedValue( any( CwmModelElement.class ), eq( TAG_CONCEPT_PARENT_NAME ) );
    doReturn( "N" ).when( cwm ).getFirstTaggedValue( any( CwmModelElement.class ), eq( TAG_TABLE_IS_DRAWN ) );
    doReturn( PHYSICAL_TBL ).when( cwm )
      .getFirstTaggedValue( any( CwmModelElement.class ), eq( TAG_BUSINESS_TABLE_PHYSICAL_TABLE_NAME ) );
    doReturn( PHYSICAL_COL ).when( cwm )
      .getFirstTaggedValue( any( CwmModelElement.class ), eq( TAG_BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME ) );

    doReturn( new CwmDescription[ 0 ] ).when( cwm ).getDescription( any( CwmModelElement.class ) );
  }

  @After
  public void tearDown() throws Exception {
    factory = null;

    cwm.removeDomain();
    cwm = null;
  }


  @Test( expected = IllegalArgumentException.class )
  public void getBusinessColumn_InvalidColumnId() throws Exception {
    String invalidId = "Territory (Cat Id)";
    assertGetBusinessColumn( invalidId );
  }

  @Test
  public void getBusinessColumn_ValidColumnId() throws Exception {
    String validId = "Territory";
    assertTrue( Util.validateId( validId ) );

    assertGetBusinessColumn( validId );
  }

  private void assertGetBusinessColumn( String rawId ) throws Exception {
    CwmDimensionedObject object = mock( CwmDimensionedObject.class );
    when( object.getName() ).thenReturn( rawId );

    PhysicalTable physicalTable = new PhysicalTable( PHYSICAL_TBL );
    physicalTable.addPhysicalColumn( new PhysicalColumn( PHYSICAL_COL ) );

    BusinessColumn column =
      factory.getBusinessColumn( cwm, object, physicalTable, new BusinessTable( BUSINESS_TBL ), new SchemaMeta() );

    column.setId( object.getName() );
  }


  @Test( expected = IllegalArgumentException.class )
  public void getBusinessTable_InvalidTableId() throws Exception {
    String invalidId = "Table (tbl)";
    assertGetBusinessTable( invalidId );
  }

  @Test
  public void getBusinessTable_ValidTableId() throws Exception {
    String validId = "Table";
    assertGetBusinessTable( validId );
  }

  private void assertGetBusinessTable( String rawId ) throws Exception {
    CwmDimension dimension = mock( CwmDimension.class );
    when( dimension.getName() ).thenReturn( rawId );

    SchemaMeta meta = new SchemaMeta();
    meta.addTable( new PhysicalTable( PHYSICAL_TBL ) );

    BusinessTable column =
      factory.getBusinessTable( cwm, dimension, meta, new BusinessModel( "businessModel" ) );
    column.setId( rawId );
  }


  @Test( expected = IllegalArgumentException.class )
  public void getBusinessCategory_InvalidCategoryId() throws Exception {
    String invalidId = "Category (cat)";
    assertGetBusinessCategory( invalidId );
  }

  @Test
  public void getBusinessCategory_ValidCategoryId() throws Exception {
    String validId = "Category";
    assertGetBusinessCategory( validId );
  }

  private void assertGetBusinessCategory( String rawId) throws Exception {
    CwmExtent extent = mock( CwmExtent.class );
    when( extent.getName() ).thenReturn( rawId );

    SchemaMeta meta = new SchemaMeta();
    meta.addTable( new PhysicalTable( PHYSICAL_TBL ) );

    BusinessCategory category =
      factory.getBusinessCategory( cwm, extent, new BusinessModel( "businessModel" ), meta );

    category.setId( extent.getName() );
  }
}
