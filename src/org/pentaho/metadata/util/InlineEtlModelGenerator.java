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
package org.pentaho.metadata.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.textfileinput.TextFileInput;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
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
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

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
  private boolean headerPresent;
  private String delimiter;
  private String enclosure;
  boolean securityEnabled;
  int defaultAcls;
  List<String> users;
  List<String> roles;
  String createdBy;
  
  public InlineEtlModelGenerator() {
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }
  
  public InlineEtlModelGenerator(String modelName, String fileLocation, boolean headerPresent, String delimiter,String enclosure,
       boolean securityEnabled,  List<String> users, List<String> roles, int defaultAcls,String createdBy) {
    this();
    this.modelName = modelName;
    this.fileLocation = fileLocation;
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
    return generate(modelName, fileLocation, headerPresent, delimiter, enclosure, securityEnabled, users, roles, defaultAcls, createdBy);
  }
  
  public Domain generate(String modelName, String fileLocation, boolean headerPresent,String delimiter, String enclosure,
      boolean securityEnabled,  List<String> users, List<String> roles, int defaultAcls,String createdBy) throws Exception {
    // use code within Kettle to gen CSV model
    InputStream inputStream = KettleVFS.getInputStream(fileLocation);
    InputStreamReader reader = new InputStreamReader(inputStream);
    
    // Read a line of data to determine the number of rows...
    String line = TextFileInput.getLine(null, reader, TextFileInputMeta.FILE_FORMAT_MIXED, new StringBuilder(1000));
    
    // Split the string, header or data into parts...
    CSVTokenizer tokenizer = new CSVTokenizer(line, delimiter, enclosure);
    
    String[] fieldNames = new String[tokenizer.countTokens()];
    if (!headerPresent) {
      // Don't use field names from the header...
      // Generate field names F1 ... F10
      DecimalFormat df = new DecimalFormat("000"); // $NON-NLS-1$
      for (int i=0;i<fieldNames.length;i++) {
        fieldNames[i] = "Field_"+df.format(i); // $NON-NLS-1$
      }
    } else {
      for (int i=0;i<fieldNames.length;i++) {
        fieldNames[i] = tokenizer.nextToken();
      }
    }

    LocaleType locale = new LocaleType(LocaleHelper.getLocale().toString(), LocaleHelper.getLocale().getDisplayName());
    
    InlineEtlPhysicalModel model = new InlineEtlPhysicalModel();
    String modelID = Settings.getBusinessModelIDPrefix()+ modelName;
    model.setId(modelID);
    model.setName(new LocalizedString(locale.getCode(), modelName));

    model.setFileLocation(fileLocation);
    model.setHeaderPresent(headerPresent);
    model.setEnclosure(enclosure);
    model.setDelimiter(delimiter);
    
    InlineEtlPhysicalTable table = new InlineEtlPhysicalTable(model);
    table.setId("INLINE_ETL_1");
    model.getPhysicalTables().add(table);
    
    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setId("MODEL_1");
    logicalModel.setName(new LocalizedString(locale.getCode(), modelName));

    Category mainCategory = new Category(logicalModel);
    String categoryID= Settings.getBusinessCategoryIDPrefix()+ modelName;
    mainCategory.setId(categoryID);
    mainCategory.setName(new LocalizedString(locale.getCode(), modelName));

    LogicalTable logicalTable = new LogicalTable(logicalModel, table);
    logicalTable.setId("LOGICAL_TABLE_1");

    
    for (int i = 0; i < fieldNames.length; i++) {
      fieldNames[i] = Const.trim(fieldNames[i]);
      InlineEtlPhysicalColumn column = new InlineEtlPhysicalColumn();
      column.setTable(table);
      column.setId("PC_" + i);
      column.setFieldName(fieldNames[i]);
      column.setName(new LocalizedString(locale.getCode(), fieldNames[i]));
      // Construct a CSV Reader to read the sample data. This data will be used to sample data
      // types of individual columns
      CsvDataReader csvDataReader = new CsvDataReader(fileLocation, headerPresent, enclosure, delimiter, ROW_LIMIT); 
      CsvDataTypeEvaluator dataTypeConverter = new CsvDataTypeEvaluator();
      // If headers are present we will get the sampling data using the column name otherwise we will use the column number
      if(headerPresent) {
        column.setDataType(dataTypeConverter.evaluateDataType(csvDataReader.getColumnData(i)));
      } else {
        column.setDataType(dataTypeConverter.evaluateDataType(csvDataReader.getColumnData(fieldNames[i])));
      }
      table.getPhysicalColumns().add(column);
      
      // create logical column
      
      LogicalColumn logicalColumn = new LogicalColumn();
      String columnID = Settings.getBusinessColumnIDPrefix();
      logicalColumn.setId(columnID + i + "_" + fieldNames[i].replaceAll("\\s","_").replaceAll("[^A-Za-z0-9_]",""));
      
      // the default name of the logical column 
      // inherits from the physical column.
      
      logicalColumn.setPhysicalColumn(column);
      logicalColumn.setLogicalTable(logicalTable);
      
      logicalTable.addLogicalColumn(logicalColumn);
      mainCategory.addLogicalColumn(logicalColumn);
    }

    logicalModel.getLogicalTables().add(logicalTable);
    logicalModel.getCategories().add(mainCategory);
    
    Domain domain = new Domain();
    domain.addPhysicalModel(model);
    
    if (getCreatedBy() != null) {
      domain.setProperty("created_by", createdBy);
    }

    if (isSecurityEnabled()) {
      Security security = new Security();
      for (String user : users) {
        SecurityOwner owner = new SecurityOwner(OwnerType.USER, user);
        security.putOwnerRights(owner, defaultAcls);  
      }
      for (String role : roles) {
        SecurityOwner owner = new SecurityOwner(OwnerType.ROLE, role);
        security.putOwnerRights(owner, defaultAcls);  
      }          
      logicalModel.setProperty(Concept.SECURITY_PROPERTY, security);
    }
    
    
    List<LocaleType> locales = new ArrayList<LocaleType>();
    locales.add(locale);
    domain.setLocales(locales);
    domain.addLogicalModel(logicalModel);
    domain.setId(modelName);
    return domain;
  }
  
  public void setSecurityEnabled(boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  public boolean isSecurityEnabled() {
    return securityEnabled;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedBy() {
    return createdBy;
  }
  public void setDefaultAcls(int defaultAcls) {
    this.defaultAcls = defaultAcls;
  }
  
  public int getDefaultAcls() {
    return defaultAcls;
  }
}
