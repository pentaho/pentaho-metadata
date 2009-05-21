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
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.InlineEtlPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.util.Settings;

/**
 * This class generates an inline ETL domain.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlModelGenerator {

  private String modelName;
  private String fileLocation;
  private boolean headerPresent;
  private String delimiter;
  private String enclosure;

  public InlineEtlModelGenerator() {
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }
  
  public InlineEtlModelGenerator(String modelName, String fileLocation, boolean headerPresent, String enclosure, String delimiter) {
    this();
    this.modelName = modelName;
    this.fileLocation = fileLocation;
    this.headerPresent = headerPresent;
    this.delimiter = delimiter;
    this.enclosure = enclosure;
  }
  
  public Domain generate() throws Exception {
    return generate(modelName, fileLocation, headerPresent, enclosure, delimiter);
  }
  
  public Domain generate(String modelName, String fileLocation, boolean headerPresent, String enclosure, String delimiter) throws Exception {
    // use code within Kettle to gen CSV model
    InputStream inputStream = KettleVFS.getInputStream(fileLocation);
    InputStreamReader reader = new InputStreamReader(inputStream);
    
    // Read a line of data to determine the number of rows...
    String line = TextFileInput.getLine(null, reader, TextFileInputMeta.FILE_FORMAT_MIXED, new StringBuilder(1000));
    
    // Split the string, header or data into parts...
    String[] fieldNames = Const.splitString(line, delimiter); 
    
    if (!headerPresent) {
      // Don't use field names from the header...
      // Generate field names F1 ... F10
      DecimalFormat df = new DecimalFormat("000"); // $NON-NLS-1$
      for (int i=0;i<fieldNames.length;i++) {
        fieldNames[i] = "Field_"+df.format(i); // $NON-NLS-1$
      }
    } else {
      if (!Const.isEmpty(enclosure)) {
          for (int i=0;i<fieldNames.length;i++) {
            if (fieldNames[i].startsWith(enclosure) && fieldNames[i].endsWith(enclosure) && fieldNames[i].length()>1) fieldNames[i] = fieldNames[i].substring(1, fieldNames[i].length()-1);
          }
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
    
    Category mainCategory = new Category();
    String categoryID= Settings.getBusinessCategoryIDPrefix()+ modelName;
    mainCategory.setId(categoryID);
    mainCategory.setName(new LocalizedString(locale.getCode(), modelName));

    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setId("MODEL_1");
    logicalModel.setName(new LocalizedString(locale.getCode(), modelName));

    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable(table);
    logicalTable.setId("LOGICAL_TABLE_1");

    
    for (int i=0; i < fieldNames.length; i++) {
      fieldNames[i] = Const.trim(fieldNames[i]);
      InlineEtlPhysicalColumn column = new InlineEtlPhysicalColumn();
      column.setTable(table);
      column.setId("PC_" + i);
      column.setName(new LocalizedString(locale.getCode(), fieldNames[i]));
      column.setColumnNumber(i);
      column.setDataType(DataType.STRING);
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
    
    List<LocaleType> locales = new ArrayList<LocaleType>();
    locales.add(locale);
    domain.setLocales(locales);
    domain.addLogicalModel(logicalModel);
    domain.setId(modelName);
    return domain;
  }
}
