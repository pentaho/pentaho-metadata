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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.metadata.datatable.Cell;
import org.pentaho.metadata.datatable.DataTable;
import org.pentaho.metadata.datatable.Row;
import org.pentaho.metadata.datatable.Types;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.thin.Element;
import org.pentaho.metadata.model.thin.MetadataModelsService;
import org.pentaho.metadata.model.thin.Model;
import org.pentaho.metadata.model.thin.ModelInfo;
import org.pentaho.metadata.model.thin.ModelProvider;
import org.pentaho.metadata.model.thin.Provider;
import org.pentaho.metadata.model.thin.Query;

@SuppressWarnings( "all" )
public class TestModelProvider implements ModelProvider {

  public static final String PROVIDER_ID = "test provider";

  public static final String GROUP_ID = "test group"; //$NON-NLS-1$
  public static final String MODEL_ID = "test model id";

  private static final TestModelProvider instance = new TestModelProvider();

  protected static Provider provider;

  static {
    MetadataModelsService.addProvider( instance );
  }

  public TestModelProvider() {

    provider = new Provider();
    provider.setId( PROVIDER_ID );
    provider.setName( "Test Provider" );

  }

  public static TestModelProvider getInstance() {
    return instance;
  }

  @Override
  public Model getModel( String id ) {

    Model model = createModel();
    if ( model.getId().equals( id ) ) {
      return model;
    } else {
      return null;
    }
  }

  @Override
  public ModelInfo[] getModelList( String providerId, String groupId, String match ) {

    ModelInfo modelInfo = createModelInfo();

    if ( providerId != null && !providerId.equals( PROVIDER_ID ) ) {
      return new ModelInfo[0];
    }
    if ( groupId != null && !groupId.equals( modelInfo.getGroupId() ) ) {
      return new ModelInfo[0];
    }
    String str = ( match == null ) ? null : match.toLowerCase();
    if ( str == null || modelInfo.getId().contains( str ) || modelInfo.getName().contains( str )
        || modelInfo.getDescription().contains( str ) ) {
      return new ModelInfo[] { modelInfo };
    }
    return new ModelInfo[0];
  }

  private ModelInfo createModelInfo() {

    ModelInfo info = new ModelInfo();

    info.setGroupId( GROUP_ID );
    info.setDescription( "Test model description" );
    info.setModelId( MODEL_ID );
    info.setName( "Test model name" );
    info.setProvider( provider );

    return info;
  }

  private Model createModel() {

    // only one model for this...

    ModelInfo info = createModelInfo();

    Model model = new Model();

    Element[] elements = new Element[3];

    HashMap<String, String> elementCapabilities = new HashMap<String, String>();
    elementCapabilities.put( Element.CAPABILITY_CAN_SEARCH, "false" );
    elementCapabilities.put( Element.CAPABILITY_CAN_SORT, "false" );

    Element groupElement = new Element();
    groupElement.setElementType( FieldType.DIMENSION.name() );
    groupElement.setHorizontalAlignment( Alignment.LEFT.name() );
    groupElement.setId( "element1" );
    groupElement.setName( "Element 1" );
    groupElement.setDescription( "Description 1" );
    groupElement.setDataType( Types.TYPE_STRING.toString() );
    groupElement.setCapabilities( elementCapabilities );
    groupElement.setIsQueryElement( false );
    elements[0] = groupElement;

    Element element1 = new Element();
    element1.setElementType( FieldType.DIMENSION.name() );
    element1.setHorizontalAlignment( Alignment.LEFT.name() );
    element1.setId( "element1" );
    element1.setName( "Element 1" );
    element1.setDataType( Types.TYPE_STRING.toString() );
    element1.setCapabilities( elementCapabilities );
    element1.setIsQueryElement( true );
    elements[1] = element1;

    elementCapabilities.put( Element.CAPABILITY_CAN_FILTER, "false" );
    Element element2 = new Element();
    element2.setElementType( FieldType.FACT.name() );
    element2.setHorizontalAlignment( Alignment.RIGHT.name() );
    element2.setId( "element2" );
    element2.setName( "Element 2" );
    element2.setDataType( Types.TYPE_NUMERIC.toString() );
    element2.setFormatMask( "#,###.00" );
    element2.setCapabilities( elementCapabilities );
    element2.setIsQueryElement( true );
    element2.setAvailableAggregations( new String[] { "SUM", "MIN" } );
    element2.setDefaultAggregation( "SUM" );
    element2.setHiddenForUser( false );
    element2.setParentId( "element1" );
    elements[2] = element2;

    model.setElements( elements );

    model.setGroupId( info.getGroupId() );
    model.setDescription( info.getDescription() );
    model.setModelId( info.getModelId() );
    model.setName( info.getName() );
    model.setProvider( provider );

    HashMap<String, String> capabilities = new HashMap<String, String>();
    capabilities.put( "across-axis", "true" );
    capabilities.put( "across-axis-customizable", "true" );
    capabilities.put( "down-axis", "false" );
    capabilities.put( "down-axis-customizable", "false" );
    capabilities.put( "filter-axis", "false" );
    capabilities.put( "filter-axis-customizable", "false" );
    capabilities.put( "sortable", "false" );

    model.setCapabilities( capabilities );

    return model;

  }

  public DataTable executeQuery( Query query, int rowLimit ) {

    // find out which stats are being requested
    Element[] qColumns = query.getElements();
    List<org.pentaho.metadata.datatable.Column> tableColumnList =
        new ArrayList<org.pentaho.metadata.datatable.Column>();
    for ( Element qColumn : qColumns ) {
      org.pentaho.metadata.datatable.Column col = createDataTableColumn( qColumn );
      if ( col != null ) {
        tableColumnList.add( col );
      }
    }

    Cell[] cells = new Cell[tableColumnList.size()];

    int idx = 0;
    for ( Element element : qColumns ) {
      if ( "element1".equals( element.getId() ) ) {
        cells[idx] = new Cell( null, "text value 1" );
      } else if ( "element2".equals( element.getId() ) ) {
        cells[idx] = new Cell( new BigDecimal( 99 ), null );
      }

      idx++;
    }

    Row row = new Row( cells );
    List<Row> rowList = new ArrayList<Row>();
    rowList.add( row );

    org.pentaho.metadata.datatable.Column[] tableColumns =
        tableColumnList.toArray( new org.pentaho.metadata.datatable.Column[tableColumnList.size()] );
    Row[] rows = rowList.toArray( new Row[rowList.size()] );
    DataTable dataTable = new DataTable();
    dataTable.setCols( tableColumns );
    dataTable.setRows( rows );
    return dataTable;
  }

  protected org.pentaho.metadata.datatable.Column createDataTableColumn( Element qColumn ) {
    String id = qColumn.getId();
    if ( "element1".equals( id ) ) {
      return new org.pentaho.metadata.datatable.Column( "element1", "Element 1", "string" );
    }
    if ( "element2".equals( id ) ) {
      return new org.pentaho.metadata.datatable.Column( "element2", "Element 2", "number" );
    }
    return null;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

}
