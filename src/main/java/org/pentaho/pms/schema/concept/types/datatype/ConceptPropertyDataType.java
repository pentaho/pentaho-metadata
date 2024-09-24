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
package org.pentaho.pms.schema.concept.types.datatype;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyDataType extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyDataType UNKNOWN = new ConceptPropertyDataType(
      "datatype", DataTypeSettings.UNKNOWN ); //$NON-NLS-1$
  public static final ConceptPropertyDataType STRING =
      new ConceptPropertyDataType( "datatype", DataTypeSettings.STRING ); //$NON-NLS-1$
  public static final ConceptPropertyDataType DATE = new ConceptPropertyDataType( "datatype", DataTypeSettings.DATE ); //$NON-NLS-1$
  public static final ConceptPropertyDataType BOOLEAN = new ConceptPropertyDataType(
      "datatype", DataTypeSettings.BOOLEAN ); //$NON-NLS-1$
  public static final ConceptPropertyDataType NUMERIC = new ConceptPropertyDataType(
      "datatype", DataTypeSettings.NUMERIC ); //$NON-NLS-1$
  public static final ConceptPropertyDataType BINARY =
      new ConceptPropertyDataType( "datatype", DataTypeSettings.BINARY ); //$NON-NLS-1$
  public static final ConceptPropertyDataType IMAGE = new ConceptPropertyDataType( "datatype", DataTypeSettings.IMAGE ); //$NON-NLS-1$
  public static final ConceptPropertyDataType URL = new ConceptPropertyDataType( "datatype", DataTypeSettings.URL ); //$NON-NLS-1$

  private DataTypeSettings value;

  public ConceptPropertyDataType( String name, DataTypeSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyDataType( String name, DataTypeSettings value, boolean required ) {
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
    ConceptPropertyDataType rtn = (ConceptPropertyDataType) super.clone();
    if ( value != null ) {
      rtn.value = new DataTypeSettings( value.getType(), value.getLength(), value.getPrecision() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.DATATYPE;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (DataTypeSettings) value;
  }

  public boolean equals( Object obj ) {
    if ( value != null ) {
      return value.equals( obj );
    } else {
      return value == obj;
    }
  }

  public int hashCode() {
    if ( value != null ) {
      return value.hashCode();
    } else {
      return -1;
    }
  }
}
