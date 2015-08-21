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
