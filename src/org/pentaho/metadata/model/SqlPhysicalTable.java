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
import org.pentaho.metadata.model.concept.types.TargetTableType;

/**
 * This is the SQL implementation of a physical table.  It acts as either
 * a table in the database or a SQL statement.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlPhysicalTable extends Concept implements IPhysicalTable{

  private static final long serialVersionUID = -2590635019353532334L;

  private static final String TARGET_SCHEMA_PROPERTY = "target_schema";
  private static final String TARGET_TABLE_PROPERTY = "target_table";
  private static final String TARGET_TABLE_TYPE_PROPERTY = "target_table_type";

  SqlPhysicalModel model;
  List<IPhysicalColumn> physicalColumns = new ArrayList<IPhysicalColumn>();

  public SqlPhysicalTable() {
    super();
  }

  
  public SqlPhysicalTable(SqlPhysicalModel model) {
    this.model = model;
    setTargetTableType(TargetTableType.TABLE);
  }


  public IPhysicalModel getPhysicalModel() {
    return model;
  }
  
  public List<IPhysicalColumn> getPhysicalColumns() {
    return physicalColumns;
  }
  
  public String getTargetSchema() {
    return (String)getProperty(TARGET_SCHEMA_PROPERTY);
  }
  
  public void setTargetSchema(String targetSchema) {
    setProperty(TARGET_SCHEMA_PROPERTY, targetSchema);
  }
  
  public String getTargetTable() {
    return (String)getProperty(TARGET_TABLE_PROPERTY);
  }

  public void setTargetTable(String targetTable) {
    setProperty(TARGET_TABLE_PROPERTY, targetTable);
  }
  
  public TargetTableType getTargetTableType() {
    return (TargetTableType)getProperty(TARGET_TABLE_TYPE_PROPERTY);
  }
  
  public void setTargetTableType(TargetTableType targetTableType) {
    setProperty(TARGET_TABLE_TYPE_PROPERTY, targetTableType);
  }
}
