package org.pentaho.metadata.query.model;

import java.io.Serializable;

public class Order implements Serializable {
  
  public enum Type{ ASC, DESC };
  
  private static final long serialVersionUID = 7828692078614137281L;
  
  private Selection selection;
  private Type type;
  
  public Order(Selection selection, Type type) {
    this.selection = selection;
    this.type = type;
  }
  
  public Selection getSelection() {
    return selection;
  }
  
  public Type getType() {
    return type;
  }

}
