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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata.automodel;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.FieldType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TableType;
import org.pentaho.metadata.util.Util;

public class PhysicalTableImporter {
  public static SqlPhysicalTable importTableDefinition(Database database, String schemaName, String tableName,
      String locale) throws KettleException {
    List<IPhysicalColumn> fields = null;

    String id = tableName;
    String tablename = tableName;

    // Remove
    id = Util.toId(tableName);

    // Set the id to a certain standard...
    id = Util.getPhysicalTableIdPrefix() + id;
    id = id.toUpperCase();

    SqlPhysicalTable physicalTable = new SqlPhysicalTable();
    physicalTable.setId(id);
    physicalTable.setTargetSchema(schemaName);
    fields = physicalTable.getPhysicalColumns();
    physicalTable.setTargetTable(tableName);

    // id, schemaName, tableName,
    // database.getDatabaseMeta(), fields);

    // Also set a localized description...
    String niceName = beautifyName(tablename);
    physicalTable.setName(new LocalizedString(locale, niceName));

    DatabaseMeta dbMeta = database.getDatabaseMeta();
    String schemaTableCombination = dbMeta.getSchemaTableCombination(dbMeta.quoteField(schemaName), dbMeta
        .quoteField(tableName));

    RowMetaInterface row = database.getTableFields(schemaTableCombination);

    if (row != null && row.size() > 0) {
      for (int i = 0; i < row.size(); i++) {
        ValueMetaInterface v = row.getValueMeta(i);
        IPhysicalColumn physicalColumn = importPhysicalColumnDefinition(v, physicalTable, locale);
        fields.add(physicalColumn);
      }
    }
    String upper = tablename.toUpperCase();

    if (upper.startsWith("D_") || upper.startsWith("DIM") || upper.endsWith("DIM"))physicalTable.setTableType(TableType.DIMENSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    if (upper.startsWith("F_") || upper.startsWith("FACT") || upper.endsWith("FACT"))physicalTable.setTableType(TableType.FACT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    return physicalTable;
  }

  public static final String beautifyName(String name) {
    return StringUtils.capitalize(name.replace("_", " ")); //$NON-NLS-1$  //$NON-NLS-2$
  }

  private static IPhysicalColumn importPhysicalColumnDefinition(ValueMetaInterface v, SqlPhysicalTable physicalTable,
      String locale) {
    // The id
    //
    String id = Util.getPhysicalColumnIdPrefix() + v.getName();
    id = id.toUpperCase();

    // The name of the column in the database
    //
    String dbname = v.getName();

    // The field type?
    //
    FieldType fieldType = FieldType.guessFieldType(v.getName());

    // Create a physical column.
    //
    SqlPhysicalColumn physicalColumn = new SqlPhysicalColumn(physicalTable);
    physicalColumn.setId(v.getName());
    physicalColumn.setTargetColumn(dbname);
    physicalColumn.setFieldType(fieldType);
    physicalColumn.setAggregationType(AggregationType.NONE);

    // Set the localized name...
    //
    String niceName = beautifyName(v.getName());
    physicalColumn.setName(new LocalizedString(locale, niceName));

    // Set the parent concept to the base concept...
    // physicalColumn.getConcept().setParentInterface(schemaMeta.findConcept(
    // Settings.getConceptNameBase()));

    // The data type...
    DataType dataType = getDataType(v);
    physicalColumn.setDataType(dataType);

    return physicalColumn;
  }

  private static DataType getDataType(ValueMetaInterface v) {
    switch (v.getType()) {
    case ValueMetaInterface.TYPE_BIGNUMBER:
    case ValueMetaInterface.TYPE_INTEGER:
    case ValueMetaInterface.TYPE_NUMBER:
      return DataType.NUMERIC;
    case ValueMetaInterface.TYPE_BINARY:
      return DataType.BINARY;
    case ValueMetaInterface.TYPE_BOOLEAN:
      return DataType.BOOLEAN;
    case ValueMetaInterface.TYPE_DATE:
      return DataType.DATE;
    case ValueMetaInterface.TYPE_STRING:
      return DataType.STRING;
    case ValueMetaInterface.TYPE_NONE:
    default:
      return DataType.UNKNOWN;
    }
    // the enum data type no longer supports length and precision
    // dataTypeSettings.setLength(v.getLength());
    // dataTypeSettings.setPrecision(v.getPrecision());

  }

}
