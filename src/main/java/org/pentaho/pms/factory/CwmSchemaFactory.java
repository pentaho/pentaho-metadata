// CHECKSTYLE:FileLength:OFF
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
 * Copyright (c) 2006 - 2018 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.CwmEvent;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.CwmParameter;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmAttribute;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmClass;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmModelElement;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmTaggedValue;
import org.pentaho.pms.cwm.pentaho.meta.instance.CwmExtent;
import org.pentaho.pms.cwm.pentaho.meta.keysindexes.CwmKeyRelationship;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimension;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimensionedObject;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmSchema;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmCube;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmCubeDimensionAssociation;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmHierarchyLevelAssociation;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmLevel;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmLevelBasedHierarchy;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmMeasure;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmCatalog;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmColumn;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmTable;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.locale.LocaleMeta;
import org.pentaho.pms.locale.Locales;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.Concept;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationList;
import org.pentaho.pms.schema.concept.types.alignment.AlignmentSettings;
import org.pentaho.pms.schema.concept.types.alignment.ConceptPropertyAlignment;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.color.ColorSettings;
import org.pentaho.pms.schema.concept.types.color.ConceptPropertyColor;
import org.pentaho.pms.schema.concept.types.columnwidth.ColumnWidth;
import org.pentaho.pms.schema.concept.types.columnwidth.ConceptPropertyColumnWidth;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.date.ConceptPropertyDate;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.font.ConceptPropertyFont;
import org.pentaho.pms.schema.concept.types.font.FontSettings;
import org.pentaho.pms.schema.concept.types.localstring.LocalizedStringSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.concept.types.timestamp.ConceptPropertyTimestamp;
import org.pentaho.pms.schema.concept.types.url.ConceptPropertyURL;
import org.pentaho.pms.schema.olap.OlapCube;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.schema.olap.OlapDimensionUsage;
import org.pentaho.pms.schema.olap.OlapHierarchy;
import org.pentaho.pms.schema.olap.OlapHierarchyLevel;
import org.pentaho.pms.schema.olap.OlapMeasure;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.schema.security.SecurityReference;
import org.pentaho.pms.schema.security.SecurityService;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for converting between the Schema metadata and the CWM
 *
 * @author Matt
 */
@SuppressWarnings( "deprecation" )
public class CwmSchemaFactory implements CwmSchemaFactoryInterface {

  private static final Log logger = LogFactory.getLog( CwmSchemaFactory.class );

  public CwmSchemaFactory() {

  }

  /*
   * ____ _ __ __ _ / ___| ___| |__ ___ _ __ ___ __ _| \/ | ___| |_ __ _ \___ \ / __| '_ \ / _ \ '_ ` _ \ / _` | |\/| |/
   * _ \ __/ _` | ___) | (__| | | | __/ | | | | | (_| | | | | __/ || (_| | |____/ \___|_| |_|\___|_| |_| |_|\__,_|_|
   * |_|\___|\__\__,_|
   */

  /**
   * This method allows you to store a complete schema (model) into the CWM using the MDR
   *
   * @param cwm        The model to use
   * @param schemaMeta The meta-data to store into the cwm model
   */
  public void storeSchemaMeta( CWM cwm, SchemaMeta schemaMeta ) throws Exception {
    this.storeSchemaMeta( cwm, schemaMeta, null );
  }

  /**
   * This method allows you to store a complete schema (model) into the CWM using the MDR
   *
   * @param cwm        The model to use
   * @param schemaMeta The meta-data to store into the cwm model
   */
  public void storeSchemaMeta( CWM cwm, SchemaMeta schemaMeta, ProgressMonitorListener monitor ) throws Exception {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    try {
      cwm.beginTransaction();

      // Save the security service information
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_SECURITY_INFO" ) ); //$NON-NLS-1$
      }
      storeSecurityService( cwm, schemaMeta.getSecurityReference().getSecurityService() );
      if ( monitor != null ) {
        monitor.worked( 1 );
      }

      // Save the databases first...
      //
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_DATABASES" ) ); //$NON-NLS-1$
      }
      for ( int i = 0; i < schemaMeta.nrDatabases(); i++ ) {
        DatabaseMeta databaseMeta = schemaMeta.getDatabase( i );
        storeDatabaseMeta( cwm, databaseMeta );
        if ( monitor != null ) {
          monitor.worked( 1 );
        }
      }

      // Save the concepts early as well...
      //
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_CONCEPTS" ) ); //$NON-NLS-1$
      }
      for ( int i = 0; i < schemaMeta.nrConcepts(); i++ ) {
        ConceptInterface concept = schemaMeta.getConcept( i );
        storeModelConcept( cwm, concept );
        if ( monitor != null ) {
          monitor.worked( 1 );
        }
      }

      // Now save the physical tables...
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_PHYSICAL_TABLES" ) ); //$NON-NLS-1$
      }
      for ( int i = 0; i < schemaMeta.nrTables(); i++ ) {
        PhysicalTable physicalTable = schemaMeta.getTable( i );
        storePhysicalTable( cwm, physicalTable );
        if ( monitor != null ) {
          monitor.worked( 1 );
        }
      }

      // Save the business models...
      //
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_BUSINESS_MODELS" ) ); //$NON-NLS-1$
      }
      for ( int i = 0; i < schemaMeta.nrBusinessModels(); i++ ) {
        BusinessModel businessModel = schemaMeta.getModel( i );
        storeBusinessModel( cwm, businessModel );
        if ( monitor != null ) {
          monitor.worked( 1 );
        }
      }

      // Save the defined locales...
      //
      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_STORING_LOCALES" ) ); //$NON-NLS-1$
      }
      Locales locales = schemaMeta.getLocales();
      for ( int i = 0; i < locales.nrLocales(); i++ ) {
        LocaleInterface locale = locales.getLocale( i );
        storeLocale( cwm, locale );
        if ( monitor != null ) {
          monitor.worked( 1 );
        }
      }

      cwm.endTransaction();

      if ( monitor != null ) {
        monitor.subTask( Messages.getString( "CwmSchemaFactory.INFO_FINISHED" ) ); //$NON-NLS-1$
      }
    } catch ( Exception e ) {
      cwm.rollback();
      throw e;
    }
  }

  /**
   * Load schema from a CWM model
   *
   * @param cwm The model to load
   * @return a newly created SchemaMeta object.
   */
  public SchemaMeta getSchemaMeta( CWM cwm ) {
    SchemaMeta schemaMeta = new SchemaMeta();

    SecurityService securityService = getSecurityService( cwm );

    // Load the security reference information from the server each time we run...
    SecurityReference securityReference;
    try {
      securityReference = new SecurityReference( securityService );
    } catch ( Exception e ) {
      securityReference = new SecurityReference();
      securityReference.setSecurityService( securityService );
    }

    schemaMeta.setSecurityReference( securityReference );

    // Set a sane default on the model name
    schemaMeta.setDomainName( cwm.getDomainName() );

    // Read all the database connections...
    //
    CwmCatalog[] catalogs = cwm.getCatalogs();
    for ( int i = 0; i < catalogs.length; i++ ) {
      CwmCatalog catalog = catalogs[ i ];

      DatabaseMeta databaseMeta = getDatabaseMeta( cwm, catalog );
      try {
        schemaMeta.addDatabase( databaseMeta );
      } catch ( ObjectAlreadyExistsException e ) {
        // Ignore the duplicates for now.
        // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
        // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
      }

    }

    // Read the model Concepts.
    // These are used as parents all over the place, make sure we load these early in the game
    //
    CwmClass[] cwmClasses = cwm.getClasses();

    // We actually load the concepts twice: once to load the data and once to set the parents.
    // The problem is that I'm not sure that the order in which they are loaded/saved is guaranteed.
    // It's a Quick&Dirty solution yes, but it should work.
    for ( int repeat = 0; repeat < 2; repeat++ ) {
      for ( int i = 0; i < cwmClasses.length; i++ ) {
        CwmClass cwmClass = cwmClasses[ i ];

        ConceptInterface concept = getModelConcept( cwm, cwmClass, schemaMeta );

        // See if we already have this one.
        ConceptInterface verify = schemaMeta.findConcept( concept.getName() );
        if ( verify == null ) {
          // add this one...
          try {
            schemaMeta.addConcept( concept );
          } catch ( ObjectAlreadyExistsException e ) {
            // Ignore the duplicates for now.
            // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
            // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
          }
        } else {
          // the concept is already in there, we are loading for the second time.
          // If we have a parent now, it should be referenced OK.
          verify.setParentInterface( concept.getParentInterface() );
        }
      }
    }

    // find all available tables and add them to the schema-metadata model...
    //
    CwmTable[] cwmTables = cwm.getTables();
    for ( int i = 0; i < cwmTables.length; i++ ) {
      CwmTable cwmTable = cwmTables[ i ];

      PhysicalTable physicalTable = getPhysicalTable( cwm, cwmTable, schemaMeta );
      try {
        schemaMeta.addTable( physicalTable );
      } catch ( ObjectAlreadyExistsException e ) {
        // Ignore the duplicates for now.
        // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
        // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
      }
    }

    // load all the business models
    //
    CwmSchema[] cwmSchemas = cwm.getSchemas();
    for ( int i = 0; i < cwmSchemas.length; i++ ) {
      CwmSchema cwmSchema = cwmSchemas[ i ];

      BusinessModel businessModel = getBusinessModel( cwm, cwmSchema, schemaMeta );
      if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, businessModel ) ) {
        try {
          schemaMeta.addModel( businessModel );
        } catch ( ObjectAlreadyExistsException e ) {
          // Ignore the duplicates for now.
          // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
          // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
        }
      }
    }

    // Load the locales
    //
    CwmParameter[] cwmParameters = cwm.getParameters();
    Locales locales = schemaMeta.getLocales();
    locales.getLocaleList().clear(); // remove any defined defaults...
    for ( int i = 0; i < cwmParameters.length; i++ ) {
      CwmParameter cwmParameter = cwmParameters[ i ];
      LocaleInterface locale = getLocale( cwm, cwmParameter );
      locales.addLocale( locale );
    }
    locales.sortLocales();

    // Clear the changed flag all over the place...
    //
    schemaMeta.clearChanged();

    // Select the active business model if there is only one
    if ( schemaMeta.nrBusinessModels() == 1 ) {
      schemaMeta.setActiveModel( schemaMeta.getModel( 0 ) );
    }

    if ( cwm.isReversingOrder() ) {
      Collections.reverse( schemaMeta.getTables().getList() );
      Collections.reverse( schemaMeta.getBusinessModels().getList() );
      Collections.reverse( schemaMeta.getConcepts().getList() );
    }

    return schemaMeta;
  }

  /*
   * ____ _ _ ____ _ / ___| ___ ___ _ _ _ __(_) |_ _ _ / ___| ___ _ ____ _(_) ___ ___ \___ \ / _ \/ __| | | | '__| | __|
   * | | | \___ \ / _ \ '__\ \ / / |/ __/ _ \ ___) | __/ (__| |_| | | | | |_| |_| | ___) | __/ | \ V /| | (_| __/ |____/
   * \___|\___|\__,_|_| |_|\__|\__, | |____/ \___|_| \_/ |_|\___\___| |___/
   */

  /**
   * Store the security service parameters in the CWM model
   *
   * @param cwm             The CWM model to store in
   * @param securityService The security service to store
   */
  public void storeSecurityService( CWM cwm, SecurityService securityService ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmEvent cwmEvent = cwm.createEvent( CWM.EVENT_SECURITY_SERVICE );

    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_SERVICE_URL, securityService.getServiceURL() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_DETAILS_NAME, securityService.getServiceName() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_DETAIL_NAME, securityService.getDetailNameParameter() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_DETAIL_TYPE, securityService.getServiceTypeDesc() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_USERNAME, securityService.getUsername() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_PASSWORD, securityService.getPassword() );

    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_PROXY_HOST, securityService.getProxyHostname() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_PROXY_PORT, securityService.getProxyPort() );
    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_NON_PROXY_HOSTS, securityService.getNonProxyHosts() );

    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_FILENAME, securityService.getFilename() );

    cwm.addTaggedValue( cwmEvent, CWM.TAG_SECURITY_URL, securityService.getURL( null ) );
  }

  /**
   * Load the security service configuration information from the CWM model
   *
   * @param cwm The CWM model
   * @return a new security service object
   */
  public SecurityService getSecurityService( CWM cwm ) {
    SecurityService securityService = new SecurityService();

    CwmEvent cwmEvent = cwm.getFirstEventWithName( CWM.EVENT_SECURITY_SERVICE ); // there is one or none
    if ( cwmEvent != null ) {
      securityService.setServiceURL( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_SERVICE_URL ) );
      securityService.setServiceName( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_DETAILS_NAME ) );
      securityService.setDetailNameParameter( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_DETAIL_NAME ) );
      securityService.setDetailServiceType( SecurityService.getServiceType( cwm.getFirstTaggedValue( cwmEvent,
        CWM.TAG_SECURITY_DETAIL_TYPE ) ) );
      securityService.setUsername( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_USERNAME ) );
      securityService.setPassword( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_PASSWORD ) );

      securityService.setProxyHostname( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_PROXY_HOST ) );
      securityService.setProxyPort( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_PROXY_PORT ) );
      securityService.setNonProxyHosts( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_NON_PROXY_HOSTS ) );

      securityService.setFilename( cwm.getFirstTaggedValue( cwmEvent, CWM.TAG_SECURITY_FILENAME ) );
    }
    return securityService;
  }

  /*
   * ____ _ _ _ _____ _ _ | _ \| |__ _ _ ___(_) ___ __ _| |_ _|_ _| |__ | | ___ | |_) | '_ \| | | / __| |/ __/ _` | | |
   * |/ _` | '_ \| |/ _ \ | __/| | | | |_| \__ \ | (_| (_| | | | | (_| | |_) | | __/ |_| |_| |_|\__, |___/_|\___\__,_|_|
   * |_|\__,_|_.__/|_|\___| |___/
   */

  /**
   * Stores the Kettle table metadata into the CWM model
   *
   * @param cwm           The CWM model
   * @param physicalTable the Kettle table metadata to store
   * @return the created CwmTable
   */
  public CwmTable storePhysicalTable( CWM cwm, PhysicalTable physicalTable ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmTable cwmTable = cwm.createTable( physicalTable.getId() );

    // Store the concept properties
    storeConceptProperties( cwm, cwmTable, physicalTable.getConcept() );

    // Database : target database
    if ( physicalTable.getDatabaseMeta() != null ) {
      cwm.addTaggedValue( cwmTable, CWM.TAG_TABLE_TARGET_DATABASE, physicalTable.getDatabaseMeta().getName() );
    }

    for ( int i = 0; i < physicalTable.nrPhysicalColumns(); i++ ) {
      PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn( i );
      storePhysicalColumn( cwm, cwmTable, physicalColumn );
    }

    return cwmTable;
  }

  /**
   * Load a physical table from a CWM model using a cwmTable reference and a list of available databases.
   *
   * @param cwm
   * @param cwmTable
   * @param schemaMeta
   * @return a new PhysicalTable object
   */
  public PhysicalTable getPhysicalTable( CWM cwm, CwmTable cwmTable, SchemaMeta schemaMeta ) {
    // Set the table name
    //
    PhysicalTable physicalTable = new PhysicalTable( cwmTable.getName() );
    physicalTable.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( schemaMeta.getTables() ) );

    // Link the database connection info...
    //
    String databaseName = cwm.getFirstTaggedValue( cwmTable, CWM.TAG_TABLE_TARGET_DATABASE );
    physicalTable.setDatabaseMeta( schemaMeta.findDatabase( databaseName ) );

    // load the concept properties
    getConceptProperties( cwm, cwmTable, physicalTable.getConcept(), schemaMeta );

    // Load the column data...
    //
    @SuppressWarnings( "unchecked" )
    Collection<CwmColumn> columnsCollection = cwmTable.getOwnedElement();
    if ( columnsCollection != null ) {
      CwmColumn[] columns = (CwmColumn[]) columnsCollection.toArray( new CwmColumn[ columnsCollection.size() ] );
      for ( int c = 0; c < columns.length; c++ ) {
        CwmColumn column = columns[ c ];

        PhysicalColumn physicalColumn = getPhysicalColumn( cwm, column, physicalTable, schemaMeta );

        try {
          physicalTable.addPhysicalColumn( physicalColumn );
        } catch ( ObjectAlreadyExistsException e ) {
          // Ignore the duplicates for now.
          // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
          // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
        }
      }
    }

    if ( cwm.isReversingOrder() ) {
      Collections.reverse( physicalTable.getPhysicalColumns().getList() );
    }

    return physicalTable;
  }

  /*
   * ____ _ _ _ ____ _ | _ \| |__ _ _ ___(_) ___ __ _| |/ ___|___ | |_ _ _ __ ___ _ __ | |_) | '_ \| | | / __| |/ __/ _`
   * | | | / _ \| | | | | '_ ` _ \| '_ \ | __/| | | | |_| \__ \ | (_| (_| | | |__| (_) | | |_| | | | | | | | | | |_| |_|
   * |_|\__, |___/_|\___\__,_|_|\____\___/|_|\__,_|_| |_| |_|_| |_| |___/
   */

  /**
   * Store a physical column into the CWM metamodel
   *
   * @param cwm            The model to store in
   * @param cwmTable       The parent table
   * @param physicalColumn the physical column to store
   */
  public void storePhysicalColumn( CWM cwm, CwmTable cwmTable, PhysicalColumn physicalColumn ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmColumn column = cwm.createColumn( physicalColumn.getId() );

    // Store all properties...
    storeConceptProperties( cwm, column, physicalColumn.getConcept() );

    @SuppressWarnings( "unchecked" )
    Collection<CwmColumn> c = cwmTable.getOwnedElement();
    c.add( column );
  }

  /**
   * Load a physical column from the CWM metamodel
   *
   * @param cwm           the model to load from
   * @param column        The CwmColumn to load the information from
   * @param physicalTable The physcial table to reference
   * @param schemaMeta    The schema
   * @return a new created Physical column, loaded from the cwm metamodel
   */
  public PhysicalColumn getPhysicalColumn( CWM cwm, CwmColumn column, PhysicalTable physicalTable,
                                           SchemaMeta schemaMeta ) {
    PhysicalColumn physicalColumn = new PhysicalColumn( column.getName() );
    physicalColumn.addIDChangedListener( ConceptUtilityBase
      .createIDChangedListener( physicalTable.getPhysicalColumns() ) );

    // load all properties...
    getConceptProperties( cwm, column, physicalColumn.getConcept(), schemaMeta );

    // Finally, set a reference up to the parent table.
    physicalColumn.setTable( physicalTable );

    return physicalColumn;
  }

  /*
   * ____ _ _ __ __ _ | _ \ __ _| |_ __ _| |__ __ _ ___ ___| \/ | ___| |_ __ _ | | | |/ _` | __/ _` | '_ \ / _` / __|/ _
   * \ |\/| |/ _ \ __/ _` | | |_| | (_| | || (_| | |_) | (_| \__ \ __/ | | | __/ || (_| | |____/ \__,_|\__\__,_|_.__/
   * \__,_|___/\___|_| |_|\___|\__\__,_|
   */

  /**
   * Uility method to store Kettle Database Metadata
   *
   * @param cwm          The model to store it in
   * @param databaseMeta The Kettle database connection metadata to use.
   */
  public void storeDatabaseMeta( CWM cwm, DatabaseMeta databaseMeta ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmCatalog catalog = cwm.createCatalog( databaseMeta.getName() );

    catalog.setName( databaseMeta.getName() );

    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_SERVER, databaseMeta.getHostname() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_TYPE, databaseMeta.getDatabaseTypeDesc() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_ACCESS, databaseMeta.getAccessTypeDesc() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_DATABASE, databaseMeta.getDatabaseName() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_PORT, databaseMeta.getDatabasePortNumberString() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_USERNAME, databaseMeta.getUsername() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_PASSWORD, databaseMeta.getPassword() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_SERVERNAME, databaseMeta.getServername() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_DATA_TABLESPACE, databaseMeta.getDataTablespace() );
    cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_INDEX_TABLESPACE, databaseMeta.getIndexTablespace() );

    // Save all the attributes as well...
    //
    List<Object> list = new ArrayList<Object>( databaseMeta.getAttributes().keySet() );
    for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
      String key = (String) iter.next();
      String attribute = (String) databaseMeta.getAttributes().get( key );

      if ( !Const.isEmpty( attribute ) ) {
        cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_ATTRIBUTE_PREFIX + key, attribute );
      }
    }

    // Add the complete calculated URL for the pleasure of the other consumers besides Kettle
    //
    try {
      cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_JDBC_URL, databaseMeta.getURL() );
    } catch ( KettleDatabaseException e ) {
      // Data to make a URL is not valid, incomplete, etc.
      cwm.addTaggedValue( catalog, CWM.TAG_DATABASE_JDBC_URL, Messages
        .getString(
          "CwmSchemaFactory.ERROR_0002_INVALID_DATA" ) ); // we don't read this back, it's only for 3rd party use.
      // $NON-NLS-1$
    }
  }

  /**
   * Read a DatabaseMeta from a CWM model by providing the catalog reference.
   *
   * @param cwm
   * @param catalog
   * @return a new DatabaseMeta instance, read from the specified CWM model.
   */
  public DatabaseMeta getDatabaseMeta( CWM cwm, CwmCatalog catalog ) {
    DatabaseMeta databaseMeta = new DatabaseMeta();

    databaseMeta.setName( catalog.getName() );

    databaseMeta.setHostname( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_SERVER ) );
    databaseMeta.setDatabaseType( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_TYPE ) );
    databaseMeta.setAccessType( DatabaseMeta
      .getAccessType( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_ACCESS ) ) );
    databaseMeta.setDBName( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_DATABASE ) );
    databaseMeta.setDBPort( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_PORT ) );
    databaseMeta.setUsername( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_USERNAME ) );
    databaseMeta.setPassword( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_PASSWORD ) );
    databaseMeta.setServername( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_SERVERNAME ) );
    databaseMeta.setDataTablespace( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_DATA_TABLESPACE ) );
    databaseMeta.setIndexTablespace( cwm.getFirstTaggedValue( catalog, CWM.TAG_DATABASE_INDEX_TABLESPACE ) );

    // And now load the attributes...
    CwmTaggedValue[] taggedValue = cwm.getTaggedValues( catalog );
    for ( int i = 0; i < taggedValue.length; i++ ) {
      if ( taggedValue[ i ].getTag().startsWith( CWM.TAG_DATABASE_ATTRIBUTE_PREFIX ) ) {
        String key = taggedValue[ i ].getTag().substring( CWM.TAG_DATABASE_ATTRIBUTE_PREFIX.length() );
        String attribute = taggedValue[ i ].getValue();

        // Add the attribute
        databaseMeta.getAttributes().put( key, attribute );
      }
    }

    return databaseMeta;
  }

  /*
   * ____ _ __ ___ | __ ) _ _ ___(_)_ __ ___ ___ __\ \ / (_) _____ __ | _ \| | | / __| | '_ \ / _ \/ __/ __\ \ / /| |/ _
   * \ \ /\ / / | |_) | |_| \__ \ | | | | __/\__ \__ \\ V / | | __/\ V V / |____/ \__,_|___/_|_| |_|\___||___/___/ \_/
   * |_|\___| \_/\_/
   */

  /**
   * This method stores a business model in a CwmSchema. The schema then in turn contains a number of
   *
   * @param cwm           The model to store in
   * @param businessModel The business model to store into the selected CWM model.
   */
  public void storeBusinessModel( CWM cwm, BusinessModel businessModel ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmSchema cwmSchema = cwm.createSchema( businessModel.getId() );

    // Store the concept properties
    storeConceptProperties( cwm, cwmSchema, businessModel.getConcept() );

    // Store the business tables...
    //
    for ( int i = 0; i < businessModel.nrBusinessTables(); i++ ) {
      BusinessTable businessTable = businessModel.getBusinessTable( i );
      storeBusinessTable( cwm, cwmSchema, businessTable );
    }

    // Store the notes...
    //
    for ( int i = 0; i < businessModel.nrNotes(); i++ ) {
      NotePadMeta notePadMeta = businessModel.getNote( i );
      storeNotePadMeta( cwm, cwmSchema, notePadMeta );
    }

    // Store the relationships...
    for ( int i = 0; i < businessModel.nrRelationships(); i++ ) {
      RelationshipMeta relationshipMeta = businessModel.getRelationship( i );
      storeRelationshipMeta( cwm, relationshipMeta, cwmSchema );
    }

    // Also store the categories...
    for ( int i = 0; i < businessModel.getRootCategory().nrBusinessCategories(); i++ ) {
      BusinessCategory businessCategory = businessModel.getRootCategory().getBusinessCategory( i );
      storeBusinessCategory( cwm, businessCategory, null, cwmSchema );
    }
    // Save the olap information
    //
    // Create a CWM Schema first for this...
    org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema = cwm.createOlapSchema( businessModel.getId() );

    // Put the OLAP stuff in there.
    //
    // The dimensions
    List dimensions = businessModel.getOlapDimensions();
    for ( int i = 0; i < dimensions.size(); i++ ) {
      OlapDimension dimension = (OlapDimension) dimensions.get( i );
      storeOlapDimension( cwm, cwmOlapSchema, dimension );
    }
    // And the cubes
    List cubes = businessModel.getOlapCubes();
    for ( int i = 0; i < cubes.size(); i++ ) {
      OlapCube cube = (OlapCube) cubes.get( i );
      storeOlapCube( cwm, cwmOlapSchema, cube );
    }
  }

  /**
   * Load a business model from a CWM model by loading it from the supplied CwmSchema and using the SchemaMeta object
   * for reference.
   *
   * @param cwm
   * @param cwmSchema
   * @param schemaMeta
   * @return a newly created Business Model
   */
  public BusinessModel getBusinessModel( CWM cwm, CwmSchema cwmSchema, SchemaMeta schemaMeta ) {
    // The name?
    BusinessModel businessModel = new BusinessModel( cwmSchema.getName() );
    businessModel.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( schemaMeta.getBusinessModels() ) );

    // load the concept properties
    getConceptProperties( cwm, cwmSchema, businessModel.getConcept(), schemaMeta );

    // Load the business tables into this business model..
    //
    CwmDimension[] CwmDimensions = cwm.getDimensions( cwmSchema );
    for ( int i = 0; i < CwmDimensions.length; i++ ) {
      CwmDimension cwmDimension = CwmDimensions[ i ];
      BusinessTable businessTable = getBusinessTable( cwm, cwmDimension, schemaMeta, businessModel );

      if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, businessTable ) ) {
        try {
          businessModel.addBusinessTable( businessTable );
        } catch ( ObjectAlreadyExistsException e ) {
          // Ignore the duplicates for now.
          // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
          // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
        }
      }
    }

    // Load the notes too
    //
    CwmDescription[] cwmDescriptions = cwm.getDescription( cwmSchema );
    for ( int i = 0; i < cwmDescriptions.length; i++ ) {
      CwmDescription cwmDescription = cwmDescriptions[ i ];
      if ( CWM.DESCRIPTION_TYPE_NOTEPAD.equals( cwmDescription.getType() ) ) {
        NotePadMeta notePadMeta = getNotePadMeta( cwm, cwmDescription );
        businessModel.addNote( notePadMeta );
      }
    }

    // Load the relationships too...
    CwmKeyRelationship[] cwmKeyRelationships = cwm.getRelationships( cwmSchema );
    for ( int i = 0; i < cwmKeyRelationships.length; i++ ) {
      CwmKeyRelationship cwmKeyRelationship = cwmKeyRelationships[ i ];
      RelationshipMeta relationshipMeta = getRelationshipMeta( cwm, cwmKeyRelationship, businessModel );
      // populate column references if a complex join
      if ( relationshipMeta.isComplex() ) {
        try {
          List<BusinessColumn> referencedColumns = new ArrayList<BusinessColumn>();
          PMSFormula formula = relationshipMeta.getComplexJoinFormula( businessModel );
          formula.parseAndValidate();
          for ( Selection columnSelection : formula.getBusinessColumns() ) {
            referencedColumns.add( columnSelection.getBusinessColumn() );
          }
          relationshipMeta.setCJReferencedColumns( referencedColumns );
        } catch ( PentahoMetadataException e ) {
          logger.error( Messages.getErrorString( "MQLQueryImpl.ERROR_0017_FAILED_TO_PARSE_COMPLEX_JOIN",
            relationshipMeta.getComplexJoin() ), e );
        }
      }
      businessModel.addRelationship( relationshipMeta );
    }

    // Load the categories...
    CwmExtent[] cwmExtents = cwm.getRootExtents( cwmSchema );
    for ( int i = 0; i < cwmExtents.length; i++ ) {
      CwmExtent cwmExtent = cwmExtents[ i ];
      BusinessCategory businessCategory = getBusinessCategory( cwm, cwmExtent, businessModel, schemaMeta );
      if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, businessCategory ) ) {
        try {
          businessModel.getRootCategory().addBusinessCategory( businessCategory );
        } catch ( ObjectAlreadyExistsException e ) {
          // Ignore the duplicates for now.
          // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
          // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
        }
      }
    }

    // Load the OLAP information too
    org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema = cwm.findOlapSchema( cwmSchema.getName() ); // same
    // name
    // as
    // business
    // model

    if ( cwmOlapSchema != null ) {
      // get the Olap Dimensions from this schema...
      Collection cwmOlapDimensions = cwmOlapSchema.getDimension();
      for ( Iterator iter = cwmOlapDimensions.iterator(); iter.hasNext(); ) {
        org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension =
          (org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension) iter.next();
        OlapDimension olapDimension = getOlapDimension( cwm, cwmOlapDimension, businessModel );
        businessModel.getOlapDimensions().add( 0, olapDimension ); // Inverse the load (MDR does it backward)
      }

      Collection cwmOlapCubes = cwmOlapSchema.getCube();
      for ( Iterator iter = cwmOlapCubes.iterator(); iter.hasNext(); ) {
        CwmCube cwmCube = (CwmCube) iter.next();
        OlapCube olapCube = getOlapCube( cwm, cwmCube, businessModel );
        businessModel.getOlapCubes().add( olapCube );
      }
    }

    if ( cwm.isReversingOrder() ) {
      Collections.reverse( businessModel.getBusinessTables().getList() );
      Collections.reverse( businessModel.getRootCategory().getBusinessCategories().getList() );
    }

    return businessModel;
  }

  /*
   * ____ _ _____ _ _ | __ ) _ _ ___(_)_ __ ___ ___ __|_ _|_ _| |__ | | ___ | _ \| | | / __| | '_ \ / _ \/ __/ __|| |/
   * _` | '_ \| |/ _ \ | |_) | |_| \__ \ | | | | __/\__ \__ \| | (_| | |_) | | __/ |____/ \__,_|___/_|_|
   * |_|\___||___/___/|_|\__,_|_.__/|_|\___|
   */

  /**
   * Store a business table in the MDR
   *
   * @param cwm           The model to store in
   * @param cwmSchema     The CWM Schema to put the tables into.
   * @param businessTable The business table to store.
   */
  public void storeBusinessTable( CWM cwm, CwmSchema cwmSchema, BusinessTable businessTable ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmDimension cwmDimension = cwm.createDimension( cwmSchema, businessTable.getId() );

    // The physical table name?
    String physicalTablename = businessTable.getPhysicalTable().getId();
    @SuppressWarnings( "all" )
    Collection<CwmTaggedValue> cg = cwmDimension.getTaggedValue();
    cg.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_TABLE_PHYSICAL_TABLE_NAME, physicalTablename ) );

    // The location, etc: GUI elements...
    //
    // Position
    cwm.setPosition( cwmDimension, businessTable.getLocation().x, businessTable.getLocation().y );
    // Drawn?
    cwm.addTaggedValue( cwmDimension, CWM.TAG_TABLE_IS_DRAWN,
      businessTable.isDrawn() ? "Y" : "N" ); //$NON-NLS-1$ //$NON-NLS-2$

    // Store the properties...
    storeConceptProperties( cwm, cwmDimension, businessTable.getConcept() );

    // Store the business columns...
    //
    for ( int i = 0; i < businessTable.nrBusinessColumns(); i++ ) {
      BusinessColumn businessColumn = businessTable.getBusinessColumn( i );
      storeBusinessColumn( cwm, cwmSchema, cwmDimension, businessColumn );
    }
  }

  /**
   * Load a BusinessTable from a CWM model by taking information from a supplied CwmDimension
   *
   * @param cwm
   * @param cwmDimension
   * @param schemaMeta
   * @return a newly created BusinessTable
   */
  public BusinessTable getBusinessTable( CWM cwm, CwmDimension cwmDimension, SchemaMeta schemaMeta,
                                         BusinessModel businessModel ) {
    BusinessTable businessTable = new BusinessTable( cwmDimension.getName() );
    businessTable
      .addIDChangedListener( ConceptUtilityBase.createIDChangedListener( businessModel.getBusinessTables() ) );

    // load the properties...
    getConceptProperties( cwm, cwmDimension, businessTable.getConcept(), schemaMeta );

    // Set the security parent to inherit from
    businessTable.getConcept().setSecurityParentInterface( businessModel.getConcept() );

    // The physical table...
    //
    String physicalTableName = cwm.getFirstTaggedValue( cwmDimension, CWM.TAG_BUSINESS_TABLE_PHYSICAL_TABLE_NAME );
    PhysicalTable physicalTable = schemaMeta.findPhysicalTable( physicalTableName );
    businessTable.setPhysicalTable( physicalTable );

    // The location, etc: GUI elements...
    //
    // Location
    int x = cwm.getPositionX( cwmDimension );
    int y = cwm.getPositionY( cwmDimension );
    businessTable.setLocation( x, y );
    // Drawn?
    // True if not explicitly set to false.
    boolean drawn =
      !( "N".equalsIgnoreCase( cwm.getFirstTaggedValue( cwmDimension, CWM.TAG_TABLE_IS_DRAWN ) ) ); //$NON-NLS-1$
    businessTable.setDrawn( drawn );

    // The physical columns...
    //
    List cwmDimensionedObjects = cwmDimension.getDimensionedObject();
    for ( int i = 0; i < cwmDimensionedObjects.size(); i++ ) {
      CwmDimensionedObject cwmDimensionedObject = (CwmDimensionedObject) cwmDimensionedObjects.get( i );
      BusinessColumn businessColumn =
        getBusinessColumn( cwm, cwmDimensionedObject, physicalTable, businessTable, schemaMeta );

      if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, businessColumn ) ) {
        try {
          businessTable.addBusinessColumn( businessColumn );
        } catch ( ObjectAlreadyExistsException e ) {
          // Ignore the duplicates for now.
          // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
          // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
        }
      }
    }

    return businessTable;
  }

  /*
   * ____ _ ____ _ | __ ) _ _ ___(_)_ __ ___ ___ ___ / ___|___ | |_ _ _ __ ___ _ __ | _ \| | | / __| | '_ \ / _ \/ __/
   * __| | / _ \| | | | | '_ ` _ \| '_ \ | |_) | |_| \__ \ | | | | __/\__ \__ \ |__| (_) | | |_| | | | | | | | | |
   * |____/ \__,_|___/_|_| |_|\___||___/___/\____\___/|_|\__,_|_| |_| |_|_| |_|
   */

  /**
   * Store the information from a business column in a dimensioned object in the MDR
   *
   * @param cwm            the model to store in
   * @param cwmSchema      The CWM schema this object belongs to
   * @param cwmDimension   The owner of the business column
   * @param businessColumn The business column to store
   */
  public void storeBusinessColumn( CWM cwm, CwmSchema cwmSchema, CwmDimension cwmDimension,
                                   BusinessColumn businessColumn ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmDimensionedObject cwmDimensionedObject = cwm.createDimensionedObject( businessColumn.getId() );
    cwmDimensionedObject.setSchema( cwmSchema );

    @SuppressWarnings( "all" )
    Collection<CwmTaggedValue> c = cwmDimensionedObject.getTaggedValue();

    // store the physical column name...
    if ( businessColumn.getPhysicalColumn() == null ) {
      logger
        .error( Messages
          .getErrorString(
            "CwmSchemaFactory.ERROR_0004_MISSING_PHYSICAL_COLUMN", businessColumn.getBusinessTable().getId(),
            businessColumn.getId() ) ); //$NON-NLS-1$
      // attempt a repair by selecting the first physical col
      businessColumn.setPhysicalColumn( businessColumn.getBusinessTable().getPhysicalTable().getPhysicalColumn( 0 ) );
    }
    String physicalColumnName = businessColumn.getPhysicalColumn().getId();
    c.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME, physicalColumnName ) );

    // Store the name of the business table as well, just for reference
    String businessTableName = businessColumn.getBusinessTable().getId();
    c.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_COLUMN_BUSINESS_TABLE, businessTableName ) );

    // And store the business column properties
    storeConceptProperties( cwm, cwmDimensionedObject, businessColumn.getConcept() );

    // Add to the dimension
    @SuppressWarnings( "all" )
    Collection<CwmDimensionedObject> cd = cwmDimension.getDimensionedObject();

    cd.add( cwmDimensionedObject );
  }

  /**
   * Create a business column from a dimensioned object...
   *
   * @param cwm
   * @param cwmDimensionedObject
   * @param physicalTable
   * @return The newly created business column
   */
  public BusinessColumn getBusinessColumn( CWM cwm, CwmDimensionedObject cwmDimensionedObject,
                                           PhysicalTable physicalTable, BusinessTable businessTable,
                                           SchemaMeta schemaMeta ) {
    BusinessColumn businessColumn = new BusinessColumn( cwmDimensionedObject.getName() );
    businessColumn.addIDChangedListener( ConceptUtilityBase
      .createIDChangedListener( businessTable.getBusinessColumns() ) );

    // The parent business table.
    businessColumn.setBusinessTable( businessTable );

    // Load the concept properties
    ConceptInterface concept = new Concept();
    getConceptProperties( cwm, cwmDimensionedObject, concept, schemaMeta );
    businessColumn.setConcept( concept );

    // Set the security parent
    businessColumn.getConcept().setSecurityParentInterface( businessTable.getConcept() );

    // Set the physical column last to allow the business column to inherit from the physical column
    String physicalColumnName =
      cwm.getFirstTaggedValue( cwmDimensionedObject, CWM.TAG_BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME );
    PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn( physicalColumnName );
    if ( physicalColumn == null ) {
      logger
        .error( Messages
          .getErrorString(
            "CwmSchemaFactory.ERROR_0003_XMI_CORRUPT_MISSING_PHYSICAL_TABLE", physicalColumnName,
            businessColumn.getBusinessTable().getId(), businessColumn.getId() ) ); //$NON-NLS-1$
      businessColumn.setPhysicalColumn( physicalTable.getPhysicalColumn( 0 ) );
    } else {
      businessColumn.setPhysicalColumn( physicalColumn );
    }

    return businessColumn;
  }

  /*
   * ____ _ _ _ _ _ __ __ _ | _ \ ___| | __ _| |_(_) ___ _ __ ___| |__ (_)_ __ | \/ | ___| |_ __ _ | |_) / _ \ |/ _` |
   * __| |/ _ \| '_ \/ __| '_ \| | '_ \| |\/| |/ _ \ __/ _` | | _ < __/ | (_| | |_| | (_) | | | \__ \ | | | | |_) | | |
   * | __/ || (_| | |_| \_\___|_|\__,_|\__|_|\___/|_| |_|___/_| |_|_| .__/|_| |_|\___|\__\__,_| |_|
   */

  /**
   * Stores a Kettle relationshipMeta object into the MDR
   *
   * @param cwm              The MDR CWM model instantiation to use.
   * @param relationshipMeta the relationshipMeta object to store.
   * @param cwmSchema        The schema to which the relationship belongs
   */
  public void storeRelationshipMeta( CWM cwm, RelationshipMeta relationshipMeta, CwmSchema cwmSchema ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmKeyRelationship relationship = cwm.createRelationship();
    @SuppressWarnings( "unchecked" )
    Collection<CwmTaggedValue> pairs = relationship.getTaggedValue();

    // Parent table
    if ( relationshipMeta.getTableFrom() != null ) {
      pairs
        .add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_TABLENAME_PARENT, relationshipMeta.getTableFrom().getId() ) );
    }

    // Child table
    if ( relationshipMeta.getTableTo() != null ) {
      pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_TABLENAME_CHILD, relationshipMeta.getTableTo().getId() ) );
    }

    // Complex join or between 2 fields?
    if ( relationshipMeta.isComplex() ) {
      pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_IS_COMPLEX, "Y" ) ); //$NON-NLS-1$

      if ( !Const.isEmpty( relationshipMeta.getComplexJoin() ) ) {
        pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_COMPLEX_JOIN, relationshipMeta.getComplexJoin() ) );
      }
    } else {
      // Parent table
      if ( relationshipMeta.getFieldFrom() != null ) {
        pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_FIELDNAME_PARENT, relationshipMeta.getFieldFrom()
          .getId() ) );
      }

      // Parent table
      if ( relationshipMeta.getFieldTo() != null ) {
        pairs
          .add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_FIELDNAME_CHILD, relationshipMeta.getFieldTo().getId() ) );
      }
    }

    // And then the type of relationship between the two tables...
    //
    pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_TYPE, relationshipMeta.getTypeDesc() ) );

    // Save the relationship join order key
    //
    pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_JOIN_ORDER_KEY, relationshipMeta.getJoinOrderKey() ) );

    // Save the relationship description too
    //
    pairs.add( cwm.createTaggedValue( CWM.TAG_RELATIONSHIP_DESCRIPTION, relationshipMeta.getDescription() ) );

    // add the relationship to the schema
    @SuppressWarnings( "unchecked" )
    Collection<CwmKeyRelationship> cr = cwmSchema.getOwnedElement();
    cr.add( relationship );
  }

  /**
   * Create a new RelationshipMeta object from a CWM model by looking at the CwmKeyRelationship and the BusinessModel
   *
   * @param cwm
   * @param relationship
   * @param businessModel
   * @return a newly created RelationshipMeta object
   */
  public RelationshipMeta getRelationshipMeta( CWM cwm, CwmKeyRelationship relationship, BusinessModel businessModel ) {
    RelationshipMeta relationshipMeta = new RelationshipMeta();

    // From which to which table are we building a relationship?
    // Parent = From, Child = to

    String parentTable =
      CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_TABLENAME_PARENT );
    String childTable = CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_TABLENAME_CHILD );

    // The business tables that link...
    relationshipMeta.setTableFrom( businessModel.findBusinessTable( parentTable ) );
    relationshipMeta.setTableTo( businessModel.findBusinessTable( childTable ) );

    // Complex join?
    //
    boolean complex =
      "Y".equalsIgnoreCase(
        CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_IS_COMPLEX ) ); //$NON-NLS-1$
    relationshipMeta.setComplex( complex );

    // Which fields?
    //
    if ( complex ) {
      String complexJoin = CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_COMPLEX_JOIN );
      relationshipMeta.setComplexJoin( complexJoin );
    } else {
      String parentField =
        CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_FIELDNAME_PARENT );
      String childField =
        CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_FIELDNAME_CHILD );

      if ( relationshipMeta.getTableFrom() != null ) {
        BusinessTable tableFrom = relationshipMeta.getTableFrom();
        BusinessColumn fieldFrom = tableFrom.findBusinessColumn( parentField );
        relationshipMeta.setFieldFrom( fieldFrom );
      }
      if ( relationshipMeta.getTableTo() != null ) {
        BusinessTable tableTo = relationshipMeta.getTableTo();
        BusinessColumn fieldTo = tableTo.findBusinessColumn( childField );
        relationshipMeta.setFieldTo( fieldTo );
      }
    }

    // What type of relationship?
    //
    String relType = CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_TYPE );
    relationshipMeta.setType( relType );

    String joinOrderKey =
      CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_JOIN_ORDER_KEY );
    relationshipMeta.setJoinOrderKey( joinOrderKey );

    String description = CWM.findFirstTaggedValue( relationship.getTaggedValue(), CWM.TAG_RELATIONSHIP_DESCRIPTION );
    relationshipMeta.setDescription( description );

    return relationshipMeta;
  }

  /*
   * _ _ _ ____ _ __ __ _ | \ | | ___ | |_ ___| _ \ __ _ __| | \/ | ___| |_ __ _ | \| |/ _ \| __/ _ \ |_) / _` |/ _` |
   * |\/| |/ _ \ __/ _` | | |\ | (_) | || __/ __/ (_| | (_| | | | | __/ || (_| | |_| \_|\___/ \__\___|_| \__,_|\__,_|_|
   * |_|\___|\__\__,_|
   */

  /**
   * Stores a notepad in the CWM model in a CwmSchema.
   *
   * @param cwm
   * @param cwmSchema
   * @param notePadMeta
   */
  public void storeNotePadMeta( CWM cwm, CwmSchema cwmSchema, NotePadMeta notePadMeta ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmDescription cwmDescription = cwm.createDescription( notePadMeta.getNote() );
    cwmDescription.setType( CWM.DESCRIPTION_TYPE_NOTEPAD );
    cwm.setPosition( cwmDescription, notePadMeta.getLocation().x, notePadMeta.getLocation().y );
    cwm.setWidth( cwmDescription, notePadMeta.getWidth() );
    cwm.setHeight( cwmDescription, notePadMeta.getHeight() );
    cwm.setDescription( cwmSchema, cwmDescription );
  }

  /**
   * Loads a NotePadMeta object from a CWM model using the supplied CWM Description object
   *
   * @param cwm
   * @param cwmDescription
   * @return a newly created NotePadMeta object
   */
  public NotePadMeta getNotePadMeta( CWM cwm, CwmDescription cwmDescription ) {
    NotePadMeta notePadMeta = new NotePadMeta();

    // The text
    notePadMeta.setNote( cwmDescription.getBody() );

    // The position & dimensions
    notePadMeta.setLocation( cwm.getPositionX( cwmDescription ), cwm.getPositionY( cwmDescription ) );
    notePadMeta.setWidth( cwm.getPositionWidth( cwmDescription ) );
    notePadMeta.setHeight( cwm.getPositionHeight( cwmDescription ) );

    return notePadMeta;
  }

  /*
   * ____ _ ____ _ | __ ) _ _ ___(_)_ __ ___ ___ ___ / ___|__ _| |_ ___ __ _ ___ _ __ _ _ | _ \| | | / __| | '_ \ / _ \/
   * __/ __| | / _` | __/ _ \/ _` |/ _ \| '__| | | | | |_) | |_| \__ \ | | | | __/\__ \__ \ |__| (_| | || __/ (_| | (_)
   * | | | |_| | |____/ \__,_|___/_|_| |_|\___||___/___/\____\__,_|\__\___|\__, |\___/|_| \__, | |___/ |___/
   */

  /**
   * Store the business category specified into the given cwm model
   *
   * @param cwm              the model to store in
   * @param businessCategory the businessCategory to store
   * @param parent           root = 0
   * @param cwmSchema        the cwmSchema to reference.
   */
  public CwmExtent storeBusinessCategory( CWM cwm, BusinessCategory businessCategory, CwmExtent parent,
                                          CwmSchema cwmSchema ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    // We're storing these items into an extent (Instance package)
    CwmExtent cwmExtent = cwm.createExtent( businessCategory.getId() );
    cwmExtent.setNamespace( cwmSchema );

    String isRoot = ( parent == null ) ? "Y" : "N"; //$NON-NLS-1$ //$NON-NLS-2$

    // Store a ROOT flag to know where to start later on.

    @SuppressWarnings( "unchecked" )
    Collection<CwmTaggedValue> cat = cwmExtent.getTaggedValue();
    cat.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_CATEGORY_ROOT, isRoot ) );

    // Store the concept properties as well
    storeConceptProperties( cwm, cwmExtent, businessCategory.getConcept() );

    // First store the BusinessColumns that we have here.
    // Note: we already have stored the business columns, we can just save a reference to it.
    // In our case it's the name of the business table and the name of the business column.
    //
    for ( int i = 0; i < businessCategory.nrBusinessColumns(); i++ ) {
      BusinessColumn businessColumn = businessCategory.getBusinessColumn( i );

      // Just take a model element and store it into this it.
      CwmAttribute cwmAttribute = cwm.createAttribute( businessColumn.getId() );
      @SuppressWarnings( "unchecked" )
      Collection<CwmTaggedValue> ct = cwmAttribute.getTaggedValue();

      ct.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_CATEGORY_TYPE, CWM.VALUE_BUSINESS_TYPE_COLUMN ) );

      @SuppressWarnings( "unchecked" )
      Collection<CwmAttribute> ca = cwmExtent.getOwnedElement();
      ca.add( cwmAttribute );
    }

    // Now store the sub-categories...
    for ( int i = 0; i < businessCategory.nrBusinessCategories(); i++ ) {
      CwmExtent childCwmExtent =
        storeBusinessCategory( cwm, businessCategory.getBusinessCategory( i ), cwmExtent, cwmSchema );
      @SuppressWarnings( "unchecked" )
      Collection<CwmTaggedValue> cac = childCwmExtent.getTaggedValue();
      cac.add( cwm.createTaggedValue( CWM.TAG_BUSINESS_CATEGORY_TYPE, CWM.VALUE_BUSINESS_TYPE_COLUMN ) );

      @SuppressWarnings( "unchecked" )
      Collection<CwmExtent> cao = cwmExtent.getOwnedElement();
      cao.add( childCwmExtent );
    }

    return cwmExtent;
  }

  /**
   * @param cwm           The model
   * @param cwmExtent     The extent to construct the category from
   * @param businessModel The business model to reference.
   * @return A new business category
   */
  public BusinessCategory getBusinessCategory( CWM cwm, CwmExtent cwmExtent, BusinessModel businessModel,
                                               SchemaMeta schemaMeta ) {
    BusinessCategory businessCategory = new BusinessCategory( cwmExtent.getName() );
    businessCategory.addIDChangedListener( ConceptUtilityBase.createIDChangedListener( businessModel.getRootCategory()
      .getBusinessCategories() ) );

    // Get the concept properties too
    ConceptInterface concept = new Concept();
    getConceptProperties( cwm, cwmExtent, concept, schemaMeta );
    businessCategory.setConcept( concept );

    // Set the security parent for the business category too...
    businessCategory.getConcept().setSecurityParentInterface( businessModel.getConcept() );

    // Now get the business columns
    Collection elements = cwmExtent.getOwnedElement();
    for ( Iterator elementsIterator = elements.iterator(); elementsIterator.hasNext(); ) {
      CwmModelElement element = (CwmModelElement) elementsIterator.next();
      String type = CWM.findFirstTaggedValue( element.getTaggedValue(), CWM.TAG_BUSINESS_CATEGORY_TYPE );
      if ( type.equals( CWM.VALUE_BUSINESS_TYPE_COLUMN ) ) {
        CwmAttribute cwmAttribute = (CwmAttribute) element;
        BusinessColumn businessColumn = businessModel.findBusinessColumn( cwmAttribute.getName() );
        if ( businessColumn != null ) {
          if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, businessColumn ) ) {
            try {
              businessCategory.addBusinessColumn( businessColumn );
            } catch ( ObjectAlreadyExistsException e ) {
              // Ignore the duplicates for now.
              // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
              // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );

            }
          }
        }
      } else if ( type.equals( CWM.VALUE_BUSINESS_TYPE_CATEGORY ) ) {
        CwmExtent childCwmExtent = (CwmExtent) element;
        BusinessCategory childBusinessCategory = getBusinessCategory( cwm, childCwmExtent, businessModel, schemaMeta );

        if ( hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_READ, childBusinessCategory ) ) {
          try {
            businessCategory.addBusinessCategory( childBusinessCategory );
          } catch ( ObjectAlreadyExistsException e ) {
            // Ignore the duplicates for now.
            // TODO: figure out how to handle this error, the duplicate shouldn't be in the CWM in the first place!
            // logger.error( Messages.getString("CwmSchemaFactory.ERROR_DUPLICATE_IN_CWM" ), e );
          }
        }
      }

    }

    if ( cwm.isReversingOrder() ) {
      Collections.reverse( businessCategory.getBusinessCategories().getList() );
      Collections.reverse( businessCategory.getBusinessColumns().getList() );
    }

    return businessCategory;
  }

  /*
   * _ _ | | ___ ___ __ _| | ___ | | / _ \ / __/ _` | |/ _ \ | |__| (_) | (_| (_| | | __/ |_____\___/ \___\__,_|_|\___|
   */

  /**
   * Store the locale meta-data into the CWM repository using CwmParamter objects.
   *
   * @param cwm    The model to store in
   * @param locale the locale to store
   */
  public void storeLocale( CWM cwm, LocaleInterface locale ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmParameter cwmParameter = cwm.createParameter( locale.getCode() );
    if ( !Const.isEmpty( locale.getDescription() ) ) {
      cwm.addTaggedValue( cwmParameter, CWM.TAG_LOCALE_DESCRIPTION, locale.getDescription() );
    }
    cwm.addTaggedValue( cwmParameter, CWM.TAG_LOCALE_ORDER, Integer.toString( locale.getOrder() ) );
    cwm.addTaggedValue( cwmParameter, CWM.TAG_LOCALE_IS_ACTIVE,
      locale.isActive() ? "Y" : "N" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Create a new locale by reading it from a CWM meta-data model
   *
   * @param cwm          The CWM model to read from
   * @param cwmParameter The CwmParameter object to use
   * @return a newly created LocaleInterface class (LocaleMeta)
   */
  public LocaleInterface getLocale( CWM cwm, CwmParameter cwmParameter ) {
    LocaleInterface locale = new LocaleMeta();
    locale.setCode( cwmParameter.getName() );

    // The description
    String description = cwm.getFirstTaggedValue( cwmParameter, CWM.TAG_LOCALE_DESCRIPTION );
    if ( !Const.isEmpty( description ) ) {
      locale.setDescription( description );
    }

    // The order
    String strOrder = cwm.getFirstTaggedValue( cwmParameter, CWM.TAG_LOCALE_ORDER );
    locale.setOrder( Const.toInt( strOrder, -1 ) );

    // Active?
    boolean active =
      "Y".equalsIgnoreCase( cwm.getFirstTaggedValue( cwmParameter, CWM.TAG_LOCALE_IS_ACTIVE ) ); //$NON-NLS-1$
    locale.setActive( active );

    return locale;
  }

  /*
   * ____ _ / ___|___ _ __ ___ ___ _ __ | |_ | | / _ \| '_ \ / __/ _ \ '_ \| __| | |__| (_) | | | | (_| __/ |_) | |_
   * \____\___/|_| |_|\___\___| .__/ \__| |_|
   */

  /**
   * Store the concept into the CWM model
   *
   * @param cwm     the model to store in
   * @param concept the concept to store
   */
  public void storeModelConcept( CWM cwm, ConceptInterface concept ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    CwmClass cwmClass = cwm.createClass( concept.getName() );

    // Attach the objects in the concepts itself to it.
    storeConceptProperties( cwm, cwmClass, concept );
  }

  /**
   * construct a new Concept by loading it from a model
   *
   * @param cwm        The model to load from
   * @param cwmClass   the CwmClass object that is used to represent the concept
   * @param schemaMeta the schema metadata to reference parents, etc.
   * @return the newly created concept, with information loaded from the CWM model.
   */
  public ConceptInterface getModelConcept( CWM cwm, CwmClass cwmClass, SchemaMeta schemaMeta ) {
    ConceptInterface concept = new Concept( cwmClass.getName() );

    // Attach the objects in the concepts itself to it.
    getConceptProperties( cwm, cwmClass, concept, schemaMeta );

    return concept;
  }

  /*
   * ____ _ ____ _ _ / ___|___ _ __ ___ ___ _ __ | |_| _ \ _ __ ___ _ __ ___ _ __| |_(_) ___ ___ | | / _ \| '_ \ / __/ _
   * \ '_ \| __| |_) | '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __| | |__| (_) | | | | (_| __/ |_) | |_| __/| | | (_) | |_)
   * | __/ | | |_| | __/\__ \ \____\___/|_| |_|\___\___| .__/ \__|_| |_| \___/| .__/ \___|_| \__|_|\___||___/ |_| |_|
   */

  /**
   * Store the concept properties into the CWM model
   *
   * @param cwm                     the model to store the concept properties in
   * @param modelElement            The model element to attach it to.
   * @param concept                 The concept utility interface to use. (the properties)
   */
  public void storeConceptProperties( CWM cwm, CwmModelElement modelElement, ConceptInterface concept ) {
    if ( !hasAccess( CwmSchemaFactoryInterface.ACCESS_TYPE_SCHEMA_ADMIN, null ) ) {
      throw new CwmSchemaFactoryException( Messages
        .getString( "CwmSchemaFactory.ERROR_0001_SCHEMA_ADMIN_ACCESS_DENIED" ) ); //$NON-NLS-1$
    }
    // Save the parent name of the concept...
    if ( concept.getParentInterface() != null ) {
      cwm.addTaggedValue( modelElement, CWM.TAG_CONCEPT_PARENT_NAME, concept.getParentInterface().getName() );
    }

    // Save the localized strings...
    //
    String[] ids = concept.getChildPropertyIDs();

    for ( int i = 0; i < ids.length; i++ ) {
      ConceptPropertyInterface property = concept.getProperty( ids[ i ] );

      // Only save IF we have a value at all...
      if ( property.getValue() != null ) {
        // Save the localized string properties
        //
        if ( property.getType().equals( ConceptPropertyType.LOCALIZED_STRING ) ) {
          LocalizedStringSettings stringSettings = (LocalizedStringSettings) property.getValue();
          String[] locales = stringSettings.getLocales();

          for ( int j = 0; j < locales.length; j++ ) {
            CwmDescription description = cwm.createDescription( stringSettings.getString( locales[ j ] ) );
            description.setLanguage( locales[ j ] );
            description.setName( property.getId() );
            description.setType( property.getType().getCode() );
            cwm.setDescription( modelElement, description );
          }
        } else if ( property.getType().equals( ConceptPropertyType.STRING ) ) {
          // Save the string properties
          //
          String string = (String) property.getValue();

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.BOOLEAN ) ) {
          // Save the boolean properties
          //
          Boolean value = (Boolean) property.getValue();

          CwmDescription description =
            cwm.createDescription( value.booleanValue() ? "Y" : "N" ); //$NON-NLS-1$ //$NON-NLS-2$
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( ( property.getType().equals( ConceptPropertyType.DATE ) )
          || ( property.getType().equals( ConceptPropertyType.TIMESTAMP ) ) )
        {
          // Save the date/timestamp properties
          //
          Date value = (Date) property.getValue();
          DateFormat df = new SimpleDateFormat( "yyyy/MM/dd'T'HH:mm:ss" ); //$NON-NLS-1$
          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = df.format( value );
          }
          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.TABLETYPE ) ) {
          // Save the table type properties
          //
          TableTypeSettings value = (TableTypeSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.getCode();
          }
          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.FIELDTYPE ) ) {
          // Save the field type properties
          //
          FieldTypeSettings value = (FieldTypeSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.getCode();
          }
          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.AGGREGATION ) ) {
          // Save the aggregation properties
          //
          AggregationSettings value = (AggregationSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.getCode();
          }
          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.AGGREGATION_LIST ) ) {
          // Save the aggregation list properties
          //
          ConceptPropertyAggregationList value = (ConceptPropertyAggregationList) property;
          CwmDescription description = cwm.createDescription( value.toXML() );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.NUMBER ) ) {
          // Save the numeric properties
          //
          BigDecimal value = (BigDecimal) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.COLOR ) ) {
          // Save the color properties
          //
          ColorSettings value = (ColorSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.DATATYPE ) ) {
          // Save the data type properties
          //
          DataTypeSettings value = (DataTypeSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.FONT ) ) {
          // Save the font properties
          //
          FontSettings value = (FontSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.URL ) ) {
          // Save the URL properties
          //
          URL value = (URL) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.SECURITY ) ) {
          // Save the Security properties
          //
          Security value = (Security) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toXML();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.ROW_LEVEL_SECURITY ) ) {
          // Save the Row Level Security properties
          //
          RowLevelSecurity value = (RowLevelSecurity) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toXML();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.ALIGNMENT ) ) {
          // Save the alignment properties
          //
          AlignmentSettings value = (AlignmentSettings) property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else if ( property.getType().equals( ConceptPropertyType.COLUMN_WIDTH ) ) {
          // Save the column width properties
          //
          Object value = property.getValue();

          String string = ""; //$NON-NLS-1$
          if ( value != null ) {
            string = value.toString();
          }

          CwmDescription description = cwm.createDescription( string );
          description.setName( property.getId() );
          description.setType( property.getType().getCode() );
          cwm.setDescription( modelElement, description );
        } else {
          throw new RuntimeException( "Unrecognized concept property type: " + property.getType() );
        }
      } else {
        logger.info( Messages.getString( "CwmSchemaFactory.ERROR_PROPERTY_VALUE_IS_NULL", property.getType()
          .toString() ) );
      }
    }
  }

  public void getConceptProperties( CWM cwm, CwmModelElement modelElement, ConceptInterface concept,
                                    SchemaMeta schemaMeta ) {
    // Set the parent interface first.
    String parentName = cwm.getFirstTaggedValue( modelElement, CWM.TAG_CONCEPT_PARENT_NAME );
    if ( parentName != null ) {
      ConceptInterface parentInterface = schemaMeta.findConcept( parentName );
      if ( parentInterface != null ) {
        concept.setParentInterface( parentInterface );
      }
    }

    // Localized descriptions...
    CwmDescription[] descriptions = cwm.getDescription( modelElement );
    for ( int i = 0; i < descriptions.length; i++ ) {
      CwmDescription description = descriptions[ i ];
      String locale = description.getLanguage();
      String value = description.getBody();
      String type = description.getType();
      String name = description.getName();

      // Load the localized strings...
      //
      if ( type == null || CWM.DESCRIPTION_TYPE_NOTEPAD.equals( type ) ) {
        // not the description we were interested in after all.
        // Remember we are somewhat abusing the description type in CWM to
        // add our concept properties to model elements.
        continue;
      } else if ( type.equals( ConceptPropertyType.LOCALIZED_STRING.getCode() ) ) {
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) && !Const.isEmpty( locale ) ) {
          concept.addLocalizedProperty( name, locale, value );
        }
      } else if ( type.equals( ConceptPropertyType.STRING.getCode() ) ) {
        // Load the strings
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyString conceptPropertyString = new ConceptPropertyString( name, value );
          concept.addProperty( conceptPropertyString );
        }
      } else if ( type.equals( ConceptPropertyType.BOOLEAN.getCode() ) ) {
        // Load the booleans
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyBoolean conceptPropertyBoolean =
            new ConceptPropertyBoolean( name, new Boolean( "Y".equalsIgnoreCase( value ) ) ); //$NON-NLS-1$
          concept.addProperty( conceptPropertyBoolean );
        }
      } else if ( type.equals( ConceptPropertyType.DATE.getCode() ) ) {
        // Load the dates
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          DateFormat df = new SimpleDateFormat( ConceptPropertyType.ISO_DATE_FORMAT );
          try {
            ConceptPropertyDate conceptPropertyDate = new ConceptPropertyDate( name, df.parse( value ) );
            concept.addProperty( conceptPropertyDate );
          } catch ( ParseException e ) {
            // TODO: ignoring the error for the time being
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_FAILED_TO_PARSE_DATE", value ), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.TIMESTAMP.getCode() ) ) {
        // Load the timestamps
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          DateFormat df = new SimpleDateFormat( ConceptPropertyType.ISO_DATE_FORMAT );
          try {
            ConceptPropertyTimestamp ts = new ConceptPropertyTimestamp( name, df.parse( value ) );
            concept.addProperty( ts );
          } catch ( ParseException e ) {
            // TODO: ignoring the error for the time being
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_FAILED_TO_PARSE_DATE", value ), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.TABLETYPE.getCode() ) ) {
        // Load the table types...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyTableType property = new ConceptPropertyTableType( name, TableTypeSettings.getType( value ) );
          concept.addProperty( property );
        }
      } else if ( type.equals( ConceptPropertyType.FIELDTYPE.getCode() ) ) {
        // Load the table types...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyFieldType property = new ConceptPropertyFieldType( name, FieldTypeSettings.getType( value ) );
          concept.addProperty( property );
        }
      } else if ( type.equals( ConceptPropertyType.AGGREGATION.getCode() ) ) {
        // Load the aggregation types...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyAggregation property =
            new ConceptPropertyAggregation( name, AggregationSettings.getType( value ) );
          concept.addProperty( property );
        }
      } else if ( type.equals( ConceptPropertyType.AGGREGATION_LIST.getCode() ) ) {
        // Load the Agg List properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          try {
            List<AggregationSettings> list = ConceptPropertyAggregationList.fromXML( value );
            ConceptPropertyInterface property = new ConceptPropertyAggregationList( name, list );
            concept.addProperty( property );
          } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.NUMBER.getCode() ) ) {
        // Load the number properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ConceptPropertyNumber property = new ConceptPropertyNumber( name, new BigDecimal( value ) );
          concept.addProperty( property );
        }
      } else if ( type.equals( ConceptPropertyType.COLOR.getCode() ) ) {
        // Load the color properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ColorSettings colorSettings = ColorSettings.fromString( value );
          if ( colorSettings != null ) {
            ConceptPropertyColor property = new ConceptPropertyColor( name, colorSettings );
            concept.addProperty( property );
          } else {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_FAILED_TO_PARSE_COLOR", value ) );
          }
        }
      } else if ( type.equals( ConceptPropertyType.DATATYPE.getCode() ) ) {
        // Load the data type properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          DataTypeSettings dataType = DataTypeSettings.fromString( value );
          if ( dataType != null ) {
            ConceptPropertyInterface property = new ConceptPropertyDataType( name, dataType );
            concept.addProperty( property );
          } else {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_FAILED_TO_PARSE_DATA_TYPE", value ) );
          }
        }
      } else if ( type.equals( ConceptPropertyType.FONT.getCode() ) ) {
        // Load the font settings properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          FontSettings fontSettings = FontSettings.fromString( value );
          if ( fontSettings != null ) {
            ConceptPropertyInterface property = new ConceptPropertyFont( name, fontSettings );
            concept.addProperty( property );
          } else {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_FAILED_TO_PARSE_FONT", value ) );
          }
        }
      } else if ( type.equals( ConceptPropertyType.URL.getCode() ) ) {
        // Load the font settings properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          try {
            URL url = new URL( value );
            ConceptPropertyInterface property = new ConceptPropertyURL( name, url );
            concept.addProperty( property );
          } catch ( Exception e ) {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_MALFORMED_URL", value ), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.SECURITY.getCode() ) ) {
        // Load the Security settings properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          try {
            Security security = Security.fromXML( value );
            ConceptPropertyInterface property = new ConceptPropertySecurity( name, security );
            concept.addProperty( property );
          } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.ROW_LEVEL_SECURITY.getCode() ) ) {
        // Load the Row Level Security settings properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          try {
            RowLevelSecurity rowLevelSecurity = RowLevelSecurity.fromXML( value );
            ConceptPropertyInterface property = new ConceptPropertyRowLevelSecurity( name, rowLevelSecurity );
            concept.addProperty( property );
          } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
          }
        }
      } else if ( type.equals( ConceptPropertyType.ALIGNMENT.getCode() ) ) {
        // Load the alignment properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          AlignmentSettings alignment = AlignmentSettings.fromString( value );
          if ( alignment != null ) {
            ConceptPropertyInterface property = new ConceptPropertyAlignment( name, alignment );
            concept.addProperty( property );
          } else {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_UNRECOGNIZED_ALIGNMENT_SETTING", value ) );
          }
        }
      } else if ( type.equals( ConceptPropertyType.COLUMN_WIDTH.getCode() ) ) {
        // Load the column width properties...
        //
        if ( !Const.isEmpty( name ) && !Const.isEmpty( value ) ) {
          ColumnWidth colWidth = ColumnWidth.fromString( value );
          if ( colWidth != null ) {
            ConceptPropertyInterface property = new ConceptPropertyColumnWidth( name, colWidth );
            concept.addProperty( property );
          } else {
            logger.error( Messages.getString( "CwmSchemaFactory.ERROR_UNRECOGNIZED_COLUMN_WIDTH", value ) );
          }
        }
      } else {
        logger.error( Messages.getString( "CwmSchemaFactory.ERROR_UNRECOGNIZED_TYPE", type ) );
      }
    }
  }

  /*
   * ___ _ ____ _ / _ \| | __ _ _ __ / ___| _| |__ ___ | | | | |/ _` | '_ \| | | | | | '_ \ / _ \ | |_| | | (_| | |_) |
   * |__| |_| | |_) | __/ \___/|_|\__,_| .__/ \____\__,_|_.__/ \___| |_|
   */

  private void storeOlapCube( CWM cwm, org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema, OlapCube cube ) {
    CwmCube cwmOlapCube = cwm.createOlapCube( cube.getName() );
    cwmOlapCube.setSchema( cwmOlapSchema );

    cwm.addTaggedValue( cwmOlapCube, CWM.TAG_CUBE_BUSINESS_TABLE, cube.getBusinessTable().getId() );

    // Store the used dimensions
    //
    List usages = cube.getOlapDimensionUsages();
    for ( int i = 0; i < usages.size(); i++ ) {
      OlapDimensionUsage usage = (OlapDimensionUsage) usages.get( i );

      // What is the CWM dimension used?
      //
      org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension = null;
      Collection dimensions = cwmOlapSchema.getDimension();
      for ( Iterator iter = dimensions.iterator(); iter.hasNext() && cwmOlapDimension == null; ) {
        org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension lookup =
          (org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension) iter.next();
        if ( lookup.getName().equals( usage.getName() ) ) {
          cwmOlapDimension = lookup;
        }
      }

      if ( cwmOlapDimension != null ) {
        // should be always the case unless there is a program error
        CwmCubeDimensionAssociation cwmCubeDimensionAssociation = cwm.createCubeDimensionAssocation( usage.getName() );
        cwmCubeDimensionAssociation.setCube( cwmOlapCube );
        cwmCubeDimensionAssociation.setDimension( cwmOlapDimension );
        @SuppressWarnings( "all" )
        Collection<CwmCubeDimensionAssociation> cu = cwmOlapCube.getCubeDimensionAssociation();
        cu.add( cwmCubeDimensionAssociation );
      }
    }

    // Store the measures too...
    List measures = cube.getOlapMeasures();
    for ( int i = 0; i < measures.size(); i++ ) {
      OlapMeasure olapMeasure = (OlapMeasure) measures.get( i );

      CwmMeasure cwmMeasure = cwm.createMeasure( olapMeasure.getName() );
      cwm.addTaggedValue( cwmMeasure, CWM.TAG_MEASURE_BUSINESS_COLUMN, olapMeasure.getBusinessColumn().getId() );
      @SuppressWarnings( "all" )
      Collection<CwmMeasure> cu = cwmOlapCube.getOwnedElement();

      cu.add( cwmMeasure ); // Not sure if this is the right thing to do
    }

    @SuppressWarnings( "all" )
    Collection<CwmCube> cu = cwmOlapSchema.getCube();
    cu.add( cwmOlapCube );
  }

  private OlapCube getOlapCube( CWM cwm, CwmCube cwmCube, BusinessModel businessModel ) {
    OlapCube olapCube = new OlapCube();
    olapCube.setName( cwmCube.getName() );

    olapCube.setBusinessTable( businessModel.findBusinessTable( cwm.getFirstTaggedValue( cwmCube,
      CWM.TAG_CUBE_BUSINESS_TABLE ) ) );

    Collection associations = cwmCube.getCubeDimensionAssociation();
    for ( Iterator iter = associations.iterator(); iter.hasNext(); ) {
      CwmCubeDimensionAssociation association = (CwmCubeDimensionAssociation) iter.next();

      org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension = association.getDimension();

      OlapDimension olapDimension = null;
      List olapDimensions = businessModel.getOlapDimensions();
      for ( Iterator iterator = olapDimensions.iterator(); iterator.hasNext() && olapDimension == null; ) {
        OlapDimension lookup = (OlapDimension) iterator.next();
        if ( lookup.getName().equals( cwmOlapDimension.getName() ) ) {
          olapDimension = lookup;
        }
      }

      if ( olapDimension != null ) {
        OlapDimensionUsage usage = new OlapDimensionUsage( association.getName(), olapDimension );
        olapCube.getOlapDimensionUsages().add( usage );
      }
    }

    Collection ownedElements = cwmCube.getOwnedElement();
    for ( Iterator iter = ownedElements.iterator(); iter.hasNext(); ) {
      CwmMeasure cwmMeasure = (CwmMeasure) iter.next();
      OlapMeasure olapMeasure = new OlapMeasure();

      olapMeasure.setName( cwmMeasure.getName() );
      BusinessColumn businessColumn =
        businessModel.findBusinessColumn( cwm.getFirstTaggedValue( cwmMeasure, CWM.TAG_MEASURE_BUSINESS_COLUMN ) );
      olapMeasure.setBusinessColumn( businessColumn );

      olapCube.getOlapMeasures().add( olapMeasure );
    }

    return olapCube;
  }

  /*
   * ___ _ ____ _ _ / _ \| | __ _ _ __ | _ \(_)_ __ ___ ___ _ __ ___(_) ___ _ __ | | | | |/ _` | '_ \| | | | | '_ ` _ \
   * / _ \ '_ \/ __| |/ _ \| '_ \ | |_| | | (_| | |_) | |_| | | | | | | | __/ | | \__ \ | (_) | | | | \___/|_|\__,_|
   * .__/|____/|_|_| |_| |_|\___|_| |_|___/_|\___/|_| |_| |_|
   */

  public void storeOlapDimension( CWM cwm, org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema,
                                  OlapDimension olapDimension ) {
    org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension =
      cwm.createOlapDimension( olapDimension.getName() );
    cwmOlapDimension.setTime( olapDimension.isTimeDimension() );

    List hierarchies = olapDimension.getHierarchies();
    for ( int i = 0; i < hierarchies.size(); i++ ) {
      OlapHierarchy olapHierarchy = (OlapHierarchy) hierarchies.get( i );
      storeOlapHierachy( cwm, olapHierarchy, cwmOlapDimension );
    }

    // Add this dimension to the OLAP schema
    @SuppressWarnings( "all" )
    Collection<org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension> cd = cwmOlapSchema.getDimension();
    cd.add( cwmOlapDimension );
  }

  public OlapDimension getOlapDimension( CWM cwm, org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension,
                                         BusinessModel businessModel ) {
    OlapDimension olapDimension = new OlapDimension();
    olapDimension.setName( cwmOlapDimension.getName() );
    olapDimension.setTimeDimension( cwmOlapDimension.isTime() );

    // Load the hierarchies...
    Collection cwmHierarchies = cwmOlapDimension.getHierarchy();
    for ( Iterator iter = cwmHierarchies.iterator(); iter.hasNext(); ) {
      CwmLevelBasedHierarchy cwmHierarchy = (CwmLevelBasedHierarchy) iter.next();
      OlapHierarchy olapHierarchy = getOlapHierarchy( cwm, cwmHierarchy, olapDimension, businessModel );
      olapDimension.getHierarchies().add( olapHierarchy );
    }

    return olapDimension;
  }

  /*
   * ___ _ _ _ _ _ / _ \| | __ _ _ __ | | | (_) ___ _ __ __ _ _ __ ___| |__ _ _ | | | | |/ _` | '_ \| |_| | |/ _ \ '__/
   * _` | '__/ __| '_ \| | | | | |_| | | (_| | |_) | _ | | __/ | | (_| | | | (__| | | | |_| | \___/|_|\__,_| .__/|_|
   * |_|_|\___|_| \__,_|_| \___|_| |_|\__, | |_| |___/
   */

  public void storeOlapHierachy( CWM cwm, OlapHierarchy olapHierarchy,
                                 org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension ) {
    CwmLevelBasedHierarchy cwmHierarchy = cwm.createLevelBasedHierarchy( olapHierarchy.getName() );
    cwmHierarchy.setDimension( cwmOlapDimension );

    cwm.addTaggedValue( cwmHierarchy, CWM.TAG_HIERARCHY_BUSINESS_TABLE, olapHierarchy.getBusinessTable().getId() );
    if ( olapHierarchy.getPrimaryKey() != null ) {
      cwm.addTaggedValue( cwmHierarchy, CWM.TAG_HIERARCHY_PRIMARY_KEY, olapHierarchy.getPrimaryKey().getId() );
    }
    cwm.addTaggedValue( cwmHierarchy, CWM.TAG_HIERARCHY_HAVING_ALL,
      olapHierarchy.isHavingAll() ? "Y" : "N" ); //$NON-NLS-1$ //$NON-NLS-2$

    // Save the levels of the hierarchy too
    List levels = olapHierarchy.getHierarchyLevels();
    for ( int i = 0; i < levels.size(); i++ ) {
      OlapHierarchyLevel level = (OlapHierarchyLevel) levels.get( i );
      storeOlapHierarchyLevel( cwm, level, cwmHierarchy, cwmOlapDimension );
    }

    @SuppressWarnings( "all" )
    Collection<CwmLevelBasedHierarchy> ch = cwmOlapDimension.getHierarchy();
    ch.add( cwmHierarchy );
  }

  public OlapHierarchy getOlapHierarchy( CWM cwm, CwmLevelBasedHierarchy cwmHierarchy, OlapDimension olapDimension,
                                         BusinessModel businessModel ) {
    OlapHierarchy olapHierarchy = new OlapHierarchy( olapDimension );
    olapHierarchy.setName( cwmHierarchy.getName() );

    olapHierarchy.setBusinessTable( businessModel.findBusinessTable( cwm.getFirstTaggedValue( cwmHierarchy,
      CWM.TAG_HIERARCHY_BUSINESS_TABLE ) ) );
    olapHierarchy.setPrimaryKey( businessModel.findBusinessColumn( cwm.getFirstTaggedValue( cwmHierarchy,
      CWM.TAG_HIERARCHY_PRIMARY_KEY ) ) );
    olapHierarchy
      .setHavingAll(
        "Y".equalsIgnoreCase( cwm.getFirstTaggedValue( cwmHierarchy, CWM.TAG_HIERARCHY_HAVING_ALL ) ) ); //$NON-NLS-1$

    // Where are the levels?
    List associations = cwmHierarchy.getHierarchyLevelAssociation();
    for ( Iterator iter = associations.iterator(); iter.hasNext(); ) {
      CwmHierarchyLevelAssociation association = (CwmHierarchyLevelAssociation) iter.next();
      OlapHierarchyLevel olapLevel = getOlapHierarchyLevel( cwm, olapHierarchy, association, businessModel );
      olapHierarchy.getHierarchyLevels().add( olapLevel );
    }

    return olapHierarchy;
  }

  /*
   * ___ _ _ _ _ _ _ _ / _ \| | __ _ _ __ | | | (_) ___ _ __ __ _ _ __ ___| |__ _ _| | _____ _____| | | | | | |/ _` | '_
   * \| |_| | |/ _ \ '__/ _` | '__/ __| '_ \| | | | | / _ \ \ / / _ \ | | |_| | | (_| | |_) | _ | | __/ | | (_| | | |
   * (__| | | | |_| | |__| __/\ V / __/ | \___/|_|\__,_| .__/|_| |_|_|\___|_| \__,_|_| \___|_| |_|\__, |_____\___| \_/
   * \___|_| |_| |___/
   */

  public void storeOlapHierarchyLevel( CWM cwm, OlapHierarchyLevel level, CwmLevelBasedHierarchy cwmLevelBasedHierarchy,
                                       org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension ) {
    // Create a new level for this dimension
    CwmLevel cwmLevel = cwm.createLevel( level.getName() );
    cwmLevel.setDimension( cwmOlapDimension );

    // Make an association between the hierarchy and the level...
    CwmHierarchyLevelAssociation cwmHierarchyLevelAssociation = cwm.createHierarchyLevelAssociation( level.getName() );
    cwmHierarchyLevelAssociation.setCurrentLevel( cwmLevel );

    // Add it to the list: no need, this is already done.
    @SuppressWarnings( "all" )
    Collection<CwmHierarchyLevelAssociation> ch = cwmLevelBasedHierarchy.getHierarchyLevelAssociation();
    ch.add( cwmHierarchyLevelAssociation );

    // Now also save the other attributes of the level... (excluding the columns)
    // Unique members flag
    cwm.addTaggedValue( cwmLevel, CWM.TAG_HIERARCHY_LEVEL_UNIQUE_MEMBERS,
      level.isHavingUniqueMembers() ? "Y" : "N" ); //$NON-NLS-1$ //$NON-NLS-2$
    // The reference column (business column ID)
    cwm.addTaggedValue( cwmLevel, CWM.TAG_HIERARCHY_LEVEL_REFERENCE_COLUMN, level.getReferenceColumn().getId() );

    // Add the column ID's
    List columns = level.getBusinessColumns();
    for ( int i = columns.size() - 1; i >= 0; i-- ) {
      // load back in reverse, CWM does this
      BusinessColumn businessColumn = (BusinessColumn) columns.get( i );
      // We want the reference to the business columns that were saved earlier.
      // Let's create new ones and save the ID's of the columns
      CwmDimensionedObject cwmDimensionedObject = cwm.createDimensionedObject( businessColumn.getId() );
      @SuppressWarnings( "unchecked" )
      Collection<CwmDimensionedObject> c = cwmLevel.getOwnedElement();
      c.add( cwmDimensionedObject );
    }
  }

  public OlapHierarchyLevel getOlapHierarchyLevel( CWM cwm, OlapHierarchy olapHierarchy,
                                                   CwmHierarchyLevelAssociation association,
                                                   BusinessModel businessModel ) {
    OlapHierarchyLevel olapLevel = new OlapHierarchyLevel( olapHierarchy );
    olapLevel.setName( association.getName() );

    CwmLevel cwmLevel = association.getCurrentLevel();

    // Unique members flag
    olapLevel
      .setHavingUniqueMembers( "Y".equalsIgnoreCase(
        cwm.getFirstTaggedValue( cwmLevel, CWM.TAG_HIERARCHY_LEVEL_UNIQUE_MEMBERS ) ) ); //$NON-NLS-1$
    // The reference column (business column ID)
    olapLevel.setReferenceColumn( businessModel.findBusinessColumn( cwm.getFirstTaggedValue( cwmLevel,
      CWM.TAG_HIERARCHY_LEVEL_REFERENCE_COLUMN ) ) );

    // The extra columns...
    Collection ownedElements = cwmLevel.getOwnedElement();
    for ( Iterator iter = ownedElements.iterator(); iter.hasNext(); ) {
      CwmDimensionedObject cwmDimensionedObject = (CwmDimensionedObject) iter.next();
      BusinessColumn column = businessModel.findBusinessColumn( cwmDimensionedObject.getName() );
      olapLevel.getBusinessColumns().add( column );
    }

    return olapLevel;
  }

  /**
   * The aclHolder cannot be null unless the access type requested is ACCESS_TYPE_SCHEMA_ADMIN.
   */
  public boolean hasAccess( int accessType, ConceptUtilityInterface aclHolder ) {
    // Subclasses can override this for ACL and Session/Credential checking
    return true;
  }

  public String generateRowLevelSecurityConstraint( BusinessModel businessModel ) {
    // subclass this method to implement row level security
    return null;
  }

}
