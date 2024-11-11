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

package org.pentaho.metadata.repository;

/**
 * This is a general exception if the domain failed to get stored.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class DomainStorageException extends Exception {
  private static final long serialVersionUID = -8381261699174809443L;

  public DomainStorageException( String str, Exception e ) {
    super( str, e );
  }
}
