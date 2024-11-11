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

package org.pentaho.metadata.model.concept.types;

public enum FieldType {
  OTHER( "FieldType.USER_OTHER_DESC" ), //$NON-NLS-1$
  DIMENSION( "FieldType.USER_DIMENSION_DESC" ), //$NON-NLS-1$
  FACT( "FieldType.USER_FACT_DESC" ), //$NON-NLS-1$
  KEY( "FieldType.USER_KEY_DESC" ), //$NON-NLS-1$
  ATTRIBUTE( "FieldType.USER_ATTRIBUTE_DESC" ); //$NON-NLS-1$

  private String description;

  FieldType( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static FieldType guessFieldType( String name ) {
    String fieldname = name.toLowerCase();
    String[] ids = new String[] { "id", "pk", "tk", "sk" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // Is it a key field?
    boolean isKey = false;
    for ( int i = 0; i < ids.length && !isKey; i++ ) {
      if ( fieldname.startsWith( ids[i] + "_" ) || fieldname.endsWith( "_" + ids[i] ) ) {
        isKey = true; //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    if ( isKey ) {
      return KEY;
    }

    return DIMENSION;
  }
}
