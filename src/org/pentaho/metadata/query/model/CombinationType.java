package org.pentaho.metadata.query.model;

import java.io.Serializable;

public enum CombinationType implements Serializable {
  AND("AND"), OR("OR"), AND_NOT("AND NOT"), OR_NOT("OR NOT");
  
  private String toStringVal;
  private CombinationType(String val){
    toStringVal = val;
  }
  
  @Override
  public String toString() {
    return toStringVal;
  }
  
}
