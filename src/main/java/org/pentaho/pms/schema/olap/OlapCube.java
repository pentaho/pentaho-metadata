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

package org.pentaho.pms.schema.olap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.schema.BusinessTable;

@SuppressWarnings( "deprecation" )
public class OlapCube extends ChangedFlag implements Cloneable {
  private String name;
  private BusinessTable businessTable;

  private List<OlapDimensionUsage> olapDimensionUsages;
  private List<OlapMeasure> olapMeasures;

  // TODO: private dimensions

  public OlapCube() {
    olapDimensionUsages = new ArrayList<OlapDimensionUsage>();
    olapMeasures = new ArrayList<OlapMeasure>();
  }

  public Object clone() {
    OlapCube olapCube = new OlapCube();

    olapCube.name = name;
    for ( int i = 0; i < olapDimensionUsages.size(); i++ ) {
      OlapDimensionUsage usage = (OlapDimensionUsage) olapDimensionUsages.get( i );
      olapCube.olapDimensionUsages.add( (OlapDimensionUsage) usage.clone() );
    }

    for ( int i = 0; i < olapMeasures.size(); i++ ) {
      OlapMeasure measure = (OlapMeasure) olapMeasures.get( i );
      olapCube.olapMeasures.add( (OlapMeasure) measure.clone() );
    }

    if ( businessTable != null ) {
      olapCube.businessTable = businessTable; // no cloning here please!
    }

    return olapCube;
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapCube) obj ).getName() );
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
   * @return the businessTable
   */
  public BusinessTable getBusinessTable() {
    return businessTable;
  }

  /**
   * @param businessTable
   *          the businessTable to set
   */
  public void setBusinessTable( BusinessTable businessTable ) {
    this.businessTable = businessTable;
  }

  /**
   * @return the olapDimensionUsages
   */
  public List<OlapDimensionUsage> getOlapDimensionUsages() {
    return olapDimensionUsages;
  }

  /**
   * @param olapDimensionUsages
   *          the olapDimensionUsages to set
   */
  public void setOlapDimensionUsages( List<OlapDimensionUsage> olapDimensionUsages ) {
    this.olapDimensionUsages = olapDimensionUsages;
  }

  /**
   * @return the olapMeasures
   */
  public List<OlapMeasure> getOlapMeasures() {
    return olapMeasures;
  }

  /**
   * @param olapMeasures
   *          the olapMeasures to set
   */
  public void setOlapMeasures( List<OlapMeasure> olapMeasures ) {
    this.olapMeasures = olapMeasures;
  }

  public OlapMeasure findOlapMeasure( String measureName ) {
    for ( int i = 0; i < olapMeasures.size(); i++ ) {
      OlapMeasure olapMeasure = (OlapMeasure) olapMeasures.get( i );
      if ( olapMeasure.getName().equals( measureName ) ) {
        return olapMeasure;
      }
    }
    return null;
  }

  public String[] getUnusedColumnNames( String locale ) {
    String[] allColumnNames = businessTable.getColumnNames( locale );
    List<String> names = new ArrayList<String>();
    names.addAll( Arrays.asList( allColumnNames ) );

    for ( int i = names.size() - 1; i >= 0; i-- ) {
      String columnName = (String) names.get( i );
      for ( int m = 0; m < olapMeasures.size(); m++ ) {
        OlapMeasure measure = (OlapMeasure) olapMeasures.get( m );
        if ( measure.getBusinessColumn().getDisplayName( locale ).equals( columnName ) ) {
          names.remove( i );
        }
      }
    }

    return (String[]) names.toArray( new String[names.size()] );
  }
}
