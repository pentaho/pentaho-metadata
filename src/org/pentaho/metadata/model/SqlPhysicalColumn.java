package org.pentaho.metadata.model;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;

public class SqlPhysicalColumn extends Concept implements IPhysicalColumn {
  
  private static final String TARGET_COLUMN = "target_column";
  private static final String TARGET_COLUMN_TYPE = "target_column_type";

  public SqlPhysicalColumn() {
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
  
  public DataType getDataType() {
    return (DataType)getProperty(IPhysicalColumn.DATATYPE_PROPERTY);
  }

  public void setDataType(DataType dataType) {
    setProperty(IPhysicalColumn.DATATYPE_PROPERTY, dataType);
  };
  
  public AggregationType getAggregationType() {
    return (AggregationType)getProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY);
  }

  public void setDataType(AggregationType aggType) {
    setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, aggType);
  };

  
}
