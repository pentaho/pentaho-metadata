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
package org.pentaho.metadata.query.model;

/**
 * This enum defines how individual constraints combine.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public enum CombinationType {
  AND("AND"), OR("OR"), AND_NOT("AND NOT"), OR_NOT("OR NOT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  
  private String toStringVal;
  private CombinationType(String val){
    toStringVal = val;
  }
  
  @Override
  public String toString() {
    return toStringVal;
  }
  
}
