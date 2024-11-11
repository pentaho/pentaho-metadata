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

package org.pentaho.metadata.repository;

/**
 * This exception occurs if a domain is being written and already exists.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class DomainAlreadyExistsException extends Exception {
  private static final long serialVersionUID = -8381261699174809443L;

  public DomainAlreadyExistsException( String str ) {
    super( str );
  }
}
