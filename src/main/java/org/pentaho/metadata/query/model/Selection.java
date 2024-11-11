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

package org.pentaho.metadata.query.model;

import java.io.Serializable;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.concept.types.AggregationType;

/**
 * A Selection within a logical query model. This may also be used in the order section of the query.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Selection implements Serializable {

  private static final long serialVersionUID = -3477700975099030430L;

  private Category category;
  private LogicalColumn logicalColumn;
  private AggregationType aggregation;

  public Selection( Category category, LogicalColumn column, AggregationType aggregation ) {
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
    return !AggregationType.NONE.equals( getActiveAggregationType() );
  }

  public AggregationType getActiveAggregationType() {
    if ( getAggregationType() == null ) {
      AggregationType aggType = logicalColumn.getAggregationType();
      if ( aggType == null ) {
        return AggregationType.NONE;
      } else {
        return aggType;
      }
    } else {
      return getAggregationType();
    }
  }

  public int hashCode() {
    return logicalColumn.getId().hashCode();
  }

  public boolean equals( Object selection ) {
    Selection sel = (Selection) selection;
    return sel.getLogicalColumn().equals( getLogicalColumn() )
        && sel.getActiveAggregationType() == getActiveAggregationType();
  }

}
