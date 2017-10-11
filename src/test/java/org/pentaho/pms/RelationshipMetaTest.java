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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.pms;

import junit.framework.TestCase;

import org.pentaho.pms.schema.RelationshipMeta;

/**
 * Test the conversion logic between the relationship type and the join type (and vice versa)<br>
 * Because it's difficult to test this in the GUI code, we do it over here.<br>
 * 
 * @author Matt Casters (mcasters@pentaho.org)
 * 
 */
@SuppressWarnings( "deprecation" )
public class RelationshipMetaTest extends TestCase {

  /**
   * Tests the mapping between the relationship type and the join type
   */
  public void testRelationshipTypeToJoinType() {
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_0_0 ),
        RelationshipMeta.TYPE_JOIN_FULL_OUTER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_0_1 ),
        RelationshipMeta.TYPE_JOIN_LEFT_OUTER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_0_N ),
        RelationshipMeta.TYPE_JOIN_LEFT_OUTER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_1_0 ),
        RelationshipMeta.TYPE_JOIN_RIGHT_OUTER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_N_0 ),
        RelationshipMeta.TYPE_JOIN_RIGHT_OUTER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_1_N ),
        RelationshipMeta.TYPE_JOIN_INNER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_N_1 ),
        RelationshipMeta.TYPE_JOIN_INNER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_1_1 ),
        RelationshipMeta.TYPE_JOIN_INNER );
    assertEquals( RelationshipMeta.getJoinType( RelationshipMeta.TYPE_RELATIONSHIP_N_N ),
        RelationshipMeta.TYPE_JOIN_INNER );
  }

  /**
   * Tests the mapping between the join type and the relationship type<br>
   * Multiple options are possible, but we stick to the more generic ...-to-N version<br>
   */
  public void testJoinTypeToRelationshipType() {
    assertEquals( RelationshipMeta.getRelationType( RelationshipMeta.TYPE_JOIN_FULL_OUTER ),
        RelationshipMeta.TYPE_RELATIONSHIP_0_0 );
    assertEquals( RelationshipMeta.getRelationType( RelationshipMeta.TYPE_JOIN_LEFT_OUTER ),
        RelationshipMeta.TYPE_RELATIONSHIP_0_N );
    assertEquals( RelationshipMeta.getRelationType( RelationshipMeta.TYPE_JOIN_RIGHT_OUTER ),
        RelationshipMeta.TYPE_RELATIONSHIP_N_0 );
    assertEquals( RelationshipMeta.getRelationType( RelationshipMeta.TYPE_JOIN_INNER ),
        RelationshipMeta.TYPE_RELATIONSHIP_N_N );
  }
}
