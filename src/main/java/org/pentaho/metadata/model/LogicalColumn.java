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

package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;

/**
 * The logical column a logical table as a parent. It inherits from a physical column. Also, the logical column may
 * exist in one or more categories.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class LogicalColumn extends Concept {

  private static final long serialVersionUID = -5818193472199662859L;

  public LogicalColumn() {
    super();
  }

  @Override
  public IConcept getSecurityParentConcept() {
    return getLogicalTable();
  }

  public IPhysicalColumn getPhysicalColumn() {
    return (IPhysicalColumn) getInheritedConcept();
  }

  public void setPhysicalColumn( IPhysicalColumn physicalColumn ) {
    setInheritedConcept( physicalColumn );
  }

  public DataType getDataType() {
    return (DataType) getProperty( IPhysicalColumn.DATATYPE_PROPERTY );
  }

  public void setDataType( DataType dataType ) {
    setProperty( IPhysicalColumn.DATATYPE_PROPERTY, dataType );
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

  public void setLogicalTable( LogicalTable logicalTable ) {
    setParent( logicalTable );
  }

  public LogicalTable getLogicalTable() {
    return (LogicalTable) getParent();
  }

  public FieldType getFieldType() {
    return (FieldType) getProperty( IPhysicalColumn.FIELDTYPE_PROPERTY );
  }

  public void setFieldType( FieldType fieldType ) {
    setProperty( IPhysicalColumn.FIELDTYPE_PROPERTY, fieldType );
  }

  public Object clone() {
    LogicalColumn clone = new LogicalColumn();
    clone.setId( getId() );
    clone.setParent( getParent() );
    clone.setInheritedConcept( getInheritedConcept() );
    clone.setParentConcept( getParentConcept() );

    // copy over properties
    for ( String key : getChildProperties().keySet() ) {
      clone.setProperty( key, getChildProperty( key ) );
    }
    return clone;
  }
}
