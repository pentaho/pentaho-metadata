package org.pentaho.pms.automodel;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;
import org.pentaho.pms.util.UniqueArrayList;
import org.pentaho.pms.util.UniqueList;

public class PhysicalTableImporter
{
	public static PhysicalTable importTableDefinition(Database database, String schemaName, String tableName,
			String locale) throws KettleException
	{
		UniqueList<PhysicalColumn> fields = new UniqueArrayList<PhysicalColumn>();

		String id = tableName;
		String tablename = tableName;

		// Remove
		id = Const.toID(tableName);

		// Set the id to a certain standard...
		id = Settings.getPhysicalTableIDPrefix() + id;
		if (Settings.isAnIdUppercase())
			id = id.toUpperCase();

		PhysicalTable physicalTable = new PhysicalTable(id, schemaName, tableName,
				database.getDatabaseMeta(), fields);

		// Also set a localized description...
		String niceName = beautifyName(tablename);
		physicalTable.getConcept().setName(locale, niceName);

		DatabaseMeta dbMeta = database.getDatabaseMeta();
		String schemaTableCombination = dbMeta.getSchemaTableCombination(dbMeta.quoteField(schemaName),
				dbMeta.quoteField(tableName));

		RowMetaInterface row = database.getTableFields(schemaTableCombination);

		if (row != null && row.size() > 0)
		{
			for (int i = 0; i < row.size(); i++)
			{
				ValueMetaInterface v = row.getValueMeta(i);
				PhysicalColumn physicalColumn = importPhysicalColumnDefinition(v, physicalTable, locale);
				try
				{
					fields.add(physicalColumn);

				} catch (ObjectAlreadyExistsException e)
				{
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

	public static final String beautifyName(String name)
	{
		return StringUtils.capitalize(name.replace("_", " ")); //$NON-NLS-1$
	}

	private static PhysicalColumn importPhysicalColumnDefinition(ValueMetaInterface v,
			PhysicalTable physicalTable, String locale)
	{
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
		PhysicalColumn physicalColumn = new PhysicalColumn(v.getName(), dbname, fieldType,
				AggregationSettings.NONE, physicalTable);

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

	private static DataTypeSettings getDataTypeSettings(ValueMetaInterface v)
	{
		DataTypeSettings dataTypeSettings = new DataTypeSettings(DataTypeSettings.DATA_TYPE_STRING);
		switch (v.getType())
		{
		case ValueMetaInterface.TYPE_BIGNUMBER:
		case ValueMetaInterface.TYPE_INTEGER:
		case ValueMetaInterface.TYPE_NUMBER:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_NUMERIC);
			break;

		case ValueMetaInterface.TYPE_BINARY:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BINARY);
			break;

		case ValueMetaInterface.TYPE_BOOLEAN:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_BOOLEAN);
			break;

		case ValueMetaInterface.TYPE_DATE:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_DATE);
			break;

		case ValueMetaInterface.TYPE_STRING:
			dataTypeSettings.setType(DataTypeSettings.DATA_TYPE_STRING);
			break;

		case ValueMetaInterface.TYPE_NONE:
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
