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
 * Copyright (c) 2018 - 2018 Hitachi Vantara. All rights reserved.
 */
package org.pentaho.pms.schema.concept.types.timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyTimestamp extends ConceptPropertyBase implements Cloneable {
  private Date value;

  public ConceptPropertyTimestamp( String name, Date value ) {
    this( name, value, false );
  }

  public ConceptPropertyTimestamp( String name, Date value, boolean required ) {
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
    ConceptPropertyTimestamp rtn = (ConceptPropertyTimestamp) super.clone();
    if ( value != null ) {
      rtn.value = new Date( value.getTime() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.TIMESTAMP;
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
