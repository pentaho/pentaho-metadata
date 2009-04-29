package org.pentaho.metadata.query.model;

import java.io.Serializable;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.concept.types.AggregationType;

public class Selection implements Serializable {

  private static final long serialVersionUID = -3477700975099030430L;
  
  private Category category;
  private LogicalColumn logicalColumn;
  private AggregationType aggregation;

  public Selection(Category category, LogicalColumn column, AggregationType aggregation) {
    this.category = category;
    this.logicalColumn = column;
    this.aggregation = aggregation;
  }
  
  public Category getCategory() {
    return category;
  }
  
  /**
   * get the selected logical column
   * 
   * @return logical column
   */
  public LogicalColumn getLogicalColumn() {
    return logicalColumn;
  }
  
  public AggregationType getAggregationType() {
    return aggregation;
  }
  
  public boolean hasAggregate() {
    // this selection is an aggregate if the business column is an aggregate and the agg type is not null
    return !AggregationType.NONE.equals(getActiveAggregationType());
  }
  
  public AggregationType getActiveAggregationType() {
    if (getAggregationType() == null) {
      return logicalColumn.getAggregationType();
    } else {
      return getAggregationType();
    }
  }
  
}
