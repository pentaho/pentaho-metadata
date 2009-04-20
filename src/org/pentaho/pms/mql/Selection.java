/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
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
package org.pentaho.pms.mql;

import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This class defines an MQL selection
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 */
public class Selection {
 
  protected BusinessColumn businessColumn;
  protected AggregationSettings aggregationType;

  public Selection(BusinessColumn businessColumn) {
    this.businessColumn = businessColumn;
  }
  
  public Selection(BusinessColumn businessColumn, AggregationSettings aggregationType) {
    this.businessColumn = businessColumn;
    this.aggregationType = aggregationType;
  }
 
  /**
   * get the selected business column
   * 
   * @return business column
   */
  public BusinessColumn getBusinessColumn() {
    return businessColumn;
  }
  
  public AggregationSettings getAggregationType() {
    return aggregationType;
  }
  
  public boolean hasAggregate() {
    // this selection is an aggregate if the business column is an aggregate and the agg type is not null
    return !getActiveAggregationType().equals(AggregationSettings.NONE);
  }
  
  public AggregationSettings getActiveAggregationType() {
    if (getAggregationType() == null) {
      return businessColumn.getAggregationType();
    } else {
      return getAggregationType();
    }
  }
}
