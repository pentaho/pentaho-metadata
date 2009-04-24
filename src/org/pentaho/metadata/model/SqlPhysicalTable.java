package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.TargetTableType;

public class SqlPhysicalTable extends Concept implements IPhysicalTable {
  
  private static final String TARGET_SCHEMA_PROPERTY = "target_schema";
  private static final String TARGET_TABLE_PROPERTY = "target_table";
  private static final String TARGET_TABLE_TYPE_PROPERTY = "target_table_type";
  
  List<IPhysicalColumn> physicalColumns = new ArrayList<IPhysicalColumn>();
  
  public SqlPhysicalTable() {
    setTargetTableType(TargetTableType.TABLE);
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
