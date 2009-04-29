package org.pentaho.metadata.query.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;

public class Query implements Serializable {

  private static final long serialVersionUID = 1828046238436174707L;
  
  private Domain domain;
  private LogicalModel logicalModel;
  
  private boolean disableDistinct;
  
  private List<Parameter> parameters = new ArrayList<Parameter>();
  private List<Selection> selections = new ArrayList<Selection>();
  private List<Constraint> constraints = new ArrayList<Constraint>();
  private List<Order> orders = new ArrayList<Order>();

  public Query(Domain domain, LogicalModel logicalModel) {
    this.domain = domain;
    this.logicalModel = logicalModel;
  }
  
  public Domain getDomain() {
    return domain;
  }
  
  public LogicalModel getLogicalModel() {
    return logicalModel;
  }
  
  public boolean getDisableDistinct() {
    return disableDistinct;
  }
  
  public void setDisableDistinct(boolean disableDistinct) {
    this.disableDistinct = disableDistinct;
  }
  
  public List<Parameter> getParameters() {
    return parameters;
  }
  
  public List<Selection> getSelections() {
    return selections;
  }
  
  public List<Constraint> getConstraints() {
    return constraints;
  }
  
  public List<Order> getOrders() {
    return orders;
  }
}
