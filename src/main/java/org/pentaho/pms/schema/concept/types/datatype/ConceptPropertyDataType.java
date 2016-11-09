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
 * Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */
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
