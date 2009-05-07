package org.pentaho.metadata.model.concept.types;

import java.io.Serializable;

public class LocaleType implements Serializable {

  private static final long serialVersionUID = 5282520977042081601L;

  private String code;
  private String description;
  
  public LocaleType() {
  }
  
  public LocaleType(String code, String description) {
    this.code = code;
    this.description = description;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  public String getCode() {
    return code;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
