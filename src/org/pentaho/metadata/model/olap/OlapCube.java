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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalTable;

public class OlapCube implements Cloneable, Serializable {
  private String name;
  private LogicalTable logicalTable;

  private List<OlapDimensionUsage> olapDimensionUsages;
  private List<OlapMeasure> olapMeasures;
  private List<OlapCalculatedMember> olapCalculatedMembers;

  // TODO: private dimensions

  public OlapCube() {
    olapDimensionUsages = new ArrayList<OlapDimensionUsage>();
    olapMeasures = new ArrayList<OlapMeasure>();
    olapCalculatedMembers = new ArrayList<OlapCalculatedMember>();
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
    
    for ( int i = 0; i < olapCalculatedMembers.size(); i++ ) {
      OlapCalculatedMember cm = (OlapCalculatedMember) olapCalculatedMembers.get( i );
      olapCube.olapCalculatedMembers.add( (OlapCalculatedMember) cm.clone() );
    }

    if ( logicalTable != null ) {
      olapCube.logicalTable = logicalTable; // no cloning here please!
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
  public LogicalTable getLogicalTable() {
    return logicalTable;
  }

  /**
   * @param logicalTable
   *          the businessTable to set
   */
  public void setLogicalTable( LogicalTable logicalTable ) {
    this.logicalTable = logicalTable;
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

  /**
   * TODO: This should use IDs vs Names
   * 
   * @param locale
   * @return
   */
  public String[] getUnusedColumnNames( String locale ) {
    List<String> names = logicalTable.getColumnNames( locale );
    for ( int i = names.size() - 1; i >= 0; i-- ) {
      String columnName = (String) names.get( i );
      for ( int m = 0; m < olapMeasures.size(); m++ ) {
        OlapMeasure measure = (OlapMeasure) olapMeasures.get( m );
        if ( measure.getLogicalColumn().getName( locale ).equals( columnName ) ) {
          names.remove( i );
        }
      }
    }

    return (String[]) names.toArray( new String[names.size()] );
  }

  public List<OlapCalculatedMember> getOlapCalculatedMembers() {
    return olapCalculatedMembers;
  }

  public void setOlapCalculatedMembers( List<OlapCalculatedMember> olapCalculatedMembers ) {
    this.olapCalculatedMembers = olapCalculatedMembers;
  }
}
