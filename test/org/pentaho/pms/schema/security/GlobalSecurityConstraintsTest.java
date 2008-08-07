package org.pentaho.pms.schema.security;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for {@link GlobalSecurityConstraints}.
 * 
 * @author mlowery
 */
public class GlobalSecurityConstraintsTest {

  @Test
  public void testGetConstraintsMap() {
    final String FORMULA = "blah";
    GlobalSecurityConstraints c = new GlobalSecurityConstraints(FORMULA);
    Map<SecurityOwner, List<String>> map = new HashMap<SecurityOwner, List<String>>();
    List<String> list = new ArrayList<String>();
    list.add(FORMULA);
    map.put(null, list);
    assertTrue(map.equals(c.getConstraintsMap()));
  }

  @Test
  public void testGetSingleFormula() {
    final String FORMULA = "blah";
    GlobalSecurityConstraints c = new GlobalSecurityConstraints(FORMULA);
    assertTrue(FORMULA.equals(c.getSingleFormula()));
  }

  @Test
  public void testClone() throws Exception {
    final String FORMULA = "blah";
    GlobalSecurityConstraints gsc = new GlobalSecurityConstraints(FORMULA);
    GlobalSecurityConstraints cloned = (GlobalSecurityConstraints) gsc.clone();
    assertTrue(gsc != cloned);
    assertTrue(gsc.equals(cloned));
  }
  
}
