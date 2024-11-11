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

package org.pentaho.metadata.query.impl.sql;

import org.pentaho.commons.connection.IPentahoMetaData;

/**
 * TODO: I'd prefer if the Sql Execution was done in this package, not having the SQL Execution happen outside of
 * metadata To make this happen, we'd need to move SQLConnection and SQLResultSet into a commons project.
 * 
 * This interface defines the result of executing an MQLQuery
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public interface SqlQuery {

  public String getQuery();

  public IPentahoMetaData generateMetadata( IPentahoMetaData nativeMetadata );

}
