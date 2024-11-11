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

package org.pentaho.metadata.query.model.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.query.model.Selection;

/**
 * This class extends The Pentaho Connection API's MemoryMetaData with additional Query Selection Metadata.
 * 
 */
public class QueryModelMetaData extends MemoryMetaData {

  private List<? extends Selection> columns;

  public QueryModelMetaData( Object[][] columnHeaders, Object[][] rowHeaders, String columnNameFormatStr,
      String[] columnTypes, String[] columnNames, String[] rowHeaderNames, List<? extends Selection> columns ) {
    super( columnHeaders, rowHeaders, columnNameFormatStr, columnTypes, columnNames, rowHeaderNames );
    this.columns = columns;
  }

  public QueryModelMetaData( QueryModelMetaData metadata ) {
    super( metadata );
    this.columns = metadata.getColumns();
  }

  /**
   * 
   * In some database implementations, the "as" identifier has a finite length; for instance, Oracle cannot handle a
   * name longer than 30 characters. So, we map a short name here to the longer id, and replace the id later in the
   * resultset metadata.
   * 
   * @param columnsMap
   *          a map representing the truncated column names used in the query, mapped to the user-requested column id
   *          identifiers
   * @param columnHeaders
   *          column headers from the native metadata object
   * @param rowHeaders
   *          row headers from the native metadata object
   */
  public QueryModelMetaData( Map columnsMap, Object[][] columnHeaders, Object[][] rowHeaders,
      List<? extends Selection> selections ) {
    super( columnHeaders, rowHeaders );
    this.columns = selections;

    Object[][] newHeaders = columnHeaders;
    Object key;

    if ( columnsMap != null ) {
      Map<String, String> upperColumnMap = null;
      TreeSet<String> existingHeaders = new TreeSet<String>();
      newHeaders = new Object[columnHeaders.length][];
      String newHeader = null;
      for ( int i = 0; i < columnHeaders.length; i++ ) {
        newHeaders[i] = new Object[columnHeaders[i].length];
        for ( int j = 0; j < columnHeaders[i].length; j++ ) {
          key = columnHeaders[i][j];
          if ( key != null ) {
            newHeader = (String) columnsMap.get( key.toString().toUpperCase() );
            if ( newHeader == null ) {
              // Look up key by raw value (required by Hive until support is added for column aliases)
              newHeader = (String) columnsMap.get( key.toString() );
            }
            if ( newHeader == null ) {
              if ( upperColumnMap == null ) {
                upperColumnMap = upperCaseKeys( columnsMap );
              }
              newHeader = upperColumnMap.get( key.toString().toUpperCase() );
            }
            if ( newHeader == null ) {
              throw new RuntimeException( Messages.getErrorString(
                  "QueryModelMetaData.ERROR_0001_MetadataColumnNotFound", key.toString() ) ); //$NON-NLS-1$
            }
            newHeader = getUniqueHeader( newHeader, existingHeaders );
            existingHeaders.add( newHeader );
            newHeaders[i][j] = newHeader;
          }
        }
      }

      this.columnHeaders = newHeaders;
    }
  }

  private Map<String, String> upperCaseKeys( Map<?, ?> source ) {
    Map<String, String> result = new HashMap<String, String>( source.size() );
    for ( Entry<?, ?> entry : source.entrySet() ) {
      String key = null;
      if ( entry.getKey() != null ) {
        key = String.valueOf( entry.getKey() ).toUpperCase();
      }
      String value = null;
      if ( entry.getValue() != null ) {
        value = String.valueOf( entry.getValue() );
      }
      result.put( key, value );
    }
    return result;
  }

  private String getUniqueHeader( String header, TreeSet<String> existingHeaders ) {
    String newHeader = header;
    int count = 1;
    while ( existingHeaders.contains( newHeader ) ) {
      newHeader = header + "_" + count++; //$NON-NLS-1$
    }
    return newHeader;
  }

  public Object getAttribute( int rowNo, int columnNo, String attributeName ) {
    // TODO support OLAP
    if ( rowNo == 0 && columnNo < columns.size() ) {
      LogicalColumn column = columns.get( columnNo ).getLogicalColumn();
      return column.getProperty( attributeName );
    }
    return null;
  }

  public List<? extends Selection> getColumns() {
    return columns;
  }

}
