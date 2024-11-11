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
