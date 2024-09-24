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
package org.pentaho.pms.schema.concept;

import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

/**
 * The interface that describes a concept property
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0. in the new model, these are just Serializable objects.
 */
public interface ConceptPropertyInterface extends Cloneable {
  /** @return the concept property type */
  public ConceptPropertyType getType();

  /** @return a string representation for this concept property */
  public String toString();

  /** @return get the id of the property */
  public String getId();

  /**
   * @param id
   *          the property id to set
   */
  public void setId( String id );

  /** @return the value of this property */
  public Object getValue();

  /**
   * @param value
   *          the value of this property to set
   */
  public void setValue( Object value );

  public boolean equals( Object obj );

  public int hashCode();

  public Object clone() throws CloneNotSupportedException;

  // /**
  // * @return a copy of the concept property
  // * @throws CloneNotSupportedException
  // */
  // public Object clone() throws CloneNotSupportedException;

  /**
   * Returns <code>true</code> if this property is a default property for this concept, otherwise <code>false</code>. A
   * property's default status varies from subject to subject.
   */
  public boolean isRequired();

  public void setRequired( boolean required );
}
