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
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.model.concept.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains a mapping between a SecurityOwner (named user or role) and the rights (integer : masks with ACLs)
 * 
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
  public Security( Map<SecurityOwner, Integer> ownerAclMap ) {
    super();
    this.ownerAclMap = ownerAclMap;
  }

  /**
   * @param owner
   *          the owner to store the rights for
   * @param rights
   *          the ACLs to store
   */
  public void putOwnerRights( SecurityOwner owner, int rights ) {
    ownerAclMap.put( owner, rights );
  }

  /**
   * @param owner
   *          the owner to get the rights for
   * @return the ACLs
   */
  public int getOwnerRights( SecurityOwner owner ) {
    return ( (Integer) ownerAclMap.get( owner ) ).intValue();
  }

  /**
   * Remove the rights out of the map for the given user
   * 
   * @param owner
   *          the owner to remove the rights for.
   */
  public void removeOwnerRights( SecurityOwner owner ) {
    ownerAclMap.remove( owner );
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
   * @param ownerAclMap
   *          the ownerAclMap to set
   */
  public void setOwnerAclMap( Map<SecurityOwner, Integer> ownerAclMap ) {
    this.ownerAclMap = ownerAclMap;
  }

  @Override
  public boolean equals( Object object ) {
    Security s = (Security) object;
    return getOwnerAclMap().equals( s.getOwnerAclMap() );
  }
}
