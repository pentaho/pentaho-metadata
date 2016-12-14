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
