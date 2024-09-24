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
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.schema.BusinessColumn;

@SuppressWarnings( "deprecation" )
public class OlapHierarchyLevel extends ChangedFlag implements Cloneable {
  private String name;
  private BusinessColumn referenceColumn; // Also has the business table of-course.
  private List<BusinessColumn> businessColumns;
  private boolean havingUniqueMembers;

  private OlapHierarchy olapHierarchy;

  public OlapHierarchyLevel( OlapHierarchy olapHierarchy ) {
    super();
    this.olapHierarchy = olapHierarchy;
    businessColumns = new ArrayList<BusinessColumn>();
  }

  /**
   * @param name
   * @param referenceColumn
   * @param businessColumns
   */
  public OlapHierarchyLevel( OlapHierarchy olapHierarchy, String name, BusinessColumn referenceColumn,
      List<BusinessColumn> businessColumns ) {
    this( olapHierarchy );
    this.name = name;
    this.referenceColumn = referenceColumn;
    this.businessColumns = businessColumns;
  }

  public Object clone() {
    OlapHierarchyLevel hierarchyLevel = new OlapHierarchyLevel( olapHierarchy ); // weak link again to the parent.

    hierarchyLevel.name = name;
    if ( referenceColumn != null ) {
      hierarchyLevel.referenceColumn = (BusinessColumn) referenceColumn.clone();
    }
    for ( int i = 0; i < businessColumns.size(); i++ ) {
      BusinessColumn businessColumn = (BusinessColumn) businessColumns.get( i );
      hierarchyLevel.businessColumns.add( (BusinessColumn) businessColumn.clone() );
    }
    hierarchyLevel.havingUniqueMembers = havingUniqueMembers;

    return hierarchyLevel;
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapHierarchyLevel) obj ).getName() );
  }

  /**
   * @return the businessColumns
   */
  public List<BusinessColumn> getBusinessColumns() {
    return businessColumns;
  }

  /**
   * @param businessColumns
   *          the businessColumns to set
   */
  public void setBusinessColumns( List<BusinessColumn> businessColumns ) {
    this.businessColumns = businessColumns;
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
   * @return the referenceColumn
   */
  public BusinessColumn getReferenceColumn() {
    return referenceColumn;
  }

  /**
   * @param referenceColumn
   *          the referenceColumn to set
   */
  public void setReferenceColumn( BusinessColumn referenceColumn ) {
    this.referenceColumn = referenceColumn;
  }

  public BusinessColumn findBusinessColumn( String locale, String thisName ) {
    if ( referenceColumn != null && referenceColumn.getDisplayName( locale ).equalsIgnoreCase( thisName ) ) {
      return referenceColumn;
    }

    for ( int i = 0; i < businessColumns.size(); i++ ) {
      BusinessColumn column = (BusinessColumn) businessColumns.get( i );
      if ( column.getDisplayName( locale ).equalsIgnoreCase( thisName ) ) {
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
}
