/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.schema.olap;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.schema.BusinessTable;

@SuppressWarnings( "deprecation" )
public class OlapDimension extends ChangedFlag implements Cloneable {
  private String name;
  private boolean timeDimension;

  private List<OlapHierarchy> hierarchies;

  public OlapDimension() {
    hierarchies = new ArrayList<OlapHierarchy>();
  }

  public Object clone() {
    OlapDimension olapDimension = new OlapDimension();

    olapDimension.name = name;
    olapDimension.timeDimension = timeDimension;
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
    return timeDimension;
  }

  /**
   * @param timeDimension
   *          the timeDimension to set
   */
  public void setTimeDimension( boolean timeDimension ) {
    this.timeDimension = timeDimension;
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
  public BusinessTable findBusinessTable() {
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      OlapHierarchy hierarchy = (OlapHierarchy) hierarchies.get( i );
      if ( hierarchy.getBusinessTable() != null ) {
        return hierarchy.getBusinessTable();
      }
    }
    return null;
  }

  public boolean hasChanged() {
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      if ( ( (OlapHierarchy) hierarchies.get( i ) ).hasChanged() ) {
        return true;
      }
    }
    return super.hasChanged();
  }

  public void clearChanged() {
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      ( (OlapHierarchy) hierarchies.get( i ) ).clearChanged();
    }
    setChanged( false );
  }
}
