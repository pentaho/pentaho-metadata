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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.query.model.util;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
      TreeSet<String> existingHeaders = new TreeSet<String>();
      newHeaders = new Object[columnHeaders.length][];
      String newHeader = null;
      for (int i = 0; i < columnHeaders.length; i++) {
        newHeaders[i] = new Object[columnHeaders[i].length];
        for (int j = 0; j < columnHeaders[i].length; j++) {
          key = columnHeaders[i][j];
          if (key != null) {
            newHeader = (String)columnsMap.get(key.toString().toUpperCase());
            if (newHeader == null) {
              // Look up key by raw value (required by Hive until support is added for column aliases)
              newHeader = (String) columnsMap.get(key.toString());
            }
            if (newHeader == null) {
              throw new RuntimeException(Messages.getErrorString("QueryModelMetaData.ERROR_0001_MetadataColumnNotFound", key.toString())); //$NON-NLS-1$
            }
            newHeader = getUniqueHeader(newHeader, existingHeaders);
            existingHeaders.add(newHeader);
            newHeaders[i][j] = newHeader;
          }
        }
      }
      
      this.columnHeaders = newHeaders;
    }
  }
  
  private String getUniqueHeader(String header, TreeSet<String> existingHeaders) {
    String newHeader = header;
    int count = 1;
    while (existingHeaders.contains(newHeader)) {
      newHeader = header + "_" + count++; //$NON-NLS-1$
    }
    return newHeader;
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
