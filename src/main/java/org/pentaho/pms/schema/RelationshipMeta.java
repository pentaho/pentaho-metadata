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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.schema;

import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.core.xml.XMLInterface;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

/**
 * Created on 28-jan-2004
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.LogicalRelationship
 */
public class RelationshipMeta extends ChangedFlag implements Cloneable, XMLInterface, ChangedFlagInterface {
  private BusinessTable table_from, table_to;
  private BusinessColumn field_from, field_to;
  private int type;
  private boolean complex;
  private String complex_join;
  private String joinOrderKey;
  private String description;
  // columns referenced in a complexJoin, only used at design time
  private List<BusinessColumn> cjReferencedColumns;

  public static final int TYPE_RELATIONSHIP_UNDEFINED = 0;
  public static final int TYPE_RELATIONSHIP_1_N = 1;
  public static final int TYPE_RELATIONSHIP_N_1 = 2;
  public static final int TYPE_RELATIONSHIP_1_1 = 3;
  public static final int TYPE_RELATIONSHIP_0_N = 4;
  public static final int TYPE_RELATIONSHIP_N_0 = 5;
  public static final int TYPE_RELATIONSHIP_0_1 = 6;
  public static final int TYPE_RELATIONSHIP_1_0 = 7;
  public static final int TYPE_RELATIONSHIP_N_N = 8;
  public static final int TYPE_RELATIONSHIP_0_0 = 9;

  public static final String[] typeRelationshipDesc = {
    "undefined", "1:N", "N:1", "1:1", "0:N", "N:0", "0:1", "1:0", "N:N", "0:0" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ // $NON-NLS-10$
  };

  public static final int TYPE_JOIN_INNER = 0;
  public static final int TYPE_JOIN_LEFT_OUTER = 1;
  public static final int TYPE_JOIN_RIGHT_OUTER = 2;
  public static final int TYPE_JOIN_FULL_OUTER = 3;

  public static final String[] typeJoinDesc = { "Inner", "Left outer", "Right outer", "Full outer", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  };

  public RelationshipMeta() {
    type = TYPE_RELATIONSHIP_UNDEFINED;
    complex = false;
    complex_join = ""; //$NON-NLS-1$
  }

  public RelationshipMeta( BusinessTable table_from, BusinessTable table_to, BusinessColumn field_from,
      BusinessColumn field_to ) {
    this();
    this.table_from = table_from;
    this.table_to = table_to;
    this.field_from = field_from;
    this.field_to = field_to;
  }

  public RelationshipMeta( BusinessTable table_from, BusinessTable table_to, String complex_join ) {
    this.table_from = table_from;
    this.table_to = table_to;
    this.field_from = null;
    this.field_to = null;
    this.type = TYPE_RELATIONSHIP_UNDEFINED;
    this.complex = true;
    this.complex_join = complex_join;
  }

  public boolean loadXML( Node relnode, List tables ) {
    try {
      String from = XMLHandler.getTagValue( relnode, "table_from" ); //$NON-NLS-1$
      table_from = findTable( tables, from );
      String to = XMLHandler.getTagValue( relnode, "table_to" ); //$NON-NLS-1$
      table_to = findTable( tables, to );

      if ( table_from != null ) {
        field_from = table_from.findBusinessColumn( XMLHandler.getTagValue( relnode, "field_from" ) ); //$NON-NLS-1$
      }
      if ( table_to != null ) {
        field_to = table_to.findBusinessColumn( XMLHandler.getTagValue( relnode, "field_to" ) ); //$NON-NLS-1$
      }
      type = getType( XMLHandler.getTagValue( relnode, "type" ) ); //$NON-NLS-1$
      complex = "Y".equalsIgnoreCase( XMLHandler.getTagValue( relnode, "complex" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      complex_join = XMLHandler.getTagValue( relnode, "complex_join" ); //$NON-NLS-1$
      joinOrderKey = XMLHandler.getTagValue( relnode, "join_order_key" ); //$NON-NLS-1$
      description = XMLHandler.getTagValue( relnode, "description" ); //$NON-NLS-1$

      return true;
    } catch ( Exception e ) {
      return false;
    }
  }

  public String getXML() {
    String retval = ""; //$NON-NLS-1$

    retval += "      <relationship>" + Const.CR; //$NON-NLS-1$
    retval += "        " + XMLHandler.addTagValue( "table_from", table_from.getId() ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "table_to", table_to.getId() ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "field_from", field_from != null ? field_from.getId() : "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    retval += "        " + XMLHandler.addTagValue( "field_to", field_to != null ? field_to.getId() : "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    retval += "        " + XMLHandler.addTagValue( "type", getTypeDesc() ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "complex", complex ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "complex_join", complex_join ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "join_order_key", joinOrderKey ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "        " + XMLHandler.addTagValue( "description", description ); //$NON-NLS-1$ //$NON-NLS-2$
    retval += "      </relationship>" + Const.CR; //$NON-NLS-1$

    return retval;
  }

  private BusinessTable findTable( List tables, String name ) {
    for ( int x = 0; x < tables.size(); x++ ) {
      BusinessTable tableinfo = (BusinessTable) tables.get( x );
      if ( tableinfo.getId().equalsIgnoreCase( name ) ) {
        return tableinfo;
      }
    }
    return null;
  }

  public Object clone() {
    try {
      RelationshipMeta retval = (RelationshipMeta) super.clone();

      retval.setTableFrom( (BusinessTable) getTableFrom().clone() );
      retval.setTableTo( (BusinessTable) getTableTo().clone() );

      return retval;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public void setTableFrom( BusinessTable table_from ) {
    this.table_from = table_from;
  }

  public BusinessTable getTableFrom() {
    return table_from;
  }

  public void setTableTo( BusinessTable table_to ) {
    this.table_to = table_to;
  }

  public BusinessTable getTableTo() {
    return table_to;
  }

  public void setFieldFrom( BusinessColumn field_from ) {
    this.field_from = field_from;
  }

  public void setFieldTo( BusinessColumn field_to ) {
    this.field_to = field_to;
  }

  public BusinessColumn getFieldFrom() {
    return field_from;
  }

  public BusinessColumn getFieldTo() {
    return field_to;
  }

  public boolean isComplex() {
    return complex;
  }

  public boolean isRegular() {
    return !complex;
  }

  public void setComplex() {
    setComplex( true );
  }

  public void setRegular() {
    setComplex( false );
  }

  public void flipComplex() {
    setComplex( !isComplex() );
  }

  public void setComplex( boolean c ) {
    complex = c;
  }

  public String getComplexJoin() {
    return complex_join;
  }

  public List<BusinessColumn> getCJReferencedColumns() {
    return cjReferencedColumns;
  }

  public void setCJReferencedColumns( List<BusinessColumn> cjReferencedColumns ) {
    this.cjReferencedColumns = cjReferencedColumns;
  }

  public void setComplexJoin( String cj ) {
    complex_join = cj;
  }

  public int getType() {
    return type;
  }

  public void setType( int type ) {
    this.type = type;
  }

  public void setType( String tdesc ) {
    this.type = getType( tdesc );
  }

  public String getTypeDesc() {
    return getType( type );
  }

  public static final String getType( int i ) {
    return typeRelationshipDesc[i];
  }

  public static final int getType( String typedesc ) {
    for ( int i = 0; i < typeRelationshipDesc.length; i++ ) {
      if ( typeRelationshipDesc[i].equalsIgnoreCase( typedesc ) ) {
        return i;
      }
    }
    return TYPE_RELATIONSHIP_UNDEFINED;
  }

  public boolean isUsingTable( BusinessTable table ) {
    if ( table == null ) {
      return false;
    }
    return ( table.equals( table_from ) || table.equals( table_to ) );
  }

  // Swap from and to...
  public void flip() {
    BusinessTable dummy = table_from;
    table_from = table_to;
    table_to = dummy;

    BusinessColumn dum = field_from;
    field_from = field_to;
    field_to = dum;

    switch ( type ) {
      case TYPE_RELATIONSHIP_UNDEFINED:
        break;
      case TYPE_RELATIONSHIP_1_N:
        type = TYPE_RELATIONSHIP_N_1;
        break;
      case TYPE_RELATIONSHIP_N_1:
        type = TYPE_RELATIONSHIP_1_N;
        break;
      case TYPE_RELATIONSHIP_1_1:
        break;
      case TYPE_RELATIONSHIP_0_N:
        type = TYPE_RELATIONSHIP_N_0;
        break;
      case TYPE_RELATIONSHIP_N_0:
        type = TYPE_RELATIONSHIP_0_N;
        break;
      case TYPE_RELATIONSHIP_0_1:
        type = TYPE_RELATIONSHIP_1_0;
        break;
      case TYPE_RELATIONSHIP_1_0:
        type = TYPE_RELATIONSHIP_0_1;
        break;
      case TYPE_RELATIONSHIP_0_0:
        break;
    }
  }

  public String toString() {
    if ( field_from != null && field_to != null ) {
      return table_from.getId() + "." + field_from.getId() + //$NON-NLS-1$
          " - " + //$NON-NLS-1$
          table_to.getId() + "." + field_to.getId(); //$NON-NLS-1$
    } else {
      try {
        return table_from.getId() + " - " + table_to.getId(); //$NON-NLS-1$
      } catch ( Exception e ) {
        return "??????????"; //$NON-NLS-1$
      }
    }
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public boolean equals( Object obj ) {
    RelationshipMeta rel = (RelationshipMeta) obj;

    return rel.table_from.equals( table_from ) && rel.table_to.equals( table_to );
  }

  public void clearChanged() {
    setChanged( false );
  }

  /**
   * @return the joinType : inner, left outer, right outer or full outer
   */
  public int getJoinType() {
    return getJoinType( type );
  }

  /**
   * Calculate the mapping between the relationship type and the join type.
   * 
   * @param relationshipType
   *          the type of relationship
   * @return the join type (inner, left outer, right outer or full outer)
   */
  public static int getJoinType( int relationshipType ) {
    switch ( relationshipType ) {
      case TYPE_RELATIONSHIP_0_N:
        return TYPE_JOIN_LEFT_OUTER;
      case TYPE_RELATIONSHIP_N_0:
        return TYPE_JOIN_RIGHT_OUTER;
      case TYPE_RELATIONSHIP_0_1:
        return TYPE_JOIN_LEFT_OUTER;
      case TYPE_RELATIONSHIP_1_0:
        return TYPE_JOIN_RIGHT_OUTER;
      case TYPE_RELATIONSHIP_0_0:
        return TYPE_JOIN_FULL_OUTER;

      default:
        return TYPE_JOIN_INNER;
    }
  }

  /**
   * Calculate the mapping between the relationship type and the join type.
   * 
   * @param joinType
   *          the type of join
   * @return the relationship type (mapped to the N variations)
   */
  public static int getRelationType( int joinType ) {
    switch ( joinType ) {
      case TYPE_JOIN_LEFT_OUTER:
        return TYPE_RELATIONSHIP_0_N;
      case TYPE_JOIN_RIGHT_OUTER:
        return TYPE_RELATIONSHIP_N_0;
      case TYPE_JOIN_FULL_OUTER:
        return TYPE_RELATIONSHIP_0_0;
      default:
        return TYPE_RELATIONSHIP_N_N;
    }
  }

  public String getJoinTypeDesc() {
    return typeJoinDesc[getJoinType()];
  }

  public boolean isOuterJoin() {
    return getJoinType() != TYPE_JOIN_INNER;
  }

  /**
   * @return the joinOrderKey
   */
  public String getJoinOrderKey() {
    return joinOrderKey;
  }

  /**
   * @param joinOrderKey
   *          the joinOrderKey to set
   */
  public void setJoinOrderKey( String joinOrderKey ) {
    this.joinOrderKey = joinOrderKey;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  public PMSFormula getComplexJoinFormula( BusinessModel model ) throws PentahoMetadataException {
    if ( !isComplex() ) {
      return null;
    }
    return new PMSFormula( model, getComplexJoin(), null );
  }
}
