package org.pentaho.pms;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.junit.Ignore;
import org.pentaho.pms.schema.v3.client.ModelServiceClient;
import org.pentaho.pms.schema.v3.model.Category;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;
import org.pentaho.pms.service.IModelService;

/**
 * These tests will only work if a server is running on http://localhost:8080/pentaho 
 * that can answer the web service calls.
 * @author jamesdixon
 *
 */
@SuppressWarnings({"all"})
public class ModelServiceClientTest extends TestCase {

  public void testListModelsClient() throws Exception {
    IModelService svc = getModelService("listModels");
    ModelEnvelope[] models = svc.listModels();

    assertNotNull( "models is null", models ); //$NON-NLS-1$
    
    assertEquals( "Wrong number of models", 3, models.length ); //$NON-NLS-1$
    assertEquals( "Wrong model name", "Human Resources", models[0].getName() ); //$NON-NLS-1$ //$NON-NLS-2$

  }
  
  private IModelService getModelService( String operation ) throws Exception {
    ModelServiceClient svc = new ModelServiceClient();
    svc.setServerContext( "http://localhost:8080/pentaho" );
    
    Options options = svc.getServiceClient(operation).getOptions();
    
    HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
    auth.setUsername("joe"); //$NON-NLS-1$
    auth.setPassword("password"); //$NON-NLS-1$
    auth.setPreemptiveAuthentication( true );
    List<String> schemes = new ArrayList<String>();
    schemes.add( HttpTransportProperties.Authenticator.BASIC );
    auth.setAuthSchemes( schemes );
    options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);
    return svc;
  }
  
  @Ignore
  public void testGetModelClient() throws Exception {

    IModelService svc = getModelService("getModel");
    Model model = svc.getModel("steel-wheels", "BV_ORDERS", false);

    assertNotNull( "models is null", model );
    
    assertEquals( "model id is wrong", "BV_ORDERS", model.getId() );
    assertEquals( "model name is wrong", "Orders", model.getName() );
    
    Category category = model.getRootCategory();
    assertNotNull( "root category is null", category );
    assertNull( "root category has columns", category.getColumns() );
    assertEquals( "wrong # of subcategories", 4, category.getSubCategories().length );

    category = category.getSubCategories()[2];
    assertEquals( "category id is wrong", "CAT_PRODUCTS", category.getId() );
    assertEquals( "category name is wrong", "Products", category.getName() );
    assertEquals( "category description is wrong", "This category contains inforamtion about products.", category.getDescription() );
    assertEquals( "category has wrong # of columns", 9, category.getColumns().length );
    assertNull( "wrong # of subcategories", category.getSubCategories() );
    
    Column column = (Column) category.getColumns()[1];
    assertEquals( "column id is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getId() );
    assertEquals( "column name is wrong", "Product Code", column.getName() );
    assertEquals( "column description is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getDescription() );
    assertNull( "column attributes wrong", column.getAttributes() );
    assertEquals( "column data type wrong", "String", column.getDataType() );
    assertEquals( "column field type wrong", "Dimension", column.getFieldType() );
    
  }
  
  @Ignore
  public void testGetModelClientDeep() throws Exception {

    IModelService svc = getModelService("getModel");
    Model model = svc.getModel("steel-wheels", "BV_ORDERS", true);

    assertNotNull( "models is null", model );
    
    assertEquals( "model id is wrong", "BV_ORDERS", model.getId() );
    assertEquals( "model name is wrong", "Orders", model.getName() );
    
    Category category = model.getRootCategory();
    assertNotNull( "root category is null", category );
    assertNull( "root category has columns", category.getColumns() );
    assertEquals( "wrong # of subcategories", 4, category.getSubCategories().length );

    category = category.getSubCategories()[2];
    assertEquals( "category id is wrong", "CAT_PRODUCTS", category.getId() );
    assertEquals( "category name is wrong", "Products", category.getName() );
    assertEquals( "category description is wrong", "This category contains inforamtion about products.", category.getDescription() );
    assertEquals( "category has wrong # of columns", 9, category.getColumns().length );
    assertNull( "wrong # of subcategories", category.getSubCategories() );
    
    Column column = (Column) category.getColumns()[1];
    assertEquals( "column id is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getId() );
    assertEquals( "column name is wrong", "Product Code", column.getName() );
    assertEquals( "column description is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getDescription() );
    assertNotNull( "column attributes wrong", column.getAttributes() );
    assertEquals( "column attribute count wrong", 3, column.getAttributes().length );
    assertEquals( "column data type wrong", "String", column.getDataType() );
    assertEquals( "column field type wrong", "Dimension", column.getFieldType() );
    
    assertEquals( "attribute formula wrong", "formula", column.getAttributes()[1].getId() );
    assertEquals( "attribute formula wrong", "PRODUCTCODE", column.getAttributes()[1].getValue() );
    assertEquals( "attribute exact wrong", "exact", column.getAttributes()[0].getId() );
    assertEquals( "attribute exact wrong", "false", column.getAttributes()[0].getValue() );

  }
  
}
