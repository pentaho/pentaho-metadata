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
  public IConcept getParent() {
    return logicalTable;
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
  
  public Object clone() {
    LogicalColumn clone = new LogicalColumn();
    clone.setId(getId());
    clone.logicalTable = logicalTable;
    clone.physicalColumn = physicalColumn;
    clone.setParentConcept(getParentConcept());
    
    // copy over properties
    for (String key : getChildProperties().keySet()) {
      clone.setProperty(key, getChildProperty(key));
    }
    return clone;
  }
}
