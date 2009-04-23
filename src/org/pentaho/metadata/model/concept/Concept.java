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

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * This is the base implementation of a concept, and may be used in generic terms
 * when defining parent concepts or modelling metadata.  More concrete implementations
 * extend the Concept class within Pentaho Metadata, found in the metadata.model package
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Concept implements IConcept {

  Map<String, Object> properties = new HashMap<String, Object>();
  String id;
  IConcept inherited;
  IConcept parent;
  IConcept security;
  
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
    return inherited;
  }
  
  public void setInheritedConcept(IConcept inherited) {
    this.inherited = inherited;
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
  
  public void setSecurityParentConcept(IConcept security) {
    this.security = security;
  }

  public Map<String, Object> getProperties() {
    Map<String,Object> all = new HashMap<String,Object>();

    // Properties inherited from the "logical relationship": 
    // BusinessColumn inherits from Physical Column, B.Table from Ph.Table
    if (inherited != null) {
      all.putAll(inherited.getProperties());
    }

    // Properties inherited from the pre-defined concepts like 
    // "Base", "ID", "Name", "Description", etc.
    //
    if (parent != null) {
      all.putAll(parent.getProperties());
    }

    // The security settings from the security parent: 
    // Business table inherits from Business model, business column from business table
    if (security != null) {
      // Only take over the security information, nothing else
      String id = DefaultPropertyID.SECURITY.getId();
      Object securityProperty = (Object) security.getProperty(id);
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

}
