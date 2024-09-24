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
package org.pentaho.pms.schema.concept.types.bool;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyBoolean extends ConceptPropertyBase implements Cloneable {
  private Boolean value;

  public ConceptPropertyBoolean( String name, Boolean value ) {
    this( name, null != value ? value.booleanValue() : false );
  }

  public ConceptPropertyBoolean( String name, boolean value ) {
    this( name, value, false );
  }

  public ConceptPropertyBoolean( String name, boolean value, boolean required ) {
    super( name, required );
    this.value = new Boolean( value );
  }

  public String toString() {
    if ( value == null ) {
      return null;
    }
    return value.booleanValue() ? "Y" : "N"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyBoolean rtn = (ConceptPropertyBoolean) super.clone();
    if ( value != null ) {
      rtn.value = new Boolean( value.booleanValue() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.BOOLEAN;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (Boolean) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
