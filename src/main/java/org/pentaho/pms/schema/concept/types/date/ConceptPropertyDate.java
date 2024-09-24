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
package org.pentaho.pms.schema.concept.types.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyDate extends ConceptPropertyBase implements Cloneable {
  private Date value;

  public ConceptPropertyDate( String name, Date value ) {
    this( name, value, false );
  }

  public ConceptPropertyDate( String name, Date value, boolean required ) {
    super( name, required );
    this.value = value;
  }

  public String toString() {
    if ( value == null ) {
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat( ConceptPropertyType.ISO_DATE_FORMAT );
    return sdf.format( value );
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyDate rtn = (ConceptPropertyDate) super.clone();
    if ( value != null ) {
      rtn.value = new Date( value.getTime() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.DATE;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (Date) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
