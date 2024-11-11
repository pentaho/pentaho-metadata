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

package org.pentaho.pms.schema.concept.types.alignment;

import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptPropertyAlignment extends ConceptPropertyBase implements Cloneable {
  public static final ConceptPropertyAlignment LEFT =
      new ConceptPropertyAlignment( "alignment", AlignmentSettings.LEFT ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment RIGHT = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.RIGHT ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment CENTERED = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.CENTERED ); //$NON-NLS-1$
  public static final ConceptPropertyAlignment JUSTIFIED = new ConceptPropertyAlignment(
      "alignment", AlignmentSettings.JUSTIFIED ); //$NON-NLS-1$

  private AlignmentSettings value;

  public ConceptPropertyAlignment( String name, AlignmentSettings value ) {
    this( name, value, false );
  }

  public ConceptPropertyAlignment( String name, AlignmentSettings value, boolean required ) {
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
    ConceptPropertyAlignment rtn = (ConceptPropertyAlignment) super.clone();
    if ( value != null ) {
      rtn.value = new AlignmentSettings( value.getType() );
    }
    return rtn;
  }

  public ConceptPropertyType getType() {
    return ConceptPropertyType.ALIGNMENT;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = (AlignmentSettings) value;
  }

  public boolean equals( Object obj ) {
    return value.equals( obj );
  }

  public int hashCode() {
    return value.hashCode();
  }
}
