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

/**
 * The join type between two logical tables.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public enum JoinType {
  INNER("Inner"), //$NON-NLS-1$
  LEFT_OUTER("Left outer"), //$NON-NLS-1$
  RIGHT_OUTER("Right outer"), //$NON-NLS-1$
  FULL_OUTER("Full outer"); //$NON-NLS-1$
  
  String type;
  
  private JoinType(String type) {
    this.type = type;
  }
  
  public String getType() {
    return type;
  }
}
