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

import static org.pentaho.pms.mql.dialect.JoinType.INNER_JOIN;

import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLWhereFormula;
import org.pentaho.pms.util.Const;

    /**
     * This class defines the join paths between the tables
     *
     */
    public class SQLJoin implements Comparable<SQLJoin> {
    	private String leftTablename;
    	private String leftTableAlias;
    	private String rightTablename;
    	private String rightTableAlias;
    	private SQLWhereFormula sqlWhereFormula;
    	private JoinType joinType;
    	private String joinOrderKey;
    	
		/**
		 * @param leftTablename the name of the left join table
		 * @param leftTableAlias the alias of the left join table
		 * @param rightTablename the name of the right join table
		 * @param rightTableAlias the alias of the right join table
		 * @param sqlWhereFormula the SQL formula to do the join (the operator is ignored)
		 * @param joinType the join type (inner, left outer, right outer, full outer)
		 * @param joinOrderKey the join order key
		 */
		public SQLJoin(String leftTablename, String leftTableAlias, String rightTablename, String rightTableAlias, SQLWhereFormula sqlWhereFormula, JoinType joinType, String joinOrderKey) {
			this.leftTablename = leftTablename;
			this.leftTableAlias = leftTableAlias;
			this.rightTablename = rightTablename;
			this.rightTableAlias = rightTableAlias;
			this.sqlWhereFormula = sqlWhereFormula;
			this.joinType = joinType;
			this.joinOrderKey = joinOrderKey;
		}

		/**
		 * @return the leftTablename
		 */
		public String getLeftTablename() {
			return leftTablename;
		}

		/**
		 * @param leftTablename the leftTablename to set
		 */
		public void setLeftTablename(String leftTablename) {
			this.leftTablename = leftTablename;
		}

		/**
		 * @return the rightTablename
		 */
		public String getRightTablename() {
			return rightTablename;
		}

		/**
		 * @param rightTablename the rightTablename to set
		 */
		public void setRightTablename(String rightTablename) {
			this.rightTablename = rightTablename;
		}

		/**
		 * @return the sqlWhereFormula
		 */
		public SQLWhereFormula getSqlWhereFormula() {
			return sqlWhereFormula;
		}

		/**
		 * @param sqlWhereFormula the sqlWhereFormula to set
		 */
		public void setSqlWhereFormula(SQLWhereFormula sqlWhereFormula) {
			this.sqlWhereFormula = sqlWhereFormula;
		}

		/**
		 * @return the join type : INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN, FULL_OUTER_JOIN
		 */
		public JoinType getJoinType() {
			return joinType;
		}

		/**
		 * @param joinType the join type :  INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN, FULL_OUTER_JOIN
		 */
		public void setJoinType(JoinType joinType) {
			this.joinType = joinType;
		}

		/**
		 * @return the joinOrderKey
		 */
		public String getJoinOrderKey() {
			return joinOrderKey;
		}

		/**
		 * @param joinOrderKey the joinOrderKey to set
		 */
		public void setJoinOrderKey(String joinOrderKey) {
			this.joinOrderKey = joinOrderKey;
		}
		
		public int compareTo(SQLJoin other) {
			
			// Case: No join order / no join order
			//
			if (Const.isEmpty(getJoinOrderKey()) && Const.isEmpty(other.getJoinOrderKey())) {
				
				// Case: inner join : goes below
				//
				if (getJoinType()==INNER_JOIN) {
					return -1;
				}
				// CASE: no inner join / inner join : goes to the top
				else if (getJoinType()==INNER_JOIN) {
					return 1;
				}
				else {
					// CASE: no inner join / no inner join : nothing to work with:
					return 0;
				}
			}
			// Case: Join order / no join order
			//
			else if (!Const.isEmpty(getJoinOrderKey()) && Const.isEmpty(other.getJoinOrderKey())) {

				// Case: ? / inner join : goes to the top
				//
				if (getJoinType()!=INNER_JOIN) {
					return 1; 
				}
				else {
					// CASE: ? / no inner join : nothing to work with:
					return 0;
				}
			}
			// Case: No join order / join order
			//
			else if (Const.isEmpty(getJoinOrderKey()) && !Const.isEmpty(other.getJoinOrderKey())) {

				// Case: inner join / ? : goes to the bottom
				//
				if (getJoinType()==INNER_JOIN) {
					return -1; 
				}
				else {
					// CASE: no inner join / ? : nothing to work with:
					return 0;
				}
			}
			// Case: both join orders are specified
			else {
				return -getJoinOrderKey().compareTo(other.getJoinOrderKey());
			}
		}

		/**
		 * @return the leftTableAlias
		 */
		public String getLeftTableAlias() {
			return leftTableAlias;
		}

		/**
		 * @param leftTableAlias the leftTableAlias to set
		 */
		public void setLeftTableAlias(String leftTableAlias) {
			this.leftTableAlias = leftTableAlias;
		}

		/**
		 * @return the rightTableAlias
		 */
		public String getRightTableAlias() {
			return rightTableAlias;
		}

		/**
		 * @param rightTableAlias the rightTableAlias to set
		 */
		public void setRightTableAlias(String rightTableAlias) {
			this.rightTableAlias = rightTableAlias;
		}
    }