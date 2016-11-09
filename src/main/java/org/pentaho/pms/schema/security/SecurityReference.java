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

import java.util.List;

import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.util.Const;

public class SecurityReference {
  private SecurityService securityService;

  public SecurityReference() {
    securityService = new SecurityService();
  }

  public String getRightsDescription( int rights ) {
    List acls = securityService.getAcls();
    // Go through the ACL list and if we find an ACL that matches, add it to the list
    StringBuffer desc = new StringBuffer();

    boolean first = true;
    boolean nothing = true;

    for ( int i = 0; i < acls.size(); i++ ) {
      SecurityACL acl = (SecurityACL) acls.get( i );
      if ( ( rights & acl.getMask() ) == acl.getMask() && acl.getMask() > 0 ) {
        if ( !first ) {
          desc.append( ", " ); //$NON-NLS-1$
        }
        desc.append( acl.getName() );

        first = false;
        nothing = false;
      }
    }

    if ( nothing ) {
      SecurityACL nothingAcl;
      try {
        nothingAcl = findAcl( 0 );
      } catch ( Exception e ) {
        nothingAcl = null;
      }
      if ( nothingAcl != null ) {
        desc = new StringBuffer( nothingAcl.getName() );
      }
    }

    return desc.toString();
  }

  private SecurityACL findAcl( int mask ) throws Exception {
    List acls = securityService.getAcls();
    for ( int i = 0; i < acls.size(); i++ ) {
      SecurityACL acl = (SecurityACL) acls.get( i );
      if ( acl.getMask() == mask ) {
        return acl;
      }
    }
    return null;
  }

  public SecurityACL findAcl( String name ) {
    List acls = securityService.getAcls();
    for ( int i = 0; i < acls.size(); i++ ) {
      SecurityACL acl = (SecurityACL) acls.get( i );
      if ( acl.getName().equals( name ) ) {
        return acl;
      }
    }
    return null;
  }

  public SecurityReference( SecurityService securityService ) throws Exception {
    this();
    this.securityService = securityService;
  }

  public String toXML() throws Exception {
    List users = securityService.getUsers();
    List roles = securityService.getRoles();
    StringBuffer xml = new StringBuffer();

    xml.append( "<content>" ).append( Const.CR ); //$NON-NLS-1$

    xml.append( "  <users>" ).append( Const.CR ); //$NON-NLS-1$
    for ( int i = 0; i < users.size(); i++ ) {
      xml.append( "    " ).append( XMLHandler.addTagValue( "user", (String) users.get( i ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    xml.append( "  </users>" ).append( Const.CR ); //$NON-NLS-1$

    xml.append( "  <roles>" ).append( Const.CR ); //$NON-NLS-1$
    for ( int i = 0; i < roles.size(); i++ ) {
      xml.append( "    " ).append( XMLHandler.addTagValue( "role", (String) roles.get( i ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    xml.append( "  </roles>" ).append( Const.CR ); //$NON-NLS-1$
    xml.append( "</content>" ).append( Const.CR ); //$NON-NLS-1$

    return xml.toString();
  }

  /**
   * @return the acls
   * @throws Exception
   */
  public List<SecurityACL> getAcls() {
    return securityService.getAcls();
  }

  /**
   * @return the roles
   * @throws Exception
   */
  public List<String> getRoles() {
    return securityService.getRoles();
  }

  /**
   * @return the users
   * @throws Exception
   */
  public List<String> getUsers() {
    return securityService.getUsers();
  }

  /**
   * @return the securityService
   */
  public SecurityService getSecurityService() {
    return securityService;
  }

  /**
   * @param securityService
   *          the securityService to set
   */
  public void setSecurityService( SecurityService securityService ) {
    this.securityService = securityService;
  }

}
