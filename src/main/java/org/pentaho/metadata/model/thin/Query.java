/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 - 2024 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.metadata.model.thin;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
