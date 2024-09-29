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

package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.security.RowLevelSecurity;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * The logical model contains logical tables and categories, and the name and description are presented to end users.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class LogicalModel extends Concept {

  private static final long serialVersionUID = 4063396040423259880L;

  public static final String ROW_LEVEL_SECURITY = "row_level_security"; //$NON-NLS-1$

  public static final String PROPERTY_OLAP_DIMS = "olap_dimensions"; //$NON-NLS-1$

  public static final String PROPERTY_OLAP_CUBES = "olap_cubes"; //$NON-NLS-1$

  public static final String PROPERTY_OLAP_CALCULATED_MEMBERS = "olap_calculated_members"; //$NON-NLS-1$

  public static final String PROPERTY_OLAP_ROLES = "olap_roles"; //$NON-NLS-1$

  public static final String PROPERTY_TARGET_TABLE_STAGED = "target_table_staged"; //$NON-NLS-1$

  private List<LogicalTable> logicalTables = new ArrayList<LogicalTable>();
  private List<LogicalRelationship> logicalRelationships = new ArrayList<LogicalRelationship>();
  private List<Category> categories = new ArrayList<Category>();
  private static final String CLASS_ID = "LogicalModel";

  public LogicalModel() {
    super();
    // logical model has the following default properties:
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
  }

  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>();
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  public void setDomain( Domain domain ) {
    setParent( domain );
  }

  public Domain getDomain() {
    return (Domain) getParent();
  }

  @Override
  public List<IConcept> getChildren() {
    ArrayList<IConcept> children = new ArrayList<IConcept>();
    children.addAll( logicalTables );
    children.addAll( logicalRelationships );
    children.addAll( categories );
    return children;
  }

  public void setPhysicalModel( IPhysicalModel physicalModel ) {
    setPhysicalConcept( physicalModel );
  }

  public IPhysicalModel getPhysicalModel() {
    return (IPhysicalModel) getPhysicalConcept();
  }

  public List<LogicalTable> getLogicalTables() {
    return logicalTables;
  }

  public void addLogicalTable( LogicalTable table ) {
    logicalTables.add( table );
  }

  public List<LogicalRelationship> getLogicalRelationships() {
    return logicalRelationships;
  }

  public void addLogicalRelationship( LogicalRelationship rel ) {
    logicalRelationships.add( rel );
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void addCategory( Category category ) {
    categories.add( category );
  }

  public RowLevelSecurity getRowLevelSecurity() {
    return (RowLevelSecurity) getProperty( ROW_LEVEL_SECURITY );
  }

  public void setRowLevelSecurity( RowLevelSecurity rls ) {
    setProperty( ROW_LEVEL_SECURITY, rls );
  }

  public Category findCategory( String categoryId ) {
    for ( Category category : getCategories() ) {
      if ( categoryId.equals( category.getId() ) ) {
        return category;
      }
    }
    return null;
  }

  /**
   * finds a logical table within the model.
   *
   * @param tableId the table to find
   * @return a logical table object.
   */
  public LogicalTable findLogicalTable( String tableId ) {
    for ( LogicalTable table : getLogicalTables() ) {
      if ( tableId.equals( table.getId() ) ) {
        return table;
      }
    }
    return null;
  }

  /**
   * finds a logical column within the model.
   *
   * @param columnId the column to find
   * @return a logical column object.
   */
  public LogicalColumn findLogicalColumn( String columnId ) {
    for ( LogicalTable table : getLogicalTables() ) {
      for ( LogicalColumn column : table.getLogicalColumns() ) {
        if ( columnId.equals( column.getId() ) ) {
          return column;
        }
      }
    }
    return null;
  }

  /**
   * finds a logical column within the model.
   *
   * @param columnId the column to find
   * @return a logical column object.
   */
  public LogicalColumn findLogicalColumnInCategories( String columnId ) {
    for ( Category cat : getCategories() ) {
      for ( LogicalColumn column : cat.getLogicalColumns() ) {
        if ( columnId.equals( column.getId() ) ) {
          return column;
        }
      }
    }
    return null;
  }

  public LogicalRelationship findRelationshipUsing( LogicalTable one, LogicalTable two ) {
    for ( LogicalRelationship relationship : getLogicalRelationships() ) {
      if ( relationship.isUsingTable( one ) && relationship.isUsingTable( two ) ) {
        return relationship;
      }
    }
    return null;
  }

  @Override
  public Object clone() {
    LogicalModel clone = new LogicalModel();
    // configure concept properties
    clone( clone );

    // shallow references
    clone.logicalRelationships = logicalRelationships;
    clone.setParent( getParent() );
    clone.setPhysicalConcept( getPhysicalConcept() );

    // actual clones
    clone.logicalTables = new ArrayList<LogicalTable>();
    for ( LogicalTable table : logicalTables ) {
      clone.addLogicalTable( (LogicalTable) table.clone() );
    }
    clone.categories = new ArrayList<Category>();
    for ( Category category : categories ) {
      clone.addCategory( (Category) category.clone() );
    }
    return clone;
  }

}
