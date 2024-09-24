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
package org.pentaho.metadata.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.util.SecurityHelper;
import org.pentaho.metadata.util.SerializationService;

/**
 * This is a file based implementation of the IMetadataDomainRepository
 * 
 * @author Will Gorman(wgorman@pentaho.com)
 */
public class FileBasedMetadataDomainRepository implements IMetadataDomainRepository {

  private static final Log logger = LogFactory.getLog( FileBasedMetadataDomainRepository.class );

  private static final String DOMAIN_SUFFIX = ".domain.xml"; //$NON-NLS-1$

  private static final String DEFAULT_DOMAIN_FOLDER = "domains"; //$NON-NLS-1$

  protected Map<String, Domain> domains = Collections.synchronizedMap( new HashMap<String, Domain>() );

  private String domainFolder = null;

  public void setDomainFolder( String folder ) {
    this.domainFolder = folder;
  }

  public void storeDomain( Domain domain, boolean overwrite ) throws DomainIdNullException,
    DomainAlreadyExistsException, DomainStorageException {
    if ( domain.getId() == null ) {
      throw new DomainIdNullException( Messages.getErrorString( "IMetadataDomainRepository.ERROR_0001_DOMAIN_ID_NULL" ) ); //$NON-NLS-1$
    }

    File folder = getDomainsFolder();
    if ( !folder.exists() ) {
      folder.mkdirs();
    }

    synchronized ( domains ) {

      if ( !overwrite && domains.get( domain.getId() ) != null ) {
        throw new DomainAlreadyExistsException( Messages.getErrorString(
            "IMetadataDomainRepository.ERROR_0002_DOMAIN_OBJECT_EXISTS", domain.getId() ) ); //$NON-NLS-1$
      }

      File domainFile = new File( folder, getDomainFilename( domain.getId() ) );

      if ( !overwrite && domainFile.exists() ) {
        throw new DomainAlreadyExistsException( Messages.getErrorString(
            "FileBasedMetadataDomainRepository.ERROR_0003_DOMAIN_FILE_EXISTS", domain.getId() ) ); //$NON-NLS-1$
      }

      SerializationService service = new SerializationService();
      FileOutputStream output = null;
      try {
        output = new FileOutputStream( domainFile );
        service.serializeDomain( domain, output );
      } catch ( FileNotFoundException e ) {
        throw new DomainStorageException( Messages
            .getErrorString( "FileBasedMetadataDomainRepository.ERROR_0004_DOMAIN_STORAGE_EXCEPTION" ), e ); //$NON-NLS-1$
      } finally {
        try {
          if ( output != null ) {
            output.close();
          }
        } catch ( IOException e ) {
          throw new DomainStorageException( Messages
              .getErrorString( "FileBasedMetadataDomainRepository.ERROR_0004_DOMAIN_STORAGE_EXCEPTION" ), e ); //$NON-NLS-1$
        }
      }

      // adds the domain to the domains list
      domains.put( domain.getId(), domain );
    }
  }

  private String getDomainFilename( String id ) {
    String cleansedName = id.replaceAll( "[^a-zA-Z0-9_]", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    return cleansedName + DOMAIN_SUFFIX;
  }

  public Domain getDomain( String id ) {
    // for now, lazy load all the domains at once. We could be smarter,
    // loading the files as requested.

    if ( domains.size() == 0 ) {
      reloadDomains();
    }
    Domain domain = domains.get( id );
    if ( domain == null ) {
      // try to reference the metadata file implicitly, for backward compatibility
      domain = domains.get( id + "/metadata.xmi" );
    }
    if ( domain != null ) {
      SecurityHelper helper = new SecurityHelper();
      Domain clone = helper.createSecureDomain( this, domain );
      return clone;
    } else {
      logger.error( Messages.getErrorString( "FileBasedMetadataDomainRepository.ERROR_0006_DOMAIN_NOT_FOUND", id ) ); //$NON-NLS-1$
      return null;
    }
  }

  public Set<String> getDomainIds() {
    if ( domains.size() == 0 ) {
      reloadDomains();
    }
    Set<String> set = null;
    synchronized ( domains ) {
      set = new TreeSet<String>( domains.keySet() );
    }
    return set;
  }

  private static class DomainFileNameFilter implements FilenameFilter {
    public boolean accept( File dir, String name ) {
      return name.endsWith( DOMAIN_SUFFIX );
    }
  }

  protected File getDomainsFolder() {
    String domainsFolder = DEFAULT_DOMAIN_FOLDER;
    if ( domainFolder != null ) {
      domainsFolder = domainFolder;
    }
    File folder = new File( domainsFolder );
    return folder;
  }

  public void flushDomains() {
    domains.clear();
  }

  public void reloadDomains() {
    // load the domains from the file system
    // for each file in the system/metadata/domains folder that ends with .domain.xml, load
    Map<String, Domain> localDomains = new HashMap<String, Domain>();
    SerializationService service = new SerializationService();
    File folder = getDomainsFolder();
    if ( folder.exists() ) {
      for ( File file : folder.listFiles( new DomainFileNameFilter() ) ) {
        // load domain
        FileInputStream fis = null;
        try {
          fis = new FileInputStream( file );
          Domain domain = service.deserializeDomain( fis );
          localDomains.put( domain.getId(), domain );
        } catch ( FileNotFoundException e ) {
          logger.error( Messages.getErrorString(
              "FileBasedMetadataDomainRepository.ERROR_0005_FAILED_TO_LOAD_DOMAIN", file.getName() ), e ); //$NON-NLS-1$
        } finally {
          if ( fis != null ) {
            try {
              if ( fis != null ) {
                fis.close();
              }
            } catch ( IOException e ) {
              logger.error( Messages.getErrorString(
                  "FileBasedMetadataDomainRepository.ERROR_0005_FAILED_TO_LOAD_DOMAIN", file.getName() ), e ); //$NON-NLS-1$
            }
          }
        }
      }
    }
    synchronized ( domains ) {
      domains.clear();
      domains.putAll( localDomains );
    }
  }

  public void removeDomain( String domainId ) {
    synchronized ( domains ) {
      File folder = getDomainsFolder();
      File domainFile = new File( folder, getDomainFilename( domainId ) );
      domains.remove( domainId );
      domainFile.delete();
    }
  }

  public void removeModel( String domainId, String modelId ) throws DomainIdNullException, DomainStorageException {
    synchronized ( domains ) {
      // get a raw domain vs. the cloned secure domain
      Domain domain = domains.get( domainId );
      if ( domain == null ) {
        throw new DomainIdNullException( Messages
            .getErrorString( "IMetadataDomainRepository.ERROR_0001_DOMAIN_ID_NULL" ) ); //$NON-NLS-1$
      }

      // remove the model
      Iterator<LogicalModel> iter = domain.getLogicalModels().iterator();
      while ( iter.hasNext() ) {
        LogicalModel model = iter.next();
        if ( modelId.equals( model.getId() ) ) {
          iter.remove();
          break;
        }
      }

      if ( domain.getLogicalModels().size() == 0 ) {
        // remove the domain all together
        removeDomain( domainId );
      } else {

        // store the modified domain
        try {
          storeDomain( domain, true );
        } catch ( DomainAlreadyExistsException e ) {
          // this should not happen
          logger.error( Messages.getErrorString(
              "FileBasedMetadataDomainRepository.ERROR_0007_DOMAIN_ALREADY_EXISTS", domain.getId() ), e ); //$NON-NLS-1$
        }
      }
    }
  }

  public String generateRowLevelSecurityConstraint( LogicalModel model ) {
    return null;
  }

  /**
   * The aclHolder cannot be null unless the access type requested is ACCESS_TYPE_SCHEMA_ADMIN.
   */
  public boolean hasAccess( int accessType, IConcept aclHolder ) {
    // Subclasses can override this for ACL and Session/Credential checking
    return true;
  }
}
