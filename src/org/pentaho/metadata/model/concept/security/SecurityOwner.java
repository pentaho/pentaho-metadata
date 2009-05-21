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


/**
 * A security owner is a combination of a user or role type and the name of that user or role.
 * 
 * @author Matt
 * @since 01-NOV-2006
 * 
 */
public class SecurityOwner implements Serializable {

  public static enum OwnerType {
    USER("SecurityOwner.USER_USER"), ROLE("SecurityOwner.USER_ROLE");

    String description;

    OwnerType(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  private OwnerType ownerType;
  private String ownerName;

  /**
   * @param ownerType the type of ACL owner : user or role
   * @param ownerName the name or the user or role
   * @param rights
   */
  public SecurityOwner(OwnerType ownerType, String ownerName) {
    this.ownerType = ownerType;
    this.ownerName = ownerName;
  }

  /**
   * @return the type of ACL : user or role
   */
  public OwnerType getOwnerType() {
    return ownerType;
  }

  /**
   * @param ownerType the type of owner to set: user or role
   */
  public void setOwnerType(OwnerType ownerType) {
    this.ownerType = ownerType;
  }

  /**
   * @return the name or the user or role
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * @param ownerName the name or the user or role
   */
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
}
