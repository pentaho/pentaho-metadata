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
