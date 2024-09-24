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
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.InlineEtlPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.query.model.util.CsvDataReader;
import org.pentaho.metadata.query.model.util.CsvDataTypeEvaluator;
import org.pentaho.pms.util.Settings;

/**
 * This class generates an inline ETL domain.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class InlineEtlModelGenerator {

  public static final int ROW_LIMIT = 5;
  private String modelName;
  private String fileLocation;
  private String fileName;
  private boolean headerPresent;
  private String delimiter;
  private String enclosure;
  boolean securityEnabled;
  int defaultAcls;
  List<String> users;
  List<String> roles;
  String createdBy;

  public InlineEtlModelGenerator() {
    if ( !Props.isInitialized() ) {
      Props.init( Props.TYPE_PROPERTIES_EMPTY );
    }
  }

  public InlineEtlModelGenerator( String modelName, String fileLocation, String fileName, boolean headerPresent,
      String delimiter, String enclosure, boolean securityEnabled, List<String> users, List<String> roles,
      int defaultAcls, String createdBy ) {
    this();
    this.modelName = modelName;
    this.fileLocation = fileLocation;
    this.fileName = fileName;
    this.headerPresent = headerPresent;
    this.delimiter = delimiter;
    this.enclosure = enclosure;
    this.securityEnabled = securityEnabled;
    this.users = users;
    this.roles = roles;
    this.createdBy = createdBy;
    this.defaultAcls = defaultAcls;
  }

  public Domain generate() throws Exception {
    return generate( modelName, fileLocation, fileName, headerPresent, delimiter, enclosure, securityEnabled, users,
        roles, defaultAcls, createdBy );
  }

  public Domain generate( String modelName, String fileLocation, String fileName, boolean headerPresent,
      String delimiter, String enclosure, boolean securityEnabled, List<String> users, List<String> roles,
      int defaultAcls, String createdBy ) throws Exception {

    // Construct a CSV Reader to read the sample data. This data will be used to sample data
    // types of individual columns, and load the header rows
    CsvDataReader csvDataReader =
        new CsvDataReader( fileLocation + fileName, headerPresent, delimiter, enclosure, ROW_LIMIT );
    CsvDataTypeEvaluator dataTypeConverter = new CsvDataTypeEvaluator();

    csvDataReader.loadData();

    String[] fieldNames = new String[csvDataReader.getColumnCount()];

    // Generate field names F1 ... F10
    // even if header is true, this is necessary in case data fields are left empty
    DecimalFormat df = new DecimalFormat( "000" ); //$NON-NLS-1$
    for ( int i = 0; i < fieldNames.length; i++ ) {
      fieldNames[i] = "Field_" + df.format( i ); //$NON-NLS-1$
    }

    if ( headerPresent ) {
      for ( int i = 0; i < csvDataReader.getHeader().size(); i++ ) {
        fieldNames[i] = csvDataReader.getHeader().get( i );
      }
    }

    LocaleType locale = new LocaleType( LocaleHelper.getLocale().toString(), LocaleHelper.getLocale().getDisplayName() );

    InlineEtlPhysicalModel model = new InlineEtlPhysicalModel();
    String modelID = Settings.getBusinessModelIDPrefix() + modelName;
    model.setId( modelID );
    model.setName( new LocalizedString( locale.getCode(), modelName ) );

    model.setFileLocation( fileName );
    model.setHeaderPresent( headerPresent );
    model.setEnclosure( enclosure );
    model.setDelimiter( delimiter );

    InlineEtlPhysicalTable table = new InlineEtlPhysicalTable( model );
    table.setId( "INLINE_ETL_1" ); //$NON-NLS-1$
    model.getPhysicalTables().add( table );

    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setPhysicalModel( model );
    logicalModel.setId( "MODEL_1" ); //$NON-NLS-1$
    logicalModel.setName( new LocalizedString( locale.getCode(), modelName ) );

    Category mainCategory = new Category( logicalModel );
    String categoryID = Settings.getBusinessCategoryIDPrefix() + modelName;
    mainCategory.setId( categoryID );
    mainCategory.setName( new LocalizedString( locale.getCode(), modelName ) );

    LogicalTable logicalTable = new LogicalTable( logicalModel, table );
    logicalTable.setId( "LOGICAL_TABLE_1" ); //$NON-NLS-1$

    for ( int i = 0; i < fieldNames.length; i++ ) {
      fieldNames[i] = Const.trim( fieldNames[i] );
      InlineEtlPhysicalColumn column = new InlineEtlPhysicalColumn();
      column.setTable( table );
      column.setId( "PC_" + i ); //$NON-NLS-1$
      column.setFieldName( fieldNames[i] );
      column.setName( new LocalizedString( locale.getCode(), fieldNames[i] ) );
      column.setDataType( dataTypeConverter.evaluateDataType( csvDataReader.getColumnData( i ) ) );
      table.getPhysicalColumns().add( column );

      // create logical column

      LogicalColumn logicalColumn = new LogicalColumn();
      String columnID = Settings.getBusinessColumnIDPrefix();
      logicalColumn
          .setId( columnID + i + "_" + fieldNames[i].replaceAll( "\\s", "_" ).replaceAll( "[^A-Za-z0-9_]", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

      // the default name of the logical column
      // inherits from the physical column.

      logicalColumn.setPhysicalColumn( column );
      logicalColumn.setLogicalTable( logicalTable );

      logicalTable.addLogicalColumn( logicalColumn );
      mainCategory.addLogicalColumn( logicalColumn );
    }

    logicalModel.getLogicalTables().add( logicalTable );
    logicalModel.getCategories().add( mainCategory );

    Domain domain = new Domain();
    domain.addPhysicalModel( model );

    if ( getCreatedBy() != null ) {
      domain.setProperty( "created_by", createdBy ); //$NON-NLS-1$
    }

    if ( isSecurityEnabled() ) {
      Security security = new Security();
      for ( String user : users ) {
        SecurityOwner owner = new SecurityOwner( OwnerType.USER, user );
        security.putOwnerRights( owner, defaultAcls );
      }
      for ( String role : roles ) {
        SecurityOwner owner = new SecurityOwner( OwnerType.ROLE, role );
        security.putOwnerRights( owner, defaultAcls );
      }
      logicalModel.setProperty( Concept.SECURITY_PROPERTY, security );
    }

    List<LocaleType> locales = new ArrayList<LocaleType>();
    locales.add( locale );
    domain.setLocales( locales );
    domain.addLogicalModel( logicalModel );
    domain.setId( modelName );
    return domain;
  }

  public void setSecurityEnabled( boolean securityEnabled ) {
    this.securityEnabled = securityEnabled;
  }

  public boolean isSecurityEnabled() {
    return securityEnabled;
  }

  public void setUsers( List<String> users ) {
    this.users = users;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setRoles( List<String> roles ) {
    this.roles = roles;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setCreatedBy( String createdBy ) {
    this.createdBy = createdBy;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setDefaultAcls( int defaultAcls ) {
    this.defaultAcls = defaultAcls;
  }

  public int getDefaultAcls() {
    return defaultAcls;
  }
}
