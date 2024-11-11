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

package org.pentaho.metadata.model.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalTable;

public class OlapHierarchy implements Cloneable, Serializable {
  private String name;
  private LogicalTable logicalTable;
  private LogicalColumn primaryKey;
  private List<OlapHierarchyLevel> hierarchyLevels;
  private boolean havingAll;

  private OlapDimension olapDimension;

  // TODO: add DefaultMember Mondrian property too
  // TODO: add allMemberName property
  // http://mondrian.pentaho.org/documentation/schema.php#The_all_member
  // TODO: add allLevelName property
  // http://mondrian.pentaho.org/documentation/schema.php#The_all_member
  //

  public OlapHierarchy() {

  }

  public OlapHierarchy( OlapDimension olapDimension ) {
    super();
    this.olapDimension = olapDimension;
    hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
    havingAll = true; // Set the default to true, said Julian
  }

  /**
   * @param name
   * @param hierarchyLevels
   */
  public OlapHierarchy( OlapDimension olapDimension, String name, List<OlapHierarchyLevel> hierarchyLevels ) {
    this( olapDimension );
    this.name = name;
    this.hierarchyLevels = hierarchyLevels;
  }

  public Object clone() {
    OlapHierarchy hierarchy = new OlapHierarchy( olapDimension ); // weak
                                                                  // reference, no
                                                                  // hard copy

    hierarchy.name = name;
    if ( logicalTable != null ) {
      hierarchy.logicalTable = (LogicalTable) logicalTable.clone();
    }
    if ( primaryKey != null ) {
      hierarchy.primaryKey = (LogicalColumn) primaryKey.clone();
    }
    for ( int i = 0; i < hierarchyLevels.size(); i++ ) {
      OlapHierarchyLevel hierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get( i );
      hierarchy.hierarchyLevels.add( (OlapHierarchyLevel) hierarchyLevel.clone() );
    }
    hierarchy.havingAll = havingAll;

    return hierarchy;
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapHierarchy) obj ).getName() );
  }

  /**
   * @return the hierarchyLevels
   */
  public List<OlapHierarchyLevel> getHierarchyLevels() {
    return hierarchyLevels;
  }

  /**
   * @param hierarchyLevels
   *          the hierarchyLevels to set
   */
  public void setHierarchyLevels( List<OlapHierarchyLevel> hierarchyLevels ) {
    this.hierarchyLevels = hierarchyLevels;
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
   * @return the havingAll
   */
  public boolean isHavingAll() {
    return havingAll;
  }

  /**
   * @param havingAll
   *          the havingAll to set
   */
  public void setHavingAll( boolean havingAll ) {
    this.havingAll = havingAll;
  }

  /**
   * @return the primaryKey
   */
  public LogicalColumn getPrimaryKey() {
    return primaryKey;
  }

  /**
   * @param primaryKey
   *          the primaryKey to set
   */
  public void setPrimaryKey( LogicalColumn primaryKey ) {
    this.primaryKey = primaryKey;
  }

  public OlapHierarchyLevel findOlapHierarchyLevel( String thisName ) {
    for ( int i = 0; i < hierarchyLevels.size(); i++ ) {
      OlapHierarchyLevel level = (OlapHierarchyLevel) hierarchyLevels.get( i );
      if ( level.getName().equalsIgnoreCase( thisName ) ) {
        return level;
      }
    }
    return null;
  }

  /**
   * @return the olapDimension
   */
  public OlapDimension getOlapDimension() {
    return olapDimension;
  }

  /**
   * @param olapDimension
   *          the olapDimension to set
   */
  public void setOlapDimension( OlapDimension olapDimension ) {
    this.olapDimension = olapDimension;
  }

  /**
   * @return the logicalTable
   */
  public LogicalTable getLogicalTable() {
    return logicalTable;
  }

  /**
   * @param logicalTable
   *          the logicalTable to set
   */
  public void setLogicalTable( LogicalTable logicalTable ) {
    this.logicalTable = logicalTable;
  }

  public List<String> getUnusedColumnNames( String locale ) {
    List<String> names = logicalTable.getColumnNames( locale );

    for ( int i = names.size() - 1; i >= 0; i-- ) {
      String columnName = (String) names.get( i );
      if ( findLogicalColumn( locale, columnName ) != null ) {
        names.remove( i );
      }
    }

    return names;
  }

  public LogicalColumn findLogicalColumn( String locale, String columnName ) {
    // Look in the levels
    for ( int i = 0; i < hierarchyLevels.size(); i++ ) {
      OlapHierarchyLevel level = (OlapHierarchyLevel) hierarchyLevels.get( i );
      LogicalColumn logicalColumn = level.findLogicalColumn( locale, columnName );
      if ( logicalColumn != null ) {
        return logicalColumn;
      }
    }
    return null;
  }

}
