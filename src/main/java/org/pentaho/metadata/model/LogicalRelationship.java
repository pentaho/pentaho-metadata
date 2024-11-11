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

package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.RelationshipType;

/**
 * Logical relationships define the relationship between two business tables through columns or an open formula
 * expression.
 * <p>
 * TODO: This class needs additional work before it can support the capabilities in the original metadata model
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class LogicalRelationship extends Concept {

  private static final long serialVersionUID = -2673951365033614344L;

  public static final String COMPLEX = "complex"; //$NON-NLS-1$
  public static final String COMPLEX_JOIN = "complex_join"; //$NON-NLS-1$
  public static final String RELATIONSHIP_TYPE = "relationship_type"; //$NON-NLS-1$
  public static final String JOIN_ORDER_KEY = "join_order_key"; //$NON-NLS-1$

  // A relationship's descriptions is not to be localized
  private String relationshipDescription = null;

  private LogicalTable fromTable, toTable;
  private LogicalColumn fromColumn, toColumn;
  private static final String CLASS_ID = "LogicalRelationship";

  public LogicalRelationship() {
    super();
    setComplex( false );
    setRelationshipType( RelationshipType.UNDEFINED );
  }

  public LogicalRelationship( LogicalModel logicalModel, LogicalTable fromTable, LogicalTable toTable,
                              LogicalColumn fromColumn, LogicalColumn toColumn ) {
    this();
    setParent( logicalModel );
    this.fromTable = fromTable;
    this.toTable = toTable;
    this.fromColumn = fromColumn;
    this.toColumn = toColumn;
  }

  public void setLogicalModel( LogicalModel logicalModel ) {
    setParent( logicalModel );
  }

  public LogicalModel getLogicalModel() {
    return (LogicalModel) getParent();
  }

  public String getRelationshipDescription() {
    return this.relationshipDescription;
  }

  public void setRelationshipDescription( String description ) {
    this.relationshipDescription = description;
  }

  @Override
  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>( getParent().getUniqueId() );
    uid.add( CLASS_ID.concat( UID_TYPE_SEPARATOR ) + getId() );
    return uid;
  }

  public Boolean isComplex() {
    return (Boolean) getProperty( COMPLEX );
  }

  public void setComplex( Boolean complex ) {
    setProperty( COMPLEX, complex );
  }

  public String getComplexJoin() {
    return (String) getProperty( COMPLEX_JOIN );
  }

  public void setComplexJoin( String complexJoin ) {
    setProperty( COMPLEX_JOIN, complexJoin );
  }

  public RelationshipType getRelationshipType() {
    return (RelationshipType) getProperty( RELATIONSHIP_TYPE );
  }

  public void setRelationshipType( RelationshipType relationshipType ) {
    setProperty( RELATIONSHIP_TYPE, relationshipType );
  }

  public String getJoinOrderKey() {
    return (String) getProperty( JOIN_ORDER_KEY );
  }

  public void setJoinOrderKey( String joinOrderKey ) {
    setProperty( JOIN_ORDER_KEY, joinOrderKey );
  }

  public LogicalTable getFromTable() {
    return fromTable;
  }

  public void setFromTable( LogicalTable fromTable ) {
    this.fromTable = fromTable;
  }

  public LogicalTable getToTable() {
    return toTable;
  }

  public void setToTable( LogicalTable toTable ) {
    this.toTable = toTable;
  }

  public LogicalColumn getFromColumn() {
    return fromColumn;
  }

  public void setFromColumn( LogicalColumn fromColumn ) {
    this.fromColumn = fromColumn;
  }

  public LogicalColumn getToColumn() {
    return toColumn;
  }

  public void setToColumn( LogicalColumn toColumn ) {
    this.toColumn = toColumn;
  }

  public boolean isUsingTable( LogicalTable table ) {
    if ( table == null ) {
      return false;
    }
    return table.getId().equals( toTable.getId() ) || table.getId().equals( fromTable.getId() );
  }

}
