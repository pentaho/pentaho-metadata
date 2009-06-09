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

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;

/**
 * This interface defines the API for all physical columns.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface IPhysicalColumn extends IConcept {
  public static final String FIELDTYPE_PROPERTY = "fieldtype";
  public static final String DATATYPE_PROPERTY = "datatype";
  public static final String AGGREGATIONTYPE_PROPERTY = "aggregation";
  public static final String AGGREGATIONLIST_PROPERTY = "aggregation_list";
  public DataType getDataType();
  public void setDataType(DataType dataType);
  public FieldType getFieldType();
  public void setFieldType(FieldType fieldType);
  public AggregationType getAggregationType();
  public void setAggregationType(AggregationType aggType);
  public List<AggregationType> getAggregationList();
  public void setAggregationList(List<AggregationType> aggList);
  public IPhysicalTable getPhysicalTable();
}
