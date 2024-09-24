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

/**
 * Concrete, lightweight, serializable object that holds information about sorting
 * 
 * @author jamesdixon
 * 
 */
public class Order {

  private static final long serialVersionUID = 4824503466813354111L;

  private String elementId;

  private String parentId;

  private String orderType;

  public String getElementId() {
    return this.elementId;
  }

  public void setElementId( String elementId ) {
    this.elementId = elementId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType( String orderType ) {
    this.orderType = orderType;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId( String parentId ) {
    this.parentId = parentId;
  }
}
