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
package org.pentaho.pms.schema;

import java.util.ArrayList;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.SqlPhysicalColumn
 */
public class PhysicalColumn extends ConceptUtilityBase implements ConceptUtilityInterface, Cloneable {
  private PhysicalTable physicalTable;

  public PhysicalColumn( String id, String formula, FieldTypeSettings fieldType, AggregationSettings aggregationType,
      PhysicalTable tableinfo ) {
    super( id );
    setFormula( formula );
    setFieldType( fieldType );
    setAggregationType( aggregationType );
    setAggregationList( new ArrayList<AggregationSettings>() );
    setDataType( DataTypeSettings.UNKNOWN );
    this.physicalTable = tableinfo;
  }

  public PhysicalColumn( String id ) {
    this( id, null, FieldTypeSettings.OTHER, AggregationSettings.NONE, null );
  }

  public PhysicalColumn() {
    this( null );
  }

  /**
   * @return the description of the model element
   */
  public String getModelElementDescription() {
    return Messages.getString( "PhysicalColumn.USER_DESCRIPTION" ); //$NON-NLS-1$
  }

  protected Object clone() {
    try {
      PhysicalColumn retval = (PhysicalColumn) super.clone();
      retval.setConcept( (ConceptInterface) getConcept().clone() ); // deep copy of the concepts
      return retval;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public void setTable( PhysicalTable tableinfo ) {
    this.physicalTable = tableinfo;
  }

  public PhysicalTable getTable() {
    return physicalTable;
  }

  public String getTableColumn() {
    String retval;

    if ( getFormula() != null && getFormula().length() > 0 ) {
      retval = getFormula();
      if ( retval == null || retval.length() == 0 ) {
        retval = getId();
      }
    } else {
      PhysicalTable table = getTable();
      retval = table.getId() + "." + getId(); //$NON-NLS-1$
    }

    return retval;
  }

  /**
   * @deprecated
   */
  public String getAliasColumn( String tableAlias, String formula ) {
    // Database?
    DatabaseMeta databaseMeta = getTable().getDatabaseMeta();
    return getAliasColumn( tableAlias, formula, databaseMeta );
  }

  public String getAliasColumn( String tableAlias, String formula, DatabaseMeta databaseMeta ) {

    String retval;

    if ( getTable() != null && formula != null ) {
      if ( !isExact() ) {
        retval = databaseMeta.quoteField( tableAlias ) + "." + databaseMeta.quoteField( formula ); //$NON-NLS-1$
      } else {
        retval = getFormula();
      }
    } else {
      retval = "??"; //$NON-NLS-1$
    }

    return retval;
  }

  public String getRenameAsColumn( DatabaseMeta dbinfo, int columnNr ) {
    String retval = ""; //$NON-NLS-1$

    if ( hasAggregate() && !isExact() ) {
      retval += "F___" + columnNr; //$NON-NLS-1$
    } else if ( isExact() ) {
      retval += "E___" + columnNr; //$NON-NLS-1$
    } else {
      retval += getFormula();
    }

    return retval;
  }

  public boolean equals( Object obj ) {

    if ( obj instanceof PhysicalColumn == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    PhysicalColumn rhs = (PhysicalColumn) obj;

    // formula comparison should be case-insensitive
    String lhsFormula = null != getFormula() ? getFormula().toUpperCase() : null;
    String rhsFormula = null != rhs.getFormula() ? rhs.getFormula().toUpperCase() : null;

    return new EqualsBuilder().appendSuper( super.equals( rhs ) ).append( lhsFormula, rhsFormula ).append(
        getAggregationType(), rhs.getAggregationType() ).append( getFieldType(), rhs.getFieldType() ).append(
        physicalTable, rhs.physicalTable ).append( getAggregationList(), rhs.getAggregationList() ).isEquals();
  }

  public int hashCode() {
    String formulaToHash = null != getFormula() ? getFormula().toUpperCase() : null;
    return new HashCodeBuilder( 17, 199 ).appendSuper( super.hashCode() ).append( formulaToHash ).append(
        getAggregationType() ).append( getFieldType() ).append( physicalTable ).toHashCode();
  }

  public String toString() {
    return getId() == null ? "NULL" : getId(); //$NON-NLS-1$
  }

  // /**
  // * @param aggregationType the aggregationType to set
  // */
  // public void setAggregationType(AggregationSettings aggregationType)
  // {
  // super.setAggregationType(aggregationType);
  // setChanged();
  // }

  public void setAggregationType( String aggregationTypeDesc ) {
    setAggregationType( AggregationSettings.getType( aggregationTypeDesc ) );
    setChanged();
  }

  public void setFieldType( String fieldTypeDescription ) {
    setFieldType( FieldTypeSettings.getType( fieldTypeDescription ) );
    setChanged();
  }
}
