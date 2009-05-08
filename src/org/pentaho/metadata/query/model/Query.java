/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.metadata.query.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;

/**
 * The Query object defines a logical query model.
 * Query models may be executed by various physical model implementations.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
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
