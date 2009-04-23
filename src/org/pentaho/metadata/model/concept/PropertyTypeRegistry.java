package org.pentaho.metadata.model.concept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
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
  Map<Class, String> propertyTypeDescription = new HashMap<Class, String>();
  
  public PropertyTypeRegistry() {
    // load these from a spring config file?
    addPropertyType(String.class, "PropertyDescription." + String.class);
    addPropertyType(LocalizedString.class, "PropertyDescription." + LocalizedString.class);
    addPropertyType(DataType.class, "PropertyDescription." + DataType.class);
  }
  
  public void addPropertyType(Class clazz, String descriptionId) {
    propertyTypes.add(clazz);
    propertyTypeMap.put(clazz.getCanonicalName(), clazz);
    propertyTypeDescription.put(clazz, descriptionId);
  }
  
  public List<Class> getPropertyTypes() {
    return unmodifiablePropertyTypes;
  }
  
  public String getPropertyTypeDescription(Class clazz) {
    return Messages.getString(propertyTypeDescription.get(clazz));
  }
  
  public Class getPropertyType(String type) {
    return propertyTypeMap.get(type);
  }
}
