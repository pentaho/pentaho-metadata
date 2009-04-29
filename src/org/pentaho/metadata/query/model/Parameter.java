package org.pentaho.metadata.query.model;

import java.io.Serializable;

public class Parameter implements Serializable {

  private static final long serialVersionUID = -1562891705335709848L;

  private String name;
  private String defaultValue;
  
  public Parameter(String name, String defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDefaultValue() {
    return defaultValue;
  }

}
