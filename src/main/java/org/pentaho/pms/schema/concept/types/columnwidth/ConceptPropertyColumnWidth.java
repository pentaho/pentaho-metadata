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
