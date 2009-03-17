/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms;

import java.util.Locale;

import junit.framework.TestCase;

import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.messages.util.LocaleHelper;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.v3.envelope.Envelope;
import org.pentaho.pms.schema.v3.model.Category;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.model.Model;
import org.pentaho.pms.schema.v3.model.ModelEnvelope;
import org.pentaho.pms.service.LocalModelService;

public class LocalModelServiceTest extends TestCase {

  BusinessModel ordersModel = null;

  CwmSchemaFactory cwmSchemaFactory = null;

  public void setUp() {
    if (ordersModel == null || cwmSchemaFactory == null) {
      loadOrdersModel();
    }
  }

  public void loadOrdersModel() {
    CWM cwm = null;
    try {
      cwm = CWM.getInstance("Orders", true); //$NON-NLS-1$
      assertNotNull("CWM singleton instance is null", cwm);
      cwm.importFromXMI("samples/orders.xmi"); //$NON-NLS-1$      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    cwmSchemaFactory = new CwmSchemaFactory();

    SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
    ordersModel = schemaMeta.findModel("Orders"); //$NON-NLS-1$
  }

  public void testListModels() throws Exception {
    
    LocaleHelper.setLocale( Locale.ENGLISH );
    
    LocalModelService modelService = new LocalModelService();
    modelService.setCwmSchemaFactory(cwmSchemaFactory);
    
    ModelEnvelope[] models = modelService.listModels();
    
    assertNotNull( "models is null", models );
    assertEquals( "wrong number of models", 1, models.length );
    
    Envelope model = models[0];
    
    assertEquals( "model id is wrong", "Orders", model.getId() );
    assertEquals( "model name is wrong", "Orders", model.getName() );
    assertEquals( "model description is wrong", "Orders", model.getDescription() );
    
  }
  
  public void testGetModel() throws Exception {
    
    LocaleHelper.setLocale( Locale.ENGLISH );
    
    LocalModelService modelService = new LocalModelService();
    modelService.setCwmSchemaFactory(cwmSchemaFactory);

    Model model = modelService.getModel("Orders", "Orders", false);
    
    assertNotNull( "models is null", model );
    
    assertEquals( "model id is wrong", "Orders", model.getId() );
    assertEquals( "model name is wrong", "Orders", model.getName() );
    assertEquals( "model description is wrong", "Orders", model.getDescription() );
    
    Category category = model.getRootCategory();
    assertNotNull( "root category is null", category );
    assertEquals( "root category has columns", 0, category.getColumns().length );
    assertEquals( "wrong # of subcategories", 4, category.getSubCategories().length );

    category = category.getSubCategories()[0];
    assertEquals( "category id is wrong", "bc_PRODUCTS", category.getId() );
    assertEquals( "category name is wrong", "Products", category.getName() );
    assertEquals( "category description is wrong", "bc_PRODUCTS", category.getDescription() );
    assertEquals( "category has wrong # of columns", 9, category.getColumns().length );
    assertEquals( "wrong # of subcategories", 0, category.getSubCategories().length );
    
    Column column = category.getColumns()[0];
    assertEquals( "column id is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getId() );
    assertEquals( "column name is wrong", "Product Code", column.getName() );
    assertEquals( "column description is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getDescription() );
    assertEquals( "column data type wrong", "String", column.getDataType() );
    assertEquals( "column field type wrong", "Dimension", column.getFieldType() );
    assertNull( "column attributes wrong", column.getAttributes() );
  
  }
  
  public void testGetModelDeep() throws Exception {
    
    LocaleHelper.setLocale( Locale.ENGLISH );
    LocalModelService modelService = new LocalModelService();
    modelService.setCwmSchemaFactory(cwmSchemaFactory);

    Model model = modelService.getModel("Orders", "Orders", true);
    
    assertNotNull( "models is null", model );
    
    assertEquals( "model id is wrong", "Orders", model.getId() );
    assertEquals( "model name is wrong", "Orders", model.getName() );
    assertEquals( "model description is wrong", "Orders", model.getDescription() );
    
    Category category = model.getRootCategory();
    assertNotNull( "root category is null", category );
    assertEquals( "root category has columns", 0, category.getColumns().length );
    assertEquals( "wrong # of subcategories", 4, category.getSubCategories().length );

    category = category.getSubCategories()[0];
    assertEquals( "category id is wrong", "bc_PRODUCTS", category.getId() );
    assertEquals( "category name is wrong", "Products", category.getName() );
    assertEquals( "category description is wrong", "bc_PRODUCTS", category.getDescription() );
    assertEquals( "category has wrong # of columns", 9, category.getColumns().length );
    assertEquals( "wrong # of subcategories", 0, category.getSubCategories().length );
    
    Column column = (Column) category.getColumns()[0];
    assertEquals( "column id is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getId() );
    assertEquals( "column name is wrong", "Product Code", column.getName() );
    assertEquals( "column description is wrong", "BC_PRODUCTS_PRODUCTCODE", column.getDescription() );
    assertNotNull( "column attributes wrong", column.getAttributes() );
    assertEquals( "column attribute count wrong", 4, column.getAttributes().length );
    assertEquals( "column data type wrong", "String", column.getDataType() );
    assertEquals( "column field type wrong", "Dimension", column.getFieldType() );
    
    assertEquals( "column formula wrong", "PRODUCTCODE", column.getAttributes()[1].getValue() );
    assertEquals( "column exact wrong", "false", column.getAttributes()[0].getValue() );
  
  }
  
}
