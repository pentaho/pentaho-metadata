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
 * Copyright (c) 2010 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.Map;

import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.repository.IMetadataDomainRepository;

/**
 * This is an interface for metadata query execution.
 * 
 * @author James Dixon
 *
 */
public interface IMetadataQueryExec {
  
  /**
   * Executes the provided query using the parameters provided.
   * @param queryObject
   * @param parameters
   * @return
   */
  public IPentahoResultSet executeQuery(Query queryObject);

  /**
   * Set whether the execution of the query should be logged
   * @param doQueryLog
   */
  public void setDoQueryLog(boolean doQueryLog);
  
  /**
   * Sets the maximum number of rows to return
   * @param maxRows
   */
  public void setMaxRows(Integer maxRows);
  
  /**
   * Sets the query timeout length (seconds) to be used
   * @param timeout
   */
  public void setTimeout(Integer timeout);
  
  /**
   * Sets the repository that the metadata model is stored in
   * @param repo
   */
  public void setMetadataDomainRepository( IMetadataDomainRepository repo );
  
  /**
   * Sets whether the query should be read-only
   * @param readOnly
   */
  public void setReadOnly( boolean readOnly );
  
  /**
   * Returns whether the results set is live (cursored) or not (held in memory)
   * @return
   */
  public boolean isLive();

  /**
   * Sets whether the query should be forward only
   * @param forwardOnly
   */
  public void setForwardOnly(boolean forwardOnly);
  
  /**
   * Sets the physical model that they query is retrieving from
   * @param physicalModel
   */
  public void setPhysicalModel( IPhysicalModel physicalModel );

  /**
   * Sets a parameter value
   * @param name
   * @param value
   */
  public void setParameter(Parameter param, Object value);

  /**
   * Sets the inputs map. These are inputs to the query execution
   * that are not parameters for the query, e.g. settings needed by
   * the implementation
   * @param name
   * @param value
   */
  public void setInputs(Map<String,Object> inputs);

}
