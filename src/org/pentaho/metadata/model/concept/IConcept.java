/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
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
public interface IConcept {

  /** @return get the id of the property */
  public String getId();

  /** @param id the property id to set */
  public void setId(String id);
  
  /** @return get the localized name */
  public LocalizedString getName();
  
  /** @param name the localized name of the concept */
  public void setName(LocalizedString name);

  /** @return get the localized description */
  public LocalizedString getDescription();
  
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
  
}
