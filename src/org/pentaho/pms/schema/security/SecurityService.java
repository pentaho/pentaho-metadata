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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.LogWriter;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.exception.KettleXMLException;

public class SecurityService extends ChangedFlag implements Cloneable
{
    public static final int SERVICE_TYPE_ALL    = 0;
    public static final int SERVICE_TYPE_USERS  = 1;
    public static final int SERVICE_TYPE_ROLES  = 2;
    public static final int SERVICE_TYPE_ACLS   = 3;
    
    public static final String[] serviceTypeCodes        = new String[] { "all", "users", "roles", "acls", };
    public static final String[] serviceTypeDescriptions = new String[] { "All", "Users", "Roles", "ACLs", };
    
    public static final String ACTION   = "action";
    public static final String USERNAME = "userid";
    public static final String PASSWORD = "password";
    
    private String serviceURL;
    private String detailsServiceName;
    private String detailServiceName;
    private int    detailServiceType;
    private String username;
    private String password;
    private String proxyHostname;
    private String proxyPort;
    private String nonProxyHosts;
    
    private String filename;
    
    public SecurityService()
    {
    }

    protected Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public String toString()
    {
        if (hasService()) return detailsServiceName;
        return "SecurityService";
    }
    
    
    /**
     * @return the detailServiceName
     */
    public String getDetailServiceName()
    {
        return detailServiceName;
    }

    /**
     * @param detailServiceName the detailServiceName to set
     */
    public void setDetailServiceName(String detailServiceName)
    {
        this.detailServiceName = detailServiceName;
    }

    /**
     * @return the detailServiceType
     */
    public int getDetailServiceType()
    {
        return detailServiceType;
    }

    /**
     * @param detailServiceType the detailServiceType to set
     */
    public void setDetailServiceType(int detailServiceType)
    {
        this.detailServiceType = detailServiceType;
    }

    /**
     * @return the detailsServiceName
     */
    public String getDetailsServiceName()
    {
        return detailsServiceName;
    }

    /**
     * @param detailsServiceName the detailsServiceName to set
     */
    public void setDetailsServiceName(String detailsServiceName)
    {
        this.detailsServiceName = detailsServiceName;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the serviceURL
     */
    public String getServiceURL()
    {
        return serviceURL;
    }

    /**
     * @param serviceURL the serviceURL to set
     */
    public void setServiceURL(String serviceURL)
    {
        this.serviceURL = serviceURL;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getServiceTypeCode()
    {
        return serviceTypeCodes[detailServiceType];
    }

    public String getServiceTypeDesc()
    {
        return serviceTypeDescriptions[detailServiceType];
    }

    /**
     * @return the nonProxyHosts
     */
    public String getNonProxyHosts()
    {
        return nonProxyHosts;
    }

    /**
     * @param nonProxyHosts the nonProxyHosts to set
     */
    public void setNonProxyHosts(String nonProxyHosts)
    {
        this.nonProxyHosts = nonProxyHosts;
    }

    /**
     * @return the proxyHostname
     */
    public String getProxyHostname()
    {
        return proxyHostname;
    }

    /**
     * @param proxyHostname the proxyHostname to set
     */
    public void setProxyHostname(String proxyHostname)
    {
        this.proxyHostname = proxyHostname;
    }

    /**
     * @return the proxyPort
     */
    public String getProxyPort()
    {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(String proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    public static final int getServiceType(String description)
    {
        for (int i=0;i<serviceTypeDescriptions.length;i++)
        {
            if (serviceTypeDescriptions[i].equalsIgnoreCase(description)) return i;
        }
        for (int i=0;i<serviceTypeCodes.length;i++)
        {
            if (serviceTypeCodes[i].equalsIgnoreCase(description)) return i;
        }
        return SERVICE_TYPE_ALL;
    }
   
    public Node getContent() throws Exception
    {
        if (hasService())
        {
            return getContentFromServer();
        }
        else
        if (hasFile())
        {
            return getContentFromFile();
        }
        throw new Exception("unable to get security reference : no service nor filename specified.");
    }
   
    /**
     * Contact the server and get back the content as XML 
     * @return the requested security reference information
     * @throws Exception in case something goes awry 
     */
    public Node getContentFromServer() throws Exception
    {
        LogWriter log = LogWriter.getInstance();
        
        String urlToUse = getURL();
        URL server;
        StringBuffer result = new StringBuffer();
        
        try
        {
            String beforeProxyHost     = System.getProperty("http.proxyHost"); 
            String beforeProxyPort     = System.getProperty("http.proxyPort"); 
            String beforeNonProxyHosts = System.getProperty("http.nonProxyHosts"); 

            BufferedReader      input        = null;
            
            try
            {
                log.logBasic(toString(), "Connecting to URL: "+urlToUse);

                if (proxyHostname!=null) 
                {
                    System.setProperty("http.proxyHost", proxyHostname);
                    System.setProperty("http.proxyPort", proxyPort);
                    if (nonProxyHosts!=null) System.setProperty("http.nonProxyHosts", nonProxyHosts);
                }
                
                if (username!=null && username.length()>0)
                {
                    Authenticator.setDefault(new Authenticator()
                        {
                            protected PasswordAuthentication getPasswordAuthentication()
                            {
                                return new PasswordAuthentication(username, password!=null ? password.toCharArray() : new char[] {} );
                            }
                        }
                    );
                }

                // Get a stream for the specified URL
                server = new URL(urlToUse);
                URLConnection connection = server.openConnection();
                
                log.logDetailed(toString(), "Start reading reply from webserver.");
    
                // Read the result from the server...
                input = new BufferedReader(new InputStreamReader( connection.getInputStream() ));
                
                long bytesRead = 0L;
                String line;
                while ( (line=input.readLine())!=null )
                {
                    result.append(line).append(Const.CR);
                    bytesRead+=line.length();
                }
                
                log.logBasic(toString(), "Finished reading "+bytesRead+" bytes as a response from the webserver");
            }
            catch(MalformedURLException e)
            {
                log.logError(toString(), "The specified URL is not valid ["+urlToUse+"] : "+e.getMessage());
                log.logError(toString(), Const.getStackTracker(e));
            }
            catch(IOException e)
            {
                log.logError(toString(), "I was unable to save the HTTP result to file because of a I/O error: "+e.getMessage());
                log.logError(toString(), Const.getStackTracker(e));
            }
            catch(Exception e)
            {
                log.logError(toString(), "Error getting file from HTTP : "+e.getMessage());
                log.logError(toString(), Const.getStackTracker(e));
            }
            finally
            {
                // Close it all
                try
                {
                    if (input!=null) input.close();
                }
                catch(Exception e)
                {
                    log.logError(toString(), "Unable to close streams : "+e.getMessage());
                    log.logError(toString(), Const.getStackTracker(e));
                }

            }

            // Set the proxy settings back as they were on the system!
            System.setProperty("http.proxyHost", Const.NVL(beforeProxyHost, ""));
            System.setProperty("http.proxyPort", Const.NVL(beforeProxyPort, ""));
            System.setProperty("http.nonProxyHosts", Const.NVL(beforeNonProxyHosts, ""));
            
            // Get the result back...
            Document doc = XMLHandler.loadXMLString(result.toString());
            Node envelope = XMLHandler.getSubNode(doc, "SOAP-ENV:Envelope");
            if (envelope!=null)
            {
                Node body = XMLHandler.getSubNode(envelope, "SOAP-ENV:Body");
                if (body!=null)
                {
                    Node response = XMLHandler.getSubNode(body, "ExecuteActivityResponse");
                    if (response!=null)
                    {
                        Node content = XMLHandler.getSubNode(response, "content");
                        return content;
                    }
                }
            }
            return null;
        }
        catch(Exception e)
        {
            throw new Exception("Unable to contact URL ["+urlToUse+"] to get the security reference information.", e);
        }
    }
    
    /**
     * Read the specified security file and get back the content as XML 
     * @return the requested security reference information
     * @throws Exception in case something goes awry 
     */
    public Node getContentFromFile() throws Exception
    {
        try
        {
            Document doc = XMLHandler.loadXMLFile(filename);
            return XMLHandler.getSubNode(doc, "content");
        }
        catch(KettleXMLException e)
        {
            throw new Exception("Unable to get security content from file ["+filename+"]", e);
        }
    }
    
    public String getContentAsXMLString() throws Exception
    {
        Node content = getContent();
        if (content==null) return null;
        return content.getChildNodes().toString();
    }
    
    
    public String getURL()
    {
        StringBuffer url = new StringBuffer();
        url.append(serviceURL);
        url.append("?").append(USERNAME).append("=").append(username);
        url.append("&").append(PASSWORD).append("=").append(password);
        url.append("&").append(ACTION).append("=").append(detailsServiceName);
        url.append("&").append(detailServiceName).append("=").append(getServiceTypeCode());
        
        return url.toString();
    }

    public boolean hasService()
    {
        return !Const.isEmpty(serviceURL) && !Const.isEmpty(detailsServiceName) && !Const.isEmpty(detailServiceName);
    }
    
    public boolean hasFile()
    {
        return !Const.isEmpty(filename);
    }

    /**
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }


}
