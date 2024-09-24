/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
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
