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
package org.pentaho.pms.schema.concept.types.url;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyURL extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable {
  private URL value;

  public ConceptPropertyURL( String name, URL value ) {
    this( name, value, false );
  }

  public ConceptPropertyURL( String name, URL value, boolean required ) {
    super( name, required );
    this.value = value;
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyURL rtn = (ConceptPropertyURL) super.clone();
    if ( value != null ) {
      try {
        rtn.value = new URL( value.toString() );
      } catch ( MalformedURLException ignored ) {
        // ignored
      }
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.URL;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (URL) value;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptPropertyURL == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ConceptPropertyURL rhs = (ConceptPropertyURL) obj;
    return new EqualsBuilder().append( value, rhs.value ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 79, 223 ).append( value ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( value ).toString();
  }

}
