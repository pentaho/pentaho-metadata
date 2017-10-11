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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.UniqueList;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.LogicalColumn
 */
public class BusinessColumn extends ConceptUtilityBase implements ChangedFlagInterface, ConceptUtilityInterface,
    Cloneable {

  private static final Log logger = LogFactory.getLog( BusinessColumn.class );

  private PhysicalColumn physicalColumn;
  private boolean enabled;

  /** The parent business table to figure out the relationships later on! */
  private BusinessTable businessTable;

  public BusinessColumn() {
    super();
    enabled = true; // enabled by default.
  }

  public BusinessColumn( String id, PhysicalColumn physicalColumn, BusinessTable businessTable ) {
    super( id );
    enabled = true; // enabled by default.
    setBusinessTable( businessTable );
    setPhysicalColumn( physicalColumn );
  }

  public BusinessColumn( String id ) {
    super( id );
    enabled = true; // enabled by default.
  }

  /**
   * @return the description of the model element
   */
  public String getModelElementDescription() {
    return Messages.getString( "BusinessColumn.USER_DESCRIPTION" ); //$NON-NLS-1$
  }

  public Object clone() {
    try {
      BusinessColumn businessColumn = (BusinessColumn) super.clone();

      businessColumn.setConcept( (ConceptInterface) getConcept().clone() ); // deep copy
      businessColumn.setPhysicalColumn( physicalColumn ); // shallow copy

      return businessColumn;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  /**
   * 
   * @param columns
   *          List of columns to compare new column id against
   * @return a new businessColumn, duplicate of this, with only the id changed to be unique in it's list
   */
  public BusinessColumn cloneUnique( String locale, UniqueList columns ) {

    BusinessColumn businessColumn = (BusinessColumn) clone();

    String newId = proposeId( locale, businessTable, physicalColumn, columns );

    try {
      businessColumn.setId( newId );
    } catch ( ObjectAlreadyExistsException e ) {
      logger.error( Messages.getErrorString( "BusinessColumn.ERROR_0005_UNEXPECTED_ID_EXISTS", newId ), e ); //$NON-NLS-1$
      return null;
    }

    return businessColumn;

  }

  public static final String proposeId( String locale, BusinessTable businessTable, PhysicalColumn physicalColumn ) {
    String baseID = Const.toID( businessTable.getDisplayName( locale ) );
    String namePart = Const.toID( Const.NVL( physicalColumn.getName( locale ), physicalColumn.getFormula() ) );
    String id = Settings.getBusinessColumnIDPrefix() + baseID + "_" + namePart; //$NON-NLS-1$
    if ( Settings.isAnIdUppercase() ) {
      id = id.toUpperCase();
    }
    return id;
  }

  public static final String proposeId( String locale, BusinessTable businessTable, PhysicalColumn physicalColumn,
      UniqueList columns ) {
    boolean gotNew = false;
    boolean found = false;
    String id = proposeId( locale, businessTable, physicalColumn );
    int catNr = 1;
    String newId = id;

    while ( !gotNew ) {

      for ( Iterator iter = columns.iterator(); iter.hasNext(); ) {
        ConceptUtilityBase element = (ConceptUtilityBase) iter.next();
        if ( element.getId().equalsIgnoreCase( newId ) ) {
          found = true;
          break;
        }
      }
      if ( found ) {
        catNr++;
        newId = id + "_" + catNr; //$NON-NLS-1$
        found = false;
      } else {
        gotNew = true;
      }
    }

    if ( Settings.isAnIdUppercase() ) {
      newId = newId.toUpperCase();
    }

    return newId;
  }

  public String toString() {
    return businessTable.getId() + "." + getId(); //$NON-NLS-1$
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled
   *          the enabled to set
   */
  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  /**
   * @return the phyiscalColumn
   */
  public PhysicalColumn getPhysicalColumn() {
    return physicalColumn;
  }

  /**
   * @param physicalColumn
   *          the phyiscalColumn to set
   */
  public void setPhysicalColumn( PhysicalColumn physicalColumn ) {
    this.physicalColumn = physicalColumn;
    if ( physicalColumn != null ) {
      getConcept().setInheritedInterface( physicalColumn.getConcept() );
    } else {
      getConcept().setInheritedInterface( null );
    }
  }

  /**
   * @return the businessTable
   */
  public BusinessTable getBusinessTable() {
    return businessTable;
  }

  /**
   * @param businessTable
   *          the businessTable to set
   */
  public void setBusinessTable( BusinessTable businessTable ) {
    this.businessTable = businessTable;
  }

}
