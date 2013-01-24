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

public enum Alignment {
  LEFT("Alignment.USER_LEFT_DESC"), //$NON-NLS-1$
  RIGHT("Alignment.USER_RIGHT_DESC"), //$NON-NLS-1$
  CENTERED("Alignment.USER_CENTERED_DESC"), //$NON-NLS-1$
  JUSTIFIED("Alignment.USER_JUSTIFIED_DESC"); //$NON-NLS-1$
  
  private String description;
  
  Alignment(String description) {
    this.description = description;
  }
  
  public String getDescription() {
    return description;
  }
  
}
