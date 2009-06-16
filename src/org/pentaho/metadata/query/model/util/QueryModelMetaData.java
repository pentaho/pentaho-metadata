/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.query.model.util;

import java.util.List;
import java.util.Map;

import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.messages.Messages;

/**
 * This class extends The Pentaho Connection API's MemoryMetaData
 * with additional Query Selection Metadata. 
 *
 */
public class QueryModelMetaData extends MemoryMetaData {

  private List<? extends Selection> columns;

  /**
   * 
   * In some database implementations, the "as" identifier has a finite length;
   * for instance, Oracle cannot handle a name longer than 30 characters. 
   * So, we map a short name here to the longer id, and replace the id
   * later in the resultset metadata. 
   *
   * @param columnsMap a map representing the truncated column names used in the query, mapped to the 
   *                   user-requested column id identifiers
   * @param columnHeaders column headers from the native metadata object
   * @param rowHeaders row headers from the native metadata object
   */
  public QueryModelMetaData(Map columnsMap, Object[][] columnHeaders, Object[][] rowHeaders, List<? extends Selection> selections) {
    super(columnHeaders, rowHeaders);
    this.columns = selections;
    
    Object[][] newHeaders = columnHeaders;
    Object key;

    if (columnsMap != null) {

      newHeaders = new Object[columnHeaders.length][];
      Object newHeader = null;
      for (int i = 0; i < columnHeaders.length; i++) {
        newHeaders[i] = new Object[columnHeaders[i].length];
        for (int j = 0; j < columnHeaders[i].length; j++) {
          key = columnHeaders[i][j];
          if (key != null) {
            newHeader = columnsMap.get(key.toString().toUpperCase());
            if (newHeader == null) {
              throw new RuntimeException(Messages.getErrorString("QueryModelMetaData.ERROR_0001_MetadataColumnNotFound", key.toString())); //$NON-NLS-1$
            }
            newHeaders[i][j] = newHeader;
          }
        }
      }
      
      this.columnHeaders = newHeaders;
    }
    
  }

  public Object getAttribute(int rowNo, int columnNo, String attributeName) {
    // TODO support OLAP

    if (rowNo == 0 && columnNo < columns.size()) {
      LogicalColumn column = columns.get(columnNo).getLogicalColumn();
      return column.getProperty(attributeName);
    }
    return null;
  }
}
