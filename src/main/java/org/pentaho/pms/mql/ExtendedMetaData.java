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

import java.util.List;
import java.util.Map;

import org.pentaho.commons.connection.memory.MemoryMetaData;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;

/**
 * This class extends The Pentaho Connection API's MemoryMetaData with additional BusinessColumn Metadata.
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.util.QueryModelMetaData
 */
public class ExtendedMetaData extends MemoryMetaData {

  private List<? extends Selection> columns;

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
  public ExtendedMetaData( Map columnsMap, Object[][] columnHeaders, Object[][] rowHeaders,
      List<? extends Selection> selections ) {
    super( columnHeaders, rowHeaders );
    this.columns = selections;

    Object[][] newHeaders = columnHeaders;
    Object key;

    if ( columnsMap != null ) {

      newHeaders = new Object[columnHeaders.length][];
      Object newHeader = null;
      for ( int i = 0; i < columnHeaders.length; i++ ) {
        newHeaders[i] = new Object[columnHeaders[i].length];
        for ( int j = 0; j < columnHeaders[i].length; j++ ) {
          key = columnHeaders[i][j];
          if ( key != null ) {
            newHeader = columnsMap.get( key.toString().toUpperCase() );
            if ( newHeader == null ) {
              throw new RuntimeException( Messages.getErrorString(
                  "ExtendedMetadata.ERROR_0001_MetadataColumnNotFound", key.toString() ) ); //$NON-NLS-1$
            }
            newHeaders[i][j] = newHeader;
          }
        }
      }

      this.columnHeaders = newHeaders;
    }

  }

  public Object getAttribute( int rowNo, int columnNo, String attributeName ) {
    // TODO support OLAP

    if ( rowNo == 0 && columnNo < columns.size() ) {
      BusinessColumn column = columns.get( columnNo ).getBusinessColumn();
      return column.getConcept().getChildProperty( attributeName );
    }
    return null;
  }
}
