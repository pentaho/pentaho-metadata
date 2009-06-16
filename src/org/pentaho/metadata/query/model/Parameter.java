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

import org.pentaho.metadata.model.concept.types.DataType;

/**
 * This defines a parameter within a logical query model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Parameter implements Serializable {

  
  
  private static final long serialVersionUID = -1562891705335709848L;

  private String name;
  private DataType type;
  private Object defaultValue;
  
  public Parameter(String name, DataType type, Object defaultValue) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
  }
  
  public String getName() {
    return name;
  }
  
  public DataType getType() {
    return type;
  }
  
  public Object getDefaultValue() {
    return defaultValue;
  }

}
