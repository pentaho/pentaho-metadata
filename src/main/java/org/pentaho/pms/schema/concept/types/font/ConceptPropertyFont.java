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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.schema.concept.types.font;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyFont extends ConceptPropertyBase implements Cloneable {
  private FontSettings value;

  public ConceptPropertyFont( String name, FontSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyFont( String name, FontSettings value, boolean required ) {
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
    ConceptPropertyFont rtn = (ConceptPropertyFont) super.clone();
    if ( value != null ) {
      rtn.value = new FontSettings( value.getName(), value.getHeight(), value.isBold(), value.isItalic() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.FONT;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (FontSettings) value;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptPropertyFont == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ConceptPropertyFont rhs = (ConceptPropertyFont) obj;
    return new EqualsBuilder().append( value, rhs.value ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 47, 71 ).append( value ).toHashCode();
  }
}
