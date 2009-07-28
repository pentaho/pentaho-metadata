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
package org.pentaho.pms.mql;

import org.pentaho.commons.connection.IPentahoMetaData;

/**
 * This interface defines the result of executing an MQLQuery
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.query.model.impl.sql.SqlQuery
 */
public interface Query {
  
  public String getQuery();

  public IPentahoMetaData generateMetadata(IPentahoMetaData nativeMetadata);
  
}
