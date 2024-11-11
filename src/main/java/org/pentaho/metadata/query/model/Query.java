/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.query.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;

/**
 * The Query object defines a logical query model. Query models may be executed by various physical model
 * implementations.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Query implements Serializable {

  private static final long serialVersionUID = 1828046238436174707L;

  private Domain domain;
  private LogicalModel logicalModel;

  private boolean disableDistinct;
  private int limit = -1;

  private List<Parameter> parameters = new ArrayList<Parameter>();
  private List<Selection> selections = new ArrayList<Selection>();
  private List<Constraint> constraints = new ArrayList<Constraint>();
  private List<Order> orders = new ArrayList<Order>();

  public Query( Domain domain, LogicalModel logicalModel ) {
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

  public void setDisableDistinct( boolean disableDistinct ) {
    this.disableDistinct = disableDistinct;
  }

  /**
   * Returns row limit. Negative integer means no limit.
   * 
   * @return limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Sets row limit. Negative integer means no limit.
   * 
   * @param limit
   */
  public void setLimit( int limit ) {
    this.limit = limit;
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
