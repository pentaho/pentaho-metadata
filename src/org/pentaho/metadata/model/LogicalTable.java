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
import org.pentaho.metadata.model.LogicalModel;

/**
 * The logical table contains logical columns, and inherits properties
 * from a physical table implementation.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LogicalTable extends Concept {

  public LogicalTable() {
    super();
  }

  private LogicalModel logicalModel;
  private IPhysicalTable physicalTable;
  
  // needs the security attribute.
  
  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();

  public IPhysicalTable getPhysicalTable() {
    return physicalTable;
  }
  
  public void setPhysicalTable(IPhysicalTable physicalTable) {
    this.physicalTable = physicalTable;
  }
  
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
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
    return logicalModel;
  }

}
