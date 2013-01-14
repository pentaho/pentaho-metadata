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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql;

import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

/**
 * This class defines an MQL selection
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.query.model.Selection
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
