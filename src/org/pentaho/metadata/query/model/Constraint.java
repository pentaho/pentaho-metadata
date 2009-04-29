package org.pentaho.metadata.query.model;

import java.io.Serializable;

public class Constraint implements Serializable {

  private static final long serialVersionUID = -8703652534217339403L;
  
  private CombinationType combinationType;
  private String formula;
  
  public Constraint(CombinationType combinationType, String formula) {
    this.combinationType = combinationType;
    this.formula = formula;
  }
  
  public CombinationType getCombinationType() {
    return combinationType;
  }
  
  public String getFormula() {
    return formula;
  }

}
