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

package org.pentaho.pms.schema.concept.types.tabletype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please see TableType
 */
public class TableTypeSettings {
  public static final int TYPE_OTHER = 0;
  public static final int TYPE_DIMENSION = 1;
  public static final int TYPE_FACT = 2;

  private static final String[] typeCodes = new String[] { "Other", "Dimension", "Fact" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  private static final String[] typeDescriptions =
      new String[] {
        Messages.getString( "TableTypeSettings.USER_OTHER_DESC" ), Messages.getString( "TableTypeSettings.USER_DIMENSION_DESC" ), Messages.getString( "TableTypeSettings.USER_FACT_DESC" ) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final TableTypeSettings OTHER = new TableTypeSettings( TYPE_OTHER );
  public static final TableTypeSettings DIMENSION = new TableTypeSettings( TYPE_DIMENSION );
  public static final TableTypeSettings FACT = new TableTypeSettings( TYPE_FACT );

  public static final TableTypeSettings[] types = new TableTypeSettings[] { OTHER, DIMENSION, FACT };

  private int type;

  /**
   * @param type
   */
  public TableTypeSettings( int type ) {
    super();
    this.type = type;
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType( int type ) {
    this.type = type;
  }

  public boolean isFact() {
    return type == TYPE_FACT;
  }

  public boolean isDimension() {
    return type == TYPE_DIMENSION;
  }

  public String getDescription() {
    return typeDescriptions[type];
  }

  public String getCode() {
    return typeCodes[type];
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof TableTypeSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    TableTypeSettings rhs = (TableTypeSettings) obj;
    return new EqualsBuilder().append( type, rhs.type ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 233, 281 ).append( type ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( type ).toString();
  }

  public static TableTypeSettings getType( String description ) {
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
    return OTHER;
  }

  public static String[] getTypeDescriptions() {
    return typeDescriptions.clone();
  }
}
