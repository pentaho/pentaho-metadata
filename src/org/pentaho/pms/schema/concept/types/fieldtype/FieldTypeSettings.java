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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.schema.concept.types.fieldtype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.FieldType
 */
public class FieldTypeSettings {
  public static final int TYPE_OTHER = 0;
  public static final int TYPE_DIMENSION = 1;
  public static final int TYPE_FACT = 2;
  public static final int TYPE_KEY = 3;
  public static final int TYPE_ATTRIBUTE = 4;

  public static final FieldTypeSettings OTHER = new FieldTypeSettings( TYPE_OTHER );
  public static final FieldTypeSettings DIMENSION = new FieldTypeSettings( TYPE_DIMENSION );
  public static final FieldTypeSettings FACT = new FieldTypeSettings( TYPE_FACT );
  public static final FieldTypeSettings KEY = new FieldTypeSettings( TYPE_KEY );
  public static final FieldTypeSettings ATTRIBUTE = new FieldTypeSettings( TYPE_ATTRIBUTE );

  public static final FieldTypeSettings[] types = new FieldTypeSettings[] { OTHER, DIMENSION, FACT, KEY, ATTRIBUTE };
  private static final String[] typeCodes = new String[] { "Other", "Dimension", "Fact", "Key", "Attribute" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  private static final String[] typeDescriptions = new String[] {
    Messages.getString( "FieldTypeSettings.USER_OTHER_DESC" ), //$NON-NLS-1$
    Messages.getString( "FieldTypeSettings.USER_DIMENSION_DESC" ), //$NON-NLS-1$
    Messages.getString( "FieldTypeSettings.USER_FACT_DESC" ), //$NON-NLS-1$
    Messages.getString( "FieldTypeSettings.USER_KEY_DESC" ), //$NON-NLS-1$
    Messages.getString( "FieldTypeSettings.USER_ATTRIBUTE_DESC" ) }; //$NON-NLS-1$

  private int type;

  /**
   * @param type
   */
  public FieldTypeSettings( int type ) {
    super();
    this.type = type;
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType( int type ) {
    this.type = type;
  }

  public boolean isFact() {
    return type == TYPE_FACT;
  }

  public boolean isDimension() {
    return type == TYPE_DIMENSION;
  }

  public String getDescription() {
    return typeDescriptions[type];
  }

  public String getCode() {
    return typeCodes[type];
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof FieldTypeSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    FieldTypeSettings rhs = (FieldTypeSettings) obj;
    return new EqualsBuilder().append( type, rhs.type ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 83, 151 ).append( type ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( type ).toString();
  }

  public static FieldTypeSettings getType( String description ) {
    for ( int i = 0; i < typeDescriptions.length; i++ ) {
      if ( typeDescriptions[i].equalsIgnoreCase( description ) ) {
        return types[i];
      }
    }
    for ( int i = 0; i < typeCodes.length; i++ ) {
      if ( typeCodes[i].equalsIgnoreCase( description ) ) {
        return types[i];
      }
    }
    return OTHER;
  }

  public static FieldTypeSettings guessFieldType( String name ) {
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

  public static String[] getTypeDescriptions() {
    return typeDescriptions.clone();
  }
}
