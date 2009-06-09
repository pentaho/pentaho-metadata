/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;

/**
 * The logical column a logical table as a parent.  It inherits from a physical column.
 * Also, the logical column may exist in one or more categories.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LogicalColumn extends Concept {

  private static final long serialVersionUID = -5818193472199662859L;

  private LogicalTable logicalTable;
  private IPhysicalColumn physicalColumn;

  public LogicalColumn() {
    super();
  }
  
  @Override
  public IConcept getInheritedConcept() {
    return physicalColumn;
  }
  
  @Override
  public IConcept getSecurityParentConcept() {
    return getLogicalTable();
  }
  
  public IPhysicalColumn getPhysicalColumn() {
    return physicalColumn;
  }

  public void setPhysicalColumn(IPhysicalColumn physicalColumn) {
    this.physicalColumn = physicalColumn;
  }

  public DataType getDataType() {
    return (DataType)getProperty(IPhysicalColumn.DATATYPE_PROPERTY);
  }

  public void setDataType(DataType dataType) {
    setProperty(IPhysicalColumn.DATATYPE_PROPERTY, dataType);
  }

  public AggregationType getAggregationType() {
    return (AggregationType)getProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY);
  }

  public void setAggregationType(AggregationType aggType) {
    setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, aggType);
  }
  
  @SuppressWarnings("unchecked")
  public List<AggregationType> getAggregationList() {
    return (List<AggregationType>)getProperty(IPhysicalColumn.AGGREGATIONLIST_PROPERTY);
  }

  public void setAggregationList(List<AggregationType> aggList) {
    setProperty(IPhysicalColumn.AGGREGATIONLIST_PROPERTY, aggList);
  }

  public void setLogicalTable(LogicalTable logicalTable) {
    this.logicalTable = logicalTable;
  }

  public LogicalTable getLogicalTable() {
    return logicalTable;
  }
  
  public FieldType getFieldType() {
    return (FieldType)getProperty(IPhysicalColumn.FIELDTYPE_PROPERTY);
  }
  
  public void setFieldType(FieldType fieldType) {
    setProperty(IPhysicalColumn.FIELDTYPE_PROPERTY, fieldType);
  }
  
}
