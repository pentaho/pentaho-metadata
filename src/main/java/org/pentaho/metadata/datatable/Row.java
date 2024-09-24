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
package org.pentaho.metadata.datatable;

/**
 * An object that stores the @see Cell objects for a row in a @see DataTable
 * 
 * @author jamesdixon
 * 
 */
public class Row {

  private Cell[] c;

  public Row() {
  }

  public Row( Cell[] cells ) {
    c = cells;
  }

  /**
   * Returns the @see cell objects for this row
   * 
   * @return
   */
  public Cell[] getc() {
    return c;
  }

  /**
   * Sets the cells for this row
   * 
   * @param c
   */
  public void setc( Cell[] c ) {
    this.c = c;
  }

}
