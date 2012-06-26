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
 * Copyright (c) 2011 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jan, 2011
 * @author jdixon
*/
package org.pentaho.metadata.model.thin;

import java.io.Serializable;

/**
 * Concrete, lightweight, serializable object that holds information about parameters
 * @author jamesdixon
 *
 */
public class Parameter implements Serializable {
  
  private static final long serialVersionUID = -3581457277760183764L;
  private String elementId;
  private String name;
  private String type;
  private String defaultValue[];
  private String value[];
  
  public Parameter() {
  }
  
  public String getType() {
    return type;
  }
  
  public String[] getDefaultValue() {
    return defaultValue;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setDefaultValue(String defaultValue[]) {
    this.defaultValue = defaultValue;
  }

  public String getElementId() {
	return elementId;
  }

  public void setElementId(String elementId) {
	this.elementId = elementId;
  }

public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getValue() {
    if( value != null ) {
      return value;
    } else {
      return defaultValue;
    }
  }

  public void setValue(String value[]) {
    this.value = value;
  }

}
