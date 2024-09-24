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
package org.pentaho.pms.core.exception;

public class PentahoMetadataException extends Exception {

  private static final long serialVersionUID = 1291055530614616329L;

  public PentahoMetadataException() {
    super();
  }

  public PentahoMetadataException( String arg0, Throwable arg1 ) {
    super( arg0, arg1 );
  }

  public PentahoMetadataException( String arg0 ) {
    super( arg0 );
  }

  public PentahoMetadataException( Throwable arg0 ) {
    super( arg0 );
  }
}
