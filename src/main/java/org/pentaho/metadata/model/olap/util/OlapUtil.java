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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.metadata.model.olap.util;

import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.metadata.model.olap.OlapCalculatedMember;
import org.pentaho.metadata.model.olap.OlapRole;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provide helper methods for olap related classes in metadata. The original purpose of this
 * class was to move some code with references to classes which were not compatible with gwt.
 * data-access depends on pentaho-metadata, so the org.pentaho.metadata.model.olap.util package
 * should be excluded from the gwt build in org/pentaho/metadata/Model.gwt.xml.
 */
public class OlapUtil {

    /**
     * Create an XML representation of a list of calculated members
     * @param members
     * @return
     */
  public static String toXmlCalculatedMembers( List<OlapCalculatedMember> members ) {
    StringBuffer xml = new StringBuffer();
    xml.append( "<calculatedMembers>" );
    for ( OlapCalculatedMember member : members ) {
      xml.append( calculatedMemberToXml( member ) );
    }
    xml.append( "</calculatedMembers>" );
    return xml.toString();
  }

  /**
   * Create OlapCalculatedMembers from XML
   *
   * @param xml
   * @return
   * @throws Exception
   */
  public static Map<String, List<OlapCalculatedMember>> fromXmlCalculatedMembers( String xml ) throws Exception {
    Map<String, List<OlapCalculatedMember>> cubeMembers = new HashMap<String, List<OlapCalculatedMember>>();

    Document doc = XMLHandler.loadXMLString( xml );
    Node cubesNode = XMLHandler.getSubNode( doc, "cubes" );
    int numCubes = XMLHandler.countNodes( cubesNode, "cube" ); //$NON-NLS-1$
    for ( int i = 0; i < numCubes; i++ ) {
      Node cubeNode = XMLHandler.getSubNodeByNr( cubesNode, "cube", i ); //$NON-NLS-1$
      Node membersNode = XMLHandler.getSubNode( cubeNode, "calculatedMembers" );
      int numMembers = XMLHandler.countNodes( membersNode,
          "calculatedMember" ); //$NON-NLS-1$ = XMLHandler.countNodes( cubesNode, "cube" ); //$NON-NLS-1$
      List<OlapCalculatedMember> members = new ArrayList<OlapCalculatedMember>();
      for ( int j = 0; j < numMembers; j++ ) {
        Node memberNode = XMLHandler.getSubNodeByNr( membersNode, "calculatedMember", j ); //$NON-NLS-1$

        members.add(
            new OlapCalculatedMember(
                XMLHandler.getTagValue( memberNode, "name" ),
                XMLHandler.getTagValue( memberNode, "dimension" ),
                XMLHandler.getTagValue( memberNode, "formula" ),
                XMLHandler.getTagValue( memberNode, "formatString" ),
                "Y".equalsIgnoreCase( XMLHandler.getTagValue( memberNode, "calculateSubtotals" ) ),
                "Y".equalsIgnoreCase( XMLHandler.getTagValue( memberNode, "hidden" ) )
            )
        );
      }
      cubeMembers.put( XMLHandler.getTagValue( cubeNode, "name" ), members );
    }
    return cubeMembers;
  }

    /**
     * Create an XML representation of OlapRoles
     * @param roles
     * @return
     */
  public static String toXmlRoles( List<OlapRole> roles ) {
    StringBuffer xml = new StringBuffer();
    xml.append( "<roles>" );
    for ( OlapRole role : roles ) {
      xml.append( olapRoleToXml( role ) );
    }
    xml.append( "</roles>" );
    return xml.toString();
  }

    /**
     * Create list of OlapRole objects from XML
     * @param xml
     * @return
     * @throws Exception
     */
  public static List<OlapRole> fromXmlRoles( String xml ) throws Exception {
    List<OlapRole> roles = new ArrayList<OlapRole>();
    Document doc = XMLHandler.loadXMLString( xml );
    Node rolesNode = XMLHandler.getSubNode( doc, "roles" );
    int num = XMLHandler.countNodes( rolesNode, "role" ); //$NON-NLS-1$
    for ( int i = 0; i < num; i++ ) {
      Node roleNode = XMLHandler.getSubNodeByNr( rolesNode, "role", i ); //$NON-NLS-1$
      roles.add( olapRoleFromNode( roleNode ) );
    }
    return roles;
  }

    /**
     * Create an OlapRole object from an XML node
     * @param node
     * @return
     * @throws KettleXMLException
     */
  public static OlapRole olapRoleFromNode( Node node ) throws KettleXMLException {
    String name = XMLHandler.getTagValue( node, "name" );
    StringBuilder xml = new StringBuilder();
    NodeList children = XMLHandler.getSubNode( node, "definition" ).getChildNodes();
    for ( int i = 0; i < children.getLength(); i++ ) {
      xml.append( ( XMLHandler.formatNode( children.item( i ) ) ) );
    }
    String definition = xml.toString();

    return new OlapRole( name, definition );
  }

    /**
     * Create an XML representation of an OlapCalculatedMember
     * @param member
     * @return
     */
  public static String calculatedMemberToXml( OlapCalculatedMember member ) {
    StringBuilder xml = new StringBuilder();

    xml.append( "<calculatedMember>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", member.getName(), false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "dimension", member.getDimension(), false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "formula", member.getFormula(), false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "formatString", member.getFormatString(), false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "calculateSubtotals", member.isCalculateSubtotals(), false ) );
    xml.append( XMLHandler.addTagValue( "hidden", member.isHidden() ) ); //$NON-NLS-1$
    xml.append( "</calculatedMember>" ); //$NON-NLS-1$

    return xml.toString();
  }

    /**
     * Create an XML representation of an OlapRole
     * @param role
     * @return
     */
  public static String olapRoleToXml( OlapRole role ) {
    StringBuilder xml = new StringBuilder();

    xml.append( "<role>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", role.getName(), false ) ); //$NON-NLS-1$
    xml.append( "<definition>" ); //$NON-NLS-1$
    xml.append( role.getDefinition() );
    xml.append( "</definition>" ); //$NON-NLS-1$
    xml.append( "</role>" ); //$NON-NLS-1$

    return xml.toString();
  }
}
