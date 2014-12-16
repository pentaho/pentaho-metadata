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
package org.pentaho.pms.schema.concept;

import org.pentaho.metadata.model.concept.Property;
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
  public Property getValue();

  /**
   * @param value
   *          the value of this property to set
   */
  public void setValue( Property value );

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
