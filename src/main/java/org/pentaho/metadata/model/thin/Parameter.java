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

import java.io.Serializable;

/**
 * Concrete, lightweight, serializable object that holds information about parameters
 * 
 * @author jamesdixon
 * 
 */
public class Parameter implements Serializable {

  private static final long serialVersionUID = -3581457277760183764L;
  private String elementId;
  private String name;
  private String type;
  private String[] defaultValue;
  private String[] value;

  public Parameter() {
  }

  public String getType() {
    return type;
  }

  public String[] getDefaultValue() {
    return defaultValue;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public void setDefaultValue( String[] defaultValue ) {
    this.defaultValue = defaultValue;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId( String elementId ) {
    this.elementId = elementId;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String[] getValue() {
    if ( value != null ) {
      return value;
    } else {
      return defaultValue;
    }
  }

  public void setValue( String[] value ) {
    this.value = value;
  }

}
