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
package org.pentaho.pms.schema;

import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;

/**
 * Represents a physical table on a physical database with physical columns.
 * 
 * @since 28-jan-2004
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.SqlPhysicalTable
 */
public class PhysicalTable extends ConceptUtilityBase implements Cloneable, ChangedFlagInterface,
    ConceptUtilityInterface {
  private DatabaseMeta databaseMeta;
  private UniqueList<PhysicalColumn> physicalColumns;

  public PhysicalTable( String id, String targetSchema, String targetTable, DatabaseMeta databaseMeta,
      UniqueList<PhysicalColumn> columns ) {
    super( id );
    this.databaseMeta = databaseMeta;
    this.physicalColumns = columns;

    if ( targetSchema != null ) {
      setTargetSchema( targetSchema );
    }
    setTargetTable( targetTable );
  }

  public PhysicalTable() {
    this( null, null, null, null, new UniqueArrayList<PhysicalColumn>() );
  }

  public PhysicalTable( String id ) {
    this( id, null, null, null, new UniqueArrayList<PhysicalColumn>() );
  }

  /**
   * @return the description of the model element
   */
  public String getModelElementDescription() {
    return Messages.getString( "PhysicalTable.USER_DESCRIPTION" ); //$NON-NLS-1$
  }

  public Object clone() {
    try {
      PhysicalTable retval = (PhysicalTable) super.clone();
      retval.setConcept( (ConceptInterface) getConcept().clone() ); // deep copy of all properties
      retval.setPhysicalColumns( new UniqueArrayList<PhysicalColumn>() ); // clear all columns: deep copy as well.
      for ( int i = 0; i < nrPhysicalColumns(); i++ ) {
        PhysicalColumn physicalColumn = getPhysicalColumn( i );
        try {
          retval.addPhysicalColumn( (PhysicalColumn) physicalColumn.clone() ); // deep copy of the columns information.
        } catch ( ObjectAlreadyExistsException e ) {
          // It's safe to say that if the original didn't have a uniqueness problem, this one has neither
          // That being said, we still throw a Runtime :-)
          // You never know.
          throw new RuntimeException( e );
        }
      }
      retval.setDatabaseMeta( databaseMeta ); // shallow copy of the database information

      return retval;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public void setDatabaseMeta( DatabaseMeta databaseMeta ) {
    this.databaseMeta = databaseMeta;
  }

  public DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }

  public void setPhysicalColumns( UniqueList<PhysicalColumn> physicalColumns ) {
    this.physicalColumns = physicalColumns;
    setChanged();
  }

  public UniqueList<PhysicalColumn> getPhysicalColumns() {
    return physicalColumns;
  }

  public PhysicalColumn getPhysicalColumn( int i ) {
    return (PhysicalColumn) physicalColumns.get( i );
  }

  public void addPhysicalColumn( PhysicalColumn column ) throws ObjectAlreadyExistsException {
    physicalColumns.add( column );
    setChanged();
  }

  public void addPhysicalColumn( int i, PhysicalColumn column ) throws ObjectAlreadyExistsException {
    physicalColumns.add( i, column );
    setChanged();
  }

  public int findPhysicalColumnNr( String columnName ) {
    for ( int i = 0; i < physicalColumns.size(); i++ ) {
      if ( getPhysicalColumn( i ).getId().equalsIgnoreCase( columnName ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Find a physical column using its ID
   * 
   * @param columnId
   *          the column ID to look out for
   * @return the physical column or null if nothing could be found.
   */
  public PhysicalColumn findPhysicalColumn( String columnId ) {
    int idx = findPhysicalColumnNr( columnId );
    if ( idx >= 0 ) {
      return getPhysicalColumn( idx );
    }
    return null;
  }

  /**
   * Find a physical column using the localised name of the column (with a search for the ID as a fallback)
   * 
   * @param locale
   *          the locale to search in
   * @param columnName
   *          the column name
   * @return the physical column or null if nothing was found.
   */
  public PhysicalColumn findPhysicalColumn( String locale, String columnName ) {
    for ( int i = 0; i < nrPhysicalColumns(); i++ ) {
      PhysicalColumn physicalColumn = getPhysicalColumn( i );

      if ( columnName.equalsIgnoreCase( physicalColumn.getConcept().getName( locale ) ) ) {
        return physicalColumn;
      }
    }

    return findPhysicalColumn( columnName );
  }

  public int indexOfPhysicalColumn( PhysicalColumn f ) {
    return physicalColumns.indexOf( f );
  }

  public void removePhysicalColumn( int i ) {
    physicalColumns.remove( i );
    setChanged();
  }

  public void removeAllPhysicalColumns() {
    physicalColumns.clear();
    setChanged();
  }

  public int nrPhysicalColumns() {
    return physicalColumns.size();
  }

  public boolean equals( Object obj ) {
    return super.equals( obj );
  }

  public int hashCode() {
    return super.hashCode();
  }

  public String toString() {
    if ( databaseMeta != null ) {
      return databaseMeta.getName() + "-->" + getId(); //$NON-NLS-1$
    }
    return getId();
  }

  /**
   * @return the IDs of all the physical columns
   */
  public String[] getColumnIDs() {
    String[] ids = new String[nrPhysicalColumns()];
    for ( int i = 0; i < nrPhysicalColumns(); i++ ) {
      ids[i] = getPhysicalColumn( i ).getId();
    }

    return ids;
  }

  /**
   * @return the names of all the physical columns
   * @param locale
   *          the locale to use
   */
  public String[] getColumnNames( String locale ) {
    String[] names = new String[nrPhysicalColumns()];
    for ( int i = 0; i < nrPhysicalColumns(); i++ ) {
      names[i] = getPhysicalColumn( i ).getDisplayName( locale );
    }

    return names;
  }

  public void clearChanged() {
    super.clearChanged();

    for ( int i = 0; i < nrPhysicalColumns(); i++ ) {
      getPhysicalColumn( i ).clearChanged();
    }
  }
}
