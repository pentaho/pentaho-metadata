/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.model;

import org.pentaho.pms.schema.v3.envelope.Envelope;

/**
 * A thin model of a business category. This class stores the
 * list of sub-categories and columns that make up a category.
 * @author jamesdixon
 *
 */
public class Category extends Envelope {

  private Category[] subCategories;
  
  private Column[] columns;

  /**
   * Returns the sub-categories of this category
   * @return
   */
  public Category[] getSubCategories() {
    return subCategories;
  }

  /**
   * Sets the sub-categories of this category
   * @param subCategories
   */
  public void setSubCategories(Category[] subCategories) {
    this.subCategories = subCategories;
  }

  /**
   * Returns the columns of this category
   * @return
   */
  public Column[] getColumns() {
    return columns;
  }

  /**
   * Sets the columns of this category
   * @param columns
   */
  public void setColumns(Column[] columns) {
    this.columns = columns;
  }
  
}
