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

import java.util.HashMap;
import java.util.Map;

import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * This is the base implementation of a concept, and may be used in generic terms
 * when defining parent concepts or modelling metadata.  More concrete implementations
 * extend the Concept class within Pentaho Metadata, found in the metadata.model package
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Concept implements IConcept {

  protected static String NAME_PROPERTY = "name";
  protected static String DESCRIPTION_PROPERTY = "description";
  protected static String SECURITY_PROPERTY = "security";
  
  Map<String, Object> properties = new HashMap<String, Object>();
  String id;
  IConcept parent;
  
  public Map<String, Object> getChildProperties() {
    return properties;
  }

  public Object getChildProperty(String name) {
    return properties.get(name);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public IConcept getInheritedConcept() {
    return null;
  }
  

  public IConcept getParentConcept() {
    return parent;
  }
  
  public void setParentConcept(IConcept parent) {
    this.parent = parent;
  }

  
  public IConcept getSecurityParentConcept() {
    return null;
  }
  
  public Map<String, Object> getProperties() {
    Map<String,Object> all = new HashMap<String,Object>();

    // Properties inherited from the "logical relationship": 
    // BusinessColumn inherits from Physical Column, B.Table from Ph.Table
    if (getInheritedConcept() != null) {
      all.putAll(getInheritedConcept().getProperties());
    }

    // Properties inherited from the pre-defined concepts like 
    // "Base", "ID", "Name", "Description", etc.
    //
    if (parent != null) {
      all.putAll(parent.getProperties());
    }

    // The security settings from the security parent: 
    // Business table inherits from Business model, business column from business table
    if (getSecurityParentConcept() != null) {
      // Only take over the security information, nothing else
      Object securityProperty = (Object) getSecurityParentConcept().getProperty(SECURITY_PROPERTY);
      if (securityProperty!=null) {
        all.put(id, securityProperty);
      }
    }

    // The child properties overwrite everything else.
    all.putAll(properties);

    return all;
  }

  public Object getProperty(String name) {
    return getProperties().get(name);
  }

  public void setProperty(String name, Object property) {
    properties.put(name, property);
  }

  public void removeChildProperty(String name) {
    properties.remove(name);
  }
  
  public LocalizedString getName() {
    return (LocalizedString)getProperty(NAME_PROPERTY);
  }
  
  public void setName(LocalizedString name) {
    setProperty(NAME_PROPERTY, name);
  }
  
  public LocalizedString getDescription() {
    return (LocalizedString)getProperty(DESCRIPTION_PROPERTY);
  }
  
  public void setDescription(LocalizedString description) {
    setProperty(NAME_PROPERTY, description);
  }
}
