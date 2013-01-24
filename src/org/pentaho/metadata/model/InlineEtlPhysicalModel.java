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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.IConcept;

/**
 * The Inline ETL Physical model is designed to handle CSV files and uses
 * inline ETL (Kettle Transformations) to execute query models.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlPhysicalModel extends AbstractPhysicalModel {

  private static final long serialVersionUID = 998991922256017536L;
  
  public static final String FILE_LOCATION = "file_location"; //$NON-NLS-1$
  public static final String HEADER_PRESENT = "header_present"; //$NON-NLS-1$
  public static final String ENCLOSURE = "enclosure"; //$NON-NLS-1$
  public static final String DELIMITER = "delimiter"; //$NON-NLS-1$
  
  // this contains a list of the physical tables
  private List<InlineEtlPhysicalTable> physicalTables = new ArrayList<InlineEtlPhysicalTable>();

  public InlineEtlPhysicalModel() {
    super();
  }
  
  @Override
  public List<IConcept> getChildren() {
    List<IConcept> children = new ArrayList<IConcept>();
    children.addAll(physicalTables);
    return children;
  }

  public String getQueryExecName() {
    return "metadataqueryexec-ETL";
  }
  
  public String getDefaultQueryClassname() {
    return "org.pentaho.metadata.query.impl.ietl.InlineEtlQueryExecutor";
  }
  
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
