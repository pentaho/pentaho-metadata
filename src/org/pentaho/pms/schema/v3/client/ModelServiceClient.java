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

import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;
import org.pentaho.pms.service.IModelService;

/**
 * This class implements the model service interface and makes web service calls
 * to a server to fulfill each call made to it.
 * This class uses the ServiceClientBase which uses introspection and the fact that
 * the webservice we are calling implements the same interface.
 *
 */
public class ModelServiceClient extends ServiceClientBase implements IModelService {
  
  public ModelServiceClient() {
    setServiceName( "ModelService" ); //$NON-NLS-1$
    setNameSpace( "http://server.metadata.services.webservice.platform.pentaho.org" ); //$NON-NLS-1$
  }
  
  public ModelEnvelope[] listModels() throws Exception {
    return (ModelEnvelope[]) executeOperation( "listModels" ); //$NON-NLS-1$ 
  }

  public Model getModel(String domain, String id, boolean deep) throws Exception {
    return (Model) executeOperation( "getModel", domain, id, deep ); //$NON-NLS-1$ 
  }
  
}
