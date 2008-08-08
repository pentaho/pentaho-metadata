package org.pentaho.pms.schema.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for {@link RowLevelSecurity}.
 * 
 * @author mlowery
 */
public class RowLevelSecurityTest {

  @Test
  public void testClone() throws Exception {
    GlobalSecurityConstraints gsc = new GlobalSecurityConstraints("TRUE()");
    RowLevelSecurity rls1 = new RowLevelSecurity(gsc);
    RowLevelSecurity cloned = (RowLevelSecurity) rls1.clone();
    assertTrue(rls1 != cloned);
    assertTrue(rls1.equals(cloned));
  }
  
  @Test
  public void testToXml() throws Exception {
    final String EXPECTED_XML = "<row-level-security><constraints><constraint><owner><null /></owner><formulas><formula><![CDATA[TRUE()]]></formula></formulas></constraint></constraints></row-level-security>";
    GlobalSecurityConstraints gsc = new GlobalSecurityConstraints("TRUE()");
    RowLevelSecurity rls1 = new RowLevelSecurity(gsc);
    String xml = rls1.toXml();
    assertTrue(String.format("actual=%s\nexpected=%s", xml, EXPECTED_XML), EXPECTED_XML.equals(xml));
  }
  
  @Test
  public void testFromXml() throws Exception {
    final String INPUT_XML = "<row-level-security><constraints><constraint><owner><null /></owner><formulas><formula><![CDATA[TRUE()]]></formula></formulas></constraint></constraints></row-level-security>";
    RowLevelSecurity rls = RowLevelSecurity.fromXML(INPUT_XML);
    GlobalSecurityConstraints gsc = new GlobalSecurityConstraints("TRUE()");
    RowLevelSecurity EXPECTED = new RowLevelSecurity(gsc);
    assertTrue(String.format("actual=%s\nexpected=%s", rls, EXPECTED), EXPECTED.equals(rls));
    
  }
  

}
