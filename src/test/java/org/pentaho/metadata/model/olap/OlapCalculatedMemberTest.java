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

package org.pentaho.metadata.model.olap;

import org.junit.Test;

import static org.junit.Assert.*;

public class OlapCalculatedMemberTest {
  @Test
  public void testConstructor() throws Exception {
    OlapCalculatedMember member =
        new OlapCalculatedMember( "aName", "aDimension", "someFormula", "anyFormat", false );
    assertEquals( "aName", member.getName() );
    assertEquals( "aDimension", member.getDimension() );
    assertEquals( "someFormula", member.getFormula() );
    assertEquals( "anyFormat", member.getFormatString() );
    assertFalse( member.isCalculateSubtotals() );
  }

  @Test
  public void testSetters() throws Exception {
    OlapCalculatedMember member = new OlapCalculatedMember();
    member.setName( "bName" );
    member.setDimension( "bDimension" );
    member.setFormula( "otherFormula" );
    member.setFormatString( "noFormat" );
    member.setCalculateSubtotals( true );

    assertEquals( "bName", member.getName() );
    assertEquals( "bDimension", member.getDimension() );
    assertEquals( "otherFormula", member.getFormula() );
    assertEquals( "noFormat", member.getFormatString() );
    assertTrue( member.isCalculateSubtotals() );
  }
}
