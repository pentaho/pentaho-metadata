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
@SuppressWarnings("deprecation")
public class RelationshipMetaTest extends TestCase {

	/**
	 * Tests the mapping between the relationship type and the join type
	 */
	public void testRelationshipTypeToJoinType() {
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_0_0), RelationshipMeta.TYPE_JOIN_FULL_OUTER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_0_1), RelationshipMeta.TYPE_JOIN_LEFT_OUTER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_0_N), RelationshipMeta.TYPE_JOIN_LEFT_OUTER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_1_0), RelationshipMeta.TYPE_JOIN_RIGHT_OUTER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_N_0), RelationshipMeta.TYPE_JOIN_RIGHT_OUTER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_1_N), RelationshipMeta.TYPE_JOIN_INNER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_N_1), RelationshipMeta.TYPE_JOIN_INNER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_1_1), RelationshipMeta.TYPE_JOIN_INNER);
		assertEquals( RelationshipMeta.getJoinType(RelationshipMeta.TYPE_RELATIONSHIP_N_N), RelationshipMeta.TYPE_JOIN_INNER);
	}
	
	/**
	 * Tests the mapping between the join type and the relationship type<br>
	 * Multiple options are possible, but we stick to the more generic ...-to-N version<br>
	 */
	public void testJoinTypeToRelationshipType() {
		assertEquals( RelationshipMeta.getRelationType(RelationshipMeta.TYPE_JOIN_FULL_OUTER), RelationshipMeta.TYPE_RELATIONSHIP_0_0);
		assertEquals( RelationshipMeta.getRelationType(RelationshipMeta.TYPE_JOIN_LEFT_OUTER), RelationshipMeta.TYPE_RELATIONSHIP_0_N);
		assertEquals( RelationshipMeta.getRelationType(RelationshipMeta.TYPE_JOIN_RIGHT_OUTER), RelationshipMeta.TYPE_RELATIONSHIP_N_0);
		assertEquals( RelationshipMeta.getRelationType(RelationshipMeta.TYPE_JOIN_INNER), RelationshipMeta.TYPE_RELATIONSHIP_N_N);
	}
}
