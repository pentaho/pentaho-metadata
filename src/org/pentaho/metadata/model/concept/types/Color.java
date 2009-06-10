/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
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
