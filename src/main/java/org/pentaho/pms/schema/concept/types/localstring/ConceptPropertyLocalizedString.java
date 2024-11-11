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

package org.pentaho.pms.schema.concept.types.localstring;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyLocalizedString extends ConceptPropertyBase implements Cloneable {
  private LocalizedStringSettings value;

  public ConceptPropertyLocalizedString( String name, LocalizedStringSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyLocalizedString( String name, LocalizedStringSettings value, boolean required ) {
    super( name, required );
    if ( null != value ) {
      this.value = value;
    } else {
      this.value = new LocalizedStringSettings();
    }
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( getId() ).append( isRequired() )
        .append( value ).toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyLocalizedString locString = (ConceptPropertyLocalizedString) super.clone();
    if ( value != null ) {
      locString.value = (LocalizedStringSettings) value.clone();
    }
    return locString;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.LOCALIZED_STRING;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (LocalizedStringSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
