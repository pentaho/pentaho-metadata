/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.metadata.model.thin;

import java.io.Serializable;
import java.util.Map;

/**
 * Concrete, lightweight, serializable object that holds the envelope information of an {@see IModel} object
 * 
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

  private Map<String, String> capabilities;

  public Map<String, String> getCapabilities() {
    return capabilities;
  }

  public void setCapabilities( Map<String, String> capabilities ) {
    this.capabilities = capabilities;
  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId( String modelId ) {
    this.modelId = modelId;
  }

  /**
   * Returns the id of the group of the model
   * 
   * @return
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Sets the domain id of the model
   * 
   * @param domainId
   */
  public void setGroupId( String groupId ) {
    this.groupId = groupId;
  }

  public Provider getProvider() {
    return provider;
  }

  public void setProvider( Provider provider ) {
    this.provider = provider;
  }

  /**
   * Returns the id of the model
   * 
   * @return
   */
  public String getId() {
    if ( provider == null ) {
      return "unknownprovider" + ID_SEPERATOR + groupId + ID_SEPERATOR + modelId;
    }
    return provider.getId() + ID_SEPERATOR + groupId + ID_SEPERATOR + modelId;
  }

  /**
   * Returns the name of the model for the current locale
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the model for the current locale
   * 
   * @param name
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Returns the description of the model for the current locale
   * 
   * @return
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the model
   * 
   * @param description
   */
  public void setDescription( String description ) {
    this.description = description;
  }

}
