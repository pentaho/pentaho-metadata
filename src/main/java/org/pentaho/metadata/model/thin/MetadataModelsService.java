/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.metadata.model.thin;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.datatable.DataTable;

/**
 * A class which can broker for a group of ModelProvider implementations. Clients can use this object to access many
 * model providers through a single call.
 * 
 * @author jamesdixon
 * 
 */
public class MetadataModelsService implements ModelProvider {

  public static final String PROVIDER_ID = "modelservice";

  private static final List<ModelProvider> modelProviders = new ArrayList<ModelProvider>();

  public static final void addProvider( ModelProvider provider ) {
    modelProviders.add( provider );
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  protected String getProviderId( String modelId ) {

    if ( modelId == null ) {
      return null;
    }
    int pos = modelId.indexOf( ModelInfo.ID_SEPERATOR );
    if ( pos != -1 ) {
      return modelId.substring( 0, pos );
    }
    return null;

  }

  @Override
  public ModelInfo[] getModelList( String providerId, String groupId, String match ) {
    List<ModelInfo> infoList = new ArrayList<ModelInfo>();
    for ( ModelProvider provider : modelProviders ) {
      if ( providerId == null || providerId.equals( provider.getId() ) ) {
        ModelInfo[] providerInfos = provider.getModelList( providerId, groupId, match );
        for ( ModelInfo info : providerInfos ) {
          infoList.add( info );
        }
      }
    }

    ModelInfo[] infos = infoList.toArray( new ModelInfo[infoList.size()] );
    return infos;
  }

  @Override
  public Model getModel( String id ) {
    String providerId = getProviderId( id );
    for ( ModelProvider provider : modelProviders ) {
      if ( provider.getId().equals( providerId ) ) {
        Model model = provider.getModel( id );
        if ( model != null ) {
          return model;
        }
      }
    }
    return null;
  }

  @Override
  public DataTable executeQuery( Query query, int rowLimit ) {

    String id = query.getSourceId();
    // find the provider
    String providerId = getProviderId( id );
    for ( ModelProvider provider : modelProviders ) {
      if ( provider.getId().equals( providerId ) ) {
        Model model = provider.getModel( id );
        if ( model != null ) {
          return provider.executeQuery( query, rowLimit );
        }
      }
    }
    return null;

  }
}
