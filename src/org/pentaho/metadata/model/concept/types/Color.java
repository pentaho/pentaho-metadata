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

public class Color implements Serializable {

  private static final long serialVersionUID = 990421291041959327L;
  
  public static final Color BLACK = new Color(0, 0, 0);
  public static final Color WHITE = new Color(255, 255, 255);
  public static final Color RED = new Color(255, 0, 0);
  public static final Color GREEN = new Color(0, 255, 0);
  public static final Color BLUE = new Color(0, 0, 255);

  int red, green, blue;

  public Color() {
    
  }
  
  /**
   * @param red
   * @param green
   * @param blue
   */
  public Color(int red, int green, int blue) {
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
   * @param blue the blue to set
   */
  public void setBlue(int blue) {
    this.blue = blue;
  }

  /**
   * @return the green
   */
  public int getGreen() {
    return green;
  }

  /**
   * @param green the green to set
   */
  public void setGreen(int green) {
    this.green = green;
  }

  /**
   * @return the red
   */
  public int getRed() {
    return red;
  }

  /**
   * @param red the red to set
   */
  public void setRed(int red) {
    this.red = red;
  }

  @Override
  public boolean equals(Object object) {
    Color c = (Color)object;
    return getRed() == c.getRed() && getBlue() == c.getBlue() && getGreen() == c.getGreen();
  }
}
