/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.StepLoader;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.InlineEtlPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.impl.ietl.InlineEtlQueryExecutor;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.util.InlineEtlModelGenerator;
import org.pentaho.pms.messages.util.LocaleHelper;

public class InlineEtlModelGeneratorTest {

  @Test
  public void testGenerator() throws Exception {
    
    InlineEtlModelGenerator gen = new InlineEtlModelGenerator(
        "testmodel", 
        "test/solution/system/metadata/csvfiles/example.csv",
        true,
        "\"",
        ","
    );
    
    Domain domain = gen.generate();
    
    String locale = LocaleHelper.getLocale().toString();
    
    Assert.assertEquals("testmodel", domain.getId());
    Assert.assertEquals(1, domain.getPhysicalModels().size());

    // TEST PHYSICAL MODEL
    
    InlineEtlPhysicalModel model = (InlineEtlPhysicalModel)domain.getPhysicalModels().get(0);
    Assert.assertEquals(1, model.getPhysicalTables().size());
    
    InlineEtlPhysicalTable table = (InlineEtlPhysicalTable)model.getPhysicalTables().get(0);
    Assert.assertEquals(4, table.getPhysicalColumns().size());
    
    InlineEtlPhysicalColumn column = (InlineEtlPhysicalColumn)table.getPhysicalColumns().get(0);
    Assert.assertEquals("PC_0", column.getId());
    Assert.assertEquals(DataType.STRING, column.getDataType());
    Assert.assertEquals("Data1", column.getName().getString(locale));
    Assert.assertEquals("Data1", column.getFieldName());
    
    // TEST LOGICAL MODEL
    
    Assert.assertEquals(1, domain.getLogicalModels().size());
    
    LogicalModel logicalModel = domain.getLogicalModels().get(0);
    Assert.assertEquals("MODEL_1", logicalModel.getId());
    Assert.assertEquals("testmodel", logicalModel.getName().getString(locale));
    Assert.assertEquals(1, logicalModel.getLogicalTables().size());
    
    LogicalTable logicalTable = logicalModel.getLogicalTables().get(0);
    Assert.assertEquals("LOGICAL_TABLE_1", logicalTable.getId());
    Assert.assertEquals(4, logicalTable.getLogicalColumns().size());
    
    LogicalColumn logicalColumn = logicalTable.getLogicalColumns().get(0);
    Assert.assertEquals("bc_0_Data1", logicalColumn.getId());
    Assert.assertEquals("Data1", logicalColumn.getName().getString(locale));
    Assert.assertNotNull(logicalColumn.getPhysicalColumn());

    Assert.assertEquals(1, logicalModel.getCategories().size());

    Category category = logicalModel.getCategories().get(0);
    Assert.assertEquals("bc_testmodel", category.getId());
    Assert.assertEquals("testmodel", category.getName().getString(locale));
    Assert.assertEquals(4, category.getLogicalColumns().size());
    
    Assert.assertEquals(logicalColumn, category.getLogicalColumns().get(0));
    
  }
  
  @Test
  public void testQueryExecution() throws Exception {
    
    EnvUtil.environmentInit();
    StepLoader.init();
    
    InlineEtlModelGenerator gen = new InlineEtlModelGenerator(
        "testmodel", 
        "test/solution/system/metadata/csvfiles/example.csv",
        true,
        "\"",
        ","
    );
    
    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get(0);
    Category category = model.getCategories().get(0);
    Query query = new Query(domain, model);

    query.getSelections().add(new Selection(category, category.getLogicalColumns().get(0), null));
    
    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery(query);
    
    Assert.assertEquals(5, resultset.getRowCount());
    Assert.assertEquals(1, resultset.getColumnCount());
    Assert.assertEquals("bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0]);
    Assert.assertEquals("1", resultset.getValueAt(0, 0));
    Assert.assertEquals("2", resultset.getValueAt(1, 0));
    Assert.assertEquals("3", resultset.getValueAt(2, 0));
    Assert.assertEquals("4", resultset.getValueAt(3, 0));
    Assert.assertEquals("5", resultset.getValueAt(4, 0));
  }
  
}



