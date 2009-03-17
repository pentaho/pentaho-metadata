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
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved. 
 *
 * @created Mar, 2009
 * @author James Dixon
 * 
*/
package org.pentaho.pms.schema.v3.client;

import java.lang.reflect.Method;

import javax.naming.OperationNotSupportedException;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

/**
 * This class makes calls to a web service using Apache Axis2.
 * This class should be subclassed and calls made to executeOperation.
 * This class assumes that the web service being called implements
 * the same Java interface as the subclass.
 * 
 * Lets say the interface (MyInterface) in question has a method -
 * public MyObject getObject( String id );
 * 
 * Both the web service class and the subclass of this class need to implement
 * MyInterface. The subclass of this class will look like this:
 * 
 * public class MyService extends ServiceClientBase implements MyInterface {
 * 
 *   public MyObject getObject( String id ) {
 *     return (MyObject) executeOperation( "getObject", id );
 *   }
 * 
 * }
 * 
 * The call to executeOperation needs to pass the name of the method being 
 * called followed by any parameters to the method. The object, if any, returned
 * from the executeOperation should be cast to match the method signature.
 */
public class ServiceClientBase {

  private String serverContext = null;
  
  private RPCServiceClient serviceClient = null;
  
  private String serviceName = null;
  
  private String nameSpace = null;
  
  /**
   * Sets the namespace for the web service. This is important and must match 
   * the package of the web service class being called.
   * @param nameSpace
   */
  public void setNameSpace(String nameSpace) {
    this.nameSpace = nameSpace;
  }

  /**
   * Sets the server context for the web service call. This should include the
   * protocol, host name, port, and web-app context
   * e.g. http://myserver:8080/pentaho
   * @param serverContext
   */
  public void setServerContext(String serverContext) {
    this.serverContext = serverContext;
  }

  /**
   * Returns a service client for the requested operation 
   * @param operation
   * @return
   * @throws AxisFault
   */
  public RPCServiceClient getServiceClient( String operation ) throws AxisFault {
    if( serviceClient == null ) {
      serviceClient = new RPCServiceClient();
    }
    Options options = serviceClient.getOptions();
    options.setTo(new EndpointReference(
        serverContext+ "/content/ws-run/"+serviceName+"/"+operation) );//$NON-NLS-1$ //$NON-NLS-2$
    return serviceClient;
  }
  
  /**
   * Executes a web service operation. See the documentation above.
   * @param oper
   * @param params
   * @return
   * @throws AxisFault
   * @throws OperationNotSupportedException
   */
  protected Object executeOperation( final String oper, final Object... params ) throws AxisFault, OperationNotSupportedException {
    
    // first find the method requested
    Method methods[] = getClass().getMethods();
    for( Method method : methods ) {
      if( method.getName().equalsIgnoreCase( oper ) ) {
        // we found the method, now call it
        Class returnTypes[] = new Class[] { method.getReturnType() };
        QName opName = new QName( nameSpace, oper); 
        Object[] response = getServiceClient( oper ).invokeBlocking(opName, params, returnTypes);
        return response[0];
      }
    }
    // we did not find the method requested so throw an exception
    throw new OperationNotSupportedException();
    
  }

  /**
   * Sets the name of the web service being called
   * @param serviceName
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }
  
}
