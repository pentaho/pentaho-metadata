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
package org.pentaho.metadata.query.model;

/**
 * This enum defines how individual constraints combine.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum CombinationType {
  AND( "AND" ), OR( "OR" ), AND_NOT( "AND NOT" ), OR_NOT( "OR NOT" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

  private String toStringVal;

  private CombinationType( String val ) {
    toStringVal = val;
  }

  public static CombinationType getCombinationType( String value ) {
    for ( CombinationType type : values() ) {
      if ( type.toString().equalsIgnoreCase( value ) ) {
        return type;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return toStringVal;
  }

}
