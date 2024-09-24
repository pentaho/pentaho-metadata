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
package org.pentaho.pms.schema.concept.types.datatype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.DataType
 */
public class DataTypeSettings {
  public static final int DATA_TYPE_UNKNOWN = 0;
  public static final int DATA_TYPE_STRING = 1;
  public static final int DATA_TYPE_DATE = 2;
  public static final int DATA_TYPE_BOOLEAN = 3;
  public static final int DATA_TYPE_NUMERIC = 4;
  public static final int DATA_TYPE_BINARY = 5;
  public static final int DATA_TYPE_IMAGE = 6;
  public static final int DATA_TYPE_URL = 7;

  public static final DataTypeSettings UNKNOWN = new DataTypeSettings( DATA_TYPE_UNKNOWN );
  public static final DataTypeSettings STRING = new DataTypeSettings( DATA_TYPE_STRING );
  public static final DataTypeSettings DATE = new DataTypeSettings( DATA_TYPE_DATE );
  public static final DataTypeSettings BOOLEAN = new DataTypeSettings( DATA_TYPE_BOOLEAN );
  public static final DataTypeSettings NUMERIC = new DataTypeSettings( DATA_TYPE_NUMERIC );
  public static final DataTypeSettings BINARY = new DataTypeSettings( DATA_TYPE_BINARY );
  public static final DataTypeSettings IMAGE = new DataTypeSettings( DATA_TYPE_IMAGE );
  public static final DataTypeSettings URL = new DataTypeSettings( DATA_TYPE_URL );

  private static final String[] typeCodes = {
    "Unknown", "String", "Date", "Boolean", "Numeric", "Binary", "Image", "URL", }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
  private static final String[] typeDescriptions = { Messages.getString( "DataTypeSettings.USER_UNKNOWN_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_STRING_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_DATE_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_BOOLEAN_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_NUMERIC_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_BINARY_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_IMAGE_DESC" ), //$NON-NLS-1$
    Messages.getString( "DataTypeSettings.USER_URL_DESC" ), }; //$NON-NLS-1$
  public static final DataTypeSettings[] types = new DataTypeSettings[] { UNKNOWN, STRING, DATE, BOOLEAN, NUMERIC,
    BINARY, IMAGE, URL, };

  private static final String SEPARATOR = ","; //$NON-NLS-1$

  private int type;
  private int length;
  private int precision;

  /**
   * @param type
   * @param length
   * @param precision
   */
  public DataTypeSettings( int type, int length, int precision ) {
    super();
    this.type = type;
    this.length = length;
    this.precision = precision;
  }

  /**
   * @param type
   * @param length
   * @param precision
   */
  public DataTypeSettings( int type ) {
    super();
    this.type = type;
    this.length = -1;
    this.precision = -1;
  }

  public String toString() {
    return getCode() + SEPARATOR + length + SEPARATOR + precision;
  }

  public static DataTypeSettings fromString( String value ) {
    String[] pieces = value.split( SEPARATOR );
    if ( pieces.length > 0 ) {
      DataTypeSettings settings = getType( pieces[0] );
      if ( pieces.length > 1 ) {
        settings.setLength( Integer.parseInt( pieces[1] ) );
        if ( pieces.length > 2 ) {
          settings.setPrecision( Integer.parseInt( pieces[2] ) );
        }
      }

      return settings;
    }
    return null;
  }

  public int getLength() {
    return length;
  }

  public void setLength( int length ) {
    this.length = length;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision( int precision ) {
    this.precision = precision;
  }

  public int getType() {
    return type;
  }

  public void setType( int type ) {
    this.type = type;
  }

  public String getCode() {
    return typeCodes[type];
  }

  public String getDescription() {
    return typeDescriptions[type];
  }

  public static DataTypeSettings getType( String description ) {
    for ( int i = 0; i < typeDescriptions.length; i++ ) {
      if ( typeDescriptions[i].equalsIgnoreCase( description ) ) {
        return types[i];
      }
    }
    for ( int i = 0; i < typeCodes.length; i++ ) {
      if ( typeCodes[i].equalsIgnoreCase( description ) ) {
        return types[i];
      }
    }
    return UNKNOWN;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof DataTypeSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    DataTypeSettings rhs = (DataTypeSettings) obj;
    return new EqualsBuilder().append( type, rhs.type ).append( length, rhs.length ).append( precision, rhs.precision )
        .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 23, 227 ).append( type ).append( length ).append( precision ).toHashCode();
  }

  public static String[] getTypeDescriptions() {
    return typeDescriptions.clone();
  }

}
