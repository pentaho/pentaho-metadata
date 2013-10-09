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
 * Copyright (c) 2009 Pentaho Corporation...  All rights reserved.
 */
package org.pentaho.pms.core.event;

/**
 * In case the ID of an Object changes (name, ID, in general the unique identifier) this is the event that is fired.
 * 
 * @author Matt
 * @since 2007-03-20
 */
public class IDChangedEvent {
  public String oldID;
  public String newID;

  public Object object;

  /**
   * @param oldID
   * @param newID
   * @param object
   */
  public IDChangedEvent( String oldID, String newID, Object object ) {
    this.oldID = oldID;
    this.newID = newID;
    this.object = object;
  }
}
