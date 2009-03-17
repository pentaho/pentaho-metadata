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

import org.pentaho.pms.schema.v3.envelope.Envelope;

/**
 * A thin model of a business column. This class stores the
 * data type, field type, and attributes of the business column
 * 
 * TODO convert the data types to XML Schema types: 
 * http://www.w3.org/TR/xmlschema-2/#built-in-primitive-datatypes
 * 
 * @author jamesdixon
 *
 */
public class Column extends Envelope {

  private String fieldType;
  
  private String dataType;
  
  private Attribute[] attributes;
  
  /**
   * Returns the field type of this column, e.g. Dimension
   * @return
   */
  public String getFieldType() {
    return fieldType;
  }

  /**
   * Sets the field type of this column, e.g. Dimension
   * @param fieldType
   */
  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  /**
   * Gets the data type of this column e.g. String
   * @return
   */
  public String getDataType() {
    return dataType;
  }

  /**
   * Sets the data type of this column e.g. String
   * @param dataType
   */
  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  /**
   * Gets the array of attributes of this column
   * @return
   */
  public Attribute[] getAttributes() {
    return attributes;
  }

  /**
   * Sets the array of attributes of this column
   * @return
   */
  public void setAttributes(Attribute[] attributes) {
    this.attributes = attributes;
  }
  
}
