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
package org.pentaho.metadata.model.concept.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores constraints that are used to filter rows. Row level security is specified at the business model level.
 * 
 * WG: Consider rewriting global and rolebased into separate classes, inheriting from a common interface
 * 
 * @author mlowery
 */
public class RowLevelSecurity implements Serializable {

  private static final long serialVersionUID = 8053164409342554552L;
  private static final String EMPTY_STRING = "";
  
  public static enum Type {
    NONE, GLOBAL, ROLEBASED
  }

  private Type type;

  /**
   * See {@link #getType()}. Type will tell you which of three options are in effect: no RLS, global, or role-based.
   */
  private String globalConstraint = "";

  /**
   * See {@link #getType()}. Type will tell you which of three options are in effect: no RLS, global, or role-based.
   */
  private Map<SecurityOwner, String> roleBasedConstraintMap = new HashMap<SecurityOwner, String>();

  public RowLevelSecurity(String globalConstraint) {
    this(Type.GLOBAL, globalConstraint, null);
  }

  public RowLevelSecurity(Map<SecurityOwner, String> roleBasedConstraintMap) {
    this(Type.ROLEBASED, null, roleBasedConstraintMap);
  }

  public RowLevelSecurity() {
    this(Type.NONE, null, null);
  }

  public RowLevelSecurity(Type type, String globalConstraint, Map<SecurityOwner, String> roleBasedConstraintMap) {
    setGlobalConstraint(globalConstraint);
    setRoleBasedConstraintMap(roleBasedConstraintMap);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getGlobalConstraint() {
    return globalConstraint;
  }

  public void setGlobalConstraint(String globalConstraint) {
    if (globalConstraint != null) {
      this.globalConstraint = globalConstraint;
    } else {
      this.globalConstraint = EMPTY_STRING;
    }
  }

  public Map<SecurityOwner, String> getRoleBasedConstraintMap() {
    return roleBasedConstraintMap;
  }

  public void setRoleBasedConstraintMap(Map<SecurityOwner, String> roleBasedConstraintMap) {
    if (roleBasedConstraintMap != null) {
      this.roleBasedConstraintMap = roleBasedConstraintMap;
    } else {
      this.roleBasedConstraintMap = new HashMap<SecurityOwner, String>();
    }
  }

  @Override
  public boolean equals(Object object) {
    RowLevelSecurity r = (RowLevelSecurity)object;
    return  
    getType() == r.getType() &&
    getGlobalConstraint().equals(r.getGlobalConstraint()) &&
    getRoleBasedConstraintMap().equals(r.getRoleBasedConstraintMap());    
  }

}
