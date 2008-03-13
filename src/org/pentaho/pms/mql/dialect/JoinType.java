package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.schema.RelationshipMeta;

public enum JoinType {
	/**
	 * Inner join
	 */
	 INNER_JOIN, 
	 
	 /**
	  * Left outer join
	  */
	 LEFT_OUTER_JOIN, 
	 
	 /**
	  * Right outer join
	  */
	 RIGHT_OUTER_JOIN, 
	 
	 /**
	  * Full outer join
	  */
	 FULL_OUTER_JOIN;
	 
	 public static JoinType getJoinType(int joinType) {
		 switch(joinType) {
		 case RelationshipMeta.TYPE_JOIN_LEFT_OUTER: return LEFT_OUTER_JOIN;
		 case RelationshipMeta.TYPE_JOIN_RIGHT_OUTER: return RIGHT_OUTER_JOIN;
		 case RelationshipMeta.TYPE_JOIN_FULL_OUTER: return FULL_OUTER_JOIN;
		 default: return INNER_JOIN; 
		 }
	 }
}
