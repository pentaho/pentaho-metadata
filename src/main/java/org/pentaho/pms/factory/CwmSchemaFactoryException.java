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

package org.pentaho.pms.factory;

public class CwmSchemaFactoryException extends RuntimeException {

  private static final long serialVersionUID = -2992524487791975326L;

  public CwmSchemaFactoryException() {
    super();
  }

  public CwmSchemaFactoryException( String msg ) {
    super( msg );
  }

  public CwmSchemaFactoryException( String msg, Throwable th ) {
    super( msg, th );
  }
}
