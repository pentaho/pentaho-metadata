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
package org.pentaho.pms.schema.concept.types.security;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.security.Security;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertySecurity extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable {
  private Security value;

  public ConceptPropertySecurity( String name, Security value ) {
    this( name, value, false );
  }

  public ConceptPropertySecurity( String name, Security value, boolean required ) {
    super( name, required );
    if ( null != value ) {
      this.value = value;
    } else {
      this.value = new Security();
    }
  }

  public String toString() {
    if ( value == null ) {
      return null;
    }
    return value.toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertySecurity clone = (ConceptPropertySecurity) super.clone();
    clone.setValue( value.clone() );
    return clone;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.SECURITY;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (Security) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
