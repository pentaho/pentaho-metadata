/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.util;

public class SQLModelGeneratorException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -6089798664483298023L;

  /**
   * 
   */
  public SQLModelGeneratorException() {
    super();
  }

  /**
   * @param message
   */
  public SQLModelGeneratorException( String message ) {
    super( message );
  }

  /**
   * @param message
   * @param reas
   */
  public SQLModelGeneratorException( String message, Throwable reas ) {
    super( message, reas );
  }

  /**
   * @param reas
   */
  public SQLModelGeneratorException( Throwable reas ) {
    super( reas );
  }

}
