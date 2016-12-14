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
 * Copyright (c) 2016 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * The Inline Etl column inherits from the abstract physical column, and also defines a column number to get data from.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class InlineEtlPhysicalColumn extends Concept implements IPhysicalColumn {

  private static final long serialVersionUID = 2960505010295811572L;

  public static final String COLUMN_NUMBER = "column_number"; //$NON-NLS-1$
  public static final String FIELD_NAME = "field_name"; //$NON-NLS-1$

  public InlineEtlPhysicalColumn() {
    super();
    // physical column has the following default properties:
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
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

  public void setTable( InlineEtlPhysicalTable table ) {
    setParent( table );
  }

  public InlineEtlPhysicalTable getPhysicalTable() {
    return (InlineEtlPhysicalTable) getParent();
  }

  public String getFieldName() {
    return (String) getProperty( FIELD_NAME );
  }

  public void setFieldName( String fieldName ) {
    setProperty( FIELD_NAME, fieldName );
  }

}
