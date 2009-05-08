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

import org.pentaho.metadata.model.concept.Concept;

/**
 * Logical relationships define the relationship between two
 * business tables through columns or an open formula expression.
 * 
 * TODO: This class needs additional work before it can support 
 * the capabilities in the original metadata model
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LogicalRelationship extends Concept {

  private static final long serialVersionUID = -2673951365033614344L;
  
  private LogicalTable fromTable, toTable;
  private LogicalColumn fromColumn, toColumn;
  /* where this relationship falls in relation to others */
  private long ordinal;

  public LogicalRelationship() {
    super();
  }
  
  public LogicalTable getFromTable() {
    return fromTable;
  }

  public void setFromTable(LogicalTable fromTable) {
    this.fromTable = fromTable;
  }

  public LogicalTable getToTable() {
    return toTable;
  }

  public void setToTable(LogicalTable toTable) {
    this.toTable = toTable;
  }

  public LogicalColumn getFromColumn() {
    return fromColumn;
  }

  public void setFromColumn(LogicalColumn fromColumn) {
    this.fromColumn = fromColumn;
  }

  public LogicalColumn getToColumn() {
    return toColumn;
  }

  public void setToColumn(LogicalColumn toColumn) {
    this.toColumn = toColumn;
  }

  public long getOrdinal() {
    return ordinal;
  }

  public void setOrdinal(long ordinal) {
    this.ordinal = ordinal;
  }

  //TODO give this enum the correct set of supported join types
  public enum JoinType { OUTER, INNER, LEFT, RIGHT }

}
