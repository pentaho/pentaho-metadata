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
package org.pentaho.metadata.model.concept.types;

/**
 * the aggregation type of a physical or logical column.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum AggregationType {
  NONE( "AggregationType.USER_NONE_DESC" ), //$NON-NLS-1$ 
  SUM( "AggregationType.USER_SUM_DESC" ), //$NON-NLS-1$
  AVERAGE( "AggregationType.USER_AVERAGE_DESC" ), //$NON-NLS-1$
  COUNT( "AggregationType.USER_COUNT_DESC" ), //$NON-NLS-1$
  COUNT_DISTINCT( "AggregationType.USER_COUNT_DISTINCT_DESC" ), //$NON-NLS-1$
  MINIMUM( "AggregationType.USER_MINIMUM_DESC" ), //$NON-NLS-1$
  MAXIMUM( "AggregationType.USER_MAXIMUM_DESC" ); //$NON-NLS-1$

  private String description;

  private AggregationType( String description ) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }
}
