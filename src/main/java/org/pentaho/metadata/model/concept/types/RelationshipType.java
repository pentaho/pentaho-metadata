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
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.model.concept.types;

/**
 * The relationship type between two logical tables.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum RelationshipType {
  UNDEFINED( "undefined" ), //$NON-NLS-1$
  _1_N( "1:N" ), //$NON-NLS-1$
  _N_1( "N:1" ), //$NON-NLS-1$
  _1_1( "1:1" ), //$NON-NLS-1$
  _0_N( "0:N" ), //$NON-NLS-1$
  _N_0( "N:0" ), //$NON-NLS-1$
  _0_1( "0:1" ), //$NON-NLS-1$
  _1_0( "1:0" ), //$NON-NLS-1$
  _N_N( "N:N" ), //$NON-NLS-1$
  _0_0( "0:0" ); //$NON-NLS-1$

  private String type;

  private RelationshipType( String type ) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  /**
   * Calculate the mapping between the relationship type and the join type.
   * 
   * @param relationshipType
   *          the type of relationship
   * @return the join type (inner, left outer, right outer or full outer)
   */
  public static JoinType getJoinType( RelationshipType relationshipType ) {
    switch ( relationshipType ) {
      case _0_N:
        return JoinType.LEFT_OUTER;
      case _N_0:
        return JoinType.RIGHT_OUTER;
      case _0_1:
        return JoinType.LEFT_OUTER;
      case _1_0:
        return JoinType.RIGHT_OUTER;
      case _0_0:
        return JoinType.FULL_OUTER;
      default:
        return JoinType.INNER;
    }
  }
}
