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
package org.pentaho.pms.service;

import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;

/**
 * The interface for the metadata model service.
 * 
 * This interface has local (embbeded), web service,
 * and a client proxy implementations
 * @author jamesdixon
 *
 */
public interface IModelService {

  /**
   * Returns a list of ModelEnvelope objects available to the current user
   * @return
   * @throws Exception
   */
  public ModelEnvelope[] listModels() throws Exception;

  /**
   * Returns a thin Model object given its domain and id. If the 'deep' flag 
   * is set the attributes of the columns will be included, otherwise they
   * will not. 
   * @param domain
   * @param id
   * @param deep
   * @return
   * @throws Exception
   */
  public Model getModel( String domain, String id, boolean deep ) throws Exception;
  
}
