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
package org.pentaho.pms.schema.concept.types.fieldtype;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyFieldType extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyFieldType DEFAULT_OTHER = new ConceptPropertyFieldType(
      "fieldtype", FieldTypeSettings.OTHER ); //$NON-NLS-1$
  public static final ConceptPropertyFieldType DEFAULT_DIMENSION = new ConceptPropertyFieldType(
      "fieldtype", FieldTypeSettings.DIMENSION ); //$NON-NLS-1$
  public static final ConceptPropertyFieldType DEFAULT_FACT = new ConceptPropertyFieldType(
      "fieldtype", FieldTypeSettings.FACT ); //$NON-NLS-1$
  public static final ConceptPropertyFieldType DEFAULT_KEY = new ConceptPropertyFieldType(
      "fieldtype", FieldTypeSettings.KEY ); //$NON-NLS-1$

  private FieldTypeSettings value;

  public ConceptPropertyFieldType( String name, FieldTypeSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyFieldType( String name, FieldTypeSettings value, boolean required ) {
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
    ConceptPropertyFieldType rtn = (ConceptPropertyFieldType) super.clone();
    if ( value != null ) {
      rtn.value = new FieldTypeSettings( value.getType() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.FIELDTYPE;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (FieldTypeSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
