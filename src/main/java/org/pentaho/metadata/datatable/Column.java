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
 * Defines the metadata for a column in a @see DataTable
 * 
 * @author jamesdixon
 * 
 */
public class Column {

  private String id;
  private String type;
  private String label;

  public Column() {
  }

  public Column( String id, String label, String type ) {
    this.id = id;
    this.label = label;
    this.type = type;
  }

  /**
   * Returns the id of this column
   * 
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of this column
   * 
   * @param id
   */
  public void setId( String id ) {
    this.id = id;
  }

  /**
   * Gets the type of this column - 'string', 'numeric', 'date', or 'boolean'
   * 
   * @return
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of this column
   * 
   * @param type
   */
  public void setType( String type ) {
    this.type = type;
  }

  /**
   * Returns the label of this column
   * 
   * @return
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label of this column
   * 
   * @param label
   */
  public void setLabel( String label ) {
    this.label = label;
  }

}
