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
import org.pentaho.metadata.model.concept.types.LocaleType;

/**
 * The domain object is the root object of a metadata domain. A domain may consist of multiple physical and logical
 * models. Each domain is normally stored in a separate file for serialization.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Domain extends Concept {

  private static final long serialVersionUID = -9093116797722021640L;

  public static final String LOCALES_PROPERTY = "locales"; //$NON-NLS-1$

  public Domain() {
    super();
  }

  private List<IPhysicalModel> physicalModels = new ArrayList<IPhysicalModel>();
  private List<LogicalModel> logicalModels = new ArrayList<LogicalModel>();
  private List<Concept> concepts = new ArrayList<Concept>();

  /**
   * the domain is the root of uniqueness for all children, it does not maintain it's own id.
   * 
   * @return null
   */
  @Override
  public List<String> getUniqueId() {
    // there is only one domain, so id is null.
    return null;
  }

  /**
   * the domain does not have a parent.
   */
  @Override
  public IConcept getParent() {
    return null;
  }

  @Override
  public List<IConcept> getChildren() {
    ArrayList<IConcept> children = new ArrayList<IConcept>();
    children.addAll( physicalModels );
    children.addAll( logicalModels );
    children.addAll( concepts );
    return children;
  }

  /**
   * The physical models stored in this domain.
   * 
   * @return physical models
   */
  public List<IPhysicalModel> getPhysicalModels() {
    return physicalModels;
  }

  public void setPhysicalModels( List<IPhysicalModel> physicalModels ) {
    this.physicalModels = physicalModels;
  }

  public void addPhysicalModel( IPhysicalModel physicalModel ) {
    physicalModels.add( physicalModel );
  }

  /**
   * The logical models stored in this domain.
   * 
   * @return logical models
   */
  public List<LogicalModel> getLogicalModels() {
    return logicalModels;
  }

  public void setLogicalModels( List<LogicalModel> logicalModels ) {
    this.logicalModels = logicalModels;
  }

  public void addLogicalModel( LogicalModel logicalModel ) {
    logicalModels.add( logicalModel );
  }

  /**
   * the list of root concepts available for inheritance within this domain.
   * 
   * @return concepts
   */
  public List<Concept> getConcepts() {
    return concepts;
  }

  public void setConcepts( List<Concept> concepts ) {
    this.concepts = concepts;
  }

  public void addConcept( Concept concept ) {
    concepts.add( concept );
  }

  public void setLocales( List<LocaleType> locales ) {
    setProperty( LOCALES_PROPERTY, new Property<List<LocaleType>>(locales) );
  }

  /**
   * return a list of supported locales for this domain.
   * 
   * @return supported locales
   */
  @SuppressWarnings( "unchecked" )
  public List<LocaleType> getLocales() {
    Property property = getProperty( LOCALES_PROPERTY );
    if( property != null ) {
      return (List<LocaleType>) property.getValue();
    }
    return null;
  }

  /**
   * Returns a string array of locale codes
   * 
   * @return locale codes
   */
  public String[] getLocaleCodes() {
    if ( getLocales() == null ) {
      return null;
    }
    String[] locales = new String[getLocales().size()];
    for ( int i = 0; i < getLocales().size(); i++ ) {
      locales[i] = getLocales().get( i ).getCode();
    }
    return locales;
  }

  public void addLocale( LocaleType locale ) {
    getLocales().add( locale );
  }

  // utility methods

  /**
   * find a logical model via a model id
   * 
   * @param modelId
   *          the id to find
   * 
   * @return logical model
   */
  public LogicalModel findLogicalModel( String modelId ) {
    for ( LogicalModel model : getLogicalModels() ) {
      if ( modelId.equals( model.getId() ) ) {
        return model;
      }
    }
    return null;
  }

  /**
   * find a physical table in the domain
   * 
   * @param tableId
   *          the table id
   * @return physical table
   */
  public IPhysicalTable findPhysicalTable( String tableId ) {
    for ( IPhysicalModel model : getPhysicalModels() ) {
      for ( IPhysicalTable table : model.getPhysicalTables() ) {
        if ( tableId.equals( table.getId() ) ) {
          return table;
        }
      }
    }
    return null;
  }

  /**
   * find a physical table in the domain
   * 
   * @param tableId
   *          the table id
   * @return physical table
   */
  public IPhysicalModel findPhysicalModel( String modelId ) {
    for ( IPhysicalModel model : getPhysicalModels() ) {
      if ( modelId.equals( model.getId() ) ) {
        return model;
      }
    }
    return null;
  }

  /**
   * find a physical table in the domain
   * 
   * @param tableId
   *          the table id
   * @return physical table
   */
  public Concept findConcept( String conceptId ) {
    for ( Concept concept : getConcepts() ) {
      if ( conceptId.equals( concept.getId() ) ) {
        return concept;
      }
    }
    return null;
  }

  @Override
  public Object clone() {
    Domain clone = new Domain();
    // shallow copies
    clone( clone );
    clone.physicalModels = physicalModels;
    clone.concepts = concepts;

    // deep copies
    clone.setLogicalModels( new ArrayList<LogicalModel>() );
    for ( LogicalModel model : getLogicalModels() ) {
      clone.addLogicalModel( (LogicalModel) model.clone() );
    }
    return clone;
  }
}
