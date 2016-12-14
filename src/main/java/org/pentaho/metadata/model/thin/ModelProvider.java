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
 * Copyright (c) 2012 Pentaho Corporation.  All rights reserved.
 * Created Jun, 2012
 * @author jdixon
 */
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
