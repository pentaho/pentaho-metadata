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
 * The Inline ETL Physical model is designed to handle CSV files and uses
 * inline ETL (Kettle Transformations) to execute query models.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlPhysicalModel extends Concept implements IPhysicalModel {

  private static final long serialVersionUID = 998991922256017536L;
  
  public static final String FILE_LOCATION = "file_location";
  public static final String HEADER_PRESENT = "header_present";
  public static final String ENCLOSURE = "enclosure";
  public static final String DELIMITER = "delimiter";
  
  // this contains a list of the physical tables
  private List<InlineEtlPhysicalTable> physicalTables = new ArrayList<InlineEtlPhysicalTable>();
  
  public String getFileLocation() {
    return (String)getProperty(FILE_LOCATION);
  }
  
  public void setFileLocation(String fileLocation) {
    setProperty(FILE_LOCATION, fileLocation);
  }
  
  public void setHeaderPresent(Boolean headerPresent) {
    setProperty(HEADER_PRESENT, headerPresent);
  }
  
  public Boolean getHeaderPresent() {
    return (Boolean)getProperty(HEADER_PRESENT);
  }

  public String getEnclosure() {
    return (String)getProperty(ENCLOSURE);
  }
  
  public void setEnclosure(String enclosure) {
    setProperty(ENCLOSURE, enclosure);
  }

  public String getDelimiter() {
    return (String)getProperty(DELIMITER);
  }

  public void setDelimiter(String delimiter) {
    setProperty(DELIMITER, delimiter);
  }
  
  
  public List<InlineEtlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }
  
}
