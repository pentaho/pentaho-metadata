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

/**
 * This class implements IModelService and provides the local (embedded) implementation.
 * This class requires a CwmSchemaFactoryInterface implementation to be provided
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;
import org.pentaho.pms.schema.v3.temp.ModelUtil;

public class LocalModelService implements IModelService {

  private CwmSchemaFactoryInterface cwmSchemaFactory;
  
  /**
   * Sets the schema factory that will be used to provide
   * the metadata models
   * @param cwmSchemaFactory
   */
  public void setCwmSchemaFactory(CwmSchemaFactoryInterface cwmSchemaFactory) {
    this.cwmSchemaFactory = cwmSchemaFactory;
  }

  /**
   * @see org.pentaho.pms.service.IModelService.getModel
   */
  public Model getModel(String domain, String id, boolean deep) throws Exception {

    // get the CWM instance amd schema
    CWM cwm = CWM.getInstance(domain, false);
    SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

    // work out which is the best locale to use and set it
    String locale = LocaleHelper.getLocale().toString();
    String locales[] = schemaMeta.getLocales().getLocaleCodes();
    locale = LocaleHelper.getClosestLocale( locale, locales );
    schemaMeta.setActiveLocale(locale);

    // find the business model and convert it to a thin model
    BusinessModel model = schemaMeta.findModel(id);
    if( model != null ) {
      return ModelUtil.getModel( model, domain, locale, deep );
    } else {
      return null;
    }
  }

  /**
   * @see org.pentaho.pms.service.IModelService.listModels
   */
  public ModelEnvelope[] listModels() throws Exception {

    List<ModelEnvelope> modelEnvelopes = new ArrayList<ModelEnvelope>();
    // iterate through all the available domains
    String domains[] = CWM.getDomainNames();
    for (String domain : domains) {
      
      // get the CWM instance amd schema
      CWM cwm = CWM.getInstance(domain, false);
      SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);

      // work out which is the best locale to use and set it
      String locale = LocaleHelper.getLocale().toString();
      String locales[] = schemaMeta.getLocales().getLocaleCodes();
      locale = LocaleHelper.getClosestLocale( locale, locales );
      schemaMeta.setActiveLocale(locale);

      // iterate through the business models adding thin models to the list
      List models = schemaMeta.getBusinessModels().getList();
      Iterator it = models.iterator();
      while (it.hasNext()) {
        BusinessModel model = (BusinessModel) it.next();
        ModelEnvelope modelEnvelope = ModelUtil.getModelEvelope(model, domain, locale);
        modelEnvelopes.add( modelEnvelope );
      }
    }
    // return the list
    return modelEnvelopes.toArray( new ModelEnvelope[modelEnvelopes.size()] );
  }

}
