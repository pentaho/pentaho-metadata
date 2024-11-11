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

import org.pentaho.metadata.datatable.DataTable;

/**
 * An interface for implementing a data source provider.
 * 
 * @author jamesdixon
 * 
 */
public interface ModelProvider {

  /**
   * Returns the id of this model provider
   * 
   * @return
   */
  public String getId();

  /**
   * Returns a model with the specified id. If the model does not exist, returns null
   * 
   * @param id
   * @return
   */
  public Model getModel( String id );

  /**
   * Returns an array of model descriptions. If the provider id does not match the current provider returns an empty
   * array.
   * 
   * @param provider
   * @param group
   *          Optional the group id of the models to search
   * @param match
   *          Optional a string to match on the model name/description
   * @return
   */
  public ModelInfo[] getModelList( String provider, String group, String match );

  /**
   * Exexcutes a query and returns the results in a DataTable object. If row limit is specified returns only the
   * specified number of rows or less. rowLimit of -1 indicates all rows.
   * 
   * @param query
   * @param rowLimit
   * @return
   */
  public DataTable executeQuery( Query query, int rowLimit );
}
