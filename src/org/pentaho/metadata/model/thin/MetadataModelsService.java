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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jun, 2012
 * @author jdixon
 */
package org.pentaho.metadata.model.thin;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.datatable.DataTable;
import org.pentaho.metadata.model.thin.Model;
import org.pentaho.metadata.model.thin.ModelInfo;
import org.pentaho.metadata.model.thin.Query;

/**
 * A class which can broker for a group of ModelProvider implementations. Clients can use this
 * object to access many model providers through a single call.
 * @author jamesdixon
 *
 */
public class MetadataModelsService implements ModelProvider {

	public static final String PROVIDER_ID = "modelservice";
	
	private static final List<ModelProvider> modelProviders = new ArrayList<ModelProvider>();
	
	public static final void addProvider( ModelProvider provider ) {
		modelProviders.add(provider);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
	
	protected String getProviderId( String modelId ) {
		
		int pos = modelId.indexOf(ModelInfo.ID_SEPERATOR);
		if( pos != -1 ) {
			return modelId.substring(0, pos );
		}
		return null;
		
	}
	
	@Override
	public ModelInfo[] getModelList( String providerId, String groupId, String match ) {
		List<ModelInfo> infoList = new ArrayList<ModelInfo>();
		for( ModelProvider provider : modelProviders ) {
			if( providerId == null || providerId.equals(provider.getId())) {
				ModelInfo[] providerInfos = provider.getModelList( providerId, groupId, match );
				for( ModelInfo info : providerInfos ) {
					infoList.add(info);
				}
			}
		}
		
		ModelInfo infos[] = infoList.toArray(new ModelInfo[infoList.size()]);
		return infos;
	}

	@Override
	public Model getModel(String id) {
		String providerId = getProviderId( id );
		for( ModelProvider provider : modelProviders ) {
			if( provider.getId().equals(providerId)) {
				Model model = provider.getModel(id);
				if( model != null ) {
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
		for( ModelProvider provider : modelProviders ) {
			if( provider.getId().equals(providerId)) {
				Model model = provider.getModel(id);
				if( model != null ) {
					return provider.executeQuery(query, rowLimit);
				}
			}
		}
		return null;

	}
}
