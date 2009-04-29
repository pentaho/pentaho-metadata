/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
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
package org.pentaho.metadata.repository;

import java.util.Set;

import org.pentaho.metadata.model.Domain;

/**
 * This interface defines a metadata domain repository.  The default implementation stores
 * domains as xml files in system/metadata/domains/*.xml
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface IMetadataDomainRepository {

  /**
   * Store a domain to the repository.  The domain should persist between JVM restarts.
   * 
   * @param domain domain object to store
   * @param overwrite if true, overwrite existing domain
   * 
   * @throws DomainIdNullException if domain id is null
   * @throws DomainAlreadyExistsException if domain exists and overwrite = false
   * @throws DomainStorageException if there is a problem storing the domain
   */
  public void storeDomain(Domain domain, boolean overwrite) throws DomainIdNullException, DomainAlreadyExistsException, DomainStorageException;
  
  /**
   * retrieve a domain from the repo.  This does lazy loading of the repo, so it calls reloadDomains()
   * if not already loaded.
   * 
   * @param id domain to get from the repository
   * 
   * @return domain object
   */
  public Domain getDomain(String id);
  
  /**
   * return a list of all the domain ids in the repository.  triggers a call to reloadDomains if necessary.
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
   * @param domainId
   */
  public void removeDomain(String domainId);
}
