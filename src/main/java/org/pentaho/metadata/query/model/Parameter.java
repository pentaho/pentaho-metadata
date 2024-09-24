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

  public Parameter( String name, DataType type, Object defaultValue ) {
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
