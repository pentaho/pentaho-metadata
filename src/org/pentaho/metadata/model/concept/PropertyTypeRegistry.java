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
package org.pentaho.metadata.model.concept;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.ColumnWidth;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.messages.Messages;
/**
 * Concept Property Types are now POJOs, but there still needs to be a mechanism for
 * pretty descriptions and listing the types available.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class PropertyTypeRegistry {
  
  /*
            new ConceptPropertyType( PROPERTY_TYPE_STRING,           "String",      Messages.getString("ConceptPropertyType.USER_STRING_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_DATE,             "Date",        Messages.getString("ConceptPropertyType.USER_DATE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_NUMBER,           "Number",      Messages.getString("ConceptPropertyType.USER_NUMBER_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_COLOR,            "Color",       Messages.getString("ConceptPropertyType.USER_COLOR_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_FONT,             "Font",        Messages.getString("ConceptPropertyType.USER_FONT_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_FIELDTYPE,        "FieldType",   Messages.getString("ConceptPropertyType.USER_FIELDTYPE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_AGGREGATION,      "Aggregation", Messages.getString("ConceptPropertyType.USER_AGGREGATION_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_BOOLEAN,          "Boolean",     Messages.getString("ConceptPropertyType.USER_BOOLEAN_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_DATATYPE,         "DataType",    Messages.getString("ConceptPropertyType.USER_DATATYPE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
       X     new ConceptPropertyType( PROPERTY_TYPE_LOCALIZED_STRING, "LocString",   Messages.getString("ConceptPropertyType.USER_LOCALIZED_STRING_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_TABLETYPE,        "TableType",   Messages.getString("ConceptPropertyType.USER_TABLE_TYPE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_URL,              "URL",         Messages.getString("ConceptPropertyType.USER_URL_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_SECURITY,         "Security",    Messages.getString("ConceptPropertyType.USER_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_ALIGNMENT,        "Alignment",   Messages.getString("ConceptPropertyType.USER_ALIGNMENT_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_COLUMN_WIDTH,     "ColumnWidth", Messages.getString("ConceptPropertyType.USER_COLUMN_WIDTH_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_ROW_LEVEL_SECURITY,     "RowLevelSecurity", Messages.getString("ConceptPropertyType.USER_ROW_LEVEL_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
            new ConceptPropertyType( PROPERTY_TYPE_AGGREGATION_LIST,     "AggregationList", Messages.getString("ConceptPropertyType.USER_ROW_LEVEL_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
   */
  
  List<Class> propertyTypes = new ArrayList<Class>();
  Map<String, Class> propertyTypeMap = new HashMap<String, Class>();
  List<Class> unmodifiablePropertyTypes = Collections.unmodifiableList(propertyTypes);
  
  public PropertyTypeRegistry() {
    // load these from a spring config file?
    addPropertyType(String.class);
    addPropertyType(LocalizedString.class);
    addPropertyType(DataType.class);
    addPropertyType(TargetTableType.class);
    addPropertyType(TargetColumnType.class);
    addPropertyType(AggregationType.class);
    
    // this isfor agg lists, 
    // we will need another way to express lists
    addPropertyType(List.class);
    
    // not used directly
    // addPropertyType(LocaleType.class);
    
    addPropertyType(Alignment.class);
    addPropertyType(Color.class);
    addPropertyType(ColumnWidth.class);
    addPropertyType(FieldType.class);
    addPropertyType(Font.class);
    addPropertyType(TableType.class);
    addPropertyType(RowLevelSecurity.class);
    addPropertyType(Security.class);
    addPropertyType(Double.class);
    addPropertyType(Boolean.class);
    addPropertyType(Date.class);

    // note that URL is not GWT compatible, we'll need to figure out what to do with this
    // once we move towards a fully supported thin client metadata editor
    addPropertyType(URL.class);
    
  }
  
  public void addPropertyType(Class clazz) {
    propertyTypes.add(clazz);
    propertyTypeMap.put(clazz.getCanonicalName(), clazz);
  }
  
  public List<Class> getPropertyTypes() {
    return unmodifiablePropertyTypes;
  }
  
  public String getPropertyTypeDescription(Class clazz) {
    return Messages.getString("PropertyDescription." + clazz.getCanonicalName());
  }
  
  public Class getPropertyType(String type) {
    return propertyTypeMap.get(type);
  }
}
