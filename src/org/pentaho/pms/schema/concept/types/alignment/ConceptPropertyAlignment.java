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
package org.pentaho.pms.schema.concept.types.alignment;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyAlignment extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyAlignment LEFT =
      new ConceptPropertyAlignment( "alignment", AlignmentSettings.LEFT ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment RIGHT = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.RIGHT ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment CENTERED = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.CENTERED ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment JUSTIFIED = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.JUSTIFIED ); //$NON-NLS-1$

  private AlignmentSettings value;

  public ConceptPropertyAlignment( String name, AlignmentSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyAlignment( String name, AlignmentSettings value, boolean required ) {
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
    ConceptPropertyAlignment rtn = (ConceptPropertyAlignment) super.clone();
    if ( value != null ) {
      rtn.value = new AlignmentSettings( value.getType() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.ALIGNMENT;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (AlignmentSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
