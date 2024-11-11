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

package org.pentaho.pms.schema.concept.types.aggregation;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyAggregation extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyAggregation NONE = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.NONE ); //$NON-NLS-1$
  public static final ConceptPropertyAggregation SUM = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.SUM ); //$NON-NLS-1$
  public static final ConceptPropertyAggregation AVERAGE = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.AVERAGE ); //$NON-NLS-1$
  public static final ConceptPropertyAggregation COUNT = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.COUNT ); //$NON-NLS-1$
  public static final ConceptPropertyAggregation MINIMUM = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.MINIMUM ); //$NON-NLS-1$
  public static final ConceptPropertyAggregation MAXIMUM = new ConceptPropertyAggregation(
      "aggregation", AggregationSettings.MAXIMUM ); //$NON-NLS-1$

  private AggregationSettings value;

  public ConceptPropertyAggregation( String name, AggregationSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyAggregation( String name, AggregationSettings value, boolean required ) {
    super( name, required );
    this.value = value;
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( getId() ).append( isRequired() )
        .append( value ).toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyAggregation rtn = (ConceptPropertyAggregation) super.clone();
    if ( value != null ) {
      rtn.value = new AggregationSettings( value.getType() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.AGGREGATION;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (AggregationSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
