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

package org.pentaho.pms.mql;

import org.pentaho.commons.connection.IPentahoMetaData;

/**
 * This interface defines the result of executing an MQLQuery
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.impl.sql.SqlQuery
 */
public interface Query {

  public String getQuery();

  public IPentahoMetaData generateMetadata( IPentahoMetaData nativeMetadata );

}
