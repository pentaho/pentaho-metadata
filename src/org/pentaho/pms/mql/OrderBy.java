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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
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
