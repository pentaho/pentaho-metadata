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
package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.Property;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;

/**
 * this is the SQL implementation of physical column.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
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
    Property property = getProperty( IPhysicalColumn.DATATYPE_PROPERTY );
    if( property != null ) {
      return (DataType) property.getValue();
    }
    return null;
  }

  public void setDataType( DataType dataType ) {
    setProperty( IPhysicalColumn.DATATYPE_PROPERTY, new Property<DataType>( dataType ) );
  }

  public FieldType getFieldType() {
    Property property = getProperty( IPhysicalColumn.FIELDTYPE_PROPERTY );
    if( property != null ) {
      return (FieldType) property.getValue();
    }    
    return null;
  }

  public void setFieldType( FieldType fieldType ) {
    setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, new Property<FieldType>( fieldType ) );
  }

  public AggregationType getAggregationType() {
    Property property = getProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY );
    if( property != null ) {
      return (AggregationType) property.getValue();
    }    
    return null;
  }

  public void setAggregationType( AggregationType aggType ) {
    setProperty( IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, new Property<AggregationType>( aggType ) );
  }

  @SuppressWarnings( "unchecked" )
  public List<AggregationType> getAggregationList() {
    Property property = getProperty( IPhysicalColumn.AGGREGATIONLIST_PROPERTY );
    if( property != null ) {
      return (List<AggregationType>) property.getValue();
    }    
    return null;
  }

  public void setAggregationList( List<AggregationType> aggList ) {
    setProperty( IPhysicalColumn.AGGREGATIONLIST_PROPERTY, new Property<List<AggregationType>>( aggList ) );
  }
  
  public String getTargetColumn() {
    Property property = getProperty( TARGET_COLUMN );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setTargetColumn( String targetTable ) {
    setProperty( TARGET_COLUMN, new Property<String>( targetTable ) );
  }

  public TargetColumnType getTargetColumnType() {
    Property property = getProperty( TARGET_COLUMN_TYPE );
    if( property != null ) {
      return (TargetColumnType) property.getValue();
    }
    return null;
  }

  public void setTargetColumnType( TargetColumnType targetTableType ) {
    setProperty( TARGET_COLUMN_TYPE, new Property<TargetColumnType>( targetTableType ) );
  }

  public IPhysicalTable getPhysicalTable() {
    return ( IPhysicalTable ) getParent();
  }

}
