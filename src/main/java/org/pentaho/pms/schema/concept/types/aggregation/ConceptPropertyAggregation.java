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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
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
