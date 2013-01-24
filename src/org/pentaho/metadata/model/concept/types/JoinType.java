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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.concept.types;

/**
 * The join type between two logical tables.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public enum JoinType {
  INNER("Inner"), //$NON-NLS-1$
  LEFT_OUTER("Left outer"), //$NON-NLS-1$
  RIGHT_OUTER("Right outer"), //$NON-NLS-1$
  FULL_OUTER("Full outer"); //$NON-NLS-1$
  
  String type;
  
  private JoinType(String type) {
    this.type = type;
  }
  
  public String getType() {
    return type;
  }
}
