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
