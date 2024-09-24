/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.pms.mql;

import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This class defines an MQL selection
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.Selection
 */
public class Selection {

  protected BusinessColumn businessColumn;
  protected AggregationSettings aggregationType;

  public Selection( BusinessColumn businessColumn ) {
    this.businessColumn = businessColumn;
  }

  public Selection( BusinessColumn businessColumn, AggregationSettings aggregationType ) {
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
    return !getActiveAggregationType().equals( AggregationSettings.NONE );
  }

  public AggregationSettings getActiveAggregationType() {
    if ( getAggregationType() == null ) {
      return businessColumn.getAggregationType();
    } else {
      return getAggregationType();
    }
  }
}
