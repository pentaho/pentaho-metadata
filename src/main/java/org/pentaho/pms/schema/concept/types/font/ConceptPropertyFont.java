/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

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
