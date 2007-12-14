package org.pentaho.pms.automodel;

import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueArrayList;
import be.ibridge.kettle.core.value.Value;

public class PhysicalTableImporter {
	public static PhysicalTable importTableDefinition(Database database, String schemaName, String tableName, String locale)
			throws KettleException {
		UniqueArrayList fields = new UniqueArrayList();

		String id = tableName;
		String tablename = tableName;

		// Remove
		id = Const.toID(tableName);

		// Set the id to a certain standard...
		id = Settings.getPhysicalTableIDPrefix() + id;
		if (Settings.isAnIdUppercase())
			id = id.toUpperCase();

		PhysicalTable physicalTable = new PhysicalTable(id, schemaName, tableName, database.getDatabaseMeta(), fields);

		// Also set a localized description...
		String niceName = beautifyName(tablename);
		physicalTable.getConcept().setName(locale, niceName);

		DatabaseMeta dbMeta = database.getDatabaseMeta();
		String schemaTableCombination = dbMeta.getSchemaTableCombination(dbMeta.quoteField(schemaName), dbMeta
				.quoteField(tableName));

		Row row = database.getTableFields(schemaTableCombination);

		if (row != null && row.size() > 0) {
			for (int i = 0; i < row.size(); i++) {
				Value v = row.getValue(i);
				PhysicalColumn physicalColumn = importPhysicalColumnDefinition(v, physicalTable, locale);
				try {
					fields.add(physicalColumn);
				} catch (ObjectAlreadyExistsException e) {
					// Don't add this column
					// TODO: show error dialog.
				}
			}
		}
		String upper = tablename.toUpperCase();

		if (upper.startsWith("D_") || upper.startsWith("DIM") || upper.endsWith("DIM"))physicalTable.setTableType(TableTypeSettings.DIMENSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (upper.startsWith("F_") || upper.startsWith("FACT") || upper.endsWith("FACT"))physicalTable.setTableType(TableTypeSettings.FACT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return physicalTable;
	}

	public static final String beautifyName(String name) {
		return new Value("niceName", name).replace("_", " ").initcap().getString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private static PhysicalColumn importPhysicalColumnDefinition(Value v, PhysicalTable physicalTable, String locale) {
		// The id
		//
		String id = Settings.getPhysicalColumnIDPrefix() + v.getName();
		if (Settings.isAnIdUppercase())
			id = id.toUpperCase();

		// The name of the column in the database
		//
		String dbname = v.getName();

		// The field type?
		//
		FieldTypeSettings fieldType = FieldTypeSettings.guessFieldType(v.getName());

		// Create a physical column.
		//
		PhysicalColumn physicalColumn = new PhysicalColumn(v.getName(), dbname, fieldType, AggregationSettings.NONE,
				physicalTable);

		// Set the localized name...
		//
		String niceName = beautifyName(v.getName());
		physicalColumn.setName(locale, niceName);

		// Set the parent concept to the base concept...
		// physicalColumn.getConcept().setParentInterface(schemaMeta.findConcept(Settings.getConceptNameBase()));

		// The data type...
		DataTypeSettings dataTypeSettings = getDataTypeSettings(v);
		physicalColumn.setDataType(dataTypeSettings);

		return physicalColumn;
	}

	private static DataTypeSettings getDataTypeSettings(Value v) {
		DataTypeSettings dataTypeSettings = new DataTypeSettings(DataTypeSettings.DATA_TYPE_STRING);
		switch (v.getType()) {
		case Value.VALUE_TYPE_BIGNUMBER:
		case Value.VALUE_TYPE_INTEGER:
		case Value.VALUE_TYPE_NUMBER:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_NUMERIC);
			break;

		case Value.VALUE_TYPE_BINARY:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BINARY);
			break;

		case Value.VALUE_TYPE_BOOLEAN:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BOOLEAN);
			break;

		case Value.VALUE_TYPE_DATE:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_DATE);
			break;

		case Value.VALUE_TYPE_STRING:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_STRING);
			break;

		case Value.VALUE_TYPE_NONE:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_UNKNOWN);
			break;

		default:
			break;
		}
		dataTypeSettings.setLength(v.getLength());
		dataTypeSettings.setPrecision(v.getPrecision());

		return dataTypeSettings;
	}

}
