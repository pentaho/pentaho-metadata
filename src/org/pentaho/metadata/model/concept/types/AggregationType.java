
package org.pentaho.metadata.model.concept.types;
import org.pentaho.pms.messages.Messages;

public enum AggregationType {
  NONE(Messages.getString("AggregationSettings.USER_NONE_DESC")), //$NON-NLS-1$
  SUM(Messages.getString("AggregationSettings.USER_SUM_DESC")), //$NON-NLS-1$
  AVG(Messages.getString("AggregationSettings.USER_AVERAGE_DESC")), //$NON-NLS-1$
  COUNT(Messages.getString("AggregationSettings.USER_COUNT_DESC")), //$NON-NLS-1$
  DISTINCT_COUNT(Messages.getString("AggregationSettings.USER_DISTINCT_COUNT_DESC")), //$NON-NLS-1$
  MIN(Messages.getString("AggregationSettings.USER_MINIMUM_DESC")), //$NON-NLS-1$
  MAX(Messages.getString("AggregationSettings.USER_MAXIMUM_DESC")); //$NON-NLS-1$

  private String description;

  private AggregationType(String description) {
    this.description = description;
  }
  
}
