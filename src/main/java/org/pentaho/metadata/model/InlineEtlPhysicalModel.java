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
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;

/**
 * The Inline ETL Physical model is designed to handle CSV files and uses inline ETL (Kettle Transformations) to execute
 * query models.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class InlineEtlPhysicalModel extends Concept implements IPhysicalModel {

  private static final long serialVersionUID = 998991922256017536L;

  public static final String FILE_LOCATION = "file_location"; //$NON-NLS-1$
  public static final String HEADER_PRESENT = "header_present"; //$NON-NLS-1$
  public static final String ENCLOSURE = "enclosure"; //$NON-NLS-1$
  public static final String DELIMITER = "delimiter"; //$NON-NLS-1$

  private static final String CLASS_ID = "IPhysicalModel";

  // this contains a list of the physical tables
  private List<InlineEtlPhysicalTable> physicalTables = new ArrayList<InlineEtlPhysicalTable>();

  public InlineEtlPhysicalModel() {
    super();
  }

  public void setDomain( Domain domain ) {
    setParent( domain );
  }

  public Domain getDomain() {
    return (Domain) getParent();
  }

  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>();
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  @Override
  public List<IConcept> getChildren() {
    List<IConcept> children = new ArrayList<IConcept>();
    children.addAll( physicalTables );
    return children;
  }

  public String getQueryExecName() {
    return "metadataqueryexec-ETL";
  }

  public String getDefaultQueryClassname() {
    return "org.pentaho.metadata.query.impl.ietl.InlineEtlQueryExecutor";
  }

  public String getFileLocation() {
    return (String) getProperty( FILE_LOCATION );
  }

  public void setFileLocation( String fileLocation ) {
    setProperty( FILE_LOCATION, fileLocation );
  }

  public void setHeaderPresent( Boolean headerPresent ) {
    setProperty( HEADER_PRESENT, headerPresent );
  }

  public Boolean getHeaderPresent() {
    return (Boolean) getProperty( HEADER_PRESENT );
  }

  public String getEnclosure() {
    return (String) getProperty( ENCLOSURE );
  }

  public void setEnclosure( String enclosure ) {
    setProperty( ENCLOSURE, enclosure );
  }

  public String getDelimiter() {
    return (String) getProperty( DELIMITER );
  }

  public void setDelimiter( String delimiter ) {
    setProperty( DELIMITER, delimiter );
  }

  public List<InlineEtlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }

}
