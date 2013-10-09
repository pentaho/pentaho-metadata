/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
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
