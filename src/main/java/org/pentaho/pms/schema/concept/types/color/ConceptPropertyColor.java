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

package org.pentaho.pms.schema.concept.types.color;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyColor extends ConceptPropertyBase implements Cloneable {
  private ColorSettings value;

  public ConceptPropertyColor( String name, ColorSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyColor( String name, ColorSettings value, boolean required ) {
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
    ConceptPropertyColor rtn = (ConceptPropertyColor) super.clone();
    if ( value != null ) {
      rtn.value = new ColorSettings( value.red, value.green, value.blue );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.COLOR;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (ColorSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
