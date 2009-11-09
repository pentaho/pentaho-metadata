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
import org.pentaho.metadata.model.LogicalModel;

/**
 * The logical table contains logical columns, and inherits properties
 * from a physical table implementation.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LogicalTable extends Concept {

  private static final long serialVersionUID = -2655375483724689568L;

  private LogicalModel logicalModel;
  private IPhysicalTable physicalTable;
  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();

  public LogicalTable() {
    super();
  }
  
  public LogicalTable(LogicalModel logicalModel, IPhysicalTable physicalTable) { 
    this.logicalModel = logicalModel;
    this.physicalTable = physicalTable;
  }

  public void setLogicalModel(LogicalModel logicalModel) {
    this.logicalModel = logicalModel;
  }

  public LogicalModel getLogicalModel() {
    return logicalModel;
  }

  public IPhysicalTable getPhysicalTable() {
    return physicalTable;
  }
  
  public void setPhysicalTable(IPhysicalTable physicalTable) {
    this.physicalTable = physicalTable;
  }
  
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
  }

  /**
   * @return the display names of all the business columns
   */
  public List<String> getColumnNames(String locale) {
    List<String> list = new ArrayList<String>();
    for (LogicalColumn column : logicalColumns) {
      list.add(column.getName(locale));
    }

    return list;
  }
  
  public void setLogicalColumns(List<LogicalColumn> columns) {
    this.logicalColumns = columns;
  }
  
  public void addLogicalColumn(LogicalColumn column) {
    logicalColumns.add(column);
  }
  
  @Override
  public IConcept getInheritedConcept() {
    return physicalTable;
  }
  
  @Override
  public IConcept getSecurityParentConcept() {
    return getLogicalModel();
  }
  
  public LogicalColumn findLogicalColumn(String id) {
    for (LogicalColumn col : logicalColumns) {
      if (id.equals(col.getId())) {
        return col;
      }
    }
    return null;
  }
  
  public boolean equals(Object obj) {
    LogicalTable other = (LogicalTable)obj;
    return other.getId().equals(getId());
  }
  
  @Override
  public Object clone() {
     LogicalTable clone = new LogicalTable();
     // shallow copy
     clone(clone);
     clone.setLogicalModel(logicalModel);
     clone.physicalTable = physicalTable;
     
     // deep copy
     clone.setLogicalColumns(new ArrayList<LogicalColumn>());
     for (LogicalColumn col : logicalColumns) {
       clone.addLogicalColumn(col);
     }
     return clone;
  }

}
