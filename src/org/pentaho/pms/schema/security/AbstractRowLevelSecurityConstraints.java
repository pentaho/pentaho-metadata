package org.pentaho.pms.schema.security;

import java.util.List;

public abstract class AbstractRowLevelSecurityConstraints implements RowLevelSecurityConstraints {

  public String getMQLFormula(String user, List<String> roles) {
    return getSingleFormula();
  }
  
  abstract public Object clone() throws CloneNotSupportedException;

}
