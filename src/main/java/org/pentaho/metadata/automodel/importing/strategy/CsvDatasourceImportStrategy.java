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



package org.pentaho.metadata.automodel.importing.strategy;

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.metadata.automodel.PhysicalTableImporter;
import org.pentaho.metadata.model.thin.Column;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for binding column id's and column names, so as to provide friendly column name
 * by column id.
 *
 * When we create csv data source, we can specify column names with the chars that are not allowed
 * for MDX queries. They will be replaced with legal chars in database level, but they should be displayed
 * as they are, with no changes.
 *
 * Column id here - is the name of the column in database and column name is
 * originally specified name by user (no matter whether it is valid for MDX queries or not)
 *
 *
 * This should be used only by csv datasource, because it is the only datasource type, where we create a table in db
 * (and not use the existing). When creating a table we have a list of specified by user column names. Some of them can be invalid
 * and will be replaced for allowing MDX queries. It's the only case where col id and col name is different. When we are creating
 * datasource from already existing tables (it's true case for other data sources) we can't modify anything (form UI) in column names
 * of an existing table, so names and ids will always be the same for this case (which makes use of this class useless)
 *
 */
public class CsvDatasourceImportStrategy implements PhysicalTableImporter.ImportStrategy {
  private Map<String, String> columnsIdToNameMap;

  public CsvDatasourceImportStrategy( Column[] columns ) {
    columnsIdToNameMap = new HashMap<>(  );
    bindIdsToNames( columns );
  }

  private void bindIdsToNames( Column[] columns ) {
    for ( Column column : columns ) {
      final String name = column.getName();
      final String id = column.getId();
      if ( name != null && id != null ) {
        columnsIdToNameMap.put( column.getId(), column.getName() );
      }
    }
  }

  @Override
  public boolean shouldInclude( ValueMetaInterface valueMeta ) {
    return true;
  }

  @Override
  public String displayName( ValueMetaInterface valueMeta ) {
    // it's an id (column name on database level) actually.
    final String columnId = valueMeta.getName();
    final String columnName = columnsIdToNameMap.get( columnId );

    return columnName == null ? columnId : columnName;
  }
}
