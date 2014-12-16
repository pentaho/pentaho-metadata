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
package org.pentaho.pms.schema.concept.types.tabletype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.metadata.model.concept.Property;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyTableType extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable {
  public static final ConceptPropertyTableType DEFAULT_OTHER = new ConceptPropertyTableType(
      "tabletype", TableTypeSettings.OTHER ); //$NON-NLS-1$
  public static final ConceptPropertyTableType DEFAULT_DIMENSION = new ConceptPropertyTableType(
      "tabletype", TableTypeSettings.DIMENSION ); //$NON-NLS-1$
  public static final ConceptPropertyTableType DEFAULT_FACT = new ConceptPropertyTableType(
      "tabletype", TableTypeSettings.FACT ); //$NON-NLS-1$

  private TableTypeSettings value;

  public ConceptPropertyTableType( String name, TableTypeSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyTableType( String name, TableTypeSettings value, boolean required ) {
    super( name, required );
    if ( null != value ) {
      this.value = value;
    } else {
      this.value = TableTypeSettings.OTHER;
    }
  }

  public Object clone() throws CloneNotSupportedException {
    ConceptPropertyTableType rtn = (ConceptPropertyTableType) super.clone();
    if ( value != null ) {
      rtn.value = new TableTypeSettings( value.getType() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.TABLETYPE;
  }

  public Property getValue() {
    return new Property<TableTypeSettings>( value );
  }

  public void setValue( Property value ) {
    this.value = value != null ? (TableTypeSettings) value.getValue() : null;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptPropertyTableType == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ConceptPropertyTableType rhs = (ConceptPropertyTableType) obj;
    return new EqualsBuilder().append( value, rhs.value ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 47, 157 ).append( value ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( value ).toString();
  }
}
