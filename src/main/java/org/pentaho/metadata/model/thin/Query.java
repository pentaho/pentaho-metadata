/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.metadata.model.thin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Defines a query model in terms of metadata.
 * 
 * @author jamesdixon
 * 
 */
public class Query {

  private static final long serialVersionUID = 8616769258583080677L;

  public static final List< Class > CLASS_LIST = new ArrayList< Class >( Arrays.asList( Query.class, Element.class, Condition.class,
          Order.class, Parameter.class ));
  private Element[] elements = new Element[0];

  private Condition[] conditions = new Condition[0];

  private Order[] orders = new Order[0];

  private Parameter[] parameters = new Parameter[0];

  private String sourceId;

  private Boolean disableDistinct;

  /**
   * Keys are parameter names; values are defaults for those parameters.
   */
  private Map<String, String> defaultParameterMap;

  public Query() {
    super();
  }

  public Element[] getElements() {
    return elements;
  }

  public Condition[] getConditions() {
    return conditions;
  }

  public String getSourceId() {
    return sourceId;
  }

  public Order[] getOrders() {
    return orders;
  }

  public void setElements( Element[] elements ) {

    this.elements = elements;
  }

  public void setConditions( Condition[] conditions ) {

    this.conditions = conditions;
  }

  public void setOrders( Order[] orders ) {

    this.orders = orders;
  }

  public void setSourceId( String sourceId ) {

    this.sourceId = sourceId;
  }

  public Map<String, String> getDefaultParameterMap() {
    return defaultParameterMap;
  }

  public void setDefaultParameterMap( Map<String, String> defaultParameterMap ) {
    this.defaultParameterMap = defaultParameterMap;
  }

  public Boolean getDisableDistinct() {
    return disableDistinct;
  }

  public void setDisableDistinct( Boolean disableDistinct ) {
    this.disableDistinct = disableDistinct;
  }

  public Parameter[] getParameters() {
    return parameters;
  }

  public void setParameters( Parameter[] parameters ) {
    this.parameters = parameters;
  }

}
