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
package org.pentaho.pms.schema.concept.types.alignment;

import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.model.concept.types.Alignment
 */
public class AlignmentSettings {
  public static final int TYPE_ALIGNMENT_LEFT = 0;
  public static final int TYPE_ALIGNMENT_RIGHT = 1;
  public static final int TYPE_ALIGNMENT_CENTERED = 2;
  public static final int TYPE_ALIGNMENT_JUSTIFIED = 3;

  public static final AlignmentSettings LEFT = new AlignmentSettings( TYPE_ALIGNMENT_LEFT );
  public static final AlignmentSettings RIGHT = new AlignmentSettings( TYPE_ALIGNMENT_RIGHT );
  public static final AlignmentSettings CENTERED = new AlignmentSettings( TYPE_ALIGNMENT_CENTERED );
  public static final AlignmentSettings JUSTIFIED = new AlignmentSettings( TYPE_ALIGNMENT_JUSTIFIED );

  private static final String[] typeCodes = { "left", "right", "centered", "justified", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  };

  /*
   * provides a mechanism for quickly getting the the integer value associated with with the typeCode
   */
  private static final HashMap<String, AlignmentSettings> typeCodeToAlignmentSettings =
      new HashMap<String, AlignmentSettings>( 4 );
  static {
    typeCodeToAlignmentSettings.put( typeCodes[TYPE_ALIGNMENT_LEFT], LEFT );
    typeCodeToAlignmentSettings.put( typeCodes[TYPE_ALIGNMENT_RIGHT], RIGHT );
    typeCodeToAlignmentSettings.put( typeCodes[TYPE_ALIGNMENT_CENTERED], CENTERED );
    typeCodeToAlignmentSettings.put( typeCodes[TYPE_ALIGNMENT_JUSTIFIED], JUSTIFIED );
  }
  public static final String[] typeDescriptions = { Messages.getString( "AlignmentSettings.USER_LEFT_DESC" ), //$NON-NLS-1$
    Messages.getString( "AlignmentSettings.USER_RIGHT_DESC" ), //$NON-NLS-1$
    Messages.getString( "AlignmentSettings.USER_CENTERED_DESC" ), //$NON-NLS-1$
    Messages.getString( "AlignmentSettings.USER_JUSTIFIED_DESC" ), //$NON-NLS-1$
  };

  public static final AlignmentSettings[] types = new AlignmentSettings[] { LEFT, RIGHT, CENTERED, JUSTIFIED, };

  private int type;

  /**
   * @param name
   * @param type
   */
  public AlignmentSettings( int type ) {
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

  public boolean equals( Object obj ) {
    if ( obj instanceof AlignmentSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    AlignmentSettings rhs = (AlignmentSettings) obj;
    return new EqualsBuilder().append( type, rhs.type ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 61, 223 ).append( type ).toHashCode();
  }

  public String toString() {
    return this.getCode();
  }

  /**
   * Given a string "left", "right", "centered", or "justified", return an appropriately initialized instance of
   * AlignmentSettings. The return value must NEVER be modified.
   * 
   * @param value
   * @return AlignmentSettings associated with <param>value</param>, or null if <param>value</param> is not a recognized
   *         value.
   */
  public static AlignmentSettings fromString( String value ) {
    return (AlignmentSettings) typeCodeToAlignmentSettings.get( value );
  }

  public static AlignmentSettings getType( String description ) {
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
    return LEFT;
  }

  public String getCode() {
    return typeCodes[type];
  }

  public String getDescription() {
    return typeDescriptions[type];
  }
}
