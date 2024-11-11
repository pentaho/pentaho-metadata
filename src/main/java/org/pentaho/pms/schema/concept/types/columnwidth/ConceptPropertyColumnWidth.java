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

package org.pentaho.pms.schema.concept.types.columnwidth;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyColumnWidth extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyColumnWidth PIXELS = new ConceptPropertyColumnWidth(
      "aggregation", ColumnWidth.PIXELS ); //$NON-NLS-1$
  public static final ConceptPropertyColumnWidth PERCENT = new ConceptPropertyColumnWidth(
      "aggregation", ColumnWidth.PERCENT ); //$NON-NLS-1$
  public static final ConceptPropertyColumnWidth INCHES = new ConceptPropertyColumnWidth(
      "aggregation", ColumnWidth.INCHES ); //$NON-NLS-1$
  public static final ConceptPropertyColumnWidth CM = new ConceptPropertyColumnWidth( "aggregation", ColumnWidth.CM ); //$NON-NLS-1$

  private ColumnWidth value;

  public ConceptPropertyColumnWidth( String name, ColumnWidth value ) {
    this( name, value, false );
  }

  public ConceptPropertyColumnWidth( String name, ColumnWidth value, boolean required ) {
    super( name, required );
    this.value = value;
  }

  public String toString() {
    if ( value == null ) {
      return null;
    }
    return value.toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyColumnWidth rtn = (ConceptPropertyColumnWidth) super.clone();
    if ( value != null ) {
      rtn.value = new ColumnWidth( value.getType(), value.getWidth() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.COLUMN_WIDTH;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (ColumnWidth) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
