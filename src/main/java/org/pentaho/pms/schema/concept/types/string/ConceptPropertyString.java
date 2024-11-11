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

package org.pentaho.pms.schema.concept.types.string;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyString extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable {
  private String value;
  private static final String EMPTY_STRING = "";

  public ConceptPropertyString( String name, String value ) {
    this( name, value, false );
  }

  public ConceptPropertyString( String name, String value, boolean required ) {
    super( name, required );
    setValue( value );
  }

  public String toString() {
    return value;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.STRING;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    if ( null != value ) {
      this.value = (String) value;
    } else {
      this.value = EMPTY_STRING;
    }
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
