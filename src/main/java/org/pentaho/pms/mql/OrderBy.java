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

package org.pentaho.pms.mql;

/**
 * Contains a selection and the sort direction, used to specify the sorting of that column.
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.Order
 */
public class OrderBy {
  private Selection selection;
  private boolean ascending;

  /**
   * @param selection
   *          the selection to sort on (ascending)
   */
  public OrderBy( Selection selection ) {
    super();
    this.selection = selection;
    this.ascending = true;
  }

  /**
   * @param selection
   *          the selection to sort on
   * @param ascending
   *          true if you want to sort ascending, false if you want to sort descending
   */
  public OrderBy( Selection selection, boolean ascending ) {
    super();
    this.selection = selection;
    this.ascending = ascending;
  }

  /**
   * @return the ascending flag, true = ascending, false = descending
   */
  public boolean isAscending() {
    return ascending;
  }

  /**
   * @param ascending
   *          the ascending flag to set, true = ascending, false = descending
   */
  public void setAscending( boolean ascending ) {
    this.ascending = ascending;
  }

  /**
   * @return the selection to sort on
   */
  public Selection getSelection() {
    return selection;
  }

  /**
   * @param selection
   *          the selection to sort on
   */
  public void setSelection( Selection selection ) {
    this.selection = selection;
  }

}
