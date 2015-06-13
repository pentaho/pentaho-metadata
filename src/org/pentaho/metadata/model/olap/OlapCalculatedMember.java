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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class OlapCalculatedMember implements Cloneable, Serializable {

  private String name;
  private String dimension;
  private String formula;
  private String formatString;

  public OlapCalculatedMember() {
  }

  public OlapCalculatedMember( String name, String dimension, String formula, String formatString ) {
    super();
    this.name = name;
    this.dimension = dimension;
    this.formula = formula;
    this.formatString = formatString;
  }

  public OlapCalculatedMember( Node node ) {
    super();
    name = XMLHandler.getTagValue( node, "name" );
    dimension = XMLHandler.getTagValue( node, "dimension" );
    formula = XMLHandler.getTagValue( node, "formula" );
    formatString = XMLHandler.getTagValue( node, "formatString" );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getDimension() {
    return dimension;
  }

  public void setDimension( String dimension ) {
    this.dimension = dimension;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula( String formula ) {
    this.formula = formula;
  }

  public String getFormatString() {
    return formatString;
  }

  public void setFormatString( String formatString ) {
    this.formatString = formatString;
  }

  @Override
  protected Object clone() {
    return new OlapCalculatedMember( name, dimension, formula, formatString );
  }

  public String toXml() {
    StringBuffer xml = new StringBuffer();

    xml.append( "<calculatedMember>" ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "name", name, false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "dimension", dimension, false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "formula", formula, false ) ); //$NON-NLS-1$
    xml.append( XMLHandler.addTagValue( "formatString", formatString, false ) ); //$NON-NLS-1$
    xml.append( "</calculatedMember>" ); //$NON-NLS-1$

    return xml.toString();
  }

  public static String toXmlMembers( List<OlapCalculatedMember> members ) {
    StringBuffer xml = new StringBuffer();
    xml.append( "<calculatedMembers>" );
    for ( OlapCalculatedMember member : members ) {
      xml.append( member.toXml() );
    }
    xml.append( "</calculatedMembers>" );
    return xml.toString();
  }

  public static Map<String, List<OlapCalculatedMember>> fromXmlMembers( String xml ) throws Exception {
    Map<String, List<OlapCalculatedMember>> cubeMembers = new HashMap<String, List<OlapCalculatedMember>>();

    Document doc = XMLHandler.loadXMLString( xml );
    Node cubesNode = XMLHandler.getSubNode( doc, "cubes" );
    int numCubes = XMLHandler.countNodes( cubesNode, "cube" ); //$NON-NLS-1$
    for ( int i = 0; i < numCubes; i++ ) {
      Node cubeNode = XMLHandler.getSubNodeByNr( cubesNode, "cube", i ); //$NON-NLS-1$
      Node membersNode = XMLHandler.getSubNode( cubeNode, "calculatedMembers" );
      int numMembers = XMLHandler.countNodes( membersNode, "calculatedMember" ); //$NON-NLS-1$ = XMLHandler.countNodes( cubesNode, "cube" ); //$NON-NLS-1$
      List<OlapCalculatedMember> members = new ArrayList<OlapCalculatedMember>();
      for ( int j = 0; j < numMembers; j++ ) {
        Node memberNode = XMLHandler.getSubNodeByNr( membersNode, "calculatedMember", j ); //$NON-NLS-1$
        members.add( new OlapCalculatedMember( memberNode ) );
      }
      cubeMembers.put( XMLHandler.getTagValue( cubeNode, "name" ), members );
    }
    return cubeMembers;
  }
}
