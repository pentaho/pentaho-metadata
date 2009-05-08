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

import java.util.ArrayList;
import java.util.List;
import org.pentaho.metadata.model.concept.Concept;

/**
 * This is the SQL implementation of the physical model.  For now 
 * it contains a string reference to it's data source (JNDI or Pentaho).
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlPhysicalModel extends Concept implements IPhysicalModel{
  
  // this property should be replaced with a thin 
  // representation of database meta, which is required
  // for full backward compatibility.
  
  /** returns a pentaho or JNDI datasource **/
  private String datasource;
  
  // this contains a list of the physical tables
  private List<SqlPhysicalTable> physicalTables = new ArrayList<SqlPhysicalTable>();

  public SqlPhysicalModel() {
    super();
  }
  
  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String getDatasource() {
    return datasource;
  }
  
  public List<SqlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }
  

}
