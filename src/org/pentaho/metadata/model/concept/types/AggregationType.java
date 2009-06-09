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
  NONE("AggregationSettings.USER_NONE_DESC"), 
  SUM("AggregationSettings.USER_SUM_DESC"), 
  AVG("AggregationSettings.USER_AVERAGE_DESC"),
  COUNT("AggregationSettings.USER_COUNT_DESC"), 
  DISTINCT_COUNT("AggregationSettings.USER_DISTINCT_COUNT_DESC"),
  MIN("AggregationSettings.USER_MINIMUM_DESC"), 
  MAX("AggregationSettings.USER_MAXIMUM_DES"); 

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

