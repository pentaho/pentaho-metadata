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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.Domain;
import org.pentaho.pms.messages.Messages;

/**
 * This is an in memory only implementation of the IMetadataDomainRepository
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class InMemoryMetadataDomainRepository implements IMetadataDomainRepository {
  
  private static final Log logger = LogFactory.getLog(InMemoryMetadataDomainRepository.class);
  
  Map<String, Domain> domains = null;
  
  public synchronized void storeDomain(Domain domain, boolean overwrite) throws DomainIdNullException, DomainAlreadyExistsException, DomainStorageException {
    // stores a domain to system/metadata/DOMAIN_ID.domain.xml
    //ISolutionRepository repo = PentahoSystem.get(ISolutionRepository.class, session);
    //repo.addSolutionFile(baseUrl, path, fileName, data, overwrite)

    if (domain.getId() == null) {
      // todo: replace with exception
      throw new DomainIdNullException(Messages.getErrorString("IMetadataDomainRepository.ERROR_0001_DOMAIN_ID_NULL")); //$NON-NLS-1$
    }
    
    if (!overwrite && domains != null && domains.get(domain.getId()) != null) {
      throw new DomainAlreadyExistsException(Messages.getErrorString("IMetadataDomainRepository.ERROR_0002_DOMAIN_OBJECT_EXISTS", domain.getId())); //$NON-NLS-1$
    }
    
    // adds the domain to the domains list
    if (domains == null) {
      domains = new HashMap<String, Domain>();
    }
    domains.put(domain.getId(), domain);
  }
    
  public Domain getDomain(String id) {
    // for now, lazy load all the domains at once.  We could be smarter,
    // loading the files as requested.
    
    if (domains == null) {
      reloadDomains();
    }
    return domains.get(id);
  }
  
  public Set<String> getDomainIds() {
    if (domains == null) {
      reloadDomains();
    }
    return domains.keySet();
  }
  
  public synchronized void flushDomains() {
    domains = null;
  }
  
  public synchronized void reloadDomains() {
    // can't reload inmemory domains, they are gone
  }
  
  public synchronized void removeDomain(String domainId) {
    domains.remove(domainId);
  }
}
