
package org.pentaho.metadata.model.concept.types;


public enum AggregationType{
  NONE("AggregationSettings.USER_NONE_DESC"), 
  SUM("AggregationSettings.USER_SUM_DESC"), 
  AVG("AggregationSettings.USER_AVERAGE_DESC"),
  COUNT("AggregationSettings.USER_COUNT_DESC"), 
  DISTINCT_COUNT("AggregationSettings.USER_DISTINCT_COUNT_DESC"),
  MIN("AggregationSettings.USER_MINIMUM_DESC"), 
  MAX("AggregationSettings.AggregationSettings"); 

  private String description;

  private AggregationType(String description) {
    this.description = description;
  }
  
}

