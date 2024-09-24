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
package org.pentaho.pms.schema.concept.types.aggregation;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.pentaho.pms.messages.Messages;

/**
 * @deprecated as of metadata 3.0. please use org.pentaho.metadata.model.concept.types.AggregationType
 */
public class AggregationSettings {
  public static final int TYPE_AGGREGATION_NONE = 0;
  public static final int TYPE_AGGREGATION_SUM = 1;
  public static final int TYPE_AGGREGATION_AVERAGE = 2;
  public static final int TYPE_AGGREGATION_COUNT = 3;
  public static final int TYPE_AGGREGATION_COUNT_DISTINCT = 4;
  public static final int TYPE_AGGREGATION_MINIMUM = 5;
  public static final int TYPE_AGGREGATION_MAXIMUM = 6;

  public static final AggregationSettings NONE = new AggregationSettings( TYPE_AGGREGATION_NONE );
  public static final AggregationSettings SUM = new AggregationSettings( TYPE_AGGREGATION_SUM );
  public static final AggregationSettings AVERAGE = new AggregationSettings( TYPE_AGGREGATION_AVERAGE );
  public static final AggregationSettings COUNT = new AggregationSettings( TYPE_AGGREGATION_COUNT );
  public static final AggregationSettings COUNT_DISTINCT = new AggregationSettings( TYPE_AGGREGATION_COUNT_DISTINCT );
  public static final AggregationSettings MINIMUM = new AggregationSettings( TYPE_AGGREGATION_MINIMUM );
  public static final AggregationSettings MAXIMUM = new AggregationSettings( TYPE_AGGREGATION_MAXIMUM );

  private static final String[] typeCodes = {
    "none", "sum", "average", "count", "count_distinct", "minimum", "maximum", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
  };

  public static final String[] typeDescriptions = { Messages.getString( "AggregationSettings.USER_NONE_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_SUM_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_AVERAGE_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_COUNT_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_DISTINCT_COUNT_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_MINIMUM_DESC" ), //$NON-NLS-1$
    Messages.getString( "AggregationSettings.USER_MAXIMUM_DESC" ), //$NON-NLS-1$
  };

  public static final AggregationSettings[] types = new AggregationSettings[] { NONE, SUM, AVERAGE, COUNT,
    COUNT_DISTINCT, MINIMUM, MAXIMUM, };

  private int type;

  /**
   * @param name
   * @param type
   */
  public AggregationSettings( int type ) {
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

  public boolean equals( Object obj ) {
    if ( obj instanceof AggregationSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    AggregationSettings rhs = (AggregationSettings) obj;
    return new EqualsBuilder().append( type, rhs.type ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 37, 109 ).append( type ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( type ).toString();
  }

  public static AggregationSettings getType( String description ) {
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
    return NONE;
  }

  public String getCode() {
    return typeCodes[type];
  }

  public String getDescription() {
    return typeDescriptions[type];
  }

  public static String[] getTypeDescriptions() {
    return typeDescriptions.clone();
  }

}
