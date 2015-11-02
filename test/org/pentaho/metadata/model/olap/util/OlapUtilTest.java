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
 * Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.metadata.model.olap.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.helpers.SQLDialectHelper;
import org.pentaho.metadata.model.olap.OlapCalculatedMember;
import org.pentaho.metadata.model.olap.OlapRole;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OlapUtilTest {
  private static final String OLAP_SAMPLE_CALC_MEMBER_NAME = "Variance Percent";
  private static final String CALC_MEMBER_NAME_1 = "calcMemberOne";
  private static final String CALC_MEMBER_DIMENSION_1 = "dimension1";
  private static final String CALC_MEMBER_FORMULA_1 = "formula1";
  private static final String CALC_MEMBER_FORMAT_STRING_1 = "formatString1";

  private static final String CALC_MEMBER_NAME_2 = "calcMemberTwo";
  private static final String CALC_MEMBER_DIMENSION_2 = "dimension2";
  private static final String CALC_MEMBER_FORMULA_2 = "formula2";
  private static final String CALC_MEMBER_FORMAT_STRING_2 = "formatString2";

  private static final String ROLE_NAME_1 = "role1";
  private static final String ROLE_DEFINITION_1 = "definition 1";
  private static final String ROLE_NAME_2 = "role2";
  private static final String ROLE_DEFINITION_2 = "definition 2";

  List<OlapCalculatedMember> olapCalculatedMemberList = new ArrayList<>();
  List<OlapRole> olapRoleList = new ArrayList<>();

  OlapCalculatedMember olapCalculatedMember1;
  OlapCalculatedMember olapCalculatedMember2;

  OlapRole olapRole1;
  OlapRole olapRole2;

  String sampleCubesXml = "";
  String sampleRolesXml = "";

  @Before
  public void setUp() throws Exception {
    olapCalculatedMember1 = new OlapCalculatedMember(
        CALC_MEMBER_NAME_1,
        CALC_MEMBER_DIMENSION_1,
        CALC_MEMBER_FORMULA_1,
        CALC_MEMBER_FORMAT_STRING_1,
        false
    );

    olapCalculatedMember2 = new OlapCalculatedMember(
        CALC_MEMBER_NAME_2,
        CALC_MEMBER_DIMENSION_2,
        CALC_MEMBER_FORMULA_2,
        CALC_MEMBER_FORMAT_STRING_2,
        true
    );

    olapCalculatedMemberList.add( olapCalculatedMember1 );
    olapCalculatedMemberList.add( olapCalculatedMember2 );

    BufferedReader cubesBufferedReader = new BufferedReader( new FileReader( "test-res/olap-cubes-sample.xml" ) );
    while ( cubesBufferedReader.ready() ) {
      sampleCubesXml += cubesBufferedReader.readLine();
    }
    cubesBufferedReader.close();

    BufferedReader rolesBufferedReader = new BufferedReader( new FileReader( "test-res/olap-roles-sample.xml" ) );
    while ( rolesBufferedReader.ready() ) {
      sampleRolesXml += rolesBufferedReader.readLine();
    }
    rolesBufferedReader.close();

    olapRole1 = new OlapRole( ROLE_NAME_1, ROLE_DEFINITION_1 );
    olapRole2 = new OlapRole( ROLE_NAME_2, ROLE_DEFINITION_2 );

    olapRoleList.add( olapRole1 );
    olapRoleList.add( olapRole2 );
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testToXmlCalculatedMembers() throws Exception {
    String xmlResult = OlapUtil.toXmlCalculatedMembers( olapCalculatedMemberList );
    assertTrue( xmlResult.contains( "<calculatedMembers><calculatedMember>" ) );
    assertTrue( xmlResult.contains( CALC_MEMBER_NAME_1 ) );
    assertTrue( xmlResult.contains( CALC_MEMBER_NAME_2 ) );
  }

  @Test
  public void testFromXmlCalculatedMembers() throws Exception {
    Map<String, List<OlapCalculatedMember>> olapCalculatedMembers = OlapUtil.fromXmlCalculatedMembers( sampleCubesXml );
    assertNotNull( olapCalculatedMembers );
    assertTrue( olapCalculatedMembers.size() == 1 );
    assertTrue(
        olapCalculatedMembers.get( "Quadrant Analysis" ).get( 0 ).getName().equals( OLAP_SAMPLE_CALC_MEMBER_NAME ) );
  }

  @Test
  public void testToXmlRoles() throws Exception {
    String xmlResult = OlapUtil.toXmlRoles( olapRoleList );
    assertNotNull( xmlResult );
    assertTrue( xmlResult.contains( "<roles><role>" ) );
    assertTrue( xmlResult.contains( ROLE_NAME_1 ) );
    assertTrue( xmlResult.contains( ROLE_DEFINITION_2 ) );
  }

  @Test
  public void testFromXmlRoles() throws Exception {
    List<OlapRole> olapRolesList = OlapUtil.fromXmlRoles( sampleRolesXml );
    assertNotNull( olapRolesList );
    assertTrue( olapRolesList.size() == 2 );
    assertTrue( olapRolesList.get( 0 ).getName().equals( ROLE_NAME_1 ) );
    assertTrue( olapRolesList.get( 1 ).getName().equals( ROLE_NAME_2 ) );
  }

  @Test
  public void testOlapRoleFromNode() throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse( new File( "test-res/olap-roles-sample.xml" ) );
    Node rolesNode = XMLHandler.getSubNode( document, "roles" );
    Node roleNode = XMLHandler.getSubNode( rolesNode, "role" );
    OlapRole olapRole = OlapUtil.olapRoleFromNode( roleNode );
    assertTrue( olapRole.getName().equals( ROLE_NAME_1 ) );
  }

  @Test
  public void testCalculatedMemberToXml() throws Exception {
    SQLDialectHelper.assertEqualsIgnoreWhitespaces(
        "<calculatedMember><name>calcMemberOne</name><dimension>dimension1</dimension><formula>formula1"
            + "</formula><formatString>formatString1</formatString><calculateSubtotals>N</calculateSubtotals>\n"
            + "</calculatedMember>",
        OlapUtil.calculatedMemberToXml( olapCalculatedMember1 ) );
    SQLDialectHelper.assertEqualsIgnoreWhitespaces(
        "<calculatedMember><name>calcMemberTwo</name><dimension>dimension2</dimension><formula>formula2</formula"
            + "><formatString>formatString2</formatString><calculateSubtotals>Y</calculateSubtotals>\n"
            + "</calculatedMember>",
        OlapUtil.calculatedMemberToXml( olapCalculatedMember2 ) );
  }

  @Test
  public void testOlapRoleToXml() throws Exception {
    String xmlResult = OlapUtil.olapRoleToXml( olapRole1 );
    assertNotNull( xmlResult );
    assertTrue( xmlResult.contains( ROLE_NAME_1 ) );
    assertTrue( xmlResult.contains( ROLE_DEFINITION_1 ) );
  }
}
