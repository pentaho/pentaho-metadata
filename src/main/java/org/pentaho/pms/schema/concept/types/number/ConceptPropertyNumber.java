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

package org.pentaho.pms.schema.concept.types.number;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (BigDecimal) value;
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
