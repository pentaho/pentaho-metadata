/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.model;

import java.io.Serializable;

/**
 * This class represents an attribute of a metadata object.
 * An attribute is made of an id, a value, and a type.
 * 
 * This should be extended to support the ConceptPropertyInterface and
 * allow richer objects to be passed
 * @author jamesdixon
 *
 */
public class Attribute implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String id;
  
  private String value;
  
  private String type;

  /**
   * Gets the id of this attribute
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of this attribute
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns the value of this attribute
   * @return
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of this attribute
   * @param value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Returns the type of this attribute
   * @return
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of this attribute
   * @param type
   */
  public void setType(String type) {
    this.type = type;
  }
  
}
