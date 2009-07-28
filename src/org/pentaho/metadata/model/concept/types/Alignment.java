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

public enum Alignment {
  LEFT("Alignment.USER_LEFT_DESC"), //$NON-NLS-1$
  RIGHT("Alignment.USER_RIGHT_DESC"), //$NON-NLS-1$
  CENTERED("Alignment.USER_CENTERED_DESC"), //$NON-NLS-1$
  JUSTIFIED("Alignment.USER_JUSTIFIED_DESC"); //$NON-NLS-1$
  
  private String description;
  
  Alignment(String description) {
    this.description = description;
  }
  
  public String getDescription() {
    return description;
  }
  
}
