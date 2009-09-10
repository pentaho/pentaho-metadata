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
package org.pentaho.metadata.model.concept.security;

import java.io.Serializable;


/**
 * A security owner is a combination of a user or role type and the name of that user or role.
 * 
 * @author Matt
 * @since 01-NOV-2006
 * 
 */
public class SecurityOwner implements Serializable, Cloneable {

  private static final long serialVersionUID = 6657148420948786542L;

  public static enum OwnerType {
    USER("SecurityOwner.USER_USER"), ROLE("SecurityOwner.USER_ROLE");  //$NON-NLS-1$  //$NON-NLS-2$

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

  public SecurityOwner() {}
  
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
  
  @Override
  public boolean equals(Object object) {
    SecurityOwner s = (SecurityOwner)object;
    return  
      getOwnerType().equals(s.getOwnerType()) &&
      getOwnerName().equals(s.getOwnerName());
  }
}
