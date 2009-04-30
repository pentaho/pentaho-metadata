package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;

public interface IPhysicalColumn extends IConcept {
  public static final String DATATYPE_PROPERTY = "datatype";
  public static final String AGGREGATIONTYPE_PROPERTY = "aggregationtype";
  public static final String AGGREGATIONLIST_PROPERTY = "aggregationlist";
  public DataType getDataType();
  public void setDataType(DataType dataType);
  public AggregationType getAggregationType();
  public void setAggregationType(AggregationType aggType);
  public List<AggregationType> getAggregationList();
  public void setAggregationList(List<AggregationType> aggList);
}
