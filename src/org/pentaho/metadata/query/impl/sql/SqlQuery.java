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
 */
package org.pentaho.metadata.query.impl.sql;

import org.pentaho.commons.connection.IPentahoMetaData;

/**
 * TODO: I'd prefer if the Sql Execution was done in this package, not having the SQL Execution happen outside of metadata
 * To make this happen, we'd need to move SQLConnection and SQLResultSet into a commons project.
 * 
 * This interface defines the result of executing an MQLQuery
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public interface SqlQuery {
  
  public String getQuery();

  public IPentahoMetaData generateMetadata(IPentahoMetaData nativeMetadata);
  
}
