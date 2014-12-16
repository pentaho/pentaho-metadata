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

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.Property;

/**
 * The Inline ETL Physical model is designed to handle CSV files and uses inline ETL (Kettle Transformations) to execute
 * query models.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class InlineEtlPhysicalModel extends Concept implements IPhysicalModel  {

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
    return ( Domain )getParent();
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
    Property property = getProperty( FILE_LOCATION );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setFileLocation( String fileLocation ) {
    setProperty( FILE_LOCATION, new Property<String>( fileLocation) );
  }

  public void setHeaderPresent( Boolean headerPresent ) {
    setProperty( HEADER_PRESENT, new Property<Boolean>( headerPresent ) );
  }

  public Boolean getHeaderPresent() {
    Property property = getProperty( HEADER_PRESENT );
    if( property != null ) {
      return (Boolean) property.getValue();
    }
    return null;
  }

  public String getEnclosure() {
    Property property = getProperty( ENCLOSURE );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setEnclosure( String enclosure ) {
    setProperty( ENCLOSURE, new Property<String>( enclosure ) );
  }

  public String getDelimiter() {
    Property property = getProperty( DELIMITER );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setDelimiter( String delimiter ) {
    setProperty( DELIMITER, new Property<String>( delimiter ) );
  }

  public List<InlineEtlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }

}
