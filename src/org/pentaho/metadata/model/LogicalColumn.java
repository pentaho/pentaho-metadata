package org.pentaho.metadata.model;

import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;

public class LogicalColumn extends Concept {

  public LogicalColumn() {
    super();
    // TODO Auto-generated constructor stub
  }

  private static final long serialVersionUID = -5818193472199662859L;

  private LogicalTable logicalTable;
  private IPhysicalColumn physicalColumn;

  @Override
  public IConcept getInheritedConcept() {
    return physicalColumn;
  }
  
  @Override
  public IConcept getSecurityParentConcept() {
    return logicalTable;
  }
  
  public IPhysicalColumn getPhysicalColumn() {
    return physicalColumn;
  }

  public void setPhysicalColumn(IPhysicalColumn physicalColumn) {
    this.physicalColumn = physicalColumn;
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

  public void setAggregationType(AggregationType aggType) {
    setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, aggType);
  };
  
  @SuppressWarnings("unchecked")
  public List<AggregationType> getAggregationList() {
    return (List<AggregationType>)getProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY);
  }

  public void setAggregationList(List<AggregationType> aggList) {
    setProperty(IPhysicalColumn.AGGREGATIONTYPE_PROPERTY, aggList);
  }
  
}
