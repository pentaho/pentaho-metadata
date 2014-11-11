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
package org.pentaho.pms.schema.concept.types.number;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.metadata.model.concept.Property;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.LocalizedString
 */
public class ConceptPropertyNumber extends ConceptPropertyBase implements Cloneable {
  private BigDecimal value;

  public ConceptPropertyNumber( String name, BigDecimal value ) {
    this( name, value, false );
  }

  public ConceptPropertyNumber( String name, double value ) {
    this( name, new BigDecimal( value ), false );
  }

  public ConceptPropertyNumber( String name, long value ) {
    this( name, new BigDecimal( value ), false );
  }

  public ConceptPropertyNumber( String name, int value ) {
    this( name, new BigDecimal( value ), false );
  }

  public ConceptPropertyNumber( String name, byte value ) {
    this( name, new BigDecimal( value ), false );
  }

  public ConceptPropertyNumber( String name, BigDecimal value, boolean required ) {
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
    ConceptPropertyNumber rtn = (ConceptPropertyNumber) super.clone();
    if ( value != null ) {
      rtn.value = new BigDecimal( value.toString() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.NUMBER;
  }

  public Property getValue() {
    return new Property<BigDecimal>( value );
  }

  public void setValue( Property value ) {
    this.value = value != null ? (BigDecimal) value.getValue() : null;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptPropertyNumber == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ConceptPropertyNumber rhs = (ConceptPropertyNumber) obj;
    return new EqualsBuilder().append( value, rhs.value ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 137, 199 ).append( value ).toHashCode();
  }
}
