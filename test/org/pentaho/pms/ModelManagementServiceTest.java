package org.pentaho.pms;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.physical.IDataSource;
import org.pentaho.pms.schema.v3.physical.SQLDataSource;
import org.pentaho.pms.service.IModelManagementService;
import org.pentaho.pms.service.ModelManagementService;


public class ModelManagementServiceTest {
  
  private IModelManagementService modelService;
  private IDataSource dataSource;
  
  @Before
  public void init() {
    String databaseType = "MySQL"; // See: DatabaseMeta.dbAccessTypeCode / DatabaseMeta.dbAccessTypeDesc 
    
    String hostname = "localhost"; 
    String port     = "3306"; 
    
    String databaseName = "foodmart";
    
    String username = "foodmart";
    String password = "foodmart";
    
    DatabaseMeta testDbMeta = new DatabaseMeta(databaseName, databaseType, "JDBC", hostname, databaseName, port, username, password); 
    
    modelService = new ModelManagementService();
    
    dataSource = new SQLDataSource(testDbMeta, "SELECT * FROM customer");
  }
  
  @Test
  public void testGetColumns() {
    List<Column> cols = modelService.getColumns(dataSource);
    for(Column col : cols) {
      System.out.println(col.getName() +": "+ col.getDataType());
    }
  }

}
