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
 * the aggregation type of a physical or logical column.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public enum AggregationType {
  NONE("AggregationType.USER_NONE_DESC"), //$NON-NLS-1$ 
  SUM("AggregationType.USER_SUM_DESC"), //$NON-NLS-1$
  AVERAGE("AggregationType.USER_AVERAGE_DESC"), //$NON-NLS-1$
  COUNT("AggregationType.USER_COUNT_DESC"), //$NON-NLS-1$
  COUNT_DISTINCT("AggregationType.USER_COUNT_DISTINCT_DESC"), //$NON-NLS-1$
  MINIMUM("AggregationType.USER_MINIMUM_DESC"), //$NON-NLS-1$
  MAXIMUM("AggregationType.USER_MAXIMUM_DESC"); //$NON-NLS-1$

  private String description;

  private AggregationType(String description) {
    this.description = description;
  }
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

