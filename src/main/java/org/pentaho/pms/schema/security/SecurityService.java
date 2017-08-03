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
 * Copyright (c) 2006 - 2017 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.schema.security;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class SecurityService extends ChangedFlag implements Cloneable {

  public static final int SERVICE_TYPE_ALL = 0;

  public static final int SERVICE_TYPE_USERS = 1;

  public static final int SERVICE_TYPE_ROLES = 2;

  public static final String[] serviceTypeCodes = new String[] { "all", "users", "roles" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

  public static final String[] serviceTypeDescriptions = new String[] {
    Messages.getString( "SecurityService.USER_ALL" ), //$NON-NLS-1$
    Messages.getString( "SecurityService.USER_USERS" ), //$NON-NLS-1$
    Messages.getString( "SecurityService.USER_ROLES" ) }; //$NON-NLS-1$

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

  LogChannelInterface log = new LogChannel( toString() );

  public SecurityService() {
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( Exception e ) {
      return null;
    }
  }

  public String toString() {
    if ( hasService() ) {
      return serviceName;
    }
    return "SecurityService"; //$NON-NLS-1$
  }

  /**
   * @return the detailNameParameter
   */
  public String getDetailNameParameter() {
    return detailNameParameter;
  }

  /**
   * @param detailServiceName
   *          the DetailServiceName to set
   */
  public void setDetailNameParameter( String detailServiceName ) {
    this.detailNameParameter = detailServiceName;
  }

  /**
   * @return the detailServiceType
   */
  public int getDetailServiceType() {
    return detailServiceType;
  }

  /**
   * @param detailServiceType
   *          the detailServiceType to set
   */
  public void setDetailServiceType( int detailServiceType ) {
    this.detailServiceType = detailServiceType;
  }

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param name
   *          the serviceName to set
   */
  public void setServiceName( String name ) {
    this.serviceName = name;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *          the password to set
   */
  public void setPassword( String password ) {
    this.password = password;
  }

  /**
   * @return the serviceURL
   */
  public String getServiceURL() {
    return serviceURL;
  }

  /**
   * @param serviceURL
   *          the serviceURL to set
   */
  public void setServiceURL( String serviceURL ) {
    this.serviceURL = serviceURL;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *          the username to set
   */
  public void setUsername( String username ) {
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
   * @param nonProxyHosts
   *          the nonProxyHosts to set
   */
  public void setNonProxyHosts( String nonProxyHosts ) {
    this.nonProxyHosts = nonProxyHosts;
  }

  /**
   * @return the proxyHostname
   */
  public String getProxyHostname() {
    return proxyHostname;
  }

  /**
   * @param proxyHostname
   *          the proxyHostname to set
   */
  public void setProxyHostname( String proxyHostname ) {
    this.proxyHostname = proxyHostname;
  }

  /**
   * @return the proxyPort
   */
  public String getProxyPort() {
    return proxyPort;
  }

  /**
   * @param proxyPort
   *          the proxyPort to set
   */
  public void setProxyPort( String proxyPort ) {
    this.proxyPort = proxyPort;
  }

  public static final int getServiceType( String description ) {
    for ( int i = 0; i < serviceTypeDescriptions.length; i++ ) {
      if ( serviceTypeDescriptions[i].equalsIgnoreCase( description ) ) {
        return i;
      }
    }
    for ( int i = 0; i < serviceTypeCodes.length; i++ ) {
      if ( serviceTypeCodes[i].equalsIgnoreCase( description ) ) {
        return i;
      }
    }
    return SERVICE_TYPE_ALL;
  }

  public Node getContent( String url ) throws Exception {
    if ( hasService() ) {
      return getContentFromServer( url );
    } else if ( hasFile() ) {
      return getContentFromFile();
    }
    throw new Exception( Messages.getString( "SecurityService.ERROR_0001_UNABLE_TO_GET_SECURITY_REFERENCE" ) ); //$NON-NLS-1$
  }

  /**
   * Contact the server and get back the content as XML
   * 
   * @return the requested security reference information
   * @throws Exception
   *           in case something goes awry
   */
  public Node getContentFromServer( String urlToUse ) throws PentahoMetadataException {

    String result = null;
    int status = -1;

    URL tempURL;
    try {

      // verify the URL is syntactically correct; we will use these pieces later in this method
      tempURL = new URL( urlToUse );

    } catch ( MalformedURLException e ) {

      String msg = Messages.getString( "SecurityService.ERROR_0002_INVALID_URL", urlToUse, e.getMessage() ); //$NON-NLS-1$
      log.logError( msg, e );
      throw new PentahoMetadataException( msg, e );

    }

    HttpClientManager.HttpClientBuilderFacade httpClientBuilder = HttpClientManager.getInstance().createBuilder();
    log.logDebug( Messages.getString( "SecurityService.INFO_CONNECTING_TO_URL", urlToUse ) ); //$NON-NLS-1$

    // Assume we are using a proxy if proxyHostName is set?
    // TODO: Mod ui to include check for enable or disable proxy; rather than rely on proxyhostname (post v1)
    HttpClientContext context = null;
    if ( StringUtils.isNotBlank( proxyHostname ) && StringUtils.isNotBlank( proxyPort ) ) {
      int port = Integer.parseInt( this.proxyPort );
      httpClientBuilder.setProxy( proxyHostname, port );

      // TODO: Where to set nonProxyHosts?
      // TODO: Credentials for proxy will be added if demand shows for it (post v1)
      // if (username != null && username.length() > 0) {
      // client.getState().setProxyCredentials(AuthScope.ANY,
      // new UsernamePasswordCredentials(username, password != null ? password : new String()));
      // }

      // If server userid/password was supplied, use basic authentication to
      // authenticate with the server.
      if ( StringUtils.isNotBlank( username ) && StringUtils.isNotBlank( password )  ) {
        AuthScope authScope = new AuthScope( tempURL.getHost(), tempURL.getPort() );
        httpClientBuilder.setCredentials( username, password, authScope );
        HttpClientUtil.createPreemptiveBasicAuthentication( proxyHostname, port, username, password );
      }
    }

    HttpClient client = httpClientBuilder.build();
    // Get a stream for the specified URL
    HttpGet getMethod = new HttpGet( urlToUse );
    try {
      HttpResponse response =
              context != null ? client.execute( getMethod, context ) : client.execute( getMethod );
      status = response.getStatusLine().getStatusCode();

      if ( status == HttpStatus.SC_OK ) {
        log.logDetailed( Messages.getString( "SecurityService.INFO_START_READING_WEBSERVER_REPLY" ) ); //$NON-NLS-1$
        result = HttpClientUtil.responseToString( response );

        log.logBasic( Messages.getString(
            "SecurityService.INFO_FINISHED_READING_RESPONSE", Integer.toString( result.length() ) ) ); //$NON-NLS-1$ 

      } else if ( status == HttpStatus.SC_UNAUTHORIZED ) {
        String msg = Messages.getString( "SecurityService.ERROR_0009_UNAUTHORIZED_ACCESS_TO_URL", urlToUse ); //$NON-NLS-1$
        log.logError( msg );
        throw new PentahoMetadataException( msg );

      }

    } catch ( IOException e ) {
      String msg = Messages.getString( "SecurityService.ERROR_0003_CANT_SAVE_IO_ERROR", e.getMessage() ); //$NON-NLS-1$
      log.logError( msg );
      log.logError( Const.getStackTracker( e ) );
      throw new PentahoMetadataException( msg, e );
    }

    if ( result != null ) {

      // Get the result back...
      Document doc;
      try {

        doc = XMLHandler.loadXMLString( result );

      } catch ( KettleXMLException e ) {

        String msg = Messages.getString( "SecurityService.ERROR_0008_ERROR_PARSING_XML", e.getMessage() ); //$NON-NLS-1$
        log.logError( msg );
        log.logError( Const.getStackTracker( e ) );
        throw new PentahoMetadataException( msg, e );

      }

      if ( serviceURL.endsWith( "ServiceAction" ) ) {
        Node envelope = XMLHandler.getSubNode( doc, "SOAP-ENV:Envelope" ); //$NON-NLS-1$
        if ( envelope != null ) {
          Node body = XMLHandler.getSubNode( envelope, "SOAP-ENV:Body" ); //$NON-NLS-1$
          if ( body != null ) {
            Node response = XMLHandler.getSubNode( body, "ExecuteActivityResponse" ); //$NON-NLS-1$
            if ( response != null ) {
              Node content = XMLHandler.getSubNode( response, "content" ); //$NON-NLS-1$
              return content;
            }
          }
        }
      } else {
        return doc.getFirstChild();
      }
    }
    return null;

  }

  /**
   * Read the specified security file and get back the content as XML
   * 
   * @return the requested security reference information
   * @throws Exception
   *           in case something goes awry
   */
  public Node getContentFromFile() throws Exception {
    try {
      Document doc = XMLHandler.loadXMLFile( filename );
      return XMLHandler.getSubNode( doc, "content" ); //$NON-NLS-1$
    } catch ( KettleXMLException e ) {
      throw new Exception(
          Messages.getString( "SecurityService.ERROR_0007_UNABLE_TO_GET_SECURITY_CONTENT", filename ), e ); //$NON-NLS-1$ 
    }
  }

  public String getContentAsXMLString() throws Exception {
    Node content = getContent( getURL( null ) );
    if ( content == null ) {
      return null;
    }
    return content.getChildNodes().toString();
  }

  public String getURL() {
    return getURL( getServiceTypeCode() );
  }

  public String getURL( String serviceTypeCode ) {
    StringBuffer url = new StringBuffer();
    url.append( serviceURL );
    if ( serviceURL != null && serviceURL.endsWith( "ServiceAction" ) ) {
      url.append( "?" ).append( ACTION ).append( "=" ).append( serviceName ); //$NON-NLS-1$ //$NON-NLS-2$
      url.append( "&" ).append( detailNameParameter ).append( "=" ).append( getServiceTypeCode() ); //$NON-NLS-1$ //$NON-NLS-2$
    } else if ( serviceTypeCode != null ) {
      if ( !serviceURL.endsWith( "/" ) ) {
        url.append( "/" );
      }
      url.append( serviceTypeCode );
    }
    return url.toString();
  }

  public boolean hasService() {
    return !Const.isEmpty( serviceURL ) && !Const.isEmpty( serviceName ) && !Const.isEmpty( detailNameParameter );
  }

  public boolean hasFile() {
    return !Const.isEmpty( filename );
  }

  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename
   *          the filename to set
   */
  public void setFilename( String filename ) {
    this.filename = filename;
  }

  public List<String> getUsers() {
    List<String> users = new ArrayList<String>();
    if ( hasService() || hasFile() ) {
      try {

        if ( getDetailServiceType() == SecurityService.SERVICE_TYPE_USERS || getDetailServiceType() == SERVICE_TYPE_ALL ) {
          String url = getURL( serviceTypeCodes[SERVICE_TYPE_USERS] );
          Node contentNode = getContent( url );
          String usersNode = "users";
          if ( serviceURL.endsWith( "ServiceAction" ) ) {
            contentNode = XMLHandler.getSubNode( contentNode, usersNode ); //$NON-NLS-1$
            usersNode = "user";
          }

          // Load the users
          int nrUsers = XMLHandler.countNodes( contentNode, usersNode ); //$NON-NLS-1$
          for ( int i = 0; i < nrUsers; i++ ) {
            Node userNode = XMLHandler.getSubNodeByNr( contentNode, usersNode, i ); //$NON-NLS-1$
            String username = XMLHandler.getNodeValue( userNode );
            if ( username != null ) {
              users.add( username );
            }
          }
        }
      } catch ( PentahoMetadataException ex ) {
        log.logError( Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), ex ); //$NON-NLS-1$
      } catch ( Exception e ) {
        log.logError( Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), e ); //$NON-NLS-1$
      }
    }
    return users;
  }

  public List<String> getRoles() {
    List<String> roles = new ArrayList<String>();
    if ( hasService() || hasFile() ) {
      try {

        if ( getDetailServiceType() == SecurityService.SERVICE_TYPE_ROLES || getDetailServiceType() == SERVICE_TYPE_ALL ) {
          String url = getURL( serviceTypeCodes[SERVICE_TYPE_ROLES] );
          Node contentNode = getContent( url );
          String rolesNode = "roles";
          if ( serviceURL.endsWith( "ServiceAction" ) ) {
            contentNode = XMLHandler.getSubNode( contentNode, rolesNode ); //$NON-NLS-1$
            rolesNode = "role";
          }

          // Load the roles
          int nrRoles = XMLHandler.countNodes( contentNode, rolesNode ); //$NON-NLS-1$
          for ( int i = 0; i < nrRoles; i++ ) {
            Node roleNode = XMLHandler.getSubNodeByNr( contentNode, rolesNode, i ); //$NON-NLS-1$
            String rolename = XMLHandler.getNodeValue( roleNode );
            if ( rolename != null ) {
              roles.add( rolename );
            }
          }
        }
      } catch ( PentahoMetadataException ex ) {
        log.logError( Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), ex ); //$NON-NLS-1$
      } catch ( Exception e ) {
        log.logError( Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), e ); //$NON-NLS-1$
      }
    }
    return roles;
  }

  public List<SecurityACL> getAcls() {
    List<SecurityACL> acls = new ArrayList<SecurityACL>();
    if ( hasService() || hasFile() ) {
      try {
        Node contentNode = getContent( getURL( null ) );

        // Load the ACLs
        Node aclsNode = XMLHandler.getSubNode( contentNode, "acls" ); //$NON-NLS-1$
        int nrAcls = XMLHandler.countNodes( aclsNode, "acl" ); //$NON-NLS-1$
        for ( int i = 0; i < nrAcls; i++ ) {
          Node aclNode = XMLHandler.getSubNodeByNr( aclsNode, "acl", i ); //$NON-NLS-1$
          SecurityACL acl = new SecurityACL( aclNode );
          acls.add( acl );
        }
        Collections.sort( acls ); // sort by acl mask, from low to high
      } catch ( PentahoMetadataException ex ) {
        log.logError(
            Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), ex.getLocalizedMessage() ); //$NON-NLS-1$
      } catch ( Exception e ) {
        log.logError(
            Messages.getString( "SecurityReference.ERROR_0001_CANT_CREATE_REFERENCE_FROM_XML" ), e.getLocalizedMessage() ); //$NON-NLS-1$
      }
    }
    return acls;
  }

}
