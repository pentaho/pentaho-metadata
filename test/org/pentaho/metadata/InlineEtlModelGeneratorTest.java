/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.InlineEtlPhysicalColumn;
import org.pentaho.metadata.model.InlineEtlPhysicalModel;
import org.pentaho.metadata.model.InlineEtlPhysicalTable;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.impl.ietl.InlineEtlQueryExecutor;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Order.Type;
import org.pentaho.metadata.query.model.Parameter;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.util.InlineEtlModelGenerator;
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;

@SuppressWarnings( "nls" )
public class InlineEtlModelGeneratorTest {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testGenerator() throws Exception {
    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    String locale = LocaleHelper.getLocale().toString();

    Assert.assertEquals( "testmodel", domain.getId() );
    Assert.assertEquals( 1, domain.getPhysicalModels().size() );

    // TEST PHYSICAL MODEL

    InlineEtlPhysicalModel model = (InlineEtlPhysicalModel) domain.getPhysicalModels().get( 0 );
    Assert.assertEquals( 1, model.getPhysicalTables().size() );

    InlineEtlPhysicalTable table = (InlineEtlPhysicalTable) model.getPhysicalTables().get( 0 );
    Assert.assertEquals( 4, table.getPhysicalColumns().size() );

    InlineEtlPhysicalColumn column = (InlineEtlPhysicalColumn) table.getPhysicalColumns().get( 0 );
    Assert.assertEquals( "PC_0", column.getId() );
    Assert.assertEquals( DataType.NUMERIC, column.getDataType() );
    Assert.assertEquals( "Data1", column.getName().getString( locale ) );
    Assert.assertEquals( "Data1", column.getFieldName() );

    // TEST LOGICAL MODEL

    Assert.assertEquals( 1, domain.getLogicalModels().size() );

    LogicalModel logicalModel = domain.getLogicalModels().get( 0 );

    Assert.assertNotNull( logicalModel.getPhysicalModel() );

    Assert.assertEquals( "MODEL_1", logicalModel.getId() );
    Assert.assertEquals( "testmodel", logicalModel.getName().getString( locale ) );
    Assert.assertEquals( 1, logicalModel.getLogicalTables().size() );

    LogicalTable logicalTable = logicalModel.getLogicalTables().get( 0 );
    Assert.assertEquals( "LOGICAL_TABLE_1", logicalTable.getId() );
    Assert.assertEquals( 4, logicalTable.getLogicalColumns().size() );

    LogicalColumn logicalColumn = logicalTable.getLogicalColumns().get( 0 );
    Assert.assertEquals( "bc_0_Data1", logicalColumn.getId() );
    Assert.assertEquals( "Data1", logicalColumn.getName().getString( locale ) );
    Assert.assertNotNull( logicalColumn.getPhysicalColumn() );

    Assert.assertEquals( 1, logicalModel.getCategories().size() );

    Category category = logicalModel.getCategories().get( 0 );
    Assert.assertEquals( "bc_testmodel", category.getId() );
    Assert.assertEquals( "testmodel", category.getName().getString( locale ) );
    Assert.assertEquals( 4, category.getLogicalColumns().size() );

    Assert.assertEquals( logicalColumn, category.getLogicalColumns().get( 0 ) );

  }

  @Test
  public void testQueryExecution() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );
    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 1.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 3.0, resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( 4.0, resultset.getValueAt( 3, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 4, 0 ) );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testQueryLimitNotSupported() throws Exception {
    Query query = new Query( null, null );
    query.setLimit( 10 );
    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, null, null );
  }

  @Test
  public void testQueryExecutionWithOrder() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );

    category.getLogicalColumns().get( 1 ).setDataType( DataType.NUMERIC );

    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query.getOrders()
        .add( new Order( new Selection( category, category.getLogicalColumns().get( 1 ), null ), Type.ASC ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    executor.setCsvFileLoc( "test/solution/system/metadata/csvfiles/" );
    IPentahoResultSet resultset = executor.executeQuery( query );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 4.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 3.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 1.0, resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 3, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 4, 0 ) );

    Query query2 = new Query( domain, model );

    query2.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query2.getOrders().add(
        new Order( new Selection( category, category.getLogicalColumns().get( 1 ), null ), Type.DESC ) );

    resultset = executor.executeQuery( query2, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 5.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 1.0, resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( 3.0, resultset.getValueAt( 3, 0 ) );
    Assert.assertEquals( 4.0, resultset.getValueAt( 4, 0 ) );
  }

  @Test
  public void testQueryExecutionWithConstraints() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );

    category.getLogicalColumns().get( 0 ).setDataType( DataType.NUMERIC );

    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "[bc_testmodel.bc_0_Data1] > 2" ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 3.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 4.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 2, 0 ) );

    Query query2 = new Query( domain, model );

    query2.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query2.getConstraints().add( new Constraint( CombinationType.AND, "[bc_testmodel.bc_1_Data2] > 4.0" ) );

    resultset = executor.executeQuery( query2, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 1.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 2, 0 ) );

    Query query4 = new Query( domain, model );

    query4.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query4.getConstraints().add(
        new Constraint( CombinationType.AND, "AND([bc_testmodel.bc_0_Data1] < 5; [bc_testmodel.bc_1_Data2] > 4.0)" ) );

    resultset = executor.executeQuery( query4, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 2, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 1.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );

    Query query45 = new Query( domain, model );

    query45.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query45.getConstraints().add(
        new Constraint( CombinationType.AND, "OR([bc_testmodel.bc_0_Data1] < 5; [bc_testmodel.bc_1_Data2] > 4.0)" ) );

    resultset = executor.executeQuery( query45, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 1.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 3.0, resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( 4.0, resultset.getValueAt( 3, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 4, 0 ) );

    Query query5 = new Query( domain, model );

    query5.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query5.getConstraints().add( new Constraint( CombinationType.AND, "[bc_testmodel.bc_0_Data1] < 5" ) );
    query5.getConstraints().add( new Constraint( CombinationType.OR, "[bc_testmodel.bc_1_Data2] > 4.0" ) );

    resultset = executor.executeQuery( query5, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_0_Data1", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( 1.0, resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( 2.0, resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( 3.0, resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( 4.0, resultset.getValueAt( 3, 0 ) );
    Assert.assertEquals( 5.0, resultset.getValueAt( 4, 0 ) );

    Query query6 = new Query( domain, model );

    query6.getParameters().add( new Parameter( "param1", DataType.BOOLEAN, false ) );
    query6.getSelections().add( new Selection( category, category.getLogicalColumns().get( 0 ), null ) );
    query6.getConstraints().add( new Constraint( CombinationType.AND, "[param:param1]" ) );

    resultset = executor.executeQuery( query6, "test/solution/system/metadata/csvfiles/", null );
    Assert.assertEquals( 0, resultset.getRowCount() );

    Map<String, Object> params = new HashMap<String, Object>();

    params.put( "param1", true );

    resultset = executor.executeQuery( query6, "test/solution/system/metadata/csvfiles/", params );
    Assert.assertEquals( 5, resultset.getRowCount() );

  }

  @Test
  public void testQueryExecutionWithFormulaFunctions() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );

    category.getLogicalColumns().get( 0 ).setDataType( DataType.NUMERIC );

    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "LIKE([bc_testmodel.bc_3_Data4];\"%Value%\")" ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    // this is a bug, String Value should only appear once

    Assert.assertEquals( 4, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "String Value", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Very Long String Value for testing columns", resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( "String Value", resultset.getValueAt( 3, 0 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "CONTAINS([bc_testmodel.bc_3_Data4];\"Value\")" ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 4, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "String Value", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Very Long String Value for testing columns", resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( "String Value", resultset.getValueAt( 3, 0 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add(
        new Constraint( CombinationType.AND, "BEGINSWITH([bc_testmodel.bc_3_Data4];\"String\")" ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 1, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "String Value", resultset.getValueAt( 0, 0 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "ENDSWITH([bc_testmodel.bc_3_Data4];\"Value\")" ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "String Value", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "String Value", resultset.getValueAt( 2, 0 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "ISNA([bc_testmodel.bc_3_Data4])" ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 0, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "NOT(ISNA([bc_testmodel.bc_3_Data4]))" ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 5, resultset.getRowCount() );
    Assert.assertEquals( 1, resultset.getColumnCount() );
  }

  @Test
  public void testQueryExecutionWithAggregations() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );
    category.getLogicalColumns().get( 1 ).setDataType( DataType.NUMERIC );
    category.getLogicalColumns().get( 1 ).setAggregationType( AggregationType.SUM );
    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 1 ), null ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 4, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_1_Data2", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "A String", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "String Value", resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( "Very Long String Value for testing columns", resultset.getValueAt( 3, 0 ) );

    Assert.assertEquals( 1.1, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 5.7, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 19.5, resultset.getValueAt( 2, 1 ) );
    Assert.assertEquals( 3.4, resultset.getValueAt( 3, 1 ) );
  }

  /**
   * This test is ignored until PMD-532 is resolved
   * 
   */
  @Test
  public void testQueryExecutionWithDifferentAggregations() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );
    category.getLogicalColumns().get( 1 ).setDataType( DataType.NUMERIC );
    category.getLogicalColumns().get( 1 ).setAggregationType( AggregationType.SUM );
    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 1 ), null ) );
    query.getSelections()
        .add( new Selection( category, category.getLogicalColumns().get( 1 ), AggregationType.AVERAGE ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 4, resultset.getRowCount() );
    Assert.assertEquals( 3, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_1_Data2", resultset.getMetaData().getColumnHeaders()[0][1] );
    Assert.assertEquals( "bc_1_Data2_1", resultset.getMetaData().getColumnHeaders()[0][2] );

    Assert.assertEquals( "A String", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "String Value", resultset.getValueAt( 2, 0 ) );
    Assert.assertEquals( "Very Long String Value for testing columns", resultset.getValueAt( 3, 0 ) );

    Assert.assertEquals( 1.1, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 5.7, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 19.5, resultset.getValueAt( 2, 1 ) );
    Assert.assertEquals( 3.4, resultset.getValueAt( 3, 1 ) );

    Assert.assertEquals( 1.1, resultset.getValueAt( 0, 2 ) );
    Assert.assertEquals( 5.7, resultset.getValueAt( 1, 2 ) );
    Assert.assertEquals( 9.75, resultset.getValueAt( 2, 2 ) );
    Assert.assertEquals( 3.4, resultset.getValueAt( 3, 2 ) );

  }

  @Test
  public void testQueryExecutionWithAggregationsAndConstraints() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "example.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );
    category.getLogicalColumns().get( 1 ).setDataType( DataType.NUMERIC );
    category.getLogicalColumns().get( 1 ).setAggregationType( AggregationType.SUM );
    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 3 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 1 ), null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "[bc_testmodel.bc_1_Data2] > 4.0" ) );
    query.getOrders().add(
        new Order( new Selection( category, category.getLogicalColumns().get( 3 ), null ), Order.Type.DESC ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 2, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_3_Data4", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_1_Data2", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "String Value", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Bigger String Value", resultset.getValueAt( 1, 0 ) );

    Assert.assertEquals( 19.5, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 5.7, resultset.getValueAt( 1, 1 ) );
  }

  @Test
  public void testQueryExecutionAllAggregations() throws Exception {

    List<String> users = new ArrayList<String>();
    users.add( "suzy" );
    List<String> roles = new ArrayList<String>();
    roles.add( "Authenticated" );
    int defaultAcls = 31;
    InlineEtlModelGenerator gen =
        new InlineEtlModelGenerator( "testmodel", "test/solution/system/metadata/csvfiles/", "People.csv", true, ",",
            "\"", true, users, roles, defaultAcls, "joe" );

    Domain domain = gen.generate();

    LogicalModel model = domain.getLogicalModels().get( 0 );
    Category category = model.getCategories().get( 0 );
    // category.getLogicalColumns().get(1).setDataType(DataType.NUMERIC);
    category.getLogicalColumns().get( 6 ).setAggregationType( AggregationType.SUM );
    List<AggregationType> aggList = new ArrayList<AggregationType>();
    aggList.add( AggregationType.AVERAGE );
    aggList.add( AggregationType.COUNT );
    aggList.add( AggregationType.MAXIMUM );
    aggList.add( AggregationType.MINIMUM );
    aggList.add( AggregationType.COUNT_DISTINCT );
    aggList.add( AggregationType.NONE );

    category.getLogicalColumns().get( 6 ).setAggregationList( aggList );
    Query query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 6 ), null ) );

    InlineEtlQueryExecutor executor = new InlineEtlQueryExecutor();
    IPentahoResultSet resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 194.0, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 32.0, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 31.0, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections()
        .add( new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.AVERAGE ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 32.333333333333336, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 32.0, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 31.0, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.NONE ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 8, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Orange", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 36.0, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 29.0, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 32.0, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.COUNT ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 6L, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 1L, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 1L, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections()
        .add( new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.MINIMUM ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 29.0, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 32.0, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 31.0, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections()
        .add( new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.MAXIMUM ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 36.0, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 32.0, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 31.0, resultset.getValueAt( 2, 1 ) );

    query = new Query( domain, model );

    query.getSelections().add( new Selection( category, category.getLogicalColumns().get( 5 ), null ) );
    query.getSelections().add(
        new Selection( category, category.getLogicalColumns().get( 6 ), AggregationType.COUNT_DISTINCT ) );

    executor = new InlineEtlQueryExecutor();
    resultset = executor.executeQuery( query, "test/solution/system/metadata/csvfiles/", null );

    Assert.assertEquals( 3, resultset.getRowCount() );
    Assert.assertEquals( 2, resultset.getColumnCount() );
    Assert.assertEquals( "bc_5_County", resultset.getMetaData().getColumnHeaders()[0][0] );
    Assert.assertEquals( "bc_6_Age", resultset.getMetaData().getColumnHeaders()[0][1] );

    Assert.assertEquals( "Orange", resultset.getValueAt( 0, 0 ) );
    Assert.assertEquals( "Seminole", resultset.getValueAt( 1, 0 ) );
    Assert.assertEquals( "Volusia", resultset.getValueAt( 2, 0 ) );

    Assert.assertEquals( 4L, resultset.getValueAt( 0, 1 ) );
    Assert.assertEquals( 1L, resultset.getValueAt( 1, 1 ) );
    Assert.assertEquals( 1L, resultset.getValueAt( 2, 1 ) );

  }

  public boolean evaluateFormula( FormulaContext context, String formulaStr, Object result ) throws Exception {
    final Formula formula = new Formula( formulaStr );
    formula.initialize( context );
    final Object eval = (Boolean) formula.evaluateTyped().getValue();
    return result.equals( eval );
  }

  public String evaluateBadFormula( FormulaContext context, String formulaStr, Object result ) throws Exception {
    final Formula formula = new Formula( formulaStr );
    formula.initialize( context );
    Object eval = formula.evaluateTyped().getValue();
    return eval.toString();
  }

  @Test
  public void testContainsFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "CONTAINS(\"ABC\";\"B\")", true ) );
    Assert.assertTrue( evaluateFormula( context, "CONTAINS(\"CBA\";\"Z\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "CONTAINS(\"This is a test\"; \"is a\")", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "CONTAINS(1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );
  }

  @Test
  public void testLikeFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "LIKE(\"ABC\";\"*B*\")", true ) );
    Assert.assertTrue( evaluateFormula( context, "LIKE(\"CBA\";\"*Z*\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "LIKE(\"This is a test\"; \"%test%\")", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "LIKE(1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );
  }

  @Test
  public void testEndsWithFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "ENDSWITH(\"ABC\";\"C\")", true ) );
    Assert.assertTrue( evaluateFormula( context, "ENDSWITH(\"CBA\";\"C\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "ENDSWITH(\"This is a test\"; \"test\")", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "ENDSWITH(1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );
  }

  @Test
  public void testBeginsWithFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "BEGINSWITH(\"ABC\";\"A\")", true ) );
    Assert.assertTrue( evaluateFormula( context, "BEGINSWITH(\"CBA\";\"A\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "BEGINSWITH(\"This is a test\"; \"This\")", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "BEGINSWITH(1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );
  }

  @Test
  public void testEqualsFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "EQUALS(1;1)", true ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(TRUE();FALSE())", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(TRUE();TRUE())", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(1;2)", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(1;1)", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(\"A\";\"B\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(\"A\";\"A\")", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(1.5;1.2)", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "EQUALS(1.5;1.5)", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "EQUALS(1;1;1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );

    msg = evaluateBadFormula( context, "EQUALS([Test];2)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "errorMessage=NA" ) >= 0 );
  }

  @Test
  public void testInFunction() throws Exception {
    LibFormulaBoot.getInstance().start();
    DefaultFormulaContext context = new DefaultFormulaContext();
    Assert.assertTrue( evaluateFormula( context, "IN(TRUE();FALSE())", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(TRUE();FALSE();TRUE())", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(TRUE();TRUE())", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(1;2)", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(1;1)", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(1;2;1)", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(\"A\";\"B\")", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(\"A\";\"B\";\"A\")", Boolean.TRUE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(1.5;1.2)", Boolean.FALSE ) );
    Assert.assertTrue( evaluateFormula( context, "IN(1.5;1.2;1.5)", Boolean.TRUE ) );

    String msg = evaluateBadFormula( context, "IN(1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "Invalid number of arguments" ) >= 0 );

    msg = evaluateBadFormula( context, "IN([Test];1)", null );
    Assert.assertTrue( "Wrong Message: " + msg, msg.indexOf( "errorMessage=NA" ) >= 0 );

  }
}
