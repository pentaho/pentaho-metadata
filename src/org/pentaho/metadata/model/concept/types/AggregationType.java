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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
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

