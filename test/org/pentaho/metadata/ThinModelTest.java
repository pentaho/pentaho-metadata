package org.pentaho.metadata;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.util.SerializationService;

public class ThinModelTest {
  
  @Test
  public void testSqlPhysicalModel() {
    
    // this is the minimum physical sql model, it could
    // theoretically be used to execute sql directly.
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    model.setDatasource("SampleData");
    SqlPhysicalTable table = new SqlPhysicalTable();
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    // basic tests
    
    Assert.assertEquals("SampleData", model.getDatasource());
    Assert.assertEquals(1, model.getPhysicalTables().size());
    Assert.assertEquals(TargetTableType.INLINE_SQL, model.getPhysicalTables().get(0).getTargetTableType());
    Assert.assertEquals("select * from customers", model.getPhysicalTables().get(0).getTargetTable());
    Assert.assertEquals(1, model.getPhysicalTables().size());
  }
  
  @Test
  public void testSqlLogicalModel() {
    
    // this sql model is the minimum required for
    // MQL execution
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    model.setDatasource("SampleData");
    SqlPhysicalTable table = new SqlPhysicalTable();
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select distinct customername from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn();
    column.setTargetColumn("customername");
    column.setName(new LocalizedString("Customer Name"));
    column.setDataType(DataType.STRING);
    
    // logical model 
    
    LogicalModel logicalModel = new LogicalModel();
    model.setId("MODEL");
    model.setName(new LocalizedString("My Model"));
    model.setDescription(new LocalizedString("A Description of the Model"));
    
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
    mainCategory.setName(new LocalizedString("Category"));
    
    // replacement for formula / is exact could be 
    // target column + target column type (calculated, exact, etc)
  }
  
  @Test
  public void testSerializeSqlPhysicalModel() {
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    model.setDatasource("SampleData");
    SqlPhysicalTable table = new SqlPhysicalTable();
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn();
    column.setTargetColumn("customername");
    column.setName(new LocalizedString("Customer Name"));
    column.setDataType(DataType.STRING);
    
    table.getPhysicalColumns().add(column);
    
    LogicalModel logicalModel = new LogicalModel();
    model.setId("MODEL");
    model.setName(new LocalizedString("My Model"));
    model.setDescription(new LocalizedString("A Description of the Model"));
    
    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setPhysicalTable(table);
    
    logicalModel.getLogicalTables().add(logicalTable);
    
    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId("LC_CUSTOMERNAME");
    logicalColumn.setPhysicalColumn(column);
    
    logicalTable.addLogicalColumn(logicalColumn);
    
    Category mainCategory = new Category();
    mainCategory.setId("CATEGORY");
    mainCategory.setName(new LocalizedString("Category"));
    mainCategory.addLogicalColumn(logicalColumn);
    
    logicalModel.getCategories().add(mainCategory);
    
    Domain domain = new Domain();
    domain.addPhysicalModel(model);
    domain.addLogicalModel(logicalModel);
    
    // basic tests
    SerializationService service = new SerializationService();
    
    String xml = service.serializeDomain(domain);

    System.out.println(xml);
    
    Domain domain2 = service.deserializeDomain(xml);
    
    Assert.assertEquals(1, domain2.getPhysicalModels().size());
    SqlPhysicalModel model2 = (SqlPhysicalModel)domain2.getPhysicalModels().get(0);
    Assert.assertEquals("SampleData", model2.getDatasource());
    Assert.assertEquals(1, model.getPhysicalTables().size());
    Assert.assertEquals(TargetTableType.INLINE_SQL, model.getPhysicalTables().get(0).getTargetTableType());
    
    Assert.assertEquals(1, domain.getLogicalModels().size());
    Assert.assertEquals(1, domain.getLogicalModels().get(0).getCategories().size());
    Assert.assertEquals(1, domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().size());
    Assert.assertEquals(domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0), 
                        domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0));
    Assert.assertEquals("Customer Name", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getName().getString("en_US"));
    
  }
}
