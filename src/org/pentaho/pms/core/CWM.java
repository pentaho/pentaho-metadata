/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.MalformedXMIException;

import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReader;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.netbeans.api.xmi.XMIWriter;
import org.netbeans.api.xmi.XMIWriterFactory;
import org.netbeans.mdr.NBMDRepositoryImpl;
import org.netbeans.mdr.persistence.btreeimpl.btreestorage.BtreeFactory;
import org.pentaho.pms.core.exception.CWMException;
import org.pentaho.pms.cwm.pentaho.PentahoPackage;
import org.pentaho.pms.cwm.pentaho.meta.MetaPackage;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.BehavioralPackage;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.CwmEvent;
import org.pentaho.pms.cwm.pentaho.meta.behavioral.CwmParameter;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.BusinessInformationPackage;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDocument;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.ModelElementDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CorePackage;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmAttribute;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmClass;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmDataType;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmExpression;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmModelElement;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmNamespace;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmPackage;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmTaggedValue;
import org.pentaho.pms.cwm.pentaho.meta.core.TaggedElement;
import org.pentaho.pms.cwm.pentaho.meta.instance.CwmExtent;
import org.pentaho.pms.cwm.pentaho.meta.instance.InstancePackage;
import org.pentaho.pms.cwm.pentaho.meta.keysindexes.CwmKeyRelationship;
import org.pentaho.pms.cwm.pentaho.meta.keysindexes.KeysIndexesPackage;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimension;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmDimensionedObject;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.CwmSchema;
import org.pentaho.pms.cwm.pentaho.meta.multidimensional.MultidimensionalPackage;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmCube;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmCubeDimensionAssociation;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmHierarchyLevelAssociation;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmLevel;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmLevelBasedHierarchy;
import org.pentaho.pms.cwm.pentaho.meta.olap.CwmMeasure;
import org.pentaho.pms.cwm.pentaho.meta.olap.OlapPackage;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmCatalog;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmColumn;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmTable;
import org.pentaho.pms.cwm.pentaho.meta.relational.RelationalPackage;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.value.Value;


/**
 *
 *  This singleton was written to allow you to access the CWM meta-data store in a more convenient way.
      
 * @author Matt
 * @since  2006/07/25
 *
 */
public class CWM
{
    public static final LogWriter log = LogWriter.getInstance();

    public static final String CWM = "CWM-Model-M3"; //$NON-NLS-1$
    
    public static final String PENTAHO         = "Pentaho"; //$NON-NLS-1$
    public static final String META            = "Meta"; //$NON-NLS-1$
    
    /* 
     * Extra TABLE information, added through tag-value pairs 
     */

    /** The target table (physical table on a database connection (catalog) */
    public static final String TAG_TABLE_TARGET_TABLE    = "TABLE_TARGET_TABLE"; //$NON-NLS-1$
    
    /** The target table database name, connection on which this table resides, references the catalog name */
    public static final String TAG_TABLE_TARGET_DATABASE = "TABLE_TARGET_DATABASE_NAME"; //$NON-NLS-1$

    /** Tag to allow us to see if the table is drawn on the canvas or not (Y/N=not stored) */
    public static final String TAG_TABLE_IS_DRAWN = "TABLE_IS_DRAWN"; //$NON-NLS-1$

    /** tag to store value of the type of table : Other, Dimension, Fact, ... */
    public static final String TAG_TABLE_TYPE = "TABLE_TYPE"; //$NON-NLS-1$

    /** tag to store value of the relative size of table (Integer value) */
    public static final String TAG_TABLE_SIZE = "TABLE_SIZE"; //$NON-NLS-1$

    /* 
     * Extra COLUMN information, added through tag-value pairs 
     */

    /** The formula that physically describes a table column, usually this is the fieldname, but can also be an SQL calculation, count(*) etc. */
    public static final String TAG_COLUMN_FORMULA        = "COLUMN_FORMULA"; //$NON-NLS-1$

    /** Stored the aggregation type */
    public static final String TAG_COLUMN_AGGREGATION_TYPE = "TAG_COLUMN_AGGREGATION_TYPE"; //$NON-NLS-1$

    /** tag to store value of the type of column field we're dealing with: Dimension, Fact, Key, ... */
    public static final String TAG_COLUMN_FIELD_TYPE = "COLUMN_FIELD_TYPE"; //$NON-NLS-1$

    /** tag to store value of the hidden flag of a column */
    public static final String TAG_COLUMN_IS_HIDDEN = "COLUMN_IS_HIDDEN"; //$NON-NLS-1$

    /** tag to store value of the exact flag of a column */
    public static final String TAG_COLUMN_IS_EXACT = "COLUMN_IS_EXACT"; //$NON-NLS-1$
    
    /* 
     * Extra DATABASE information, added through tag-value pairs 
     */

    public static final String TAG_DATABASE_SERVER           = "DATABASE_SERVER"; //$NON-NLS-1$
    public static final String TAG_DATABASE_TYPE             = "DATABASE_TYPE";  //$NON-NLS-1$
    public static final String TAG_DATABASE_ACCESS           = "DATABASE_ACCESS"; //$NON-NLS-1$
    public static final String TAG_DATABASE_DATABASE         = "DATABASE_DATABASE"; //$NON-NLS-1$
    public static final String TAG_DATABASE_PORT             = "DATABASE_PORT"; //$NON-NLS-1$ 
    public static final String TAG_DATABASE_USERNAME         = "DATABASE_USERNAME"; //$NON-NLS-1$
    public static final String TAG_DATABASE_PASSWORD         = "DATABASE_PASSWORD"; //$NON-NLS-1$
    public static final String TAG_DATABASE_SERVERNAME       = "DATABASE_SERVERNAME"; //$NON-NLS-1$
    public static final String TAG_DATABASE_DATA_TABLESPACE  = "DATABASE_DATA_TABLESPACE"; //$NON-NLS-1$
    public static final String TAG_DATABASE_INDEX_TABLESPACE = "DATABASE_INDEX_TABLESPACE"; //$NON-NLS-1$
    public static final String TAG_DATABASE_ATTRIBUTE_PREFIX = "DATABASE_ATTRIBUTE_PREFIX_"; //$NON-NLS-1$
    
    /** calculated for use by other Pentaho Tools */
    public static final String TAG_DATABASE_JDBC_URL         = "DATABASE_JDBC_URL"; //$NON-NLS-1$

    /* 
     * Extra general GUI information, added through tag-value pairs 
     */
    
    /** The tag for the X-position of a GUI model element */
    private static final String TAG_POSITION_X = "TAG_POSITION_X"; //$NON-NLS-1$
    
    /** The tag for the Y-position of a GUI model element */
    private static final String TAG_POSITION_Y = "TAG_POSITION_Y"; //$NON-NLS-1$

    /** The tag for the width of a GUI model element */
    private static final String TAG_POSITION_WIDTH = "TAG_POSITION_WIDTH"; //$NON-NLS-1$
    
    /** The tag for the height of a GUI model element */
    private static final String TAG_POSITION_HEIGHT = "TAG_POSITION_HEIGHT"; //$NON-NLS-1$

    /* 
     * Extra RELATIONSHIP information, added through tag-value pairs 
     */

    /** tag to indicate the parent table name of the relationship */ 
    public static final String TAG_RELATIONSHIP_TABLENAME_PARENT = "RELATIONSHIP_TABLENAME_PARENT"; //$NON-NLS-1$

    /** tag to indicate the child table name of the relationship */ 
    public static final String TAG_RELATIONSHIP_TABLENAME_CHILD = "RELATIONSHIP_TABLENAME_CHILD"; //$NON-NLS-1$

    /** tag to indicate the fieldname in the parent table of the relationship */ 
    public static final String TAG_RELATIONSHIP_FIELDNAME_PARENT = "RELATIONSHIP_FIELDNAME_PARENT"; //$NON-NLS-1$

    /** tag to indicate the fieldname in the child table of the relationship */ 
    public static final String TAG_RELATIONSHIP_FIELDNAME_CHILD = "RELATIONSHIP_FIELDNAME_CHILD"; //$NON-NLS-1$

    /** tag to indicate the complex join expression in the relationship */
    public static final String TAG_RELATIONSHIP_COMPLEX_JOIN = "RELATIONSHIP_COMPLEX_JOIN"; //$NON-NLS-1$

    /** tag to store value to indicate wheter the relationship is complex (Y/N=not stored) */ 
    public static final String TAG_RELATIONSHIP_IS_COMPLEX = "RELATIONSHIP_IS_COMPLEX"; //$NON-NLS-1$

    /** tag to store value of the relationship type, for example 1:1, 1:N, N:N, 0:N, etc. */
    public static final String TAG_RELATIONSHIP_TYPE = "RELATIONSHIP_TYPE"; //$NON-NLS-1$

    /* 
     * Extra DOCUMENT information, added through tag-value pairs 
     */

    /** Tag to locate the value of the content of the document */
    public static final String TAG_DOCUMENT_CONTENT = "DOCUMENT_CONTENT"; //$NON-NLS-1$


    /*
     * Extra Business Table information, physical table name, column name, etc.
     */
    
    /** business column tag to identify the physical column name (id) */
    public static final String TAG_BUSINESS_TABLE_PHYSICAL_TABLE_NAME = "BUSINESS_TABLE_PHYSICAL_TABLE_NAME"; //$NON-NLS-1$

    /*
     * Extra Business Column information, physical table name, column name, etc.
     */
    
    /** business column tag to identify the physical column name (id) */
    public static final String TAG_BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME = "BUSINESS_COLUMN_PHYSICAL_COLUMN_NAME"; //$NON-NLS-1$

    /** business column tag to identify the name of the business table */
    public static final String TAG_BUSINESS_COLUMN_BUSINESS_TABLE = "BUSINESS_COLUMN_BUSINESS_TABLE"; //$NON-NLS-1$
    
    /*
     * Extra Business Category information, tied to the extent.
     */
    /** tag to indicate that this extent is one at the root */
    public static final String TAG_BUSINESS_CATEGORY_ROOT     = "BUSINESS_CATEGORY_ROOT"; //$NON-NLS-1$

    /** The business category type value can either be Category or Column */
    public static final String TAG_BUSINESS_CATEGORY_TYPE     = "BUSINESS_CATEGORY_TYPE"; //$NON-NLS-1$
    public static final String VALUE_BUSINESS_TYPE_CATEGORY = "Category"; //$NON-NLS-1$
    public static final String VALUE_BUSINESS_TYPE_COLUMN   = "Column"; //$NON-NLS-1$

    /*
     * Extra Concept information tags.
     */
    /** tag to indicate that the value stored in the tagged value is the name of the parent of the concept */
    public static final String TAG_CONCEPT_PARENT_NAME = "CONCEPT_PARENT_NAME"; //$NON-NLS-1$

    /*
     * Extra Locale information tags.
     */
    /** The description of a locale */
    public static final String TAG_LOCALE_DESCRIPTION = "LOCALE_DESCRIPTION"; //$NON-NLS-1$
    /** The order of fallback of a locale */
    public static final String TAG_LOCALE_ORDER       = "LOCALE_ORDER"; //$NON-NLS-1$
    /** boolean : is the locale the default yes or no : Y/N */
    public static final String TAG_LOCALE_IS_ACTIVE  = "LOCALE_IS_DEFAULT"; //$NON-NLS-1$

    /* 
     * Other constants... 
     */

    /** The language of the notepad descriptions: PENTAHO_MODEL */
    public static final String LANGUAGE_PENTAHO_MODEL = "PENTAHO_MODEL"; //$NON-NLS-1$

    /** The type of a description... */
    public static final String DESCRIPTION_TYPE_NOTEPAD = "NOTEPAD"; //$NON-NLS-1$

    /** The security service parameter */
    public static final String EVENT_SECURITY_SERVICE = "SECURITY_SERVICE"; //$NON-NLS-1$

    /** The tag for the security service base URL */
    public static final String TAG_SECURITY_SERVICE_URL = "SECURITY_SERVICE_URL"; //$NON-NLS-1$

    /** The tag for the security details service name */
    public static final String TAG_SECURITY_DETAILS_NAME = "SECURITY_DETAILS_NAME"; //$NON-NLS-1$

    /** The tag for the security detail service name */
    public static final String TAG_SECURITY_DETAIL_NAME = "SECURITY_DETAIL_NAME"; //$NON-NLS-1$

    /** The tag for the security detail service type (all, users, roles, acls) */
    public static final String TAG_SECURITY_DETAIL_TYPE = "SECURITY_DETAIL_TYPE"; //$NON-NLS-1$

    /** The tag for the security service username */
    public static final String TAG_SECURITY_USERNAME = "SECURITY_USERNAME"; //$NON-NLS-1$

    /** The tag for the security service password */
    public static final String TAG_SECURITY_PASSWORD = "SECURITY_PASSWORD"; //$NON-NLS-1$

    /** The tag for the security service proxy hostname */
    public static final String TAG_SECURITY_PROXY_HOST = "SECURITY_PROXY_HOST"; //$NON-NLS-1$

    /** The tag for the security service proxy port */
    public static final String TAG_SECURITY_PROXY_PORT = "SECURITY_PROXY_PORT"; //$NON-NLS-1$

    /** The tag for the security service non proxy hostnames */
    public static final String TAG_SECURITY_NON_PROXY_HOSTS = "SECURITY_NON_PROXY_HOSTS"; //$NON-NLS-1$

    /** The tag for the security service filename */
    public static final String TAG_SECURITY_FILENAME = "SECURITY_FILENAME"; //$NON-NLS-1$

    /** The tag for the security service generated URL (derived information!)*/
    public static final String TAG_SECURITY_URL = "SECURITY_URL"; //$NON-NLS-1$

    /** The tag to link the hierarchy to a business table by saving the table's ID */
    public static final String TAG_HIERARCHY_BUSINESS_TABLE = "HIERARCHY_BUSINESS_TABLE"; //$NON-NLS-1$

    /** The tag to link the hierarchy to the column that defines the primary key */
    public static final String TAG_HIERARCHY_PRIMARY_KEY = "HIERARCHY_PRIMARY_KEY"; //$NON-NLS-1$

    /** The tag to save "Having all" flag of a hierarchy */
    public static final String TAG_HIERARCHY_HAVING_ALL = "HIERARCHY_HAVING_ALL"; //$NON-NLS-1$

    /** The tag to save "Unique members" flag of a hierarchy level (CwmLevel) */
    public static final String TAG_HIERARCHY_LEVEL_UNIQUE_MEMBERS = "HIERARCHY_LEVEL_UNIQUE_MEMBERS"; //$NON-NLS-1$

    /** The tag to save the Id of the reference business column for a hierarchy level (CwmLevel) */
    public static final String TAG_HIERARCHY_LEVEL_REFERENCE_COLUMN = "HIERARCHY_LEVEL_REFERENCE_COLUMN"; //$NON-NLS-1$

    /** The tag to save the business table ID for the cube */
    public static final String TAG_CUBE_BUSINESS_TABLE = "CUBE_BUSINESS_TABLE"; //$NON-NLS-1$

    /** The tag to save the business column ID for a cube measure */
    public static final String TAG_MEASURE_BUSINESS_COLUMN = "MEASURE_BUSINESS_COLUMN"; //$NON-NLS-1$


    
    private static Map domains = Collections.synchronizedMap( new HashMap() );
    
    private String domainName;
    
    private static MDRepository        repository;
    
    private PentahoPackage             pentahoPackage; // Top level package
    private MetaPackage                metaPackage;    // meta package
    
    private RelationalPackage          relationalPackage;
    private CorePackage                corePackage;
    private BusinessInformationPackage businessInformationPackage;
    private KeysIndexesPackage         keysIndexesPackage;
    private MultidimensionalPackage    multiDimensionalPackage;
    private InstancePackage            instancePackage;
    private BehavioralPackage          behavioralPackage;
    private OlapPackage                olapPackage;
    
    /*
    private RelationshipsPackage       relationshipsPackage;
    private DataTypesPackage           dataTypesPackage;
    */
    
    private boolean reversingOrder;
    
    /**
     * @param args
     */
    private CWM(String domainName, boolean autoCreate)
    {
        this.domainName = domainName;
        try
        {
            repository = getRepositoryInstance();
            
            // If the storeage factory class is BTree, we have to reverse the order on many object collections...
            // The JDBC stuff doesn't seem to suffer from that problem.
            // 
            String storageFactoryClassName = System.getProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName", "");  //$NON-NLS-1$ //$NON-NLS-2$
            if (BtreeFactory.class.getName().equals(storageFactoryClassName))
            {
                reversingOrder = true;
            }
            
            /* 
             * Load the M3 CWM model
             * 
             */
            RefPackage cwmPackageM3 = repository.getExtent(CWM);
            if( cwmPackageM3==null && autoCreate )
            {
                cwmPackageM3 = repository.createExtent(CWM);
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("cwm/PentahoCWM.xml")); //$NON-NLS-1$
                XMIReaderFactory.getDefault().createXMIReader().read(inputStream, null, cwmPackageM3);
                log.logBasic(Messages.getString("CWM.INFO_TITLE"), Messages.getString("CWM.INFO_LOADED_CWM_MODEL")); //$NON-NLS-1$ //$NON-NLS-2$
            }

            /*
             * Create an extent for the domain if that extent doesn't exist yet.
             */
            RefPackage refPackage = repository.getExtent(domainName);
            
            try {
            		pentahoPackage = (PentahoPackage) refPackage;
            } catch (Exception e) {
            	
            }
            
            if( pentahoPackage == null ) {
            		if( autoCreate ){
	                pentahoPackage  = (PentahoPackage) repository.createExtent(domainName, getModelPackage(PENTAHO) );
	                log.logBasic(Messages.getString("CWM.INFO_TITLE"), Messages.getString("CWM.INFO_INSTANCED_TOP_PACKAGE")); //$NON-NLS-1$ //$NON-NLS-2$
	            } else {
	            		throw new CWMException( Messages.getErrorString("CWM.ERROR_0004_DOMAIN_NOT_FOUND", domainName) ); //$NON-NLS-1$
	            }
            }
                        
            // The rest is just derived...
            //
            metaPackage = pentahoPackage.getMeta();
            
            corePackage                = metaPackage.getCore();
            relationalPackage          = metaPackage.getRelational();
            businessInformationPackage = metaPackage.getBusinessInformation();
            keysIndexesPackage         = metaPackage.getKeysIndexes();
            multiDimensionalPackage    = metaPackage.getMultidimensional();
            instancePackage            = metaPackage.getInstance();
            behavioralPackage          = metaPackage.getBehavioral();
            olapPackage                = metaPackage.getOlap();
            
            /*
            relationshipsPackage       = metaPackage.getRelationships();
            dataTypesPackage           = metaPackage.getDataTypes();
            */
        }
        catch(Throwable e)
        {
            throw new RuntimeException(Messages.getErrorString("CWM.ERROR_0001_CANT_INITIALIZE_PMS"), e); //$NON-NLS-1$
        }
    }
    
    public synchronized static final MDRepository getRepositoryInstance( Properties properties, InputStream xmiInputStream ) throws CWMException
    {
        if (repository!=null) return repository;
        repository = getRepository( properties, xmiInputStream );
        return repository;
    }

    public synchronized static final MDRepository getRepositoryInstance() throws CWMException
    {
        if (repository!=null) return repository;
        repository = getRepository();
        return repository;
    }

    private static final MDRepository getRepository() throws CWMException
    {
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("jdbc/repository.properties")); //$NON-NLS-1$
            return getRepository( properties, null );
            
        }
        catch(Exception e)
        {
            throw new CWMException(Messages.getErrorString("CWM.ERROR_0002_CANT_ACCESS_REPOSITORY"), e); //$NON-NLS-1$
        }
    }

    private static final MDRepository getRepository( Properties properties, InputStream xmiInputStream ) throws CWMException
    {
        try
        {
            // The system relies on properties set in the virtual machine (system wide)
            // 
            Properties systemProperties = System.getProperties();
            Properties backup = new Properties();
            backup.putAll((Map)systemProperties.clone());
            
            systemProperties.putAll(properties);
            
            String storageFactoryClassName = System.getProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName", ""); //$NON-NLS-1$ //$NON-NLS-2$
    
            try
            {
                MDRepository mdRepository = new NBMDRepositoryImpl();

                RefPackage cwmPackageM3 = mdRepository.getExtent(CWM);
                if (cwmPackageM3==null && xmiInputStream != null )
                {
                    cwmPackageM3 = mdRepository.createExtent(CWM);
                    BufferedInputStream inputStream = new BufferedInputStream( xmiInputStream );
                    XMIReaderFactory.getDefault().createXMIReader().read(inputStream, null, cwmPackageM3);
                    log.logBasic(Messages.getString("CWM.INFO_TITLE"), Messages.getString("CWM.INFO_LOADED_CWM_MODEL")); //$NON-NLS-1$ //$NON-NLS-2$
                }

                return mdRepository;
            }
            catch(Exception e)
            {
                throw new CWMException(Messages.getErrorString("CWM.ERROR_0003_UNABLE_TO_ACCESS_CLASS", storageFactoryClassName), e); //$NON-NLS-1$
            }
        }
        catch(Exception e)
        {
            throw new CWMException(Messages.getErrorString("CWM.ERROR_0002_CANT_ACCESS_REPOSITORY"), e); //$NON-NLS-1$
        }
    }

    public static final CWM getInstance(String domainName ) {
    		return getInstance( domainName, true );
    }

    /**
     * Get (and create if necessary) the instance of CWM associated with domainName. This
     * class is a per-domain name singleton. It lazy loads/creates the CWM instance on demand.
     * 
     * NOTE: this method is synchronized to prevent multiple threads from simultaneously
     * doing a domain.get( x ), having the get() return null, and having multiple threads
     * do a "new CWM()", and a domain.put, thus overwriting each other. Also note that 
     * the member "domains" is a synchronized collection, so it is fine if other threads
     * are manipulating "domains" simultaneously.
     *  
     * @param domainName
     * @param autoCreate
     * @return CWM the CWM instance associated with domainName
     * @throws IllegalArgumentException
     */
    public static final synchronized CWM getInstance(String domainName, boolean autoCreate ) throws IllegalArgumentException
    {
        if (domainName == null) {
            throw new IllegalArgumentException( Messages.getErrorString( "CWM.ERROR_0005_DOMAIN_NAME_CANNOT_BE_NULL" ) );
        }

        CWM cwm = null;
        cwm = (CWM) domains.get(domainName);
        if ( null == cwm )
        {
          cwm = new CWM(domainName, autoCreate);
          domains.put(domainName, cwm);
        }
        
        return cwm;
    }

    /**
     * See if the domain already exists
     * @param domainName The domain to check for
     * @return true if the domain with the given name exists in the MDR repository
     */
    public static final boolean exists(String domainName) throws CWMException
    {
        // Do we have the domain yet?
        // Simply look up the extent name, if it exists...
        return getRepositoryInstance().getExtent(domainName) != null;
    }

    
    
    /**
     * @return a list of top level domains
     */
    public static final String[] getDomainNames() throws CWMException
    {
        String ext[] = getRepositoryInstance().getExtentNames();
        ArrayList domainNames = new ArrayList();
        
        for (int i = 0; i < ext.length; i++)
        {
            if (!ext[i].equals("MOF") && !ext[i].equals(CWM)) domainNames.add(ext[i]); //$NON-NLS-1$
                
        }
        
        return Const.sortStrings( (String[])domainNames.toArray(new String[domainNames.size()]) );
    }
    
    /**
     * Remove the domain by removing the extent.
     * 
     * @param name the domain to remove.
     */
    public synchronized void removeDomain() throws CWMException
    {
        repository.beginTrans(true); 
        repository.endTrans(false); // just to make sure, this forces a commit on the database
        
        RefPackage refPackage = repository.getExtent(domainName);
        if (refPackage!=null)
        {
            // Delete the root package...
            refPackage.refDelete();
            removeFromList();
        }
    }
    
    /* 
     * ==============================================
     * Management stuff...
     * ==============================================
     */
    
    public void beginTransaction()
    {
        repository.beginTrans(true);
    }

    public void endTransaction()
    {
        repository.endTrans();
    }
    

    public void rollback()
    {
        repository.endTrans(true);
    }
    
    /**
     * Remove the domain from the domains in memory to cause the domain to be re-loaded the next time around.
     * NOTE: removeFromList() does not need to be synchronized. The only member variable removeFromList 
     * manipulates is domains. domains is a synchronized map, and is already protected.
     */
    public void removeFromList()
    {
        // repository.shutdown();
        domains.remove(domainName); // removing it from the list causes it to be re-opened next time around. 
    }
    
    public synchronized static final void quitAndSync() throws CWMException
    {
        // Shut down the repository...
        getRepositoryInstance().shutdown();
        repository=null;
    }

    public synchronized static final void clearCache() throws CWMException {
        // Shut down the repository...
        getRepositoryInstance().shutdown();
        repository=getRepository();
    }

    public synchronized static final void clearCache( Properties properties, InputStream xmiInputStream) throws CWMException {
        // Shut down the repository...
        getRepositoryInstance().shutdown();
        repository = getRepository( properties, xmiInputStream );
    }
    
    
    
    /* 
     * ==============================================
     * Create new stuff...
     * ==============================================
     */
    
    public CwmTable createTable(String tableName)
    {
        CwmTable table = relationalPackage.getCwmTable().createCwmTable();
        table.setName(tableName);
        
        return table;
    }
    
    public CwmTable createTable(String tableName, Row fields)
    {
        CwmTable table = createTable(tableName);
        Collection collection = table.getOwnedElement();
        
        for (int i=fields.size()-1;i>=0;i--)  // Reversed order please!
        {
            Value field = fields.getValue(i);
            CwmColumn column = createColumn(field);
            collection.add(column);
        }
        
        return table;
    }
        
    public CwmColumn createColumn(String columnName)
    {
        CwmColumn column = relationalPackage.getCwmColumn().createCwmColumn();
        column.setName(columnName);
        
        return column;
    }

    public CwmColumn createColumn(Value value)
    {
        CwmColumn column = relationalPackage.getCwmColumn().createCwmColumn();
        column.setName(value.getName());
        column.setType(createDataType(value));
        column.setLength(new Integer(value.getLength()));
        column.setPrecision(new Integer(value.getPrecision()));
        return column;
    }

    public CwmDataType createDataType(Value value)
    {
        return createDataType(value.getTypeDesc());
    }
    
    public CwmDataType createDataType(String dataTypeDesc)
    {
        CwmDataType dataType = corePackage.getCwmDataType().createCwmDataType();
        dataType.setName(dataTypeDesc);
        
        return dataType;
    }
    
    public CwmDescription createDescription(String body)
    {
        CwmDescription description = businessInformationPackage.getCwmDescription().createCwmDescription();
        description.setBody(body);
        
        return description;
    }

    public void setDescription(CwmModelElement modelElement, CwmDescription description)
    {
        ModelElementDescription modelElementDescription = businessInformationPackage.getModelElementDescription();
        modelElementDescription.add(modelElement, description);
    }

    /**
     * Create a key-value pair that can be attached to a model element (table, column, domain(model), etc)
     * 
     * @param key
     * @param value
     * @return The TaggedValue object
     */
    public CwmTaggedValue createTaggedValue(String key, String value)
    {
        return corePackage.getCwmTaggedValue().createCwmTaggedValue(key, value);
    }
    
    /**
     * Add a key-value pair to a modelElement (column, table, etc.)
     * 
     * @param modelElement The model Element to add the key-value pair to
     * @param key The key
     * @param value The value
     */
    public void addTaggedValue(CwmModelElement modelElement, String key, String value)
    {
        CwmTaggedValue taggedValue = createTaggedValue(key, value);
        TaggedElement taggedElement = corePackage.getTaggedElement();  
        taggedElement.add(modelElement, taggedValue);
    }
    
    /**
     * Creates a new Package (a collection of model elements) if a package with that name doesn't already exists.
     * Otherwise it returns the existing package.
     * 
     * @param packageName
     * @return
     */
    public CwmPackage createPackage(String packageName)
    {
        // First see if this package doesn't exist already!
        CwmPackage p = corePackage.getCwmPackage().createCwmPackage();
        p.setName(packageName);
        return p;
    }
    
    public CwmExpression createExpression(String body, String language)
    {
        return corePackage.getCwmExpression().createCwmExpression(body, language);
    }
    
    /**
     * For lack of a better place to store it, we're saving database information in the CwmCatalog.
     * 
     * @param databaseMeta The Kettle DatabaseMeta to convert to.
     * @return
     */
    public CwmCatalog createCatalog(String name)
    {
        CwmCatalog catalog = relationalPackage.getCwmCatalog().createCwmCatalog();
        catalog.setName(name);
        
        return catalog;
    }
    
    /**
     * We consider the locale to be a paramter for the CWM model.
     * 
     * @param name The name of the parameter
     * @return a new CwmParameter 
     */
    public CwmParameter createParameter(String name)
    {
        CwmParameter paramter = behavioralPackage.getCwmParameter().createCwmParameter();
        paramter.setName(name);
        
        return paramter;
    }

    /**
     * Set the GUI position of a model element (table, etc)
     * 
     * @param modelElement The model element to store the location for.
     * @param x 
     * @param y
     */
    public void setPosition(CwmModelElement modelElement, int x, int y)
    {
        addTaggedValue(modelElement, TAG_POSITION_X, Integer.toString(x));
        addTaggedValue(modelElement, TAG_POSITION_Y, Integer.toString(y));
    }
    
    public void setWidth(CwmModelElement modelElement, int width)
    {
        addTaggedValue(modelElement, TAG_POSITION_WIDTH, Integer.toString(width));
    }

    public void setHeight(CwmModelElement modelElement, int height)
    {
        addTaggedValue(modelElement, TAG_POSITION_HEIGHT, Integer.toString(height));
    }

    
    public CwmKeyRelationship createRelationship()
    {
         return keysIndexesPackage.getCwmKeyRelationship().createCwmKeyRelationship();
    }
    
    public CwmDocument createDocument()
    {
        return businessInformationPackage.getCwmDocument().createCwmDocument();
    }
    
    public CwmSchema createSchema(String name)
    {
        CwmSchema cwmSchema = multiDimensionalPackage.getCwmSchema().createCwmSchema();
        cwmSchema.setName(name);
        
        return cwmSchema;
    }

    public CwmDimension createDimension(CwmSchema cwmSchema, String name)
    {
        CwmDimension cwmDimension = multiDimensionalPackage.getCwmDimension().createCwmDimension();
        cwmDimension.setSchema(cwmSchema);
        cwmDimension.setName(name);
        return cwmDimension;
    }

    public CwmDimensionedObject createDimensionedObject(String name)
    {
        CwmDimensionedObject cwmDimensionedObject = multiDimensionalPackage.getCwmDimensionedObject().createCwmDimensionedObject();
        cwmDimensionedObject.setName(name);
        return cwmDimensionedObject;
    }

    public CwmExtent createExtent(String name)
    {
        CwmExtent cwmExtent = instancePackage.getCwmExtent().createCwmExtent();
        cwmExtent.setName(name);
        
        return cwmExtent;
    }    

    public CwmAttribute createAttribute(String name)
    {
        CwmAttribute cwmAttribute = corePackage.getCwmAttribute().createCwmAttribute();
        cwmAttribute.setName(name);
        return cwmAttribute;
    }
    
    public CwmClass createClass(String name)
    {
        CwmClass cwmClass = corePackage.getCwmClass().createCwmClass();
        cwmClass.setName(name);
        return cwmClass;
    }
    

    public org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension createOlapDimension(String name)
    {
        org.pentaho.pms.cwm.pentaho.meta.olap.CwmDimension cwmDimension = olapPackage.getCwmDimension().createCwmDimension();
        cwmDimension.setName(name);
        return cwmDimension;
    }

    public org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema createOlapSchema(String name)
    {
        org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmSchema = olapPackage.getCwmSchema().createCwmSchema();
        cwmSchema.setName(name);
        return cwmSchema;
    }
    
    public org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema findOlapSchema(String name)
    {
        Collection collection = olapPackage.getCwmSchema().refAllOfClass();
        for (Iterator iter = collection.iterator(); iter.hasNext();)
        {
            org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema cwmOlapSchema = (org.pentaho.pms.cwm.pentaho.meta.olap.CwmSchema) iter.next();
            if (cwmOlapSchema.getName().equals(name)) return cwmOlapSchema;
        }
        return null;
    }

    public CwmLevelBasedHierarchy createLevelBasedHierarchy(String name)
    {
        CwmLevelBasedHierarchy lbh = olapPackage.getCwmLevelBasedHierarchy().createCwmLevelBasedHierarchy();
        lbh.setName(name);
        return lbh;
    }
    
    public CwmLevel createLevel(String name)
    {
        CwmLevel cwmLevel = olapPackage.getCwmLevel().createCwmLevel();
        cwmLevel.setName(name);
        return cwmLevel;
    }
    
    public CwmHierarchyLevelAssociation createHierarchyLevelAssociation(String name)
    {
        CwmHierarchyLevelAssociation hla = olapPackage.getCwmHierarchyLevelAssociation().createCwmHierarchyLevelAssociation();
        hla.setName(name);
        return hla;
    }

    
    public CwmEvent createEvent(String name)
    {
        CwmEvent cwmEvent = behavioralPackage.getCwmEvent().createCwmEvent();
        cwmEvent.setName(name);
        return cwmEvent;
    }
    
    public boolean createEventParameter(CwmEvent cwmEvent, CwmParameter cwmParameter)
    {
        return behavioralPackage.getEventParameter().add(cwmEvent, cwmParameter);
    }
    
    public CwmCube createOlapCube(String name)
    {
        CwmCube cwmCube = olapPackage.getCwmCube().createCwmCube();
        cwmCube.setName(name);
        return cwmCube;
    }


    public CwmCubeDimensionAssociation createCubeDimensionAssocation(String name)
    {
        CwmCubeDimensionAssociation association = olapPackage.getCwmCubeDimensionAssociation().createCwmCubeDimensionAssociation();
        association.setName(name);
        return association;
    }

    public CwmMeasure createMeasure(String name)
    {
        CwmMeasure cwmMeasure = olapPackage.getCwmMeasure().createCwmMeasure();
        cwmMeasure.setName(name);
        return cwmMeasure;
    }

    

    /* 
     * ==============================================
     * Remove meta-data...
     * ==============================================
     */

    public void removeTable(String tableName)
    {
        CwmTable table = getTable(tableName);
        if (table!=null)
        {
            table.refDelete();
            log.logBasic(Messages.getString("CWM.INFO_TITLE"), Messages.getString("CWM.INFO_REMOVED_TABLE", tableName)); // Not working //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    public void removePackage(String packageName)
    {
        CwmPackage p = getPackage(packageName);
        if (p!=null)
        {
            p.refDelete();
        }
    }
    
    /* 
     * ==============================================
     * Read out meta-data...
     * ==============================================
     */
    public CwmTable getTable(String tableName)
    {
        // All tables:
        CwmTable tables[] = getTables();
        
        for (int i=0;i<tables.length;i++)
        {
            if (tables[i].getName().equals(tableName))
            {
                return tables[i];
            }
        }
        return null;
    }
    
    public CwmTable[] getTables()
    {
        Collection collection = relationalPackage.getCwmTable().refAllOfClass();
        
        return (CwmTable[]) collection.toArray(new CwmTable[collection.size()]);
    }

    /**
     * @param modelElement The model element to look for
     * @return All descriptions for the given model element (Table, Column, etc.)
     */
    public CwmDescription[] getDescription(CwmModelElement modelElement)
    {
        ModelElementDescription modelElementDescription = businessInformationPackage.getModelElementDescription();
        Collection collection = modelElementDescription.getDescription(modelElement);
        
        return (CwmDescription[]) collection.toArray(new CwmDescription[collection.size()]); // There can be more than 1 description for a model element
    }
    
    /**
     * @return All documents (notepads)
     */
    public CwmDescription[] getDescriptions()
    {
        Collection descriptions = businessInformationPackage.getCwmDescription().refAllOfClass();
        return (CwmDescription[]) descriptions.toArray(new CwmDescription[descriptions.size()]);
    }
    
    /**
     * @return  All defined packages.
     */
    public CwmPackage[] getPackages()
    {
        Collection packages = corePackage.getCwmPackage().refAllOfClass();
        return (CwmPackage[]) packages.toArray(new CwmPackage[packages.size()]);
    }
    
    /**
     * Returns the Package with the specified name or null if that package could not be found.
     * @param packageName The package name.
     * @return The package with the selected name.
     */
    public CwmPackage getPackage(String packageName)
    {
        Collection packages = corePackage.getCwmPackage().refAllOfClass();
        for (Iterator iter = packages.iterator(); iter.hasNext();)
        {
            CwmPackage p = (CwmPackage) iter.next();
            if (p.getName().equals(packageName)) return p;
            
        }
        return null;
    }
    
    public CwmTaggedValue[] getTaggedValues(CwmModelElement modelElement)
    {
        Collection pairs = corePackage.getTaggedElement().getTaggedValue(modelElement);
        return (CwmTaggedValue[])pairs.toArray(new CwmTaggedValue[pairs.size()]);
    }
    
    public String getFirstTaggedValue(CwmModelElement modelElement, String tag)
    {
        Collection pairs = corePackage.getTaggedElement().getTaggedValue(modelElement);
        CwmTaggedValue[] found = findTaggedValues(pairs, tag);
        if (found.length>0) return found[0].getValue();
        
        return null;
    }

    public String[] getTaggedValues(CwmModelElement modelElement, String tag)
    {
        Collection pairs = corePackage.getTaggedElement().getTaggedValue(modelElement);
        CwmTaggedValue[] found = findTaggedValues(pairs, tag);
        ArrayList strings = new ArrayList();
        for (int i = 0; i < found.length; i++)
        {
            strings.add(found[i].getValue());
        }
        
        return (String[])strings.toArray(new String[strings.size()]);
    }

    /**
     * @return all the catalogs in this model. (database connection information)
     */
    public CwmCatalog[] getCatalogs()
    {
        Collection catalogs = relationalPackage.getCwmCatalog().refAllOfClass();
        return (CwmCatalog[])catalogs.toArray(new CwmCatalog[catalogs.size()]);
    }

    public int getPositionX(CwmModelElement modelElement)
    {
        try { return Integer.parseInt(getFirstTaggedValue(modelElement, TAG_POSITION_X)); }  catch(Exception e) { return -1; }
    }
    
    public int getPositionY(CwmModelElement modelElement)
    {
        try { return Integer.parseInt(getFirstTaggedValue(modelElement, TAG_POSITION_Y)); }  catch(Exception e) { return -1; }
    }

    public int getPositionWidth(CwmModelElement modelElement)
    {
        try { return Integer.parseInt(getFirstTaggedValue(modelElement, TAG_POSITION_WIDTH)); }  catch(Exception e) { return -1; }
    }
    
    public int getPositionHeight(CwmModelElement modelElement)
    {
        try { return Integer.parseInt(getFirstTaggedValue(modelElement, TAG_POSITION_HEIGHT)); }  catch(Exception e) { return -1; }
    }

    
    public CwmKeyRelationship[] getRelationships(CwmSchema cwmSchema)
    {
        List relationships = new ArrayList();
        
        Collection allElements = cwmSchema.getOwnedElement();
        for (Iterator iter = allElements.iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (element instanceof CwmKeyRelationship)
            {
                relationships.add(element);
            }
        }
        
        return (CwmKeyRelationship[])relationships.toArray(new CwmKeyRelationship[relationships.size()]);
    }
    

    public CwmSchema[] getSchemas()
    {
        Collection schemas = multiDimensionalPackage.getCwmSchema().refAllOfClass();
        return (CwmSchema[])schemas.toArray(new CwmSchema[schemas.size()]);
    }
    
    /**
     * Get all the dimensions for the selected schema...
     * @param cwmSchema
     * @return
     */
    public CwmDimension[] getDimensions(CwmSchema cwmSchema)
    {
        List dimensions = new ArrayList();
        Collection allDimensions = multiDimensionalPackage.getCwmDimension().refAllOfClass();
        for (Iterator iter = allDimensions.iterator(); iter.hasNext();)
        {
            CwmDimension cwmDimension = (CwmDimension) iter.next();
            CwmSchema schema = cwmDimension.getSchema();
            if (schema!=null && schema.equals(cwmSchema))
            {
                dimensions.add(cwmDimension);
            }
        }
        return (CwmDimension[])dimensions.toArray(new CwmDimension[dimensions.size()]);
    }
    

    /**
     * @param cwmSchema The used schema/namespace
     * @return The available extents in that schema. (at the root)
     */
    public CwmExtent[] getRootExtents(CwmSchema cwmSchema)
    {
        List extents = new ArrayList();
        Collection allExtents = instancePackage.getCwmExtent().refAllOfClass();
        for (Iterator iter = allExtents.iterator(); iter.hasNext();)
        {
            CwmExtent cwmExtent = (CwmExtent) iter.next();
            
            CwmNamespace namespace = cwmExtent.getNamespace();
            if (namespace!=null && namespace.getName().equals(cwmSchema.getName()))
            {
                // Only elements with a root tag set to Y
                String isRoot = findFirstTaggedValue(cwmExtent.getTaggedValue(), TAG_BUSINESS_CATEGORY_ROOT);
                if (isRoot.equalsIgnoreCase("Y")) //$NON-NLS-1$
                {
                    extents.add(cwmExtent);
                }
            }
        }
        return (CwmExtent[])extents.toArray(new CwmExtent[extents.size()]);
    }
    
    public CwmClass[] getClasses()
    {
        Collection classes = corePackage.getCwmClass().refAllOfClass();
        return (CwmClass[]) classes.toArray(new CwmClass[classes.size()]);
    }
    

    public CwmParameter[] getParameters()
    {
        Collection parameters = behavioralPackage.getCwmParameter().refAllOfClass();
        return (CwmParameter[]) parameters.toArray(new CwmParameter[parameters.size()]);
    }
    
    public CwmParameter getFirstParameterWithName(String name)
    {
        Collection parameters = behavioralPackage.getCwmParameter().refAllOfClass();
        for (Iterator iter = parameters.iterator(); iter.hasNext();)
        {
            CwmParameter cwmParameter = (CwmParameter) iter.next();
            if (cwmParameter.getName().equals(name)) return cwmParameter;
            
        }
        return null;
    }
    
    public CwmEvent getFirstEventWithName(String name)
    {
        Collection parameters = behavioralPackage.getCwmEvent().refAllOfClass();
        for (Iterator iter = parameters.iterator(); iter.hasNext();)
        {
            CwmEvent cwmEvent = (CwmEvent) iter.next();
            if (cwmEvent.getName().equals(name)) return cwmEvent;
        }
        return null;
    }
    /* 
     * ==============================================
     * Utility methods
     * ==============================================
     */
    
    private MofPackage getModelPackage(String packageName)
    {
        ModelPackage mofPackage = (ModelPackage)repository.getExtent(CWM);
        
        for (Iterator it = mofPackage.getMofPackage().refAllOfClass().iterator(); it.hasNext();)
        {
            MofPackage result = (MofPackage)it.next();
            if (result.getName().equals(packageName))
            {
                return result;
            }
        }
        
        return null;
    }

    /**
     * @return the domainName
     */
    public String getDomainName()
    {
        return domainName;
    }

    /**
     * @param domainName the domainName to set
     */
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    
    public static final CwmTaggedValue[] findTaggedValues(Collection pairs, String tag)
    {
        ArrayList found = new ArrayList();
        
        for (Iterator iter = pairs.iterator(); iter.hasNext();)
        {
            CwmTaggedValue pair = (CwmTaggedValue) iter.next();
            if (pair.getTag().equals(tag)) found.add(pair);
        }
        
        return (CwmTaggedValue[])found.toArray(new CwmTaggedValue[found.size()]);
    }

    public static final String findFirstTaggedValue(Collection pairs, String tag)
    {
        CwmTaggedValue[] found = findTaggedValues(pairs, tag);
        if (found.length>0) return found[0].getValue();
        return null;
    }

    /**
     * @return the pentahoPackage
     */
    public PentahoPackage getPentahoPackage()
    {
        return pentahoPackage;
    }

    /**
     * @param pentahoPackage the pentahoPackage to set
     */
    public void setPentahoPackage(PentahoPackage pentahoPackage)
    {
        this.pentahoPackage = pentahoPackage;
    }

    public BehavioralPackage getBehavioralPackage()
    {
        return behavioralPackage;
    }

    public BusinessInformationPackage getBusinessInformationPackage()
    {
        return businessInformationPackage;
    }

    public CorePackage getCorePackage()
    {
        return corePackage;
    }

    public InstancePackage getInstancePackage()
    {
        return instancePackage;
    }

    public KeysIndexesPackage getKeysIndexesPackage()
    {
        return keysIndexesPackage;
    }

    public MetaPackage getMetaPackage()
    {
        return metaPackage;
    }

    public MultidimensionalPackage getMultiDimensionalPackage()
    {
        return multiDimensionalPackage;
    }

    public RelationalPackage getRelationalPackage()
    {
        return relationalPackage;
    }

    public void exportToXMI(String filename) throws IOException
    {
        XMIWriterFactory factory = XMIWriterFactory.getDefault();
        XMIWriter writer = factory.createXMIWriter();
        writer.getConfiguration().setEncoding(Const.XML_ENCODING);
        writer.write(new FileOutputStream(filename), getPentahoPackage(), "1.2"); //$NON-NLS-1$
    }
    
    public String getXMI() throws IOException
    {
        XMIWriterFactory factory = XMIWriterFactory.getDefault();
        XMIWriter writer = factory.createXMIWriter();
        writer.getConfiguration().setEncoding(Const.XML_ENCODING);
        ByteArrayOutputStream stream = new ByteArrayOutputStream(250000); // start with 250k
        writer.write(stream, getPentahoPackage(), "1.2"); //$NON-NLS-1$
        stream.close();
        
        return stream.toString();
    }

    public void importFromXMI(String filename) throws IOException, MalformedXMIException
    {
        FileInputStream inputStream = new FileInputStream(filename);
        importFromXMI( inputStream );
    }
    
    public void importFromXMI( InputStream inputStream ) throws IOException, MalformedXMIException
    {
        XMIReaderFactory factory = XMIReaderFactory.getDefault();
        XMIReader reader = factory.createXMIReader();
        reader.read(inputStream, null, getPentahoPackage());
        inputStream.close();
    }
    
    public void importFromXMIString(String xmi) throws IOException, MalformedXMIException
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmi.getBytes(Const.XML_ENCODING));
        importFromXMI( inputStream );
    }

    /**
     * @return the reversingOrder
     */
    public boolean isReversingOrder()
    {
        return reversingOrder;
    }

    /**
     * @param reversingOrder the reversingOrder to set
     */
    public void setReversingOrder(boolean reversingOrder)
    {
        this.reversingOrder = reversingOrder;
    }

    

}
