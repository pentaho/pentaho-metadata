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

}
