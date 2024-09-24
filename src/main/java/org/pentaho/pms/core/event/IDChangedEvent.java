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
