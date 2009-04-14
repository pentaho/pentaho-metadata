package org.pentaho.pms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.pms.automodel.PhysicalTableImporter;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.physical.IDataSource;
import org.pentaho.pms.schema.v3.temp.ModelUtil;

public class BusinessModelFactory {

  public BusinessModelFactory() {
    if(!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }

  public BusinessModel createModelWithCategory(IDataSource dataSource, String categoryName, List<Column> columns) throws Exception {
    BusinessModel businessModel = getModel(categoryName);

    final String LOCALE = "en_us"; //FIXME: unhardcode this

    BusinessCategory businessCategory = new BusinessCategory(categoryName);
    //TODO: proposed unique id like this. do we really need to?
    //    businessCategory.setId(BusinessCategory.proposeId(locale, this, businessCategory, categories));
    //TODO: set a unique concept name for this category. See BusinessTable.generateCategory for an approach
    businessCategory.getConcept().setName(categoryName); //need to care about locale here

    BusinessTable businessTable = new BusinessTable(null);
    Map<String, PhysicalColumn> physicalColumnsMap;
    try {
      physicalColumnsMap = getPhysicalColumns(dataSource.getDbMeta(), columns, LOCALE);
    } catch (KettleException e) {
      //FIXME: throw a better suited exception here
      throw new Exception("failed to get physical columns", e);
    }
    for (Column col : columns) {
      String key = col.getPhysicalTableName()+"."+col.getPhysicalColumnName();
      PhysicalColumn physicalColumn = physicalColumnsMap.get(key);

      BusinessColumn businessColumn = new BusinessColumn(physicalColumn.getId(), physicalColumn, businessTable);
      businessColumn.setName(LOCALE, col.getName());
      String desc = col.getDescription();
      if(desc != null) {
        businessColumn.setDescription(LOCALE, col.getDescription());
      }
//      businessColumn.setDataType(col.getDataType()); //FIXME: need a utility to convert string data type to DataTypeSettings
//      businessColumn.setFieldType(col.getFieldType()); //FIXME: need a utility to convert string data type to FieldTypeSettings

      //TODO: we might want to propose a new id like so.
      // businessColumn.setId(BusinessColumn.proposeId(locale, businessTable, physicalColumn));

      try {
        businessCategory.addBusinessColumn(businessColumn);
        businessTable.addBusinessColumn(businessColumn);
      } catch (ObjectAlreadyExistsException e) {
        System.err.println("failed to add businessColumn " + businessColumn.getId() + " to category " + categoryName);
        e.printStackTrace();
      }
    }

    try {
      businessModel.addBusinessTable(businessTable); //not sure if this step is necessary but won't hurt
    } catch (ObjectAlreadyExistsException e) {
      // TODO Auto-generated catch block
      System.err.println("failed to add businessTable " + businessTable.getId() + " to businessModel "
          + businessModel.getId());
      e.printStackTrace();
    }
    businessModel.setRootCategory(businessCategory);
    return businessModel;
  }

  /*
   * Create a new model for now.. we may want to attach the new category to an existing model eventually
   */
  private BusinessModel getModel(String modelName) {
    String bmID = "AUTOGEN_MODEL_" + modelName + "_" + new Date().getTime();
    return new BusinessModel(bmID);
  }

  //  public BusinessModel generateBusinessModel() throws PentahoMetadataException {
  //    SchemaTable[] schemaTables = schemaTablesFromSQL();
  //
  //    AutoModeler modeler = new AutoModeler("en_US", "Customers", physicalModel.getDbMeta(), schemaTables);
  //    
  //    //initialize kettle properties (required)
  //    Props.init(Props.TYPE_PROPERTIES_EMPTY);
  //    
  //    SchemaMeta schemaMeta = modeler.generateSchemaMeta(); // throws exception
  //    //TODO: in what scenario will there be several business models?
  //    return schemaMeta.getBusinessModels().get(0);
  //  }
  //
  //  //TODO: not sure where this code should live.. should the physical model be able to connect and extract 
  //  //this or should this live in a utility of some kind?  Should we use Kettle apis to extract this information?
  //  //Should the AutoModeler be changed to build this list internally?  (this one seems most agreeable)
  //  private SchemaTable[] schemaTablesFromSQL() {
  //    String sql = physicalModel.getRevealingSQL();
  //    
  //    // TODO Auto-generated method stub
  //    return null;
  //  }

  //  public BusinessModel createBusinessModel() {
  //    BusinessModel businessModel = new BusinessModel();
  //    for(String columnName : getColumnNames()) {
  //      businessModel.a
  //    }

  /**
   * Given a data source and a list of lightweight columns, this method hydrates physical column objects that represent
   * and back those lightweight columns and returns them in a map for easy access.
   * @return a map with key <tablename>.<columnname> and value PhysicalColumn
   * @throws KettleException 
   */
  public static Map<String, PhysicalColumn> getPhysicalColumns(DatabaseMeta dbmeta, List<Column> columns,
      final String locale) throws KettleException  {
    Map<String, PhysicalColumn> physicalColumnsMap = new HashMap<String, PhysicalColumn>();

    String schemaName = null; //FIXME assuming any schema for now
    Database database = new Database(dbmeta);
    database.connect();

    List<PhysicalTable> physicalTables = new ArrayList<PhysicalTable>();
    for (String tableName : ModelUtil.getBackingTableNames(columns)) {
      physicalTables.add(PhysicalTableImporter.importTableDefinition(database, schemaName, tableName, locale));
    }

    //build intermediate maps for quicker construction of the returned map
    Map<String, Map<String, PhysicalColumn>> tableColumnMap = new HashMap<String, Map<String, PhysicalColumn>>();
    for (PhysicalTable pt : physicalTables) {
      Map<String, PhysicalColumn> nameToColumnMap = new HashMap<String, PhysicalColumn>();
      for (PhysicalColumn c : pt.getPhysicalColumns()) {
        nameToColumnMap.put(c.getTableColumn(), c);
      }
      tableColumnMap.put(pt.getTargetTable(), nameToColumnMap);
    }

    //we have all the physical tables, now create the map
    for (final Column col : columns) {
      physicalColumnsMap.put(col.getPhysicalTableName() + "." + col.getPhysicalColumnName(), tableColumnMap.get(
          col.getPhysicalTableName()).get(col.getPhysicalColumnName()));
    }

    return physicalColumnsMap;
  }
}
