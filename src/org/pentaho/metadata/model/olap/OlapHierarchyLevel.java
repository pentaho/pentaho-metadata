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
 * Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalColumn;

public class OlapHierarchyLevel implements Cloneable, Serializable {
  private String name;
  private LogicalColumn referenceColumn; // Also has the logical table
                                         // of-course.
  private LogicalColumn referenceOrdinalColumn;
  private LogicalColumn referenceCaptionColumn;

  private List<LogicalColumn> logicalColumns;
  private boolean havingUniqueMembers;
  private String levelType;

  private List<OlapAnnotation> annotations;

  private OlapHierarchy olapHierarchy;

  private boolean hidden;
  private String formatter;

  public static final String HIERARCHY_LEVEL_HIDDEN = "HIERARCHY_LEVEL_HIDDEN";
  public static final String HIERARCHY_LEVEL_FORMATTER = "HIERARCHY_LEVEL_FORMATTER";

  public OlapHierarchyLevel() {

  }

  public OlapHierarchyLevel( OlapHierarchy olapHierarchy ) {
    super();
    this.olapHierarchy = olapHierarchy;
    logicalColumns = new ArrayList<LogicalColumn>();
    annotations = new ArrayList<OlapAnnotation>();
  }

  /**
   * @param name
   * @param referenceColumn
   * @param logicalColumns
   */
  public OlapHierarchyLevel( OlapHierarchy olapHierarchy, String name, LogicalColumn referenceColumn,
      List<LogicalColumn> logicalColumns ) {
    this( olapHierarchy );
    this.name = name;
    this.referenceColumn = referenceColumn;
    this.logicalColumns = logicalColumns;
    this.annotations = new ArrayList<OlapAnnotation>();
  }

  public OlapHierarchyLevel( OlapHierarchy olapHierarchy, String name, LogicalColumn referenceColumn,
      List<LogicalColumn> logicalColumns, List<OlapAnnotation> annotations ) {
    this( olapHierarchy );
    this.name = name;
    this.referenceColumn = referenceColumn;
    this.logicalColumns = logicalColumns;
    this.annotations = annotations;
  }

  public Object clone() {
    // weak link again to the parent
    OlapHierarchyLevel hierarchyLevel = new OlapHierarchyLevel( olapHierarchy );

    hierarchyLevel.name = name;
    hierarchyLevel.levelType = levelType;
    if ( referenceColumn != null ) {
      hierarchyLevel.referenceColumn = (LogicalColumn) referenceColumn.clone();
    }
    if ( referenceOrdinalColumn != null ) {
      hierarchyLevel.referenceOrdinalColumn = (LogicalColumn) referenceOrdinalColumn.clone();
    }
    if ( referenceCaptionColumn != null ) {
      hierarchyLevel.referenceCaptionColumn = (LogicalColumn) referenceCaptionColumn.clone();
    }
    for ( int i = 0; i < logicalColumns.size(); i++ ) {
      LogicalColumn logicalColumn = (LogicalColumn) logicalColumns.get( i );
      hierarchyLevel.logicalColumns.add( (LogicalColumn) logicalColumn.clone() );
    }
    hierarchyLevel.havingUniqueMembers = havingUniqueMembers;

    return hierarchyLevel;
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapHierarchyLevel) obj ).getName() );
  }

  /**
   * @return the logicalColumns
   */
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
  }

  /**
   * @param logicalColumns
   *          the logicalColumns to set
   */
  public void setLogicalColumns( List<LogicalColumn> logicalColumns ) {
    this.logicalColumns = logicalColumns;
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
   * @return the leveltype
   */
  public String getLevelType() {
    return levelType;
  }

  /**
   * @param levelType
   *          the name to set
   */
  public void setLevelType( String levelType ) {
    this.levelType = levelType;
  }

  /**
   * @return the referenceColumn
   */
  public LogicalColumn getReferenceColumn() {
    return referenceColumn;
  }

  /**
   * @param referenceColumn
   *          the referenceColumn to set
   */
  public void setReferenceColumn( LogicalColumn referenceColumn ) {
    this.referenceColumn = referenceColumn;
  }

  /**
   * @return the referenceOrdinalColumn
   */
  public LogicalColumn getReferenceOrdinalColumn() {
    return referenceOrdinalColumn;
  }

  /**
   * @param referenceOrdinalColumn
   *          the referenceOrdinalColumn to set
   */
  public void setReferenceOrdinalColumn( LogicalColumn referenceOrdinalColumn ) {
    this.referenceOrdinalColumn = referenceOrdinalColumn;
  }

  /**
   * @return the referenceCaptionColumn
   */
  public LogicalColumn getReferenceCaptionColumn() {
    return referenceCaptionColumn;
  }

  /**
   * @param referenceCaptionColumn
   *          the referenceCaptionColumn to set
   */
  public void setReferenceCaptionColumn( LogicalColumn referenceCaptionColumn ) {
    this.referenceCaptionColumn = referenceCaptionColumn;
  }

  public LogicalColumn findLogicalColumn( String locale, String thisName ) {
    if ( referenceColumn != null && referenceColumn.getName( locale ).equalsIgnoreCase( thisName ) ) {
      return referenceColumn;
    }

    for ( int i = 0; i < logicalColumns.size(); i++ ) {
      LogicalColumn column = (LogicalColumn) logicalColumns.get( i );
      if ( column.getName( locale ).equalsIgnoreCase( thisName ) ) {
        return column;
      }
    }
    return null;
  }

  /**
   * @return the olapHierarchy
   */
  public OlapHierarchy getOlapHierarchy() {
    return olapHierarchy;
  }

  /**
   * @param olapHierarchy
   *          the olapHierarchy to set
   */
  public void setOlapHierarchy( OlapHierarchy olapHierarchy ) {
    this.olapHierarchy = olapHierarchy;
  }

  /**
   * @return the havingUniqueMembers
   */
  public boolean isHavingUniqueMembers() {
    return havingUniqueMembers;
  }

  /**
   * @param havingUniqueMembers
   *          the havingUniqueMembers to set
   */
  public void setHavingUniqueMembers( boolean havingUniqueMembers ) {
    this.havingUniqueMembers = havingUniqueMembers;
  }

  public List<OlapAnnotation> getAnnotations() {
    if ( annotations == null ) {
      annotations = new ArrayList<OlapAnnotation>();
    }
    return annotations;
  }

  public void setAnnotations( List<OlapAnnotation> annotations ) {
    this.annotations = annotations;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }

  public void setFormatter( final String formatter ) {
    this.formatter = formatter;
  }

  public String getFormatter() {
    return formatter;
  }
}
