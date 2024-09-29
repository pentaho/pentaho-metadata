/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.pms.schema.security;

import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

public class SecurityACL implements Cloneable, Comparable<SecurityACL> {
  private String name;
  private int mask;

  public SecurityACL() {
  }

  /**
   * @param name
   * @param mask
   */
  public SecurityACL( String name, int mask ) {
    super();
    this.name = name;
    this.mask = mask;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public int compareTo( SecurityACL o ) {
    SecurityACL other = o;
    return mask - other.mask;
  }

  public String toXML() {
    StringBuffer xml = new StringBuffer();

    xml.append( "<acl>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", name, false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "mask", mask, false ) ); //$NON-NLS-1$
    xml.append( "</acl>" ); //$NON-NLS-1$

    return xml.toString();
  }

  public SecurityACL( Node aclNode ) {
    name = XMLHandler.getTagValue( aclNode, "name" ); //$NON-NLS-1$
    mask = Const.toInt( XMLHandler.getTagValue( aclNode, "mask" ), 0 ); //$NON-NLS-1$
  }

  public String toString() {
    return toXML();
  }

  /**
   * @return the mask
   */
  public int getMask() {
    return mask;
  }

  /**
   * @param mask
   *          the mask to set
   */
  public void setMask( int mask ) {
    this.mask = mask;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

}
