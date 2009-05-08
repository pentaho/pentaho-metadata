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

import java.io.Serializable;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.concept.types.AggregationType;

/**
 * A Selection within a logical query model.  This may also be used
 * in the order section of the query.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Selection implements Serializable {

  private static final long serialVersionUID = -3477700975099030430L;
  
  private Category category;
  private LogicalColumn logicalColumn;
  private AggregationType aggregation;

  public Selection(Category category, LogicalColumn column, AggregationType aggregation) {
    this.category = category;
    this.logicalColumn = column;
    this.aggregation = aggregation;
  }
  
  public Category getCategory() {
    return category;
  }
  
  /**
   * get the selected logical column
   * 
   * @return logical column
   */
  public LogicalColumn getLogicalColumn() {
    return logicalColumn;
  }
  
  public AggregationType getAggregationType() {
    return aggregation;
  }
  
  public boolean hasAggregate() {
    // this selection is an aggregate if the business column is an aggregate and the agg type is not null
    return !AggregationType.NONE.equals(getActiveAggregationType());
  }
  
  public AggregationType getActiveAggregationType() {
    if (getAggregationType() == null) {
      return logicalColumn.getAggregationType();
    } else {
      return getAggregationType();
    }
  }
  
}
