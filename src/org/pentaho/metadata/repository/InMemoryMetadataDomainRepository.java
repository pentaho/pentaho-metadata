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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.util.SecurityHelper;
import org.pentaho.metadata.messages.Messages;

/**
 * This is an in memory only implementation of the IMetadataDomainRepository
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class InMemoryMetadataDomainRepository implements IMetadataDomainRepository {
  
  private static final Log logger = LogFactory.getLog(InMemoryMetadataDomainRepository.class);
  
  Map<String, Domain> domains = new HashMap<String, Domain>();
  
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
    if (domains == null) {
      return null;
    }
    Domain domain = domains.get(id);
    if (domain != null) {
      SecurityHelper helper = new SecurityHelper();
      Domain clone = helper.createSecureDomain(this, domain);
      return clone;
    } else {
      logger.error("domain not found : " + id);
      return null;
    }
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
    domains = new HashMap<String, Domain>();
  }
  
  public synchronized void removeDomain(String domainId) {
    domains.remove(domainId);
  }
  
  public String generateRowLevelSecurityConstraint(LogicalModel model) {
    return null;
  }
  
  /**
   * The aclHolder cannot be null unless the access type requested is ACCESS_TYPE_SCHEMA_ADMIN.
   */
  public boolean hasAccess(int accessType, IConcept aclHolder) {
    // Subclasses can override this for ACL and Session/Credential checking
    return true;
  }
  
  public synchronized void removeModel(String domainId, String modelId) throws DomainIdNullException, DomainStorageException {
    
    // get a raw domain vs. the cloned secure domain
    Domain domain = domains.get(domainId);
    if (domain == null) {
      throw new DomainIdNullException(Messages.getErrorString("IMetadataDomainRepository.ERROR_0001_DOMAIN_ID_NULL")); //$NON-NLS-1$
    }
    
    // remove the model
    Iterator<LogicalModel> iter = domain.getLogicalModels().iterator();
    while (iter.hasNext()) {
      LogicalModel model = iter.next();
      if (modelId.equals(model.getId())) {
        iter.remove();
        break;
      }
    }
    
    if (domain.getLogicalModels().size() == 0) {
      // remove the domain all together
      removeDomain(domainId);
    } else {
      
      // store the modified domain
      try {
        storeDomain(domain, true);
      } catch (DomainAlreadyExistsException e) {
        logger.error("this should not happen", e);
      }
    }
  }
}
