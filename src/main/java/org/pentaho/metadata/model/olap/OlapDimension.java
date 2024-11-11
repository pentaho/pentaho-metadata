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

package org.pentaho.metadata.model.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalTable;

public class OlapDimension implements Cloneable, Serializable {
  public static final String TYPE_TIME_DIMENSION = "TimeDimension";
  public static final String TYPE_STANDARD_DIMENSION = "StandardDimension";

  private String name;
  private String type = TYPE_STANDARD_DIMENSION;

  private List<OlapHierarchy> hierarchies;

  public OlapDimension() {
    hierarchies = new ArrayList<OlapHierarchy>();
  }

  public Object clone() {
    OlapDimension olapDimension = new OlapDimension();

    olapDimension.name = name;
    olapDimension.type = type;
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get( i );
      olapDimension.hierarchies.add( (OlapHierarchy) hierarchy.clone() );
    }

    return olapDimension;
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapDimension) obj ).getName() );
  }

  /**
   * @return the hierarchies
   */
  public List<OlapHierarchy> getHierarchies() {
    return hierarchies;
  }

  /**
   * @param hierarchies
   *          the hierarchies to set
   */
  public void setHierarchies( List<OlapHierarchy> hierarchies ) {
    this.hierarchies = hierarchies;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @return the timeDimension
   */
  public boolean isTimeDimension() {
    return TYPE_TIME_DIMENSION.equals( type );
  }

  /**
   * @param timeDimension
   *          the timeDimension to set
   */
  public void setTimeDimension( boolean timeDimension ) {
    this.type = timeDimension ? TYPE_TIME_DIMENSION : TYPE_STANDARD_DIMENSION;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public OlapHierarchy findOlapHierarchy( String thisName ) {
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get( i );
      if ( hierarchy.getName().equalsIgnoreCase( thisName ) ) {
        return hierarchy;
      }
    }
    return null;
  }

  /**
   * @return the businessTable
   */
  public LogicalTable findLogicalTable() {
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get( i );
      if ( hierarchy.getLogicalTable() != null ) {
        return hierarchy.getLogicalTable();
      }
    }
    return null;
  }
}
