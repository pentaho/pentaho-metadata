package org.pentaho.metadata;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.SQLModelGenerator;
import org.pentaho.metadata.util.SQLModelGeneratorException;
import org.pentaho.metadata.util.SerializationService;

public class SQLModelGeneratorTest {
  
  @Test
  public void testSQLModelGenerator() {
    String query = "select customername, customernumber, city from customers where customernumber < 171";
    Connection connection = null;
    try {
    connection = getDataSourceConnection("org.hsqldb.jdbcDriver","SampleData"
        ,"pentaho_user", "password"
          ,"jdbc:hsqldb:file:test/solution/system/data/sampledata");
    } catch(Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    SQLModelGenerator generator = new SQLModelGenerator("newdatasource", connection, query);
    Domain domain = null;
    try {
      domain = generator.generate(); 
    } catch(SQLModelGeneratorException smge) {
      Assert.fail();
    }
    
    // basic tests
    SerializationService service = new SerializationService();
    
    String xml = service.serializeDomain(domain);
  
    System.out.println(xml);
    
    Domain domain2 = service.deserializeDomain(xml);
    
    Assert.assertEquals(1, domain2.getPhysicalModels().size());
  }
  
  private Connection getDataSourceConnection(String driverClass, String name, String username, String password, String url) throws Exception {
    Connection conn = null;

    if (StringUtils.isEmpty(driverClass)) {
      throw new Exception("Connection attempt failed"); //$NON-NLS-1$  
    }
    Class<?> driverC = null;

    try {
      driverC = Class.forName(driverClass);
    } catch (ClassNotFoundException e) {
      throw new Exception("Driver not found in the class path. Driver was " + driverClass, e); //$NON-NLS-1$
    }
    if (!Driver.class.isAssignableFrom(driverC)) {
      throw new Exception("Driver not found in the class path. Driver was " + driverClass); //$NON-NLS-1$    }
    }
    Driver driver = null;
    
    try {
      driver = driverC.asSubclass(Driver.class).newInstance();
    } catch (InstantiationException e) {
      throw new Exception("Unable to instance the driver", e); //$NON-NLS-1$
    } catch (IllegalAccessException e) {
      throw new Exception("Unable to instance the driver", e); //$NON-NLS-1$    }
    }
    try {
      DriverManager.registerDriver(driver);
      conn = DriverManager.getConnection(url, username, password);
      return conn;
    } catch (SQLException e) {
      throw new Exception("Unable to connect", e); //$NON-NLS-1$
    }
  }
}
