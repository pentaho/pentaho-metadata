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

import java.util.Iterator;
import java.util.Map;

/**
 * A mapped query holds a query string that has the "as" identifiers mapped to truncated
 * values in order to avoid the limitation of finite identifier lengths in some databases 
 * (known issue in Oracle, DB2). 
 * 
 * The MappedQuery holds the query string and a map of truncated names mapped to the real 
 * identifiers, so that the truncation is as transparent as possible to the user. 
 * 
 * @author gmoran
 *
 */
public class MappedQuery extends Query {

  private Map columnsMap;
  
  public MappedQuery(String sql, Map columnsMap){
    query = sql;
    this.columnsMap = columnsMap;
  }
  
/**
 * The "display" query returns the query with the full identifiers in the "as" portion 
 * of the statement. NOTE that this is NOT the query that should be executed. Use getQuery()
 * for the proper executable query string. 
 * 
 * @return displayable query string
 */
  public String getDisplayQuery(){
    
    String execQuery = new String(query);
    for (Iterator iter = columnsMap.keySet().iterator(); iter.hasNext();) {

      String element = (String) iter.next();
      String identifier = (String) columnsMap.get(element);
      execQuery = execQuery.replaceAll(element, identifier);
      
    }
    return execQuery;
  }
  
  /**
   * The map holds the mapping from short ids to long ids. 
   * @return the map of short ids to long ids
   */
  public Map getMap(){
    return columnsMap;
  }
}
