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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.concept.types.Color;
import org.pentaho.metadata.model.concept.types.ColumnWidth;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;

/**
 * Concept Property Types are now POJOs, but there still needs to be a mechanism for pretty descriptions and listing the
 * types available.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class PropertyTypeRegistry {

  /*
   * new ConceptPropertyType( PROPERTY_TYPE_STRING, "String",
   * Messages.getString("ConceptPropertyType.USER_STRING_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType(
   * PROPERTY_TYPE_DATE, "Date", Messages.getString("ConceptPropertyType.USER_DATE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
   * new ConceptPropertyType( PROPERTY_TYPE_NUMBER, "Number",
   * Messages.getString("ConceptPropertyType.USER_NUMBER_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType(
   * PROPERTY_TYPE_COLOR, "Color", Messages.getString("ConceptPropertyType.USER_COLOR_DESC")), //$NON-NLS-1$
   * //$NON-NLS-2$ new ConceptPropertyType( PROPERTY_TYPE_FONT, "Font",
   * Messages.getString("ConceptPropertyType.USER_FONT_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType(
   * PROPERTY_TYPE_FIELDTYPE, "FieldType", Messages.getString("ConceptPropertyType.USER_FIELDTYPE_DESC")), //$NON-NLS-1$
   * //$NON-NLS-2$ new ConceptPropertyType( PROPERTY_TYPE_AGGREGATION, "Aggregation",
   * Messages.getString("ConceptPropertyType.USER_AGGREGATION_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new
   * ConceptPropertyType( PROPERTY_TYPE_BOOLEAN, "Boolean",
   * Messages.getString("ConceptPropertyType.USER_BOOLEAN_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType(
   * PROPERTY_TYPE_DATATYPE, "DataType", Messages.getString("ConceptPropertyType.USER_DATATYPE_DESC")), //$NON-NLS-1$
   * //$NON-NLS-2$ X new ConceptPropertyType( PROPERTY_TYPE_LOCALIZED_STRING, "LocString",
   * Messages.getString("ConceptPropertyType.USER_LOCALIZED_STRING_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new
   * ConceptPropertyType( PROPERTY_TYPE_TABLETYPE, "TableType",
   * Messages.getString("ConceptPropertyType.USER_TABLE_TYPE_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new
   * ConceptPropertyType( PROPERTY_TYPE_URL, "URL", Messages.getString("ConceptPropertyType.USER_URL_DESC")),
   * //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType( PROPERTY_TYPE_SECURITY, "Security",
   * Messages.getString("ConceptPropertyType.USER_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new ConceptPropertyType(
   * PROPERTY_TYPE_ALIGNMENT, "Alignment", Messages.getString("ConceptPropertyType.USER_ALIGNMENT_DESC")), //$NON-NLS-1$
   * //$NON-NLS-2$ new ConceptPropertyType( PROPERTY_TYPE_COLUMN_WIDTH, "ColumnWidth",
   * Messages.getString("ConceptPropertyType.USER_COLUMN_WIDTH_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new
   * ConceptPropertyType( PROPERTY_TYPE_ROW_LEVEL_SECURITY, "RowLevelSecurity",
   * Messages.getString("ConceptPropertyType.USER_ROW_LEVEL_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$ new
   * ConceptPropertyType( PROPERTY_TYPE_AGGREGATION_LIST, "AggregationList",
   * Messages.getString("ConceptPropertyType.USER_ROW_LEVEL_SECURITY_DESC")), //$NON-NLS-1$ //$NON-NLS-2$
   */

  List<Class> propertyTypes = new ArrayList<Class>();
  Map<String, Class> propertyTypeMap = new HashMap<String, Class>();
  List<Class> unmodifiablePropertyTypes = Collections.unmodifiableList( propertyTypes );

  public PropertyTypeRegistry() {
    // load these from a spring config file?
    addPropertyType( String.class );
    addPropertyType( LocalizedString.class );
    addPropertyType( DataType.class );
    addPropertyType( TargetTableType.class );
    addPropertyType( TargetColumnType.class );
    addPropertyType( AggregationType.class );

    // this is for agg lists,
    // we will need another way to express lists
    addPropertyType( List.class );

    addPropertyType( Alignment.class );
    addPropertyType( Color.class );
    addPropertyType( ColumnWidth.class );
    addPropertyType( FieldType.class );
    addPropertyType( Font.class );
    addPropertyType( TableType.class );
    addPropertyType( RowLevelSecurity.class );
    addPropertyType( Security.class );
    addPropertyType( Double.class );
    addPropertyType( Boolean.class );
    addPropertyType( Date.class );

    // note that URL is not GWT compatible, we'll need to figure out what to do with this
    // once we move towards a fully supported thin client metadata editor
    addPropertyType( URL.class );

  }

  public void addPropertyType( Class clazz ) {
    propertyTypes.add( clazz );
    propertyTypeMap.put( clazz.getCanonicalName(), clazz );
  }

  public List<Class> getPropertyTypes() {
    return unmodifiablePropertyTypes;
  }

  public String getPropertyTypeDescription( Class clazz ) {
    return Messages.getString( "PropertyDescription." + clazz.getCanonicalName() ); //$NON-NLS-1$
  }

  public Class getPropertyType( String type ) {
    return propertyTypeMap.get( type );
  }
}
