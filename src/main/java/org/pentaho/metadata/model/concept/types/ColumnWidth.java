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

import java.io.Serializable;

public class ColumnWidth implements Serializable {

  private static final long serialVersionUID = -5781026318422512440L;

  public enum WidthType {
    PIXELS( 100, "ColumnWidth.USER_PIXELS_DESC" ), //$NON-NLS-1$
    PERCENT( 10, "ColumnWidth.USER_PERCENT_DESC" ), //$NON-NLS-1$
    INCHES( 3, "ColumnWidth.USER_INCHES_DESC" ), //$NON-NLS-1$
    CM( 10, "ColumnWidth.USER_CENTIMETERS_DESC" ), //$NON-NLS-1$
    POINTS( 1, "ColumnWidth.USER_POINTS_DESC" ); //$NON-NLS-1$

    double defaultValue;
    String description;

    WidthType( double defaultVal, String description ) {
      this.defaultValue = defaultVal;
      this.description = description;
    }

    public double getDefaultValue() {
      return defaultValue;
    }

    public String getDescription() {
      return description;
    }
  }

  private WidthType type;
  private double width;

  public ColumnWidth() {

  }

  /**
   * @param type
   *          the column width type
   * @param width
   *          the prefered width of the column
   */
  public ColumnWidth( WidthType type, double width ) {
    this.type = type;
    this.width = width;
  }

  /**
   * @return the type
   */
  public WidthType getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType( WidthType type ) {
    this.type = type;
  }

  /**
   * @return the width
   */
  public double getWidth() {
    return width;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth( double width ) {
    this.width = width;
  }

  @Override
  public boolean equals( Object object ) {
    ColumnWidth cw = (ColumnWidth) object;
    return getType() == cw.getType() && getWidth() == cw.getWidth();
  }
}
