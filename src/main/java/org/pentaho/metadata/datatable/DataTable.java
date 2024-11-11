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
 * A serializable data table. The JSON serialization of this class is compatible with the javascript objects expected by
 * the Data Access client API and the Visualization API.
 * 
 * @author jamesdixon
 * 
 */
public class DataTable {

  private Column[] cols;

  private Row[] rows;

  public DataTable() {

  }

  public DataTable( Row[] rows, Column[] cols ) {
    this.rows = rows;
    this.cols = cols;
  }

  /**
   * Returns an array of @see Column metadata
   * 
   * @return
   */
  public Column[] getCols() {
    return cols;
  }

  /**
   * Sets the column metadata
   * 
   * @param cols
   */
  public void setCols( Column[] cols ) {
    this.cols = cols;
  }

  /**
   * Returns the @see Row objects for this table
   * 
   * @return
   */
  public Row[] getRows() {
    return rows;
  }

  /**
   * Sets the rows for this table
   * 
   * @param rows
   */
  public void setRows( Row[] rows ) {
    this.rows = rows;
  }

}
