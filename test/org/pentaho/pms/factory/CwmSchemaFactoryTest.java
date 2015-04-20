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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
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
public class CwmSchemaFactoryTest {

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
  public void setUp() throws Exception {
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


  @Test
  public void getBusinessColumn_InvalidColumnId() throws Exception {
    String invalidId = "Territory (Cat Id)";
    assertFalse( Util.validateId( invalidId ) );

    assertGetBusinessColumn( invalidId, Util.toId( invalidId ) );
  }

  @Test
  public void getBusinessColumn_ValidColumnId() throws Exception {
    String validId = "Territory";
    assertTrue( Util.validateId( validId ) );

    assertGetBusinessColumn( validId, validId );
  }

  private void assertGetBusinessColumn( String rawId, String expectedId ) throws Exception {
    CwmDimensionedObject object = mock( CwmDimensionedObject.class );
    when( object.getName() ).thenReturn( rawId );

    PhysicalTable physicalTable = new PhysicalTable( PHYSICAL_TBL );
    physicalTable.addPhysicalColumn( new PhysicalColumn( PHYSICAL_COL ) );

    BusinessColumn column =
      factory.getBusinessColumn( cwm, object, physicalTable, new BusinessTable( BUSINESS_TBL ), new SchemaMeta() );
    assertEquals( expectedId, column.getId() );
  }


  @Test
  public void getBusinessTable_InvalidTableId() throws Exception {
    String invalidId = "Table (tbl)";
    assertFalse( Util.validateId( invalidId ) );

    assertGetBusinessTable( invalidId, Util.toId( invalidId ) );
  }

  @Test
  public void getBusinessTable_ValidTableId() throws Exception {
    String validId = "Table";
    assertTrue( Util.validateId( validId ) );

    assertGetBusinessTable( validId, validId );
  }

  private void assertGetBusinessTable( String rawId, String expectedId ) throws Exception {
    CwmDimension dimension = mock( CwmDimension.class );
    when( dimension.getName() ).thenReturn( rawId );

    SchemaMeta meta = new SchemaMeta();
    meta.addTable( new PhysicalTable( PHYSICAL_TBL ) );

    BusinessTable column =
      factory.getBusinessTable( cwm, dimension, meta, new BusinessModel( "businessModel" ) );
    assertEquals( expectedId, column.getId() );
  }


  @Test
  public void getBusinessCategory_InvalidCategoryId() throws Exception {
    String invalidId = "Category (cat)";
    assertFalse( Util.validateId( invalidId ) );

    assertGetBusinessCategory( invalidId, Util.toId( invalidId ) );
  }

  @Test
  public void getBusinessCategory_ValidCategoryId() throws Exception {
    String validId = "Category";
    assertTrue( Util.validateId( validId ) );

    assertGetBusinessCategory( validId, validId );
  }

  private void assertGetBusinessCategory( String rawId, String expectedId ) throws Exception {
    CwmExtent extent = mock( CwmExtent.class );
    when( extent.getName() ).thenReturn( rawId );

    SchemaMeta meta = new SchemaMeta();
    meta.addTable( new PhysicalTable( PHYSICAL_TBL ) );

    BusinessCategory category =
      factory.getBusinessCategory( cwm, extent, new BusinessModel( "businessModel" ), meta );
    assertEquals( expectedId, category.getId() );
  }
}