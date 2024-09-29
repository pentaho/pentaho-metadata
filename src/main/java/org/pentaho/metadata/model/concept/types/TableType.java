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

public enum TableType {
  OTHER( "TableType.USER_OTHER_DESC" ), //$NON-NLS-1$
  DIMENSION( "TableType.USER_DIMENSION_DESC" ), //$NON-NLS-1$
  FACT( "TableType.USER_FACT_DESC" ); //$NON-NLS-1$

  private String description;

  TableType( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
