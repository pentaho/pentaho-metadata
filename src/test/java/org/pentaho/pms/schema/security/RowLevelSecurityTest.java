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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for {@link RowLevelSecurity}.
 * 
 * @author mlowery
 */
@SuppressWarnings( "deprecation" )
public class RowLevelSecurityTest {

  private static final String XML_FRAG_NONE =
      "<row-level-security type=\"none\"><formula><![CDATA[]]></formula><entries></entries></row-level-security>";

  private static final String XML_FRAG_ROLE_BASED =
      "<row-level-security type=\"role-based\"><formula><![CDATA[]]></formula><entries><entry><owner><type>role</type><name>Admin</name></owner><formula><![CDATA[TRUE()]]></formula></entry></entries></row-level-security>";

  private static final String XML_FRAG_GLOBAL =
      "<row-level-security type=\"global\"><formula><![CDATA[TRUE()]]></formula><entries></entries></row-level-security>";

  @Test
  public void testClone() throws Exception {
    RowLevelSecurity rls1 = new RowLevelSecurity( "TRUE()" );
    RowLevelSecurity cloned1 = (RowLevelSecurity) rls1.clone();
    assertTrue( rls1 != cloned1 );
    assertTrue( rls1.equals( cloned1 ) );

    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    map.put( new SecurityOwner( SecurityOwner.OWNER_TYPE_ROLE, "Admin" ), "TRUE()" );
    RowLevelSecurity rls2 = new RowLevelSecurity( map );
    RowLevelSecurity cloned2 = (RowLevelSecurity) rls2.clone();
    assertTrue( rls2 != cloned2 );
    assertTrue( rls2.equals( cloned2 ) );

    RowLevelSecurity rls3 = new RowLevelSecurity();
    RowLevelSecurity cloned3 = (RowLevelSecurity) rls3.clone();
    assertTrue( rls3 != cloned3 );
    assertTrue( rls3.equals( cloned3 ) );

  }

  @Test
  public void testToXml() throws Exception {
    RowLevelSecurity rls1 = new RowLevelSecurity( "TRUE()" );
    String xml1 = rls1.toXML();
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", xml1, XML_FRAG_GLOBAL ), XML_FRAG_GLOBAL.equals( xml1 ) );

    final String EXPECTED_XML_2 = XML_FRAG_ROLE_BASED;
    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    map.put( new SecurityOwner( SecurityOwner.OWNER_TYPE_ROLE, "Admin" ), "TRUE()" );
    RowLevelSecurity rls2 = new RowLevelSecurity( map );
    String xml2 = rls2.toXML();
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", xml2, EXPECTED_XML_2 ), EXPECTED_XML_2.equals( xml2 ) );

    final String EXPECTED_XML_3 = XML_FRAG_NONE;
    RowLevelSecurity rls3 = new RowLevelSecurity();
    String xml3 = rls3.toXML();
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", xml3, EXPECTED_XML_3 ), EXPECTED_XML_3.equals( xml3 ) );
  }

  @Test
  public void testFromXml() throws Exception {
    RowLevelSecurity rls1 = RowLevelSecurity.fromXML( XML_FRAG_GLOBAL );
    RowLevelSecurity EXPECTED1 = new RowLevelSecurity( "TRUE()" );
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", rls1, EXPECTED1 ), EXPECTED1.equals( rls1 ) );

    RowLevelSecurity rls2 = RowLevelSecurity.fromXML( XML_FRAG_ROLE_BASED );
    Map<SecurityOwner, String> map = new HashMap<SecurityOwner, String>();
    map.put( new SecurityOwner( SecurityOwner.OWNER_TYPE_ROLE, "Admin" ), "TRUE()" );
    RowLevelSecurity EXPECTED2 = new RowLevelSecurity( map );
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", rls2, EXPECTED2 ), EXPECTED2.equals( rls2 ) );

    RowLevelSecurity rls3 = RowLevelSecurity.fromXML( XML_FRAG_NONE );
    RowLevelSecurity EXPECTED3 = new RowLevelSecurity();
    assertTrue( String.format( "\nactual  =%s\nexpected=%s", rls3, EXPECTED3 ), EXPECTED3.equals( rls3 ) );
  }

}
