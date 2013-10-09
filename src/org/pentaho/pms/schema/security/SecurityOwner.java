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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.schema.security;

import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.messages.Messages;
import org.w3c.dom.Node;

/**
 * A security owner is a combination of a user or role type and the name of that user or role.
 * 
 * @author Matt
 * @since 01-NOV-2006
 * 
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.model.concept.security.SecurityOwner
 */
public class SecurityOwner implements Cloneable {
  public static final String STRING_USER_DESC = Messages.getString( "SecurityOwner.USER_USER" ); //$NON-NLS-1$
  public static final String STRING_ROLE_DESC = Messages.getString( "SecurityOwner.USER_ROLE" ); //$NON-NLS-1$

  public static final int OWNER_TYPE_USER = 0;
  public static final int OWNER_TYPE_ROLE = 1;

  public static final String[] ownerTypeCodes = new String[] { "user", "role" }; //$NON-NLS-1$ //$NON-NLS-2$
  public static final String[] ownerTypeDescriptions = new String[] { STRING_USER_DESC, STRING_ROLE_DESC };

  private int ownerType;
  private String ownerName;

  /**
   * @param ownerType
   *          the type of ACL owner : user or role
   * @param ownerName
   *          the name or the user or role
   * @param rights
   */
  public SecurityOwner( int ownerType, String ownerName, int rights ) {
    this.ownerType = ownerType;
    this.ownerName = ownerName;
  }

  /**
   * Create a new ACL Set without any rights.
   * 
   * @param ownerType
   *          the type of ACL : user or role
   * @param ownerName
   *          the name or the user or role
   */
  public SecurityOwner( int ownerType, String ownerName ) {
    this( ownerType, ownerName, 0 );
  }

  public String toString() {
    return ownerName + ":" + getOwnerTypeDescription(); //$NON-NLS-1$
  }

  public String toXML() {
    StringBuffer xml = new StringBuffer();

    xml.append( "<owner>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "type", getOwnerTypeCode(), false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", ownerName, false ) ); //$NON-NLS-1$
    xml.append( "</owner>" ); //$NON-NLS-1$

    return xml.toString();
  }

  public SecurityOwner( Node ownerNode ) {
    ownerType = getOwnerType( XMLHandler.getTagValue( ownerNode, "type" ) ); //$NON-NLS-1$
    ownerName = XMLHandler.getTagValue( ownerNode, "name" ); //$NON-NLS-1$
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public boolean equals( Object obj ) {
    boolean result = false;
    if ( obj instanceof SecurityOwner ) {
      SecurityOwner set = (SecurityOwner) obj;
      result = ( ownerType == set.ownerType && ownerName.equals( set.ownerName ) ); // I WANT this to NPE if ownerName
                                                                                    // is null :-)
    }
    return result;
  }

  public int hashCode() {
    return new Integer( ownerType ).hashCode() ^ ownerName.hashCode();
  }

  /**
   * @return the type of ACL : user or role
   */
  public int getOwnerType() {
    return ownerType;
  }

  /**
   * @param aclType
   *          the type of ACL to set: user or role
   */
  public void setOwnerType( int aclType ) {
    this.ownerType = aclType;
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

  /**
   * @return the code for the type of ACL : user or role
   */
  public String getOwnerTypeCode() {
    return ownerTypeCodes[ownerType];
  }

  /**
   * @return the description for the type of ACL : user or role
   */
  public String getOwnerTypeDescription() {
    return ownerTypeDescriptions[ownerType];
  }

  /**
   * @param description
   *          the description or code of the ACL owner type (user or role)
   * @return the ACL type
   */
  public static final int getOwnerType( String description ) {
    for ( int i = 0; i < ownerTypeDescriptions.length; i++ ) {
      if ( ownerTypeDescriptions[i].equalsIgnoreCase( description ) ) {
        return i;
      }
    }
    for ( int i = 0; i < ownerTypeCodes.length; i++ ) {
      if ( ownerTypeCodes[i].equalsIgnoreCase( description ) ) {
        return i;
      }
    }
    return OWNER_TYPE_USER;
  }

}
