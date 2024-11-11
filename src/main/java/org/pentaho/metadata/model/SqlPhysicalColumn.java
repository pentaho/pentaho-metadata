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

package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;

/**
 * this is the SQL implementation of physical column.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class SqlPhysicalColumn extends Concept implements IPhysicalColumn {

  private static final long serialVersionUID = -9131564777458111496L;

  public static final String TARGET_COLUMN = "target_column"; //$NON-NLS-1$
  public static final String TARGET_COLUMN_TYPE = "target_column_type"; //$NON-NLS-1$

  public SqlPhysicalColumn() {
    super();
    setTargetColumnType( TargetColumnType.COLUMN_NAME );
    // physical column has the following default properties:
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
  }

  public SqlPhysicalColumn( SqlPhysicalTable table ) {
    this();
    setParent( table );
  }

  public DataType getDataType() {
    return (DataType) getProperty( IPhysicalColumn.DATATYPE_PROPERTY );
  }

  public void setDataType( DataType dataType ) {
    setProperty( IPhysicalColumn.DATATYPE_PROPERTY, dataType );
  }

  public FieldType getFieldType() {
    return (FieldType) getProperty( IPhysicalColumn.FIELDTYPE_PROPERTY );
  }

  public void setFieldType( FieldType fieldType ) {
    setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, fieldType );
  }

  public AggregationType getAggregationType() {
    return (AggregationType) getProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY );
  }

  public void setAggregationType( AggregationType aggType ) {
    setProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, aggType );
  }

  @SuppressWarnings( "unchecked" )
  public List<AggregationType> getAggregationList() {
    return (List<AggregationType>) getProperty( IPhysicalColumn.AGGREGATIONLIST_PROPERTY );
  }

  public void setAggregationList( List<AggregationType> aggList ) {
    setProperty( IPhysicalColumn.AGGREGATIONLIST_PROPERTY, aggList );
  }

  public String getTargetColumn() {
    return (String) getProperty( TARGET_COLUMN );
  }

  public void setTargetColumn( String targetTable ) {
    setProperty( TARGET_COLUMN, targetTable );
  }

  public TargetColumnType getTargetColumnType() {
    return (TargetColumnType) getProperty( TARGET_COLUMN_TYPE );
  }

  public void setTargetColumnType( TargetColumnType targetTableType ) {
    setProperty( TARGET_COLUMN_TYPE, targetTableType );
  }

  public IPhysicalTable getPhysicalTable() {
    return (IPhysicalTable) getParent();
  }

}
