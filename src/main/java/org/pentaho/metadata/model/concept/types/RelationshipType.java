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
