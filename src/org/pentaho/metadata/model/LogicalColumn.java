package org.pentaho.metadata.model;

import org.pentaho.pms.schema.PhysicalColumn;

public class LogicalColumn extends Entity {
  
  //TODO add to this enum
  public enum DataType { STRING, INTEGER, FLOAT }
  
  private PhysicalColumn physicalColumn;
  
  public PhysicalColumn getPhysicalColumn() {
    return physicalColumn;
  }

  public void setPhysicalColumn(PhysicalColumn physicalColumn) {
    this.physicalColumn = physicalColumn;
  }

  private DataType dataType;
  
  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  };

}
