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

package org.pentaho.pms.factory;

import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.CwmParameter;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmClass;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmModelElement;
import org.pentaho.pms.cwm.pentaho.meta.instance.CwmExtent;
import org.pentaho.pms.cwm.pentaho.meta.keysindexes.CwmKeyRelationship;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimension;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimensionedObject;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmSchema;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmHierarchyLevelAssociation;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmLevelBasedHierarchy;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmCatalog;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmColumn;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmTable;
import org.pentaho.pms.locale.LocaleInterface;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.olap.OlapDimension;
import org.pentaho.pms.schema.olap.OlapHierarchy;
import org.pentaho.pms.schema.olap.OlapHierarchyLevel;
import org.pentaho.pms.schema.security.SecurityService;

@SuppressWarnings( "deprecation" )
public interface CwmSchemaFactoryInterface {

  public static final int ACCESS_TYPE_READ = 0;
  public static final int ACCESS_TYPE_WRITE = 1;
  public static final int ACCESS_TYPE_UPDATE = 2;
  public static final int ACCESS_TYPE_DELETE = 3;
  public static final int ACCESS_TYPE_ADMIN = 4;
  public static final int ACCESS_TYPE_SCHEMA_ADMIN = 5;

  /**
   * This method allows you to store a complete schema (model) into the CWM using the MDR
   * 
   * @param cwm
   *          The model to use
   * @param schemaMeta
   *          The meta-data to store into the cwm model
   * @throws Exception
   *           when the model could not be saved.
   */
  public void storeSchemaMeta( CWM cwm, SchemaMeta schemaMeta, ProgressMonitorListener monitor ) throws Exception;

  /**
   * Load schema from a CWM model
   * 
   * @param cwm
   *          The model to load
   * @return a newly created SchemaMeta object.
   */
  public SchemaMeta getSchemaMeta( CWM cwm );

  /*
   * ____ / ___| ___ \___ \ / _ \ ___) | __/ |____/ \___|curity Service
   */

  /**
   * Store the security service parameters in the CWM model
   * 
   * @param cwm
   *          The CWM model to store in
   * @param securityService
   *          The security service to store
   */
  public void storeSecurityService( CWM cwm, SecurityService securityService );

  /**
   * Load the security service configuration information from the CWM model
   * 
   * @param cwm
   *          The CWM model
   * @return a new security service object
   */
  public SecurityService getSecurityService( CWM cwm );

  /*
   * ____ _ _ _ _____ _ _ | _ \| |__ _ _ ___(_) ___ __ _| |_ _|_ _| |__ | | ___ | |_) | '_ \| | | / __| |/ __/ _` | | |
   * |/ _` | '_ \| |/ _ \ | __/| | | | |_| \__ \ | (_| (_| | | | | (_| | |_) | | __/ |_| |_| |_|\__, |___/_|\___\__,_|_|
   * |_|\__,_|_.__/|_|\___| |___/
   */
  /**
   * Stores the Kettle table metadata into the CWM model
   * 
   * @param cwm
   *          The CWM model
   * @param physicalTable
   *          the Kettle table metadata to store
   * @return the created CwmTable
   */
  public CwmTable storePhysicalTable( CWM cwm, PhysicalTable physicalTable );

  /**
   * Load a physical table from a CWM model using a cwmTable reference and a list of available databases.
   * 
   * @param cwm
   * @param cwmTable
   * @param databases
   * @return a new PhysicalTable object
   */
  public PhysicalTable getPhysicalTable( CWM cwm, CwmTable cwmTable, SchemaMeta schemaMeta );

  /*
   * ____ _ _ _ ____ _ | _ \| |__ _ _ ___(_) ___ __ _| |/ ___|___ | |_ _ _ __ ___ _ __ | |_) | '_ \| | | / __| |/ __/ _`
   * | | | / _ \| | | | | '_ ` _ \| '_ \ | __/| | | | |_| \__ \ | (_| (_| | | |__| (_) | | |_| | | | | | | | | | |_| |_|
   * |_|\__, |___/_|\___\__,_|_|\____\___/|_|\__,_|_| |_| |_|_| |_| |___/
   */

  /**
   * Store a physical column into the CWM metamodel
   * 
   * @param cwm
   *          The model to store in
   * @param cwmTable
   *          The parent table
   * @param physicalColumn
   *          the physical column to store
   */
  public void storePhysicalColumn( CWM cwm, CwmTable cwmTable, PhysicalColumn physicalColumn );

  /**
   * Load a physical column from the CWM metamodel
   * 
   * @param cwm
   *          the model to load from
   * @param column
   *          The CwmColumn to load the information from
   * @param physicalTable
   *          The physcial table to reference
   * @param schemaMeta
   *          The schema
   * @return a new created Physical column, loaded from the cwm metamodel
   */
  public PhysicalColumn
    getPhysicalColumn( CWM cwm, CwmColumn column, PhysicalTable physicalTable, SchemaMeta schemaMeta );

  /*
   * ____ _ _ __ __ _ | _ \ __ _| |_ __ _| |__ __ _ ___ ___| \/ | ___| |_ __ _ | | | |/ _` | __/ _` | '_ \ / _` / __|/ _
   * \ |\/| |/ _ \ __/ _` | | |_| | (_| | || (_| | |_) | (_| \__ \ __/ | | | __/ || (_| | |____/ \__,_|\__\__,_|_.__/
   * \__,_|___/\___|_| |_|\___|\__\__,_|
   */

  /**
   * Uility method to store Kettle Database Metadata
   * 
   * @param cwm
   *          The model to store it in
   * @param databaseMeta
   *          The Kettle database connection metadata to use.
   */
  public void storeDatabaseMeta( CWM cwm, DatabaseMeta databaseMeta );

  /**
   * Read a DatabaseMeta from a CWM model by providing the catalog reference.
   * 
   * @param cwm
   * @param catalog
   * @return a new DatabaseMeta instance, read from the specified CWM model.
   * @throws KettleXMLException
   */
  public DatabaseMeta getDatabaseMeta( CWM cwm, CwmCatalog catalog );

  /*
   * ____ _ __ ___ | __ ) _ _ ___(_)_ __ ___ ___ __\ \ / (_) _____ __ | _ \| | | / __| | '_ \ / _ \/ __/ __\ \ / /| |/ _
   * \ \ /\ / / | |_) | |_| \__ \ | | | | __/\__ \__ \\ V / | | __/\ V V / |____/ \__,_|___/_|_| |_|\___||___/___/ \_/
   * |_|\___| \_/\_/
   */

  /**
   * This method stores a business model in a CwmSchema. The schema then in turn contains a number of
   * 
   * @param cwm
   *          The model to store in
   * @param businessModel
   *          The business model to store into the selected CWM model.
   */
  public void storeBusinessModel( CWM cwm, BusinessModel businessModel );

  /**
   * Load a business model from a CWM model by loading it from the supplied CwmSchema and using the SchemaMeta object
   * for reference.
   * 
   * @param cwm
   * @param cwmSchema
   * @param schemaMeta
   * @return a newly created Business Model
   */
  public BusinessModel getBusinessModel( CWM cwm, CwmSchema cwmSchema, SchemaMeta schemaMeta );

  /*
   * ____ _ _____ _ _ | __ ) _ _ ___(_)_ __ ___ ___ __|_ _|_ _| |__ | | ___ | _ \| | | / __| | '_ \ / _ \/ __/ __|| |/
   * _` | '_ \| |/ _ \ | |_) | |_| \__ \ | | | | __/\__ \__ \| | (_| | |_) | | __/ |____/ \__,_|___/_|_|
   * |_|\___||___/___/|_|\__,_|_.__/|_|\___|
   */

  /**
   * Store a business table in the MDR
   * 
   * @param cwm
   *          The model to store in
   * @param cwmSchema
   *          The CWM Schema to put the tables into.
   * @param businessTable
   *          The business table to store.
   */
  public void storeBusinessTable( CWM cwm, CwmSchema cwmSchema, BusinessTable businessTable );

  /**
   * Load a BusinessTable from a CWM model by taking information from a supplied CwmDimension
   * 
   * @param cwm
   * @param cwmDimension
   * @param schemaMeta
   * @return a newly created BusinessTable
   */
  public BusinessTable getBusinessTable( CWM cwm, CwmDimension cwmDimension, SchemaMeta schemaMeta,
      BusinessModel businessModel );

  /*
   * ____ _ ____ _ | __ ) _ _ ___(_)_ __ ___ ___ ___ / ___|___ | |_ _ _ __ ___ _ __ | _ \| | | / __| | '_ \ / _ \/ __/
   * __| | / _ \| | | | | '_ ` _ \| '_ \ | |_) | |_| \__ \ | | | | __/\__ \__ \ |__| (_) | | |_| | | | | | | | | |
   * |____/ \__,_|___/_|_| |_|\___||___/___/\____\___/|_|\__,_|_| |_| |_|_| |_|
   */

  /**
   * Store the information from a business column in a dimensioned object in the MDR
   * 
   * @param cwm
   *          the model to store in
   * @param cwmSchema
   *          The CWM schema this object belongs to
   * @param cwmDimension
   *          The owner of the business column
   * @param businessColumn
   *          The business column to store
   */
  public void storeBusinessColumn( CWM cwm, CwmSchema cwmSchema, CwmDimension cwmDimension,
      BusinessColumn businessColumn );

  /**
   * Create a business column from a dimensioned object...
   * 
   * @param cwm
   * @param cwmDimensionedObject
   * @param physicalTable
   * @return The newly created business column
   */
  public BusinessColumn getBusinessColumn( CWM cwm, CwmDimensionedObject cwmDimensionedObject,
      PhysicalTable physicalTable, BusinessTable businessTable, SchemaMeta schemaMeta );

  /*
   * ____ _ _ _ _ _ __ __ _ | _ \ ___| | __ _| |_(_) ___ _ __ ___| |__ (_)_ __ | \/ | ___| |_ __ _ | |_) / _ \ |/ _` |
   * __| |/ _ \| '_ \/ __| '_ \| | '_ \| |\/| |/ _ \ __/ _` | | _ < __/ | (_| | |_| | (_) | | | \__ \ | | | | |_) | | |
   * | __/ || (_| | |_| \_\___|_|\__,_|\__|_|\___/|_| |_|___/_| |_|_| .__/|_| |_|\___|\__\__,_| |_|
   */

  /**
   * Stores a Kettle relationshipMeta object into the MDR
   * 
   * @param cwm
   *          The MDR CWM model instantiation to use.
   * @param relationshipMeta
   *          the relationshipMeta object to store.
   * @param cwmSchema
   *          The schema to which the relationship belongs
   */
  public void storeRelationshipMeta( CWM cwm, RelationshipMeta relationshipMeta, CwmSchema cwmSchema );

  /**
   * Create a new RelationshipMeta object from a CWM model by looking at the CwmKeyRelationship and the BusinessModel
   * 
   * @param cwm
   * @param relationship
   * @param businessModel
   * @return a newly created RelationshipMeta object
   */
  public RelationshipMeta getRelationshipMeta( CWM cwm, CwmKeyRelationship relationship, BusinessModel businessModel );

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
  public void storeNotePadMeta( CWM cwm, CwmSchema cwmSchema, NotePadMeta notePadMeta );

  /**
   * Loads a NotePadMeta object from a CWM model using the supplied CWM Description object
   * 
   * @param cwm
   * @param cwmDescription
   * @return a newly created NotePadMeta object
   */
  public NotePadMeta getNotePadMeta( CWM cwm, CwmDescription cwmDescription );

  /*
   * ____ _ ____ _ | __ ) _ _ ___(_)_ __ ___ ___ ___ / ___|__ _| |_ ___ __ _ ___ _ __ _ _ | _ \| | | / __| | '_ \ / _ \/
   * __/ __| | / _` | __/ _ \/ _` |/ _ \| '__| | | | | |_) | |_| \__ \ | | | | __/\__ \__ \ |__| (_| | || __/ (_| | (_)
   * | | | |_| | |____/ \__,_|___/_|_| |_|\___||___/___/\____\__,_|\__\___|\__, |\___/|_| \__, | |___/ |___/
   */

  /**
   * Store the business category specified into the given cwm model
   * 
   * @param cwm
   *          the model to store in
   * @param businessCategory
   *          the businessCategory to store
   * @param level
   *          root = 0
   * @param cwmSchema
   *          the cwmSchema to reference.
   */
  public CwmExtent storeBusinessCategory( CWM cwm, BusinessCategory businessCategory, CwmExtent parent,
      CwmSchema cwmSchema );

  /**
   * @param cwm
   *          The model
   * @param cwmExtent
   *          The extent to construct the category from
   * @param businessModel
   *          The business model to reference.
   * @return A new business category
   */
  public BusinessCategory getBusinessCategory( CWM cwm, CwmExtent cwmExtent, BusinessModel businessModel,
      SchemaMeta schemaMeta );

  /*
   * _ _ | | ___ ___ __ _| | ___ | | / _ \ / __/ _` | |/ _ \ | |__| (_) | (_| (_| | | __/ |_____\___/ \___\__,_|_|\___|
   */

  /**
   * Store the locale meta-data into the CWM repository using CwmParamter objects.
   * 
   * @param cwm
   *          The model to store in
   * @param locale
   *          the locale to store
   */
  public void storeLocale( CWM cwm, LocaleInterface locale );

  /**
   * Create a new locale by reading it from a CWM meta-data model
   * 
   * @param cwm
   *          The CWM model to read from
   * @param cwmParameter
   *          The CwmParameter object to use
   * @return a newly created LocaleInterface class (LocaleMeta)
   */
  public LocaleInterface getLocale( CWM cwm, CwmParameter cwmParameter );

  /*
   * ____ _ / ___|___ _ __ ___ ___ _ __ | |_ | | / _ \| '_ \ / __/ _ \ '_ \| __| | |__| (_) | | | | (_| __/ |_) | |_
   * \____\___/|_| |_|\___\___| .__/ \__| |_|
   */

  /**
   * Store the concept into the CWM model
   * 
   * @param cwm
   *          the model to store in
   * @param concept
   *          the concept to store
   */
  public void storeModelConcept( CWM cwm, ConceptInterface concept );

  /**
   * construct a new Concept by loading it from a model
   * 
   * @param cwm
   *          The model to load from
   * @param cwmClass
   *          the CwmClass object that is used to represent the concept
   * @param schemaMeta
   *          the schema metadata to reference parents, etc.
   * @return the newly created concept, with information loaded from the CWM model.
   */
  public ConceptInterface getModelConcept( CWM cwm, CwmClass cwmClass, SchemaMeta schemaMeta );

  /*
   * ____ _ ____ _ _ / ___|___ _ __ ___ ___ _ __ | |_| _ \ _ __ ___ _ __ ___ _ __| |_(_) ___ ___ | | / _ \| '_ \ / __/ _
   * \ '_ \| __| |_) | '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __| | |__| (_) | | | | (_| __/ |_) | |_| __/| | | (_) | |_)
   * | __/ | | |_| | __/\__ \ \____\___/|_| |_|\___\___| .__/ \__|_| |_| \___/| .__/ \___|_| \__|_|\___||___/ |_| |_|
   */

  /**
   * Store the concept properties into the CWM model
   * 
   * @param cwm
   *          the model to store the concept properties in
   * @param modelElement
   *          The model element to attach it to.
   * @param conceptUtilityInterface
   *          The concept utility interface to use. (the properties)
   */
  public void storeConceptProperties( CWM cwm, CwmModelElement modelElement, ConceptInterface concept );

  public void getConceptProperties( CWM cwm, CwmModelElement modelElement, ConceptInterface concept,
      SchemaMeta schemaMeta );

  /**
   * returns a single MQL Row Level Security constraint string based on the current business model. A null returned
   * means that no security constraint should be applied
   * 
   * @param businessModel
   *          the business model
   * 
   * @return mql string
   */
  public String generateRowLevelSecurityConstraint( BusinessModel businessModel );

  /**
   * Decides whether you can access the object
   * 
   * @param accessType
   *          see the access types at the top
   * @param aclHolder
   *          An object that has an ACL list
   * @return true if the operation is allowed.
   */
  public boolean hasAccess( int accessType, ConceptUtilityInterface aclHolder );

  /* New OLAP Methods */

  public void storeOlapDimension( CWM cwm, org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema,
      OlapDimension olapDimension );

  public void storeOlapHierachy( CWM cwm, OlapHierarchy olapHierarchy,
      org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension );

  public void
    storeOlapHierarchyLevel( CWM cwm, OlapHierarchyLevel level, CwmLevelBasedHierarchy cwmLevelBasedHierarchy,
        org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension );

  public OlapDimension getOlapDimension( CWM cwm, org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmOlapDimension,
      BusinessModel businessModel );

  public OlapHierarchy getOlapHierarchy( CWM cwm, CwmLevelBasedHierarchy cwmHierarchy, OlapDimension olapDimension,
      BusinessModel businessModel );

  public OlapHierarchyLevel getOlapHierarchyLevel( CWM cwm, OlapHierarchy olapHierarchy,
      CwmHierarchyLevelAssociation association, BusinessModel businessModel );

}
