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
package org.pentaho.pms.schema.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.exception.KettleXMLException;

public class SecurityService extends ChangedFlag implements Cloneable {
  
  public static final int SERVICE_TYPE_ALL = 0;

  public static final int SERVICE_TYPE_USERS = 1;

  public static final int SERVICE_TYPE_ROLES = 2;

  public static final String[] serviceTypeCodes = new String[] { "all", "users", "roles" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

  public static final String[] serviceTypeDescriptions = new String[] { Messages.getString("SecurityService.USER_ALL"), //$NON-NLS-1$
      Messages.getString("SecurityService.USER_USERS"), //$NON-NLS-1$
      Messages.getString("SecurityService.USER_ROLES") }; //$NON-NLS-1$

  public static final String ACTION = "action"; //$NON-NLS-1$

  public static final String USERNAME = "userid"; //$NON-NLS-1$

  public static final String PASSWORD = "password"; //$NON-NLS-1$

  private String serviceURL;

  private String serviceName;

  private String detailNameParameter;

  private int detailServiceType;

  private String username;

  private String password;

  private String proxyHostname;

  private String proxyPort;

  private String nonProxyHosts;

  private String filename;
  
  LogWriter log = null;

  public SecurityService() {
    try {
      LogWriter log = LogWriter.getInstance(Const.META_EDITOR_LOG_FILE, false, LogWriter.LOG_LEVEL_BASIC);
    } catch (KettleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  protected Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      return null;
    }
  }

  public String toString() {
    if (hasService())
      return serviceName;
    return "SecurityService"; //$NON-NLS-1$
  }

  /**
   * @return the detailNameParameter
   */
  public String getDetailNameParameter() {
    return detailNameParameter;
  }

  /**
   * @param detailNameParameter the detailNameParameter to set
   */
  public void setDetailNameParameter(String detailServiceName) {
    this.detailNameParameter = detailServiceName;
  }

  /**
   * @return the detailServiceType
   */
  public int getDetailServiceType() {
    return detailServiceType;
  }

  /**
   * @param detailServiceType the detailServiceType to set
   */
  public void setDetailServiceType(int detailServiceType) {
    this.detailServiceType = detailServiceType;
  }

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param serviceName the serviceName to set
   */
  public void setServiceName(String name) {
    this.serviceName = name;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the serviceURL
   */
  public String getServiceURL() {
    return serviceURL;
  }

  /**
   * @param serviceURL the serviceURL to set
   */
  public void setServiceURL(String serviceURL) {
    this.serviceURL = serviceURL;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  public String getServiceTypeCode() {
    return serviceTypeCodes[detailServiceType];
  }

  public String getServiceTypeDesc() {
    return serviceTypeDescriptions[detailServiceType];
  }

  /**
   * @return the nonProxyHosts
   */
  public String getNonProxyHosts() {
    return nonProxyHosts;
  }

  /**
   * @param nonProxyHosts the nonProxyHosts to set
   */
  public void setNonProxyHosts(String nonProxyHosts) {
    this.nonProxyHosts = nonProxyHosts;
  }

  /**
   * @return the proxyHostname
   */
  public String getProxyHostname() {
    return proxyHostname;
  }

  /**
   * @param proxyHostname the proxyHostname to set
   */
  public void setProxyHostname(String proxyHostname) {
    this.proxyHostname = proxyHostname;
  }

  /**
   * @return the proxyPort
   */
  public String getProxyPort() {
    return proxyPort;
  }

  /**
   * @param proxyPort the proxyPort to set
   */
  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public static final int getServiceType(String description) {
    for (int i = 0; i < serviceTypeDescriptions.length; i++) {
      if (serviceTypeDescriptions[i].equalsIgnoreCase(description))
        return i;
    }
    for (int i = 0; i < serviceTypeCodes.length; i++) {
      if (serviceTypeCodes[i].equalsIgnoreCase(description))
        return i;
    }
    return SERVICE_TYPE_ALL;
  }

  public Node getContent() throws Exception {
    if (hasService()) {
      return getContentFromServer();
    } else if (hasFile()) {
      return getContentFromFile();
    }
    throw new Exception(Messages.getString("SecurityService.ERROR_0001_UNABLE_TO_GET_SECURITY_REFERENCE")); //$NON-NLS-1$
  }

  /**
   * Contact the server and get back the content as XML 
   * @return the requested security reference information
   * @throws Exception in case something goes awry 
   */
  public Node getContentFromServer() throws PentahoMetadataException  {
    
    LogWriter log = LogWriter.getInstance();

    String urlToUse = getURL();
    String result = null;
    int status = -1;

    URL tempURL;
    try {
    
      // verify the URL is syntactically correct; we will use these pieces later in this method
      tempURL = new URL(urlToUse);
    
    } catch (MalformedURLException e) {
    
      String msg = Messages.getString("SecurityService.ERROR_0002_INVALID_URL", urlToUse, e.getMessage()); //$NON-NLS-1$
      log.logError(toString(), msg); 
      log.logError(toString(), Const.getStackTracker(e));
      throw new PentahoMetadataException(msg, e); 
    
    }

    HttpClient client = new HttpClient();
    log.logBasic(toString(), Messages.getString("SecurityService.INFO_CONNECTING_TO_URL", urlToUse)); //$NON-NLS-1$

    // Assume we are using a proxy if proxyHostName is set? 
    // TODO: Mod ui to include check for enable or disable proxy; rather than rely on proxyhostname (post v1)
    if ((proxyHostname != null) && (proxyHostname.trim().length() > 0)) {

      int port = (proxyPort == null) || (proxyPort.trim().length() == 0) ? 
          client.getHostConfiguration().getPort(): Integer.parseInt(proxyPort);

      //TODO: Where to set nonProxyHosts?

      client.getHostConfiguration().setProxy(proxyHostname, port);

      //TODO: Credentials for proxy will be added if demand shows for it (post v1)
      //          if (username != null && username.length() > 0) {
      //            client.getState().setProxyCredentials(AuthScope.ANY, 
      //                new UsernamePasswordCredentials(username, password != null ? password : new String()));
      //          }

    }

    // If server userid/password was supplied, use basic authentication to
    // authenticate with the server.
    if ((username != null) && (username.length() > 0) && (password != null) && (password.length() > 0)) {

      Credentials creds = new UsernamePasswordCredentials(username, password);
      client.getState().setCredentials(new AuthScope(tempURL.getHost(), tempURL.getPort()), creds);
      client.getParams().setAuthenticationPreemptive(true);

    }

    // Get a stream for the specified URL
    GetMethod getMethod = new GetMethod(urlToUse);
    try {

      status = client.executeMethod(getMethod);
    
      if (status == HttpStatus.SC_OK) {

        log.logDetailed(toString(), Messages.getString("SecurityService.INFO_START_READING_WEBSERVER_REPLY")); //$NON-NLS-1$
        result = getMethod.getResponseBodyAsString();
        log.logBasic(toString(), Messages.getString("SecurityService.INFO_FINISHED_READING_RESPONSE", Integer.toString(result.length()))); //$NON-NLS-1$ 

      } else if (status == HttpStatus.SC_UNAUTHORIZED) {

        String msg = Messages.getString("SecurityService.ERROR_0009_UNAUTHORIZED_ACCESS_TO_URL", urlToUse); //$NON-NLS-1$
        log.logError(toString(), msg); 
        throw new PentahoMetadataException(msg);
      
      }

    } catch (HttpException e) {
    
      String msg = Messages.getString("SecurityService.ERROR_0003_CANT_SAVE_IO_ERROR", e.getMessage()); //$NON-NLS-1$
      log.logError(toString(), msg); 
      log.logError(toString(), Const.getStackTracker(e));
      throw new PentahoMetadataException(msg, e); 
    
    } catch (IOException e) {
      
      String msg = Messages.getString("SecurityService.ERROR_0004_ERROR_RETRIEVING_FILE_FROM_HTTP", e.getMessage()); //$NON-NLS-1$
      log.logError(toString(), msg); 
      log.logError(toString(), Const.getStackTracker(e));
      throw new PentahoMetadataException(msg, e); 
    
    }


    if (result != null){

      // Get the result back...
      Document doc;
      try {
        
        doc = XMLHandler.loadXMLString(result);
      
      } catch (KettleXMLException e) {

        String msg = Messages.getString("SecurityService.ERROR_0008_ERROR_PARSING_XML", e.getMessage()); //$NON-NLS-1$
        log.logError(toString(), msg); 
        log.logError(toString(), Const.getStackTracker(e));
        throw new PentahoMetadataException(msg, e); 
        
      }
      
      Node envelope = XMLHandler.getSubNode(doc, "SOAP-ENV:Envelope"); //$NON-NLS-1$
      if (envelope != null) {
        Node body = XMLHandler.getSubNode(envelope, "SOAP-ENV:Body"); //$NON-NLS-1$
        if (body != null) {
          Node response = XMLHandler.getSubNode(body, "ExecuteActivityResponse"); //$NON-NLS-1$
          if (response != null) {
            Node content = XMLHandler.getSubNode(response, "content"); //$NON-NLS-1$
            return content;
          }
        }
      }
      
    }
    return null;

  }


  /**
   * Read the specified security file and get back the content as XML 
   * @return the requested security reference information
   * @throws Exception in case something goes awry 
   */
  public Node getContentFromFile() throws Exception {
    try {
      Document doc = XMLHandler.loadXMLFile(filename);
      return XMLHandler.getSubNode(doc, "content"); //$NON-NLS-1$
    } catch (KettleXMLException e) {
      throw new Exception(Messages.getString("SecurityService.ERROR_0007_UNABLE_TO_GET_SECURITY_CONTENT", filename), e); //$NON-NLS-1$ 
    }
  }

  public String getContentAsXMLString() throws Exception {
    Node content = getContent();
    if (content == null)
      return null;
    return content.getChildNodes().toString();
  }

  public String getURL() {
    StringBuffer url = new StringBuffer();
    url.append(serviceURL);
    url.append("?").append(ACTION).append("=").append(serviceName); //$NON-NLS-1$ //$NON-NLS-2$
    url.append("&").append(detailNameParameter).append("=").append(getServiceTypeCode()); //$NON-NLS-1$ //$NON-NLS-2$

    return url.toString();
  }

  public boolean hasService() {
    return !Const.isEmpty(serviceURL) && !Const.isEmpty(serviceName) && !Const.isEmpty(detailNameParameter);
  }

  public boolean hasFile() {
    return !Const.isEmpty(filename);
  }

  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename the filename to set
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  public List getUsers() {
    List users = new ArrayList();
    if (hasService() || hasFile()) {
      try {
        Node contentNode = getContent();

        // Load the users
        Node usersNode = XMLHandler.getSubNode(contentNode, "users"); //$NON-NLS-1$
        int nrUsers = XMLHandler.countNodes(usersNode, "user"); //$NON-NLS-1$
        for (int i = 0; i < nrUsers; i++) {
          Node userNode = XMLHandler.getSubNodeByNr(usersNode, "user", i); //$NON-NLS-1$
          String username = XMLHandler.getNodeValue(userNode);
          if (username != null)
            users.add(username);
        }
      } catch (PentahoMetadataException ex) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), ex.getLocalizedMessage()); //$NON-NLS-1$
      } catch (Exception e) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), e.getLocalizedMessage()); //$NON-NLS-1$
      }
    }
    return users;
  }
  
  public List getRoles() {
    List roles = new ArrayList();
    if (hasService() || hasFile()) {
      try {
        Node contentNode = getContent();

        // Load the roles
        Node rolesNode = XMLHandler.getSubNode(contentNode, "roles"); //$NON-NLS-1$
        int nrRoles = XMLHandler.countNodes(rolesNode, "role"); //$NON-NLS-1$
        for (int i=0;i<nrRoles;i++)
        {
            Node roleNode = XMLHandler.getSubNodeByNr(rolesNode, "role", i); //$NON-NLS-1$
            String rolename = XMLHandler.getNodeValue(roleNode);
            if (rolename!=null) roles.add(rolename);
        }
      } catch (PentahoMetadataException ex) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), ex.getLocalizedMessage()); //$NON-NLS-1$
      } catch (Exception e) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), e.getLocalizedMessage()); //$NON-NLS-1$
      }
    }
    return roles;
  }

  public List getAcls() {
    List acls = new ArrayList();
    if (hasService() || hasFile()) {
      try {
        Node contentNode = getContent();

        // Load the ACLs
        Node aclsNode = XMLHandler.getSubNode(contentNode, "acls"); //$NON-NLS-1$
        int nrAcls = XMLHandler.countNodes(aclsNode, "acl"); //$NON-NLS-1$
        for (int i=0;i<nrAcls;i++)
        {
            Node aclNode = XMLHandler.getSubNodeByNr(aclsNode, "acl", i); //$NON-NLS-1$
            SecurityACL acl = new SecurityACL(aclNode);
            acls.add(acl);
        }
        Collections.sort(acls); // sort by acl mask, from low to high
      } catch (PentahoMetadataException ex) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), ex.getLocalizedMessage()); //$NON-NLS-1$
      } catch (Exception e) {
        log.logError(Messages.getString("SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML"), e.getLocalizedMessage()); //$NON-NLS-1$
      }
    }
    return acls;
  }

}
