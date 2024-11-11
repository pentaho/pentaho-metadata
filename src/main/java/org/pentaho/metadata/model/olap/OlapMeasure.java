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

package org.pentaho.metadata.model.olap;

import java.io.Serializable;

import org.pentaho.metadata.model.LogicalColumn;

public class OlapMeasure implements Cloneable, Serializable {
  private String name;
  private LogicalColumn logicalColumn;
  private boolean hidden;

  public static final String MEASURE_HIDDEN = "MEASURE_HIDDEN";

  public OlapMeasure() {
  }

  /**
   * @param name
   * @param logicalColumn
   */
  public OlapMeasure( String name, LogicalColumn logicalColumn ) {
    this();
    this.name = name;
    this.logicalColumn = logicalColumn;
  }

  public Object clone() {
    // shallow copy of logical column is desired
    return new OlapMeasure( name, logicalColumn );
  }

  /**
   * @return the logicalColumn
   */
  public LogicalColumn getLogicalColumn() {
    return logicalColumn;
  }

  /**
   * @param logicalColumn
   *          the logicalColumn to set
   */
  public void setLogicalColumn( LogicalColumn logicalColumn ) {
    this.logicalColumn = logicalColumn;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }
}
