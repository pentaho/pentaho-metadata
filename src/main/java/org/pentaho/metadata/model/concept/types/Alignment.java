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
package org.pentaho.metadata.model.concept.types;

public enum Alignment {
  LEFT( "Alignment.USER_LEFT_DESC" ), //$NON-NLS-1$
  RIGHT( "Alignment.USER_RIGHT_DESC" ), //$NON-NLS-1$
  CENTERED( "Alignment.USER_CENTERED_DESC" ), //$NON-NLS-1$
  JUSTIFIED( "Alignment.USER_JUSTIFIED_DESC" ); //$NON-NLS-1$

  private String description;

  Alignment( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
