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

import java.io.Serializable;

public class Color implements Serializable {

  private static final long serialVersionUID = 990421291041959327L;

  public static final Color BLACK = new Color( 0, 0, 0 );
  public static final Color WHITE = new Color( 255, 255, 255 );
  public static final Color RED = new Color( 255, 0, 0 );
  public static final Color GREEN = new Color( 0, 255, 0 );
  public static final Color BLUE = new Color( 0, 0, 255 );

  int red, green, blue;

  public Color() {

  }

  /**
   * @param red
   * @param green
   * @param blue
   */
  public Color( int red, int green, int blue ) {
    super();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /**
   * @return the blue
   */
  public int getBlue() {
    return blue;
  }

  /**
   * @param blue
   *          the blue to set
   */
  public void setBlue( int blue ) {
    this.blue = blue;
  }

  /**
   * @return the green
   */
  public int getGreen() {
    return green;
  }

  /**
   * @param green
   *          the green to set
   */
  public void setGreen( int green ) {
    this.green = green;
  }

  /**
   * @return the red
   */
  public int getRed() {
    return red;
  }

  /**
   * @param red
   *          the red to set
   */
  public void setRed( int red ) {
    this.red = red;
  }

  @Override
  public boolean equals( Object object ) {
    if ( !( object instanceof Color ) ) {
      return false;
    }
    Color c = (Color) object;
    return getRed() == c.getRed() && getBlue() == c.getBlue() && getGreen() == c.getGreen();
  }

  @Override
  public String toString() {
    return "{class=Color, red=" + getRed() + ", green=" + getGreen() + ", blue=" + getBlue() + "}";
  }
}
