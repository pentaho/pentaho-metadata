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
  public static final String FIELDTYPE_PROPERTY = "fieldtype"; //$NON-NLS-1$
  public static final String DATATYPE_PROPERTY = "datatype"; //$NON-NLS-1$
  public static final String AGGREGATIONTYPE_PROPERTY = "aggregation"; //$NON-NLS-1$
  public static final String AGGREGATIONLIST_PROPERTY = "aggregation_list"; //$NON-NLS-1$

  public DataType getDataType();

  public void setDataType( DataType dataType );

  public FieldType getFieldType();

  public void setFieldType( FieldType fieldType );

  public AggregationType getAggregationType();

  public void setAggregationType( AggregationType aggType );

  public List<AggregationType> getAggregationList();

  public void setAggregationList( List<AggregationType> aggList );

  public IPhysicalTable getPhysicalTable();
}
