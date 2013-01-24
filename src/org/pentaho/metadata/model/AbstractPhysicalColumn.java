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
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * this class implements the some of the shared functionality required by all
 * physical columns.
 *  
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public abstract class AbstractPhysicalColumn extends Concept implements IPhysicalColumn {

  private static final long serialVersionUID = 8636970915875556072L;

  public AbstractPhysicalColumn() {
    // physical column has the following default properties:
    setName(new LocalizedString());
    setDescription(new LocalizedString());
  }
  
  public DataType getDataType() {
    return (DataType)getProperty(IPhysicalColumn.DATATYPE_PROPERTY);
  }

  public void setDataType(DataType dataType) {
    setProperty(IPhysicalColumn.DATATYPE_PROPERTY, dataType);
  }
  
  public FieldType getFieldType() {
    return (FieldType)getProperty(IPhysicalColumn.FIELDTYPE_PROPERTY);
  }
  
  public void setFieldType(FieldType fieldType) {
    setProperty(IPhysicalColumn.FIELDTYPE_PROPERTY, fieldType);
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

}
