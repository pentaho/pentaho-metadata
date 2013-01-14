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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.schema.RelationshipMeta;

@SuppressWarnings("deprecation")
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
