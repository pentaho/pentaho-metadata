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

public class ColumnWidth implements Serializable {
  
  private static final long serialVersionUID = -5781026318422512440L;

  public enum WidthType {
    PIXELS(100, "ColumnWidth.USER_PIXELS_DESC"),
    PERCENT(10, "ColumnWidth.USER_PERCENT_DESC"),
    INCHES(3, "ColumnWidth.USER_INCHES_DESC"),
    CM(10, "ColumnWidth.USER_CENTIMETERS_DESC"),
    POINTS(1,"ColumnWidth.USER_POINTS_DESC");
    
    double defaultValue;
    String description;
    
    WidthType(double defaultVal, String description) {
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

  /**
   * @param type the column width type
   * @param width the prefered width of the column
   */
  public ColumnWidth(WidthType type, double width) {
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
   * @param type the type to set
   */
  public void setType(WidthType type) {
    this.type = type;
  }

  /**
   * @return the width
   */
  public double getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(double width) {
    this.width = width;
  }
  
  @Override
  public boolean equals(Object object) {
    ColumnWidth cw = (ColumnWidth)object;
    return getType() == cw.getType() && getWidth() == cw.getWidth();
  }
}
