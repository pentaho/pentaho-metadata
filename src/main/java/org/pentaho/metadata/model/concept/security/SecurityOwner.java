/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.model.concept.security;

import java.io.Serializable;

/**
 * A security owner is a combination of a user or role type and the name of that user or role.
 * 
 * @author Matt
 * @since 01-NOV-2006
 * 
 */
public class SecurityOwner implements Serializable, Cloneable, Comparable {

  private static final long serialVersionUID = 6657148420948786542L;

  public static enum OwnerType {
    USER( "SecurityOwner.USER_USER" ), ROLE( "SecurityOwner.USER_ROLE" ); //$NON-NLS-1$  //$NON-NLS-2$

    String description;

    OwnerType( String description ) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  private OwnerType ownerType;
  private String ownerName;

  public SecurityOwner() {
  }

  /**
   * @param ownerType
   *          the type of ACL owner : user or role
   * @param ownerName
   *          the name or the user or role
   * @param rights
   */
  public SecurityOwner( OwnerType ownerType, String ownerName ) {
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
   * @param ownerType
   *          the type of owner to set: user or role
   */
  public void setOwnerType( OwnerType ownerType ) {
    this.ownerType = ownerType;
  }

  /**
   * @return the name or the user or role
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * @param ownerName
   *          the name or the user or role
   */
  public void setOwnerName( String ownerName ) {
    this.ownerName = ownerName;
  }

  @Override
  public boolean equals( Object object ) {
    if ( !( object instanceof SecurityOwner ) ) {
      return false;
    }
    SecurityOwner s = (SecurityOwner) object;
    return getOwnerType().equals( s.getOwnerType() ) && getOwnerName().equals( s.getOwnerName() );
  }

  @Override
  public String toString() {
    return "{class=SecurityOwner, ownerType=" + getOwnerType() + ", ownerName=" + getOwnerName() + "}";
  }

  @Override
  public int compareTo( Object object ) {
    return toString().compareTo( object.toString() );
  }
}
