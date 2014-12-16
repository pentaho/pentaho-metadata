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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.Property;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.model.concept.types.TargetTableType;

/**
 * This is the SQL implementation of a physical table. It acts as either a table in the database or a SQL statement.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class SqlPhysicalTable extends Concept implements IPhysicalTable  {

  private static final long serialVersionUID = -2590635019353532334L;

  public static final String TARGET_SCHEMA = "target_schema"; //$NON-NLS-1$
  public static final String TARGET_TABLE = "target_table"; //$NON-NLS-1$
  public static final String TARGET_TABLE_TYPE = "target_table_type"; //$NON-NLS-1$
  public static final String RELATIVE_SIZE = "relative_size"; //$NON-NLS-1$

  List<IPhysicalColumn> physicalColumns = new ArrayList<IPhysicalColumn>();

  public SqlPhysicalTable() {
    super();
    setTargetTableType( TargetTableType.TABLE );
    // physical table has the following default properties:
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
  }

  public SqlPhysicalTable( SqlPhysicalModel model ) {
    this();
    setParent( model );
  }

  @Override
  public List<IConcept> getChildren() {
    List<IConcept> children = new ArrayList<IConcept>();
    children.addAll( physicalColumns );
    return children;
  }

  public IPhysicalModel getPhysicalModel() {
    return ( IPhysicalModel ) getParent();
  }

  public List<IPhysicalColumn> getPhysicalColumns() {
    return physicalColumns;
  }

  public void addPhysicalColumn( IPhysicalColumn column ) {
    physicalColumns.add( column );
  }

  public String getTargetSchema() {
    Property property = getProperty( TARGET_SCHEMA );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setTargetSchema( String targetSchema ) {
    setProperty( TARGET_SCHEMA, new Property<String>( targetSchema ) );
  }

  public String getTargetTable() {
    Property property = getProperty( TARGET_TABLE );
    if( property != null ) {
      return (String) property.getValue();
    }
    return null;
  }

  public void setTargetTable( String targetTable ) {
    setProperty( TARGET_TABLE, new Property<String>( targetTable ) );
  }

  public TargetTableType getTargetTableType() {
    Property property = getProperty( TARGET_TABLE_TYPE );
    if( property != null ) {
      return (TargetTableType) property.getValue();
    }
    return null;
  }

  public void setTargetTableType( TargetTableType targetTableType ) {
    setProperty( TARGET_TABLE_TYPE, new Property<TargetTableType>( targetTableType ) );
  }

  public TableType getTableType() {
    Property property = getProperty( TABLETYPE_PROPERTY );
    if( property != null ) {
      return (TableType) property.getValue();
    }
    return null;
  }

  public void setTableType( TableType tableType ) {
    setProperty( TABLETYPE_PROPERTY, new Property<TableType>( tableType ) );
  }

  public Integer getRelativeSize() {
    Property property = getProperty( RELATIVE_SIZE );
    if( property != null ) {
      return (Integer) property.getValue();
    }
    return null;
  }

  public void setRelativeSize( Integer relativeSize ) {
    setProperty( RELATIVE_SIZE, new Property<Integer>( relativeSize ) );
  }
}
