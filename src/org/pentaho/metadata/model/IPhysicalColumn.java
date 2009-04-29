package org.pentaho.metadata.model;

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;

public interface IPhysicalColumn extends IConcept {
  public static final String DATATYPE_PROPERTY = "datatype";
  public static final String AGGREGATIONTYPE_PROPERTY = "aggregationtype";
  public DataType getDataType();
  public void setDataType(DataType dataType);
  public AggregationType getAggregationType();
  public void setDataType(AggregationType aggType);
}
