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
package org.pentaho.metadata.model.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OlapRole implements Cloneable, Serializable {
  
  private String name;
  private String definition;
  
  public OlapRole() {
  }
  
  public OlapRole( String name, String roleXml ) {
    super();
    this.name = name;
    this.definition = roleXml;
  }
  
  public OlapRole( Node node ) throws KettleXMLException {
    super();
    name = XMLHandler.getTagValue( node, "name" );
    StringBuffer xml = new StringBuffer();
    NodeList children = XMLHandler.getSubNode( node, "definition" ).getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      xml.append( (XMLHandler.formatNode( children.item( i ) )));
    }
    definition = xml.toString();
  }

  @Override
  protected Object clone() {
    return new OlapRole( name, definition );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition( String roleXml ) {
    this.definition = roleXml;
  }
  
  public String toXml() {
    StringBuffer xml = new StringBuffer();

    xml.append( "<role>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", name, false ) ); //$NON-NLS-1$
    xml.append( "<definition>" ); //$NON-NLS-1$
    xml.append( definition );
    xml.append( "</definition>" ); //$NON-NLS-1$
    xml.append( "</role>" ); //$NON-NLS-1$

    return xml.toString();
  }
  public static String toXmlRoles( List<OlapRole> roles ) {
    StringBuffer xml = new StringBuffer();
    xml.append( "<roles>" ); 
    for ( OlapRole role : roles ) {
      xml.append( role.toXml() );
    }
    xml.append( "</roles>" ); 
    return xml.toString();
  }
  
  public static List<OlapRole> fromXmlRoles (String xml) throws Exception {
    List<OlapRole> roles = new ArrayList<OlapRole> ();
    Document doc = XMLHandler.loadXMLString( xml );
    Node rolesNode = XMLHandler.getSubNode( doc, "roles" );
    int num = XMLHandler.countNodes( rolesNode, "role" ); //$NON-NLS-1$
    for ( int i = 0; i < num; i++ ) {
      Node roleNode = XMLHandler.getSubNodeByNr( rolesNode, "role", i ); //$NON-NLS-1$
      roles.add( new OlapRole (roleNode) );
    }
    return roles;
  }
}
