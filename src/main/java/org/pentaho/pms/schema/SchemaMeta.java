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

package org.pentaho.pms.schema;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignment;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColor;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.Domain
 */
public class SchemaMeta {
  private String name;

  public String domainName;

  public UniqueList<DatabaseMeta> databases;

  private UniqueList<PhysicalTable> tables;

  private UniqueList<BusinessModel> businessModels;

  private UniqueList<ConceptInterface> concepts;

  private Locales locales;

  private SecurityReference securityReference;

  private RequiredProperties requiredProperties;

  private BusinessModel activeModel;

  private boolean changed, changedDatabases, changedTables, changedBusinessModels, changedConcepts;

  public SchemaMeta() {
    clear();
  }

  public void clear() {
    databases = new UniqueArrayList<DatabaseMeta>();
    tables = new UniqueArrayList<PhysicalTable>();
    businessModels = new UniqueArrayList<BusinessModel>();
    concepts = new UniqueArrayList<ConceptInterface>();
    locales = new Locales();
    securityReference = new SecurityReference();

    requiredProperties = new RequiredProperties();

    activeModel = null;

    clearChanged();
  }

  public void addDefaults() {

    Concept baseConcept = new Concept( Settings.getConceptNameBase() );
    baseConcept.addProperty( new ConceptPropertyFont( DefaultPropertyID.FONT.getId(), new FontSettings(
        "Arial", 10, false, false ) ) ); //$NON-NLS-1$
    baseConcept
        .addProperty( new ConceptPropertyAlignment( DefaultPropertyID.ALIGNMENT.getId(), AlignmentSettings.LEFT ) );
    baseConcept.addProperty( new ConceptPropertyColor( DefaultPropertyID.COLOR_FG.getId(), ColorSettings.BLACK ) );
    // baseConcept.addProperty(new ConceptPropertyColor(DefaultPropertyID.COLOR_BG.getId(), ColorSettings.WHITE));

    try {
      addConcept( baseConcept );
    } catch ( ObjectAlreadyExistsException e ) {
      // Can't really happen, but hey, throw a runtime for good measure.
      throw new RuntimeException( e );
    }
  }

  public String getName() {
    return name;
  }

  public void setName( String n ) {
    name = n;
  }

  public void addDatabase( DatabaseMeta databaseMeta ) throws ObjectAlreadyExistsException {
    databases.add( databaseMeta );
    changedDatabases = true;
  }

  public void addTable( PhysicalTable ti ) throws ObjectAlreadyExistsException {
    tables.add( ti );
    changedTables = true;
  }

  public void addModel( BusinessModel businessModel ) throws ObjectAlreadyExistsException {
    businessModels.add( businessModel );
    changedBusinessModels = true;
  }

  public void addDatabase( int p, DatabaseMeta databaseMeta ) throws ObjectAlreadyExistsException {
    databases.add( p, databaseMeta );
    changedDatabases = true;
  }

  public void addTable( int p, PhysicalTable ti ) throws ObjectAlreadyExistsException {
    tables.add( p, ti );
    changedTables = true;
  }

  public void addModel( int p, BusinessModel businessModel ) throws ObjectAlreadyExistsException {
    businessModels.add( p, businessModel );
    changedBusinessModels = true;
  }

  public void setChanged() {
    setChanged( true );
  }

  public void setChanged( boolean ch ) {
    changed = ch;
  }

  public DatabaseMeta getDatabase( int i ) {
    return (DatabaseMeta) databases.get( i );
  }

  public PhysicalTable getTable( int i ) {
    return (PhysicalTable) tables.get( i );
  }

  public BusinessModel getModel( int i ) {
    return (BusinessModel) businessModels.get( i );
  }

  public void removeDatabaseMeta( DatabaseMeta databaseMeta ) {
    int i = databases.indexOf( databaseMeta );
    removeDatabaseMeta( i );
  }

  public void removeDatabaseMeta( int i ) {
    if ( i < 0 || i >= databases.size() ) {
      return;
    }
    databases.remove( i );
    changedDatabases = true;
  }

  public void removeTable( int i ) {
    if ( i < 0 || i >= tables.size() ) {
      return;
    }

    tables.remove( i );
    changedTables = true;
  }

  public void removeBusinessModel( BusinessModel businessModel ) {
    int i = businessModels.indexOf( businessModel );
    removeBusinessModel( i );
  }

  public void removeBusinessModel( int i ) {
    if ( i < 0 || i >= businessModels.size() ) {
      return;
    }

    businessModels.remove( i );
    changedBusinessModels = true;
  }

  public int nrDatabases() {
    return databases.size();
  }

  public int nrTables() {
    return tables.size();
  }

  public int nrBusinessModels() {
    return businessModels.size();
  }

  public boolean haveDatabasesChanged() {
    if ( changedDatabases ) {
      return true;
    }

    for ( int i = 0; i < nrDatabases(); i++ ) {
      DatabaseMeta ci = getDatabase( i );
      if ( ci.hasChanged() ) {
        return true;
      }
    }
    return false;
  }

  public boolean haveTablesChanged() {
    if ( changedTables ) {
      return true;
    }

    for ( int i = 0; i < nrTables(); i++ ) {
      PhysicalTable ti = getTable( i );
      if ( ti.hasChanged() ) {
        return true;
      }
      for ( int ix = 0; ix < ti.nrPhysicalColumns(); ix++ ) {
        PhysicalColumn tc = ti.getPhysicalColumn( ix );
        if ( tc.hasChanged() ) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean haveBusinessModelsChanged() {
    for ( int i = 0; i < nrBusinessModels(); i++ ) {
      BusinessModel businessModel = getModel( i );
      if ( businessModel.hasChanged() ) {
        return true;
      }
    }
    return changedBusinessModels;
  }

  public boolean haveConceptsChanged() {
    if ( changedConcepts ) {
      return true;
    }

    for ( int i = 0; i < nrConcepts(); i++ ) {
      ConceptInterface concept = getConcept( i );
      if ( concept.hasChanged() ) {
        return true;
      }
    }
    return false;
  }

  public boolean hasChanged() {
    if ( haveDatabasesChanged() ) {
      return true;
    }
    if ( haveTablesChanged() ) {
      return true;
    }
    if ( haveBusinessModelsChanged() ) {
      return true;
    }
    if ( haveConceptsChanged() ) {
      return true;
    }
    if ( locales.hasChanged() ) {
      return true;
    }
    if ( securityReference.getSecurityService().hasChanged() ) {
      return true;
    }

    return changed;
  }

  public void clearChanged() {
    changed = false;
    changedDatabases = false;
    changedTables = false;
    changedBusinessModels = false;
    changedConcepts = false;

    for ( int i = 0; i < nrDatabases(); i++ ) {
      getDatabase( i ).setChanged( false );
    }
    for ( int i = 0; i < nrTables(); i++ ) {
      getTable( i ).clearChanged();
    }
    for ( int i = 0; i < nrBusinessModels(); i++ ) {
      getModel( i ).clearChanged();
    }
    for ( int i = 0; i < nrConcepts(); i++ ) {
      getConcept( i ).clearChanged();
    }
    locales.clearChanged();
    securityReference.getSecurityService().setChanged( false );
  }

  public DatabaseMeta findDatabase( String dbName ) {
    int i;
    for ( i = 0; i < nrDatabases(); i++ ) {
      DatabaseMeta ci = getDatabase( i );
      if ( ci.getName().equalsIgnoreCase( dbName ) ) {
        return ci;
      }
    }
    return null;
  }

  /**
   * Search for a physical table on ID
   * 
   * @param id
   *          the id to look out for
   * @return the physical table or null if nothing was found
   */
  public PhysicalTable findPhysicalTable( String id ) {
    return findPhysicalTable( id, (PhysicalTable) null );
  }

  /**
   * Search for a physical table, excluding a certain given one.
   * 
   * @param id
   *          the id
   * @param exclude
   *          the table to exclude from the search
   * @return the physical table or null if nothing was found.
   */
  public PhysicalTable findPhysicalTable( String id, PhysicalTable exclude ) {
    int i;
    int excl = -1;
    if ( exclude != null ) {
      excl = indexOfTable( exclude );
    }

    // This is slow!
    for ( i = 0; i < nrTables(); i++ ) {
      PhysicalTable ti = getTable( i );
      if ( i != excl && ti.getId().equalsIgnoreCase( id ) ) {
        return ti;
      }
    }
    return null;
  }

  /**
   * Find a physical table name in a certain locale. If nothing was found, search the table on ID.
   * 
   * @param locale
   *          the locale to search in
   * @param nameToFind
   *          the name to look for
   * @return the physical table or null if nothing was found.
   */
  public PhysicalTable findPhysicalTable( String locale, String nameToFind ) {
    for ( int i = 0; i < nrTables(); i++ ) {
      PhysicalTable physicalTable = getTable( i );
      if ( nameToFind.equalsIgnoreCase( physicalTable.getConcept().getName( locale ) ) ) {
        return physicalTable;
      }
    }
    return findPhysicalTable( nameToFind );
  }

  public int indexOfDatabase( DatabaseMeta ci ) {
    return databases.indexOf( ci );
  }

  public int indexOfTable( PhysicalTable ti ) {
    return tables.indexOf( ti );
  }

  public int indexOfBusinessModel( BusinessModel model ) {
    return businessModels.indexOf( model );
  }

  public BusinessModel findModel( String id ) {
    for ( int i = 0; i < nrBusinessModels(); i++ ) {
      BusinessModel businessModel = getModel( i );
      if ( businessModel.getId().equals( id ) ) {
        return businessModel;
      }
    }
    return null;
  }

  /**
   * Search a business model based on the localized name or if that is not found, the ID
   * 
   * @param nameToFind
   *          The name (or ID) to search for
   * @param locale
   *          The locale in which we want to search
   * @return The business model or null if nothing could be found.
   */
  public BusinessModel findModel( String locale, String nameToFind ) {
    for ( int i = 0; i < nrBusinessModels(); i++ ) {
      BusinessModel businessModel = getModel( i );

      String locName = businessModel.getConcept().getName( locale );
      if ( ( locName != null && locName.equals( nameToFind ) ) || businessModel.getId().equals( nameToFind ) ) {
        return businessModel;
      }
    }
    return null;
  }

  /**
   * Search for a physical column in a table
   * 
   * @param tableId
   *          the id of the table
   * @param columnId
   *          the id of the column
   * @return the physical column or null if nothing could be found
   */
  public PhysicalColumn findPhysicalColumn( String tableId, String columnId ) {
    PhysicalTable ti = findPhysicalTable( tableId );
    if ( ti != null ) {
      return ti.findPhysicalColumn( columnId );
    }
    return null;
  }

  public String toString() {
    return this.getClass().getName();
  }

  public UniqueList<DatabaseMeta> getDatabases() {
    return databases;
  }

  public UniqueList<PhysicalTable> getTables() {
    return tables;
  }

  public PhysicalTable[] getTablesOnDatabase( DatabaseMeta databaseMeta ) {
    List<PhysicalTable> allTables = new ArrayList<PhysicalTable>();
    for ( int i = 0; i < nrTables(); i++ ) {
      PhysicalTable table = getTable( i );
      if ( ( table.getDatabaseMeta() != null ) && ( table.getDatabaseMeta().equals( databaseMeta ) ) ) {
        allTables.add( table );
      }
    }
    return (PhysicalTable[]) allTables.toArray( new PhysicalTable[allTables.size()] );
  }

  /**
   * @return the activeModel
   */
  public BusinessModel getActiveModel() {
    return activeModel;
  }

  /**
   * @param activeModel
   *          the activeModel to set
   */
  public void setActiveModel( BusinessModel activeModel ) {
    this.activeModel = activeModel;
  }

  public UniqueList<BusinessModel> getBusinessModels() {
    return businessModels;
  }

  public String[] getBusinessModelIDs() {
    String[] ids = new String[businessModels.size()];
    for ( int i = 0; i < businessModels.size(); i++ ) {
      ids[i] = ( (BusinessModel) businessModels.get( i ) ).getId();
    }
    return ids;
  }

  public String[] getBusinessModelNames( String locale ) {
    String[] names = new String[businessModels.size()];
    for ( int i = 0; i < businessModels.size(); i++ ) {
      names[i] = ( (BusinessModel) businessModels.get( i ) ).getDisplayName( locale );
    }
    return names;
  }

  /**
   * @return the concepts
   */
  public UniqueList getConcepts() {
    return concepts;
  }

  public int nrConcepts() {
    return concepts.size();
  }

  public ConceptInterface getConcept( int i ) {
    return (ConceptInterface) concepts.get( i );
  }

  public void removeConcept( int i ) {
    concepts.remove( i );
    changedConcepts = true;
  }

  public void addConcept( ConceptInterface concept ) throws ObjectAlreadyExistsException {
    concepts.add( concept );
    changedConcepts = true;
  }

  public void addConcept( int index, ConceptInterface concept ) throws ObjectAlreadyExistsException {
    concepts.add( index, concept );
    changedConcepts = true;
  }

  public int indexOfConcept( ConceptInterface concept ) {
    return concepts.indexOf( concept );
  }

  public void setConcept( int idx, ConceptInterface concept ) throws ObjectAlreadyExistsException {
    concepts.set( idx, concept );
    changedConcepts = true;
  }

  /**
   * Find a concept using the path from root over parents to concept
   * 
   * @param path
   *          the path from root --> ... --> gp --> parent --> concept
   * @return the concept if one could be found or null if nothing was found.
   */
  public ConceptInterface findConcept( String[] path ) {
    for ( int i = 0; i < nrConcepts(); i++ ) {
      ConceptInterface concept = getConcept( i );
      if ( concept.matches( path ) ) {
        return concept;
      }
    }
    return null;
  }

  /**
   * Search for all the locale that are used in the schema
   * 
   * @return all the locale that are used in the schema
   */
  public String[] getUsedLocale() {
    Map<String, String> allLocales = new Hashtable<String, String>();
    for ( int i = 0; i < nrBusinessModels(); i++ ) {
      String[] usedLocale = getModel( i ).getConcept().getUsedLocale();
      for ( int j = 0; j < usedLocale.length; j++ ) {
        allLocales.put( usedLocale[i], "" ); //$NON-NLS-1$
      }
    }

    Set<String> keySet = allLocales.keySet();
    return (String[]) keySet.toArray( new String[keySet.size()] );
  }

  /**
   * Search for a concept with a certain name
   * 
   * @param conceptName
   *          the name of the concept to look for
   * @return the concept or null if nothing could be found
   */
  public ConceptInterface findConcept( String conceptName ) {
    for ( int i = 0; i < nrConcepts(); i++ ) {
      ConceptInterface concept = getConcept( i );
      if ( concept.getName().equals( conceptName ) ) {
        return concept;
      }
    }
    return null;
  }

  public RequiredProperties getDefaultProperties() {
    return requiredProperties;
  }

  public void setDefaultProperties( RequiredProperties requiredProperties ) {
    this.requiredProperties = requiredProperties;
  }

  /**
   * @param locale
   *          the locale to present the names in.
   * @return an array of the defined physical table names
   * 
   */
  public String[] getTableNames( String locale ) {
    String[] names = new String[nrTables()];
    for ( int i = 0; i < nrTables(); i++ ) {
      names[i] = getTable( i ).getDisplayName( locale );
    }
    return names;
  }

  public String getActiveLocale() {
    return locales.getActiveLocale();
  }

  public void setActiveLocale( String selectedLocale ) {
    locales.setActiveLocale( selectedLocale );
  }

  /**
   * @return the locales
   */
  public Locales getLocales() {
    return locales;
  }

  /**
   * @param locales
   *          the locales to set
   */
  public void setLocales( Locales locales ) {
    this.locales = locales;
  }

  /**
   * @return the domainName
   */
  public String getDomainName() {
    return domainName;
  }

  /**
   * @param domainName
   *          the domainName to set
   */
  public void setDomainName( String domainName ) {
    this.domainName = domainName;
  }

  /**
   * @return the securityReference
   */
  public SecurityReference getSecurityReference() {
    return securityReference;
  }

  /**
   * @param securityReference
   *          the securityReference to set
   */
  public void setSecurityReference( SecurityReference securityReference ) {
    this.securityReference = securityReference;
  }

  /**
   * @return a list of all used conceptUtilityInterfaces
   */
  public List<ConceptUtilityInterface> getConceptUtilityInterfaces() {
    List<ConceptUtilityInterface> list = new ArrayList<ConceptUtilityInterface>();

    // The physical tables
    for ( int i = 0; i < nrTables(); i++ ) {
      list.add( getTable( i ) );
    }

    // The business businessModels
    for ( int v = 0; v < nrBusinessModels(); v++ ) {
      BusinessModel businessModel = getModel( v );
      list.add( businessModel );
      // Business tables
      for ( int t = 0; t < businessModel.nrBusinessTables(); t++ ) {
        BusinessTable table = businessModel.getBusinessTable( t );
        list.add( table );
        for ( int c = 0; c < table.nrBusinessColumns(); c++ ) {
          BusinessColumn column = table.getBusinessColumn( c );
          list.add( column );
        }
      }
      // Business categories: todo: make it recursive too.
      for ( int c = 0; c < businessModel.getRootCategory().nrBusinessCategories(); c++ ) {
        list.add( businessModel.getRootCategory().getBusinessCategory( c ) );
      }
    }

    return list;
  }

  public String[] getConceptNames() {
    String[] conceptNames = new String[nrConcepts()];
    for ( int i = 0; i < conceptNames.length; i++ ) {
      conceptNames[i] = getConcept( i ).getName();
    }
    Const.sortStrings( conceptNames );
    return conceptNames;
  }

}
