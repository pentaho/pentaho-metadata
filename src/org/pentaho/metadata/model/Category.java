/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;

/**
 * The category class contains links to logical columns, which are part of 
 * the logical model.  This can be considered the view of the logical model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Category extends Concept {

  private static final long serialVersionUID = -2367402604729602739L;

  private LogicalModel parent;
  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();

  public Category() {
    super();
  }
  
  /**
   * The security parent for category is the logical model.
   */
  @Override
  public IConcept getSecurityParentConcept() {
    return parent;
  }
  
  /**
   * the list of logical columns in this category
   * 
   * @return list of logical columns.
   */
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
  }

  public void setLogicalColumns(List<LogicalColumn> columns) {
    this.logicalColumns = columns;
  }
  
  public void addLogicalColumn(LogicalColumn column) {
    logicalColumns.add(column);
  }
  
  /**
   * searches the category for a specific column id.
   * 
   * @param columnId column to search for
   * 
   * @return logical column object if found
   */
  public LogicalColumn findLogicalColumn(String columnId) {
    for (LogicalColumn col : getLogicalColumns()) {
      if (columnId.equals(col.getId())) {
        return col;
      }
    }
    return null;
  }
  
  @Override
  public Object clone() {
    Category clone = new Category();
    // shallow copies
    clone(clone);
    clone.parent = parent;
    
    // deep copies
    clone.setLogicalColumns(new ArrayList<LogicalColumn>());
    for (LogicalColumn col : getLogicalColumns()) {
      clone.addLogicalColumn(col);
    }
    return clone;
  }
}
