package org.pentaho.pms.schema.security;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for {@link RoleBasedSecurityConstraints}.
 * 
 * @author mlowery
 */
public class RoleBasedSecurityConstraintsTest {

  @Test
  public void testGetConstraintsMap() {
    final String FORMULA = "blah";
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    List<String> list = new ArrayList<String>();
    list.add(FORMULA);
    map.put(null, list);
    RoleBasedSecurityConstraints c = new RoleBasedSecurityConstraints(map);
    assertTrue(map.equals(c.getConstraintsMap()));
  }

  @Test
  public void testGetSingleFormula() {
    final String FORMULA = "blah";
    final String FORMULA2 = "blah2";
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    List<String> list = new ArrayList<String>();
    List<String> list2 = new ArrayList<String>();
    list.add(FORMULA);
    list2.add(FORMULA2);
    map.put(null, list);
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, "roleA"), list2);
    RoleBasedSecurityConstraints c = new RoleBasedSecurityConstraints(map);
    final String res = "OR(blah;AND(IN(roleA;ROLES());blah2))";
    final String res2 = "OR(AND(IN(roleA;ROLES());blah2);blah)";
    // keys in map are not currently ordered; need to check for both (2 possibilities here)
    assertTrue(String.format("actual=%s\nexpected=%s or %s", c.getSingleFormula(), res, res2), res.equals(c
        .getSingleFormula())
        || res2.equals(c.getSingleFormula()));
  }

  @Test
  public void testClone() throws Exception {
    final String FORMULA = "blah";
    final String ROLE1 = "role1";
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    List<String> list = new ArrayList<String>();
    list.add(FORMULA);
    map.put(new SecurityOwner(SecurityOwner.OWNER_TYPE_ROLE, ROLE1), list);
    RoleBasedSecurityConstraints mbsc = new RoleBasedSecurityConstraints(map);
    RoleBasedSecurityConstraints cloned = (RoleBasedSecurityConstraints) mbsc.clone();
    assertTrue(mbsc != cloned);
    assertTrue(mbsc.equals(cloned));
  }
}
