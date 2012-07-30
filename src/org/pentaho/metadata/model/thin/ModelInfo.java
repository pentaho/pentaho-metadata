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
 * Copyright (c) 2011 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jan, 2011
 * @author jdixon
*/
package org.pentaho.metadata.model.thin;

import java.io.Serializable;
import java.util.Map;

/**
 * Concrete, lightweight, serializable object that holds the envelope information
 * of an {@see IModel} object
 * @author jamesdixon
 *
 */
public class ModelInfo implements Serializable {

	public static final String ID_SEPERATOR = "~";
	
  private static final long serialVersionUID = -8341925508348437605L;

  protected String groupId;
  
  protected String modelId;

  protected String name;
  
  protected String description;
  
  protected Provider provider;

  private Map<String,String> capabilities;
  
  public Map<String,String> getCapabilities() {
	return capabilities;
  }

  public void setCapabilities(Map<String,String> capabilities) {
	this.capabilities = capabilities;
  }

  
  public String getModelId() {
	return modelId;
  }

  public void setModelId(String modelId) {
	this.modelId = modelId;
  }

/**
   * Returns the id of the group of the model
   * @return
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Sets the domain id of the model
   * @param domainId
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public Provider getProvider() {
	return provider;
}

public void setProvider(Provider provider) {
	this.provider = provider;
}

/**
   * Returns the id of the model
   * @return
   */
  public String getId() {
	  if( provider == null ) {
		  return "unknownprovider"+ID_SEPERATOR+groupId+ID_SEPERATOR+modelId;
	  }
    return provider.getId()+ID_SEPERATOR+groupId+ID_SEPERATOR+modelId;
  }

  /**
   * Returns the name of the model for the current locale
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the model for the current locale
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of the model for the current locale
   * @return
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the model
   * @param description
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
}
