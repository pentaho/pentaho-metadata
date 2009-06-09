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
import java.util.Set;

/**
 * Contains a mapping between a SecurityOwner (named user or role) and the rights (integer : masks with ACLs)
 * @author Matt
 *
 */
public class Security implements Serializable {

  private static final long serialVersionUID = 456798829969333527L;
  
  private Map<SecurityOwner, Integer> ownerAclMap;

  public Security() {
    ownerAclMap = new HashMap<SecurityOwner, Integer>();
  }

  /**
   * @param ownerAclMap
   */
  public Security(Map<SecurityOwner, Integer> ownerAclMap) {
    super();
    this.ownerAclMap = ownerAclMap;
  }

  /**
   * @param owner the owner to store the rights for
   * @param rights the ACLs to store
   */
  public void putOwnerRights(SecurityOwner owner, int rights) {
    ownerAclMap.put(owner, rights);
  }

  /**
   * @param owner the owner to get the rights for
   * @return the ACLs
   */
  public int getOwnerRights(SecurityOwner owner) {
    return ((Integer) ownerAclMap.get(owner)).intValue();
  }

  /**
   * Remove the rights out of the map for the given user
   * @param owner the owner to remove the rights for.
   */
  public void removeOwnerRights(SecurityOwner owner) {
    ownerAclMap.remove(owner);
  }

  /**
   * @return a list of all the owners in the map
   */
  public Set<SecurityOwner> getOwners() {
    return ownerAclMap.keySet();
  }

  /**
   * @return the ownerAclMap
   */
  public Map<SecurityOwner, Integer> getOwnerAclMap() {
    return ownerAclMap;
  }

  /**
   * @param ownerAclMap the ownerAclMap to set
   */
  public void setOwnerAclMap(Map<SecurityOwner, Integer> ownerAclMap) {
    this.ownerAclMap = ownerAclMap;
  }

  @Override
  public boolean equals(Object object) {
    Security s = (Security)object;
    return  
    getOwnerAclMap().equals(s.getOwnerAclMap());     
  }
}
