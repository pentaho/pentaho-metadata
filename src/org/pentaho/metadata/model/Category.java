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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * The category class contains links to logical columns, which are part of 
 * the logical model.  This can be considered the view of the logical model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Category extends Concept  {

  private static final long serialVersionUID = -2367402604729602739L;

  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
  private static final String CLASS_ID = "Category";

  public Category() {
    super();
    // category has the following default properties
    setName(new LocalizedString());
    setDescription(new LocalizedString());
  }

  public Category( IConcept logicalModel ) {
    setParent( logicalModel );
  }
  
  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>( getParent().getUniqueId() );
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  /**
   * The security parent for category is the logical model.
   */
  @Override
  public IConcept getSecurityParentConcept() {
    return getParent();
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
    clone( clone );
    clone.setParent( getParent() );

    // deep copies
    clone.setLogicalColumns(new ArrayList<LogicalColumn>());
    for (LogicalColumn col : getLogicalColumns()) {
      clone.addLogicalColumn(col);
    }
    return clone;
  }
}
