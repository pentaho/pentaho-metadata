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
package org.pentaho.pms.schema.concept.types;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.util.PropertyTypeRegistry
 */
public class ConceptPropertyType {
  public static final int PROPERTY_TYPE_STRING = 0;
  public static final int PROPERTY_TYPE_DATE = 1;
  public static final int PROPERTY_TYPE_NUMBER = 2;
  public static final int PROPERTY_TYPE_COLOR = 3;
  public static final int PROPERTY_TYPE_FONT = 4;
  public static final int PROPERTY_TYPE_FIELDTYPE = 5;
  public static final int PROPERTY_TYPE_AGGREGATION = 6;
  public static final int PROPERTY_TYPE_BOOLEAN = 7;
  public static final int PROPERTY_TYPE_DATATYPE = 8;
  public static final int PROPERTY_TYPE_LOCALIZED_STRING = 9;
  public static final int PROPERTY_TYPE_TABLETYPE = 10;
  public static final int PROPERTY_TYPE_URL = 11;
  public static final int PROPERTY_TYPE_SECURITY = 12;
  public static final int PROPERTY_TYPE_ALIGNMENT = 13;
  public static final int PROPERTY_TYPE_COLUMN_WIDTH = 14;
  public static final int PROPERTY_TYPE_ROW_LEVEL_SECURITY = 15;
  public static final int PROPERTY_TYPE_AGGREGATION_LIST = 16;

  /**
   * These are the core property types to bootstrap the property system.
   */
  public static final ConceptPropertyType[] propertyTypes =
    {
    new ConceptPropertyType( PROPERTY_TYPE_STRING,
        "String", Messages.getString( "ConceptPropertyType.USER_STRING_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_DATE, "Date", Messages.getString( "ConceptPropertyType.USER_DATE_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_NUMBER,
        "Number", Messages.getString( "ConceptPropertyType.USER_NUMBER_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_COLOR,
        "Color", Messages.getString( "ConceptPropertyType.USER_COLOR_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_FONT, "Font", Messages.getString( "ConceptPropertyType.USER_FONT_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_FIELDTYPE,
        "FieldType", Messages.getString( "ConceptPropertyType.USER_FIELDTYPE_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_AGGREGATION,
        "Aggregation", Messages.getString( "ConceptPropertyType.USER_AGGREGATION_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_BOOLEAN,
        "Boolean", Messages.getString( "ConceptPropertyType.USER_BOOLEAN_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_DATATYPE,
        "DataType", Messages.getString( "ConceptPropertyType.USER_DATATYPE_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_LOCALIZED_STRING,
        "LocString", Messages.getString( "ConceptPropertyType.USER_LOCALIZED_STRING_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_TABLETYPE,
        "TableType", Messages.getString( "ConceptPropertyType.USER_TABLE_TYPE_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_URL, "URL", Messages.getString( "ConceptPropertyType.USER_URL_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_SECURITY,
        "Security", Messages.getString( "ConceptPropertyType.USER_SECURITY_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_ALIGNMENT,
        "Alignment", Messages.getString( "ConceptPropertyType.USER_ALIGNMENT_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_COLUMN_WIDTH,
        "ColumnWidth", Messages.getString( "ConceptPropertyType.USER_COLUMN_WIDTH_DESC" ) ), //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_ROW_LEVEL_SECURITY,
        "RowLevelSecurity", Messages.getString( "ConceptPropertyType.USER_ROW_LEVEL_SECURITY_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    new ConceptPropertyType( PROPERTY_TYPE_AGGREGATION_LIST,
        "AggregationList", Messages.getString( "ConceptPropertyType.USER_AGGREGATION_LIST_DESC" ) ),
      //$NON-NLS-1$ //$NON-NLS-2$
    };

  public static final ConceptPropertyType STRING = propertyTypes[ PROPERTY_TYPE_STRING ];
  public static final ConceptPropertyType DATE = propertyTypes[ PROPERTY_TYPE_DATE ];
  public static final ConceptPropertyType NUMBER = propertyTypes[ PROPERTY_TYPE_NUMBER ];
  public static final ConceptPropertyType COLOR = propertyTypes[ PROPERTY_TYPE_COLOR ];
  public static final ConceptPropertyType FONT = propertyTypes[ PROPERTY_TYPE_FONT ];
  public static final ConceptPropertyType FIELDTYPE = propertyTypes[ PROPERTY_TYPE_FIELDTYPE ];
  public static final ConceptPropertyType AGGREGATION = propertyTypes[ PROPERTY_TYPE_AGGREGATION ];
  public static final ConceptPropertyType BOOLEAN = propertyTypes[ PROPERTY_TYPE_BOOLEAN ];
  public static final ConceptPropertyType DATATYPE = propertyTypes[ PROPERTY_TYPE_DATATYPE ];
  public static final ConceptPropertyType LOCALIZED_STRING = propertyTypes[ PROPERTY_TYPE_LOCALIZED_STRING ];
  public static final ConceptPropertyType TABLETYPE = propertyTypes[ PROPERTY_TYPE_TABLETYPE ];
  public static final ConceptPropertyType URL = propertyTypes[ PROPERTY_TYPE_URL ];
  public static final ConceptPropertyType SECURITY = propertyTypes[ PROPERTY_TYPE_SECURITY ];
  public static final ConceptPropertyType ALIGNMENT = propertyTypes[ PROPERTY_TYPE_ALIGNMENT ];
  public static final ConceptPropertyType COLUMN_WIDTH = propertyTypes[ PROPERTY_TYPE_COLUMN_WIDTH ];
  public static final ConceptPropertyType ROW_LEVEL_SECURITY = propertyTypes[ PROPERTY_TYPE_ROW_LEVEL_SECURITY ];
  public static final ConceptPropertyType AGGREGATION_LIST = propertyTypes[ PROPERTY_TYPE_AGGREGATION_LIST ];

  public static final String ISO_DATE_FORMAT = "yyyy/MM/dd'T'HH:mm:ss"; //$NON-NLS-1$

  /**
   * @return an array of the core concept property type descriptions
   */
  public static String[] getTypeDescriptions() {
    String[] types = new String[ propertyTypes.length ];
    for ( int i = 0; i < types.length; i++ ) {
      types[ i ] = propertyTypes[ i ].getDescription();
    }
    return types;
  }

  /**
   * @return an array of the core concept property type codes
   */
  public static String[] getTypeCodes() {
    String[] types = new String[ propertyTypes.length ];
    for ( int i = 0; i < types.length; i++ ) {
      types[ i ] = propertyTypes[ i ].getCode();
    }
    return types;
  }

  private int type;
  private String code;
  private String description;

  /**
   * @param type
   * @param code
   * @param description
   */
  public ConceptPropertyType( int type, String code, String description ) {
    this.type = type;
    this.code = code;
    this.description = description;
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( type ).append( code ).append(
      description ).toString();
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptPropertyType == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    ConceptPropertyType rhs = (ConceptPropertyType) obj;
    return new EqualsBuilder().append( type, rhs.type ).append( code, rhs.code ).append( description, rhs.description )
      .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 97, 269 ).append( type ).append( code ).append( description ).toHashCode();
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode( String code ) {
    this.code = code;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType( int type ) {
    this.type = type;
  }

  /**
   * @param typeDesc The description or code of the type
   * @return The concept property type or null if nothing was found to match.
   */
  public static ConceptPropertyType getType( String typeDesc ) {
    for ( int i = 0; i < propertyTypes.length; i++ ) {
      if ( propertyTypes[ i ].getDescription().equals( typeDesc ) ) {
        return propertyTypes[ i ];
      }
      if ( propertyTypes[ i ].getCode().equals( typeDesc ) ) {
        return propertyTypes[ i ];
      }
    }
    return null;
  }
}
