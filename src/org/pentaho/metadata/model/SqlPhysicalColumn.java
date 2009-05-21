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

import org.pentaho.metadata.model.concept.types.TargetColumnType;


/**
 * this is the SQL implementation of physical column.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlPhysicalColumn extends AbstractPhysicalColumn {

  private static final long serialVersionUID = -9131564777458111496L;

  public static final String TARGET_COLUMN = "target_column";
  public static final String TARGET_COLUMN_TYPE = "target_column_type";

  private SqlPhysicalTable table;

  public SqlPhysicalColumn() {
    super();
  }
  
  public SqlPhysicalColumn(SqlPhysicalTable table) {
    this.table = table;
    setTargetColumnType(TargetColumnType.COLUMN_NAME);
  }
  
  public String getTargetColumn() {
    return (String)getProperty(TARGET_COLUMN);
  }

  public void setTargetColumn(String targetTable) {
    setProperty(TARGET_COLUMN, targetTable);
  }
  
  public TargetColumnType getTargetColumnType() {
    return (TargetColumnType)getProperty(TARGET_COLUMN_TYPE);
  }
  
  public void setTargetColumnType(TargetColumnType targetTableType) {
    setProperty(TARGET_COLUMN_TYPE, targetTableType);
  }

  public IPhysicalTable getPhysicalTable() {
    return table;
  }
  
}
