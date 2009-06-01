package org.pentaho.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;

public class ThinModelTest {
  
  @Test
  public void testSqlPhysicalModel() {
    
    // this is the minimum physical sql model, it could
    // theoretically be used to execute sql directly.
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName("SampleData");
    model.setDatasource(dataSource);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    // basic tests
    
    Assert.assertEquals("SampleData", model.getDatasource().getDatabaseName());
    Assert.assertEquals(1, model.getPhysicalTables().size());
    Assert.assertEquals(TargetTableType.INLINE_SQL, model.getPhysicalTables().get(0).getTargetTableType());
    Assert.assertEquals("select * from customers", model.getPhysicalTables().get(0).getTargetTable());
    Assert.assertEquals(1, model.getPhysicalTables().size());
  }
  
  @Test
  public void testSqlLogicalModel() {
    
    String locale = LocaleHelper.getLocale().toString();
    
    // this sql model is the minimum required for
    // MQL execution
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName("SampleData");
    model.setDatasource(dataSource);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    table.setId("PT1");
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select distinct customername from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn(table);
    column.setId("PC1");
    column.setTargetColumn("customername");
    column.setName(new LocalizedString(locale, "Customer Name"));
    column.setDescription(new LocalizedString(locale, "Customer Description"));
    column.setDataType(DataType.STRING);
    
    // logical model 
    
    LogicalModel logicalModel = new LogicalModel();
    model.setId("MODEL");
    model.setName(new LocalizedString(locale, "My Model"));
    model.setDescription(new LocalizedString(locale, "A Description of the Model"));
    
    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable(table);
    
    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId("LC_CUSTOMERNAME");
    logicalColumn.setPhysicalColumn(column);
    
    // test name inheritance
    Assert.assertEquals(
        column.getName().getString(Locale.getDefault().toString()),
        logicalColumn.getName().getString(Locale.getDefault().toString()));
    
    // test datatype inheritance
    Assert.assertEquals(
        column.getDataType(),
        logicalColumn.getDataType());
    
    
    Category mainCategory = new Category();
    mainCategory.setId("CATEGORY");
    mainCategory.setName(new LocalizedString(locale, "Category"));
    
    // replacement for formula / is exact could be 
    // target column + target column type (calculated, exact, etc)
  }
  
  @Test
  public void testSerializeSqlPhysicalModel() {
    
    String locale = LocaleHelper.getLocale().toString();
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName("SampleData");
    model.setDatasource(dataSource);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn(table);
    column.setTargetColumn("customername");
    column.setName(new LocalizedString(locale, "Customer Name"));
    column.setDescription(new LocalizedString(locale, "Customer Name Desc"));
    column.setDataType(DataType.STRING);
    
    table.getPhysicalColumns().add(column);
    
    LogicalModel logicalModel = new LogicalModel();
    model.setId("MODEL");
    model.setName(new LocalizedString(locale, "My Model"));
    model.setDescription(new LocalizedString(locale, "A Description of the Model"));
    
    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable(table);
    
    logicalModel.getLogicalTables().add(logicalTable);
    
    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId("LC_CUSTOMERNAME");
    logicalColumn.setPhysicalColumn(column);
    
    logicalTable.addLogicalColumn(logicalColumn);
    
    Category mainCategory = new Category();
    mainCategory.setId("CATEGORY");
    mainCategory.setName(new LocalizedString(locale, "Category"));
    mainCategory.addLogicalColumn(logicalColumn);
    
    logicalModel.getCategories().add(mainCategory);
    
    Domain domain = new Domain();
    domain.addPhysicalModel(model);
    domain.addLogicalModel(logicalModel);
    
    // basic tests
    SerializationService service = new SerializationService();
    
    String xml = service.serializeDomain(domain);

    // System.out.println(xml);
    
    Domain domain2 = service.deserializeDomain(xml);
    
    Assert.assertEquals(1, domain2.getPhysicalModels().size());
    SqlPhysicalModel model2 = (SqlPhysicalModel)domain2.getPhysicalModels().get(0);
    Assert.assertEquals("SampleData", model2.getDatasource().getDatabaseName());
    Assert.assertEquals(1, model.getPhysicalTables().size());
    Assert.assertEquals(TargetTableType.INLINE_SQL, model.getPhysicalTables().get(0).getTargetTableType());
    
    Assert.assertEquals(1, domain.getLogicalModels().size());
    Assert.assertEquals(1, domain.getLogicalModels().get(0).getCategories().size());
    Assert.assertEquals(1, domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().size());
    Assert.assertEquals(domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0), 
                        domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0));
    Assert.assertEquals("Customer Name", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getName().getString("en_US"));
    Assert.assertEquals("Customer Name Desc", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getDescription().getString("en_US"));    
    
  }
  
  public Domain getBasicDomain() {
    
    String locale = LocaleHelper.getLocale().toString();
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName("SampleData");
    model.setDatasource(dataSource);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    table.setId("PT1");
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn(table);
    column.setId("PC1");
    column.setTargetColumn("customername");
    column.setName(new LocalizedString(locale, "Customer Name"));
    column.setDescription(new LocalizedString(locale, "Customer Name Desc"));
    column.setDataType(DataType.STRING);
    table.getPhysicalColumns().add(column);
    
    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setId("MODEL");
    logicalModel.setName(new LocalizedString(locale, "My Model"));
    logicalModel.setDescription(new LocalizedString(locale, "A Description of the Model"));
    
    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setId("LT");
    logicalTable.setPhysicalTable(table);
    
    logicalModel.getLogicalTables().add(logicalTable);
    
    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId("LC_CUSTOMERNAME");
    logicalColumn.setPhysicalColumn(column);
    logicalColumn.setLogicalTable(logicalTable);
    logicalTable.addLogicalColumn(logicalColumn);
    
    Category mainCategory = new Category();
    mainCategory.setId("CATEGORY");
    mainCategory.setName(new LocalizedString(locale, "Category"));
    mainCategory.addLogicalColumn(logicalColumn);
    
    logicalModel.getCategories().add(mainCategory);
    
    Domain domain = new Domain();
    domain.setId("DOMAIN");
    domain.addPhysicalModel(model);
    domain.addLogicalModel(logicalModel);
    
    List<LocaleType> list = new ArrayList<LocaleType>();
    list.add(new LocaleType(locale, "Locale Description"));
    domain.setLocales(list);
    
    return domain;
  }
  
  
  @Test
  public void testToFromLegacy() {
    Domain domain = getBasicDomain();
    SchemaMeta meta = null;
    try {
      meta = ThinModelConverter.convertToLegacy(domain);
    } catch (Exception e){
      e.printStackTrace();
      Assert.fail();
    }
    
    String locale = Locale.getDefault().toString();
    
    Assert.assertEquals(1, meta.getLocales().nrLocales());
    Assert.assertEquals("en_US", meta.getLocales().getLocale(0).getCode());
    
    // verify conversion worked.
    BusinessModel model = meta.findModel("MODEL");
    Assert.assertNotNull(model);
    Assert.assertEquals("My Model", model.getName(locale));
    Assert.assertEquals("A Description of the Model", model.getDescription(locale));
    
    BusinessCategory cat = model.getRootCategory().findBusinessCategory("CATEGORY");
    Assert.assertNotNull(cat);
    Assert.assertEquals("Category", cat.getName(locale));
    
    Assert.assertEquals(1, cat.getBusinessColumns().size());
    
    // this tests the inheritance of physical cols made it through
    BusinessColumn col = cat.getBusinessColumn(0);
    Assert.assertEquals("Customer Name", col.getName(locale));
    Assert.assertEquals("Customer Name Desc", col.getDescription(locale));
    Assert.assertNotNull(col.getBusinessTable());
    Assert.assertEquals("LT", col.getBusinessTable().getId());

    Assert.assertEquals(col.getDataType(), DataTypeSettings.STRING);
    Assert.assertEquals("select * from customers", col.getBusinessTable().getTargetTable());
    Assert.assertEquals("select * from customers", col.getPhysicalColumn().getTable().getTargetTable());
    Assert.assertEquals("customername", col.getPhysicalColumn().getFormula());
    Assert.assertEquals(false, col.getPhysicalColumn().isExact());
  
    Domain domain2 = null;
    
    try {
      domain2 = ThinModelConverter.convertFromLegacy(meta);
    } catch (Exception e){
      e.printStackTrace();
      Assert.fail();
    }
    
    Assert.assertEquals(1, domain2.getLocales().size());
    Assert.assertEquals("en_US", domain2.getLocales().get(0).getCode());
    
    // verify conversion worked.
    LogicalModel logicalModel = domain2.findLogicalModel("MODEL");
    Assert.assertNotNull(logicalModel);
    Assert.assertEquals("My Model", logicalModel.getName().getString(locale));
    Assert.assertEquals("A Description of the Model", logicalModel.getDescription().getString(locale));
    
    Category category = logicalModel.findCategory("CATEGORY");
    Assert.assertNotNull(category);
    Assert.assertEquals("Category", category.getName().getString(locale));
    
    Assert.assertEquals(1, category.getLogicalColumns().size());
    
    // this tests the inheritance of physical cols made it through
    LogicalColumn column = category.getLogicalColumns().get(0);
    Assert.assertEquals("Customer Name", column.getName().getString(locale));
    Assert.assertEquals("Customer Name Desc", column.getDescription().getString(locale));
    Assert.assertNotNull(column.getLogicalTable());
    Assert.assertEquals("LT", column.getLogicalTable().getId());

    Assert.assertEquals(column.getDataType(), DataType.STRING);
    Assert.assertEquals("select * from customers", column.getLogicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE));
    Assert.assertEquals("select * from customers", column.getPhysicalColumn().getPhysicalTable().getProperty(SqlPhysicalTable.TARGET_TABLE));
    Assert.assertEquals("customername", column.getPhysicalColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN));
    Assert.assertEquals(TargetColumnType.COLUMN_NAME, column.getPhysicalColumn().getProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE));
  }
}
