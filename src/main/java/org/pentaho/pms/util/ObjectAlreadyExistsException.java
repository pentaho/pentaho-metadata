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
package org.pentaho.pms.util;

public class ObjectAlreadyExistsException extends Exception {
  private static final long serialVersionUID = 1947159965766738332L;

  /**
     * 
     */
  public ObjectAlreadyExistsException() {
    super();
  }

  /**
   * @param arg0
   * @param arg1
   */
  public ObjectAlreadyExistsException( String arg0, Throwable arg1 ) {
    super( arg0, arg1 );
  }

  /**
   * @param arg0
   */
  public ObjectAlreadyExistsException( String arg0 ) {
    super( arg0 );
  }

  /**
   * @param arg0
   */
  public ObjectAlreadyExistsException( Throwable arg0 ) {
    super( arg0 );
  }
}
