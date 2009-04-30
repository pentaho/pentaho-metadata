package org.pentaho.metadata;

import org.junit.Assert;
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
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;

public class ThinQueryTest {
  
  public Domain getBasicDomain() {
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    model.setDatasource("SampleData");
    SqlPhysicalTable table = new SqlPhysicalTable();
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn();
    column.setTargetColumn("customername");
    column.setName(new LocalizedString("Customer Name"));
    column.setDescription(new LocalizedString("Customer Name Desc"));
    column.setDataType(DataType.STRING);
    
    table.getPhysicalColumns().add(column);
    
    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setId("MODEL");
    logicalModel.setName(new LocalizedString("My Model"));
    logicalModel.setDescription(new LocalizedString("A Description of the Model"));
    
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
    domain.setId("DOMAIN");
    domain.addPhysicalModel(model);
    domain.addLogicalModel(logicalModel);
    
    return domain;
  }
  
  @Test
  public void testQueryXmlSerialization() {
    Domain domain = getBasicDomain();
    LogicalModel model = domain.findLogicalModel("MODEL");
    Query query = new Query(domain, model);
    
    Category category = model.findCategory("CATEGORY");
    LogicalColumn column = category.findLogicalColumn("LC_CUSTOMERNAME");
    query.getSelections().add(new Selection(category, column, null));
    
    query.getConstraints().add(new Constraint(CombinationType.AND, "[CATEGORY.LC_CUSTOMERNAME] = \"bob\""));

    query.getOrders().add(new Order(new Selection(category, column, null), Order.Type.ASC));
    
    QueryXmlHelper helper = new QueryXmlHelper();
    String xml = helper.toXML(query);
    
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    try {
      repo.storeDomain(domain, true);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Query newQuery = null;
    try {
      newQuery = helper.fromXML(repo, xml);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    // verify that when we serialize and deserialize, the xml stays the same. 
    Assert.assertEquals(xml, helper.toXML(newQuery));
    
  }
}
