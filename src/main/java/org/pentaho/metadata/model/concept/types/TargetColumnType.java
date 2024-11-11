/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.model.concept.types;

/**
 * enum defining the various column types. This replaces the earlier version metadata property isExact().
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public enum TargetColumnType {
  COLUMN_NAME(), OPEN_FORMULA();
}
