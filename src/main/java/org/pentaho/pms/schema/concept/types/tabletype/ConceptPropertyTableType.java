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
package org.pentaho.pms.schema.concept.types.tabletype;

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

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (TableTypeSettings) value;
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
