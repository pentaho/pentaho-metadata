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
 * The join type between two logical tables.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum JoinType {
  INNER( "Inner" ), //$NON-NLS-1$
  LEFT_OUTER( "Left outer" ), //$NON-NLS-1$
  RIGHT_OUTER( "Right outer" ), //$NON-NLS-1$
  FULL_OUTER( "Full outer" ); //$NON-NLS-1$

  String type;

  private JoinType( String type ) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
