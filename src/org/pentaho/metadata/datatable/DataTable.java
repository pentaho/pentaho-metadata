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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jun, 2012
 * @author jdixon
 */
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
