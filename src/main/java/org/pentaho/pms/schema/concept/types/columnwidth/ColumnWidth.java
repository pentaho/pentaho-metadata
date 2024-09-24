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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.schema.concept.types.columnwidth;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.ColumnWidth
 */
public class ColumnWidth {
  public static final int TYPE_WIDTH_PIXELS = 0;
  public static final int TYPE_WIDTH_PERCENT = 1;
  public static final int TYPE_WIDTH_INCHES = 2;
  public static final int TYPE_WIDTH_CM = 3;
  public static final int TYPE_WIDTH_POINTS = 4;

  public static final ColumnWidth PIXELS = new ColumnWidth( TYPE_WIDTH_PIXELS, 100 );
  public static final ColumnWidth PERCENT = new ColumnWidth( TYPE_WIDTH_PERCENT, 10 );
  public static final ColumnWidth INCHES = new ColumnWidth( TYPE_WIDTH_INCHES, 3 );
  public static final ColumnWidth CM = new ColumnWidth( TYPE_WIDTH_CM, 10 );
  public static final ColumnWidth POINTS = new ColumnWidth( TYPE_WIDTH_POINTS, 1 );

  private static final String[] typeCodes = { "pixels", "percent", "inches", "cm", "points",
    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  };

  private static final String[] typeDescriptions =
    {
      Messages.getString( "ColumnWidth.USER_PIXELS_DESC" ), Messages.getString( "ColumnWidth.USER_PERCENT_DESC" ),
      Messages.getString( "ColumnWidth.USER_INCHES_DESC" ), Messages.getString( "ColumnWidth.USER_CENTIMETERS_DESC" ),
      Messages.getString( "ColumnWidth.USER_POINTS_DESC" ),
      //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    };

  public static final ColumnWidth[] types = new ColumnWidth[] { PIXELS, PERCENT, INCHES, CM, POINTS, };

  private int type;
  private BigDecimal width;
  private static final String SEPARATOR = ",";

  /**
   * @param type  the column width type
   * @param width the prefered width of the column
   */
  public ColumnWidth( int type, BigDecimal width ) {
    this.type = type;
    this.width = width;
  }

  /**
   * @param type  the column width type
   * @param width the prefered width of the column
   */
  public ColumnWidth( int type, int width ) {
    this.type = type;
    this.width = new BigDecimal( width );
  }

  /**
   * @param type  the column width type
   * @param width the prefered width of the column
   */
  public ColumnWidth( int type, double width ) {
    this.type = type;
    this.width = new BigDecimal( width );
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType( int type ) {
    this.type = type;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ColumnWidth == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ColumnWidth rhs = (ColumnWidth) obj;
    return new EqualsBuilder().append( type, rhs.type ).append( width, rhs.width ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 19, 163 ).append( type ).append( width ).toHashCode();
  }

  public String toString() {
    return type + SEPARATOR + width;
  }

  public static ColumnWidth fromString( String value ) {
    String[] parts = value.split( SEPARATOR );
    if ( parts.length != 2 ) {
      return null;
    }
    return new ColumnWidth( Integer.parseInt( parts[ 0 ] ), new BigDecimal( parts[ 1 ] ) );
  }

  public static ColumnWidth getType( String description ) {
    for ( int i = 0; i < typeDescriptions.length; i++ ) {
      if ( typeDescriptions[ i ].equalsIgnoreCase( description ) ) {
        return types[ i ];
      }
    }
    for ( int i = 0; i < typeCodes.length; i++ ) {
      if ( typeCodes[ i ].equalsIgnoreCase( description ) ) {
        return types[ i ];
      }
    }
    return PIXELS;
  }

  public String getCode() {
    return typeCodes[ type ];
  }

  public String getDescription() {
    return typeDescriptions[ type ];
  }

  /**
   * @return the width
   */
  public BigDecimal getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth( BigDecimal width ) {
    this.width = width;
  }

  public static String[] getTypeDescriptions() {
    return typeDescriptions.clone();
  }

}
