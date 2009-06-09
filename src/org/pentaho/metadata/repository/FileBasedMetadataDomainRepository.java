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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.util.SecurityHelper;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.pms.messages.Messages;

/**
 * This is a file based implementation of the IMetadataDomainRepository
 * 
 * @author Will Gorman(wgorman@pentaho.com)
 */
public class FileBasedMetadataDomainRepository implements IMetadataDomainRepository {
  
  private static final Log logger = LogFactory.getLog(FileBasedMetadataDomainRepository.class);
  
  private static final String DOMAIN_SUFFIX = ".domain.xml"; //$NON-NLS-1$
  private static final String DEFAULT_DOMAIN_FOLDER = "domains"; //$NON-NLS-1$
  
  protected Map<String, Domain> domains = null;
  private String domainFolder = null;
  
  public void setDomainFolder(String folder) {
    this.domainFolder = folder;
  }
  
  public synchronized void storeDomain(Domain domain, boolean overwrite) throws DomainIdNullException, DomainAlreadyExistsException, DomainStorageException {

    if (domain.getId() == null) {
      // todo: replace with exception
      throw new DomainIdNullException(Messages.getErrorString("IMetadataDomainRepository.ERROR_0001_DOMAIN_ID_NULL")); //$NON-NLS-1$
    }
    
    if (!overwrite && domains != null && domains.get(domain.getId()) != null) {
      throw new DomainAlreadyExistsException(Messages.getErrorString("IMetadataDomainRepository.ERROR_0002_DOMAIN_OBJECT_EXISTS", domain.getId())); //$NON-NLS-1$
    }
    
    File folder = getDomainsFolder();
    if (!folder.exists()) {
      folder.mkdirs();
    }
    
    File domainFile = new File(folder, getDomainFilename(domain.getId()));
    
    if (!overwrite && domainFile.exists()) {
      throw new DomainAlreadyExistsException(Messages.getErrorString("FileBasedMetadataDomainRepository.ERROR_0003_DOMAIN_FILE_EXISTS", domain.getId())); //$NON-NLS-1$
    }
    
    SerializationService service = new SerializationService();
    try {
      FileOutputStream output = new FileOutputStream(domainFile);
      service.serializeDomain(domain, output);
    } catch (FileNotFoundException e) {
      throw new DomainStorageException(Messages.getErrorString("FileBasedMetadataDomainRepository.ERROR_0004_DOMAIN_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
    }
    
    // adds the domain to the domains list
    if (domains == null) {
      domains = new HashMap<String, Domain>();
    }
    domains.put(domain.getId(), domain);
  }
  
  private String getDomainFilename(String id) {
    String cleansedName = id.replaceAll("[^a-zA-Z0-9_]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
    return cleansedName + DOMAIN_SUFFIX;
  }
  
  public Domain getDomain(String id) {
    // for now, lazy load all the domains at once.  We could be smarter,
    // loading the files as requested.
    
    if (domains == null) {
      reloadDomains();
    }
    Domain domain = domains.get(id);
    SecurityHelper helper = new SecurityHelper();
    Domain clone = helper.createSecureDomain(this, domain);
    return clone;
  }
  
  public Set<String> getDomainIds() {
    if (domains == null) {
      reloadDomains();
    }
    return domains.keySet();
  }
  
  private static class DomainFileNameFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return name.endsWith(DOMAIN_SUFFIX);
    }
  }
  
  protected File getDomainsFolder() {
    String domainsFolder = DEFAULT_DOMAIN_FOLDER;
    if (domainFolder != null) {
      domainsFolder = domainFolder;
    }
    File folder = new File(domainsFolder);
    return folder;
  }
  
  public synchronized void flushDomains() {
    domains = null;
  }
  
  public synchronized void reloadDomains() {
    // load the domains from the file system
    // for each file in the system/metadata/domains folder that ends with .domain.xml, load
    Map<String, Domain> localDomains = new HashMap<String, Domain>();
    SerializationService service = new SerializationService();
    File folder = getDomainsFolder();
    if (folder.exists()) {
      for (File file : folder.listFiles(new DomainFileNameFilter())) {
        // load domain
        try {
          Domain domain = service.deserializeDomain(new FileInputStream(file));
          localDomains.put(domain.getId(), domain);
        } catch (FileNotFoundException e) {
          logger.error(Messages.getErrorString("FileBasedMetadataDomainRepository.ERROR_0005_FAILED_TO_LOAD_DOMAIN", file.getName()) , e); //$NON-NLS-1$
        }
      }
    }
    
    domains = localDomains;
  }
  
  public synchronized void removeDomain(String domainId) {
    File folder = getDomainsFolder();
    File domainFile = new File(folder, getDomainFilename(domainId));
    domains.remove(domainId);
    domainFile.delete();
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

}
