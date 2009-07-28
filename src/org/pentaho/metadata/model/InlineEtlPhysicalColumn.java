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
package org.pentaho.metadata.model;

/**
 * The Inline Etl column inherits from the abstract physical column, and also
 * defines a column number to get data from.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlPhysicalColumn extends AbstractPhysicalColumn {

  private static final long serialVersionUID = 2960505010295811572L;

  public static final String COLUMN_NUMBER = "column_number"; //$NON-NLS-1$
  public static final String FIELD_NAME = "field_name"; //$NON-NLS-1$
  
  private InlineEtlPhysicalTable table;
  public InlineEtlPhysicalColumn() {
    super();
  }
  
  public void setTable(InlineEtlPhysicalTable table) {
    this.table = table;
  }
  
  public InlineEtlPhysicalTable getPhysicalTable() {
    return table;
  }
  
  public String getFieldName() {
    return (String)getProperty(FIELD_NAME);
  }
  
  public void setFieldName(String fieldName) {
    setProperty(FIELD_NAME, fieldName);
  }

}
