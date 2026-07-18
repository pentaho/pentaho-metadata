/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.pms.core.exception;

public class CWMException extends Exception {
  private static final long serialVersionUID = 3858588189630708963L;

  public CWMException() {
    super();
  }

  public CWMException( String arg0, Throwable arg1 ) {
    super( arg0, arg1 );
  }

  public CWMException( String arg0 ) {
    super( arg0 );
  }

  public CWMException( Throwable arg0 ) {
    super( arg0 );
  }

}
