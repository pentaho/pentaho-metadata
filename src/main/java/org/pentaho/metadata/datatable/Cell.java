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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 * 
 * Created Jun, 2012
 * @author jdixon
 */
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
