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

import java.util.Set;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.IConcept;

/**
 * This interface defines a metadata domain repository, used to maintain a system wide set of metadata domains.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public interface IMetadataDomainRepository {

  public static final int ACCESS_TYPE_READ = 0;
  public static final int ACCESS_TYPE_WRITE = 1;
  public static final int ACCESS_TYPE_UPDATE = 2;
  public static final int ACCESS_TYPE_DELETE = 3;
  public static final int ACCESS_TYPE_ADMIN = 4;
  public static final int ACCESS_TYPE_SCHEMA_ADMIN = 5;

  /**
   * Store a domain to the repository. The domain should persist between JVM restarts.
   * 
   * @param domain
   *          domain object to store
   * @param overwrite
   *          if true, overwrite existing domain
   * 
   * @throws DomainIdNullException
   *           if domain id is null
   * @throws DomainAlreadyExistsException
   *           if domain exists and overwrite = false
   * @throws DomainStorageException
   *           if there is a problem storing the domain
   */
  public void storeDomain( Domain domain, boolean overwrite ) throws DomainIdNullException,
    DomainAlreadyExistsException, DomainStorageException;

  /**
   * retrieve a domain from the repo. This does lazy loading of the repo, so it calls reloadDomains() if not already
   * loaded.
   * 
   * @param id
   *          domain to get from the repository
   * 
   * @return domain object
   */
  public Domain getDomain( String id );

  /**
   * return a list of all the domain ids in the repository. triggers a call to reloadDomains if necessary.
   * 
   * @return the domain Ids.
   */
  public Set<String> getDomainIds();

  /**
   * reload domains from disk
   */
  public void reloadDomains();

  /**
   * flush the domains from memory
   */
  public void flushDomains();

  /**
   * remove a domain from disk and memory.
   * 
   * @param domainId
   */
  public void removeDomain( String domainId );

  /**
   * remove a model from a domain which is stored either on a disk or memory.
   * 
   * @param domainId
   * @param modelId
   */
  public void removeModel( String domainId, String modelId ) throws DomainIdNullException, DomainStorageException;

  public String generateRowLevelSecurityConstraint( LogicalModel model );

  /**
   * The aclHolder cannot be null unless the access type requested is ACCESS_TYPE_SCHEMA_ADMIN.
   */
  public boolean hasAccess( final int accessType, final IConcept aclHolder );

}
