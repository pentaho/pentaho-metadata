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

package org.pentaho.metadata.datatable;

import java.math.BigDecimal;

/**
 * Defines a cell in a @see DataTable
 * 
 * @author jamesdixon
 * 
 */
public class Cell {

  private String f;

  private BigDecimal v;

  public Cell() {
  }

  public Cell( BigDecimal value ) {
    this.v = value;
  }

  public Cell( BigDecimal value, String formatted ) {
    this.v = value;
    this.f = formatted;
  }

  /**
   * Returns the string value of this cell. Can be the formatted value of a numeric column, a string value, a formatted
   * date value etc.
   * 
   * @return
   */
  public String getf() {
    return f;
  }

  /**
   * Sets the string value of this cell
   * 
   * @param f
   */
  public void setf( String f ) {
    this.f = f;
  }

  /**
   * Returns the numeric value, if any. Can be null.
   * 
   * @return
   */

  public BigDecimal getv() {
    return v;
  }

  /**
   * Sets the numeric value of the cell
   * 
   * @param v
   */
  public void setv( BigDecimal v ) {
    this.v = v;
  }

}
