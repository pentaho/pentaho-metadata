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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.concept;

import java.util.Map;

import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * this is the root container for Pentaho Metadata.  Metadata objects are concepts,
 * and concepts may have properties.
 * 
 * Concepts have three forms of inheritance
 * 
 * - the first form is inherit, which is derived from the relationships of 
 *   the metadata objects
 *   
 * - the second form is parent, which may be explicitly configured in the UI 
 * 
 * - the third form is security parent, which is derived from the relationships
 *   of the metadata objects and only applies to the security types.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface IConcept extends Cloneable {

  /** @return get the id of the property */
  public String getId();

  /** @param id the property id to set */
  public void setId(String id);
  
  /** @return get the localized name */
  public LocalizedString getName();

  /** 
   * @param locale the locale to resolve the name to 
   * @return the name of the concept
   */
  public String getName(String locale);
  
  /** @param name the localized name of the concept */
  public void setName(LocalizedString name);

  
  /** @return get the localized description */
  public LocalizedString getDescription();

  /** 
   * @param locale the locale to resolve the description to 
   * @return the description of the concept
   */
  public String getDescription(String locale);
  
  /** @param description the localized description */
  public void setDescription(LocalizedString description);

  /**
   * returns the active property for id
   * 
   * @param name 
   * 
   * @return concept property
   */
  public Object getProperty(String name);

  /**
   * sets the property
   * 
   * @param property
   */
  public void setProperty(String name, Object property);
  
  /**
   * removes the property
   * 
   * @param property
   */
  public void removeChildProperty(String name);
  
  /**
   * returns the local property for id
   * 
   * @param name
   * @return
   */
  public Object getChildProperty(String name);
  
  /**
   * this is an unmodifiable map of properties
   * 
   * @return property
   */
  public Map<String, Object> getProperties();

  /**
   * this is an unmodifiable map of the current concept properties
   * 
   * @return property
   */  
  public Map<String, Object> getChildProperties();
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getInheritedConcept();
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getParentConcept();
  
  /**
   * sets the parent concept
   * 
   * @param concept inherited concept
   */
  public void setParentConcept(IConcept concept);
  
  /**
   * returns the inherited concept
   * 
   * @return inherited concept
   */
  public IConcept getSecurityParentConcept();
  
  /**
   * returns a clone of the concept.
   * 
   * @return clone of concept object
   */
  public Object clone();
}
