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
 * Copyright (c) 2006 - 2018 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.mql.MQLQueryFactory;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.mql.OrderBy;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.mql.WhereCondition;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;

@SuppressWarnings( { "deprecation", "nls" } )
public class MQLQueryIT extends TestCase {

  BusinessModel ordersModel = null;

  CwmSchemaFactory cwmSchemaFactory = null;

  public void setUp() throws Exception {
    MetadataTestBase.initKettleEnvironment();
    if ( ordersModel == null || cwmSchemaFactory == null ) {
      loadOrdersModel();
    }
  }

  @Override
  protected void tearDown() throws Exception {
    deleteFile( "mdr.btb" );
    deleteFile( "mdr.btd" );
    deleteFile( "mdr.btx" );
  }

  private void deleteFile( String filename ) {
    File f = new File( filename );
    if ( f.exists() ) {
      f.delete();
    }
  }

  public String loadXmlFile( String filename ) {
    try {
      return IOUtils.toString( getClass().getResourceAsStream( filename ) );
    } catch ( Throwable t ) {
      t.printStackTrace();
      fail();
    }
    return null;
  }

  public void loadOrdersModel() {
    CWM cwm = null;
    try {
      cwm = CWM.getInstance( "Orders", true ); //$NON-NLS-1$
      assertNotNull( "CWM singleton instance is null", cwm );
      cwm.importFromXMI( getClass().getResourceAsStream( "/samples/orders.xmi" ) ); //$NON-NLS-1$
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
    cwmSchemaFactory = new CwmSchemaFactory();

    SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta( cwm );
    ordersModel = schemaMeta.findModel( "Orders" ); //$NON-NLS-1$
  }

  public void handleFormula( BusinessModel model, String databaseToTest, String mqlFormula, String expectedSql ) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta( "", databaseToTest, "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula( model, databaseMeta, mqlFormula, null );
      formula.parseAndValidate();
      String sql = formula.generateSQL( "en_US" ); //$NON-NLS-1$
      assertNotNull( sql );
      sql = sql.trim();
      assertEquals( expectedSql, sql );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void handleFormulaFailure( BusinessModel model, String databaseToTest, String mqlFormula,
                                    String expectedException ) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta( "", databaseToTest, "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula( model, databaseMeta, mqlFormula, null );
      formula.parseAndValidate();
      formula.generateSQL( "en_US" ); //$NON-NLS-1$
      fail();
    } catch ( Exception e ) {
      assertEquals( e.getMessage(), expectedException );
    }
  }

  public void handleFormula( BusinessModel model, BusinessTable table, String databaseToTest, String mqlFormula,
                             String expectedSql ) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta( "", databaseToTest, "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula( model, table, databaseMeta, mqlFormula, null );
      formula.parseAndValidate();
      String sql = formula.generateSQL( "en_US" ); //$NON-NLS-1$
      assertNotNull( sql );
      sql = sql.trim();
      assertEquals( expectedSql, sql );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void handleWhereCondition( BusinessModel model, String mqlFormula, String expectedSql ) {
    try {
      WhereCondition cond = new WhereCondition( model, "", mqlFormula ); //$NON-NLS-1$
      String sql = cond.getWhereClause( "en_US", false ); //$NON-NLS-1$
      assertNotNull( sql );
      sql = sql.trim();
      /*
       * System.out.println("-----------"); System.out.println("Expected SQL: [" + expectedSql + "]");
       * System.out.println("-----------"); System.out.println("Actual SQL:   [" + sql + "]");
       * System.out.println("-----------");
       */
      assertEquals( expectedSql, sql );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void testDateFunctionMath() {

    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.DAY_OF_MONTH, 1 );

    SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd" );
    String dateStr = fmt.format( cal.getTime() );

    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
        "DATEMATH(\"0:MS\")", //$NON-NLS-1$
        "TO_DATE('" + dateStr + "','YYYY-MM-DD')" //$NON-NLS-1$
    );

  }

  public void testAggregationFormulas() {
    BusinessTable table = ordersModel.findBusinessTable( "BT_ORDER_DETAILS" ); //$NON-NLS-1$
    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "SUM([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
      "SUM( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "SUM([QUANTITYORDERED])", //$NON-NLS-1$
      "SUM( BT_ORDER_DETAILS.QUANTITYORDERED )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "COUNT([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
      "COUNT( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "COUNT([PRICEEACH])", //$NON-NLS-1$
      "COUNT( BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "AVG([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
      "AVG( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "AVG([QUANTITYORDERED])", //$NON-NLS-1$
      "AVG( BT_ORDER_DETAILS.QUANTITYORDERED )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "MIN([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
      "MIN( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "MIN([PRICEEACH])", //$NON-NLS-1$
      "MIN( BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "MAX([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
      "MAX( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )" ); //$NON-NLS-1$

    handleFormula( ordersModel, table, "Oracle", //$NON-NLS-1$
      "MAX([QUANTITYORDERED])", //$NON-NLS-1$
      "MAX( BT_ORDER_DETAILS.QUANTITYORDERED )" ); //$NON-NLS-1$

  }

  public void testComplexFormulaAndAggFunctions() {
    String complexJoin = "" +
            "AND([BT_ORDERS.BC_ORDERS_ORDERNUMBER]=\n" +
            "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_ORDERNUMBER])";
    String expectedSql = "( BT_ORDERS.ORDERNUMBER  =  BT_ORDER_DETAILS.ORDERNUMBER )";
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "Oracle", "Native", "", "", "", "", "" );
    BusinessTable btOrders = ordersModel.findBusinessTable( "BT_ORDERS" );
    BusinessColumn bcOrderNumber = btOrders.findBusinessColumn( "BC_ORDERS_ORDERNUMBER" );

    RelationshipMeta relation = Mockito.mock( RelationshipMeta.class );
    Mockito.when( relation.getComplexJoin() ).thenReturn( complexJoin );
    Mockito.when( relation.isComplex() ).thenReturn( true );

    bcOrderNumber.setAggregationType( AggregationSettings.COUNT_DISTINCT );
    try {
      PMSFormula formula = new PMSFormula( ordersModel, databaseMeta, relation, null );
      formula.parseAndValidate();
      String sql = formula.generateSQL( "en_US" ); //$NON-NLS-1$
      assertNotNull( sql );
      sql = sql.trim();
      assertEquals( expectedSql, sql );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void testFormulaAndAggFunctions() {
    String complexJoin = "" +
            "AND([BT_ORDERS.BC_ORDERS_ORDERNUMBER]=\n" +
            "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_ORDERNUMBER])";
    String expectedSql = "( COUNT(DISTINCT BT_ORDERS.ORDERNUMBER)  =  BT_ORDER_DETAILS.ORDERNUMBER )";
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "Oracle", "Native", "", "", "", "", "" );
    BusinessTable btOrders = ordersModel.findBusinessTable( "BT_ORDERS" );
    BusinessColumn bcOrderNumber = btOrders.findBusinessColumn( "BC_ORDERS_ORDERNUMBER" );

    bcOrderNumber.setAggregationType( AggregationSettings.COUNT_DISTINCT );
    try {
      PMSFormula formula = new PMSFormula( ordersModel, databaseMeta, complexJoin, null );
      formula.parseAndValidate();
      String sql = formula.generateSQL( "en_US" ); //$NON-NLS-1$
      assertNotNull( sql );
      sql = sql.trim();
      assertEquals( expectedSql, sql );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void testNestedAndOrs() {
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "AND(1 <> 2; OR(2<> 3; 3<>4); 4<>5)", //$NON-NLS-1$
      "(1 <> 2) AND ((2 <> 3) OR (3 <> 4)) AND (4 <> 5)" //$NON-NLS-1$
    );
  }

  public void testNotFunction() {
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "NOT(1 <> 2)", //$NON-NLS-1$
      "NOT(1 <> 2)" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "NOT(AND(1 <> 2; 2<>3))", //$NON-NLS-1$
      "NOT((1 <> 2) AND (2 <> 3))" //$NON-NLS-1$
    );

    handleFormulaFailure( ordersModel, "Oracle", //$NON-NLS-1$
      "NOT (1 <> 2; 2  <> 3)", //$NON-NLS-1$
      "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function NOT, expecting 1 params" //$NON-NLS-1$
    );
  }

  public void testBooleanFunctions() {
    handleFormula( ordersModel, "MySQL", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "TRUE" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "MySQL", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "FALSE" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "TRUE()", //$NON-NLS-1$
      "1=1" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "FALSE()", //$NON-NLS-1$
      "1=0" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "TRUE" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "FALSE" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "DB2", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "1=1" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "DB2", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "1=0" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSSQL", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "(1=1)" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "MSSQL", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "(0=1)" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSAccess", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "TRUE" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "MSAccess", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "FALSE" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "PostgreSQL", //$NON-NLS-1$ 
      "TRUE()", //$NON-NLS-1$
      "TRUE" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "PostgreSQL", //$NON-NLS-1$ 
      "FALSE()", //$NON-NLS-1$
      "FALSE" //$NON-NLS-1$
    );
  }

  public void testDateFunctionNow() {
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "NOW()", //$NON-NLS-1$
      "SYSDATE" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MySQL", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "NOW()" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "NOW()" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "DB2", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "( CURRENT DATE )" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSSQL", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "GETDATE()" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSAccess", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "NOW()" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "PostgreSQL", //$NON-NLS-1$ 
      "NOW()", //$NON-NLS-1$
      "now" //$NON-NLS-1$
    );
  }

  public void testDateFunctionDate() {

    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "DATE(2007;5;23)", //$NON-NLS-1$
      "TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "MySQL", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "'2007-05-23 00:00:00.0'" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "DB2", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSSQL", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "'20070523'" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSAccess", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "#05/23/2007#" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "PostgreSQL", //$NON-NLS-1$ 
      "DATE(2007;5;23)", //$NON-NLS-1$
      "date '2007-05-23'" //$NON-NLS-1$
    );
  }

  public void testDateFunctionDateValue() {
    handleFormula( ordersModel, "Oracle", //$NON-NLS-1$
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "MySQL", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "'2007-05-23 00:00:00.0'" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "DB2", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSSQL", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "'20070523'" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "MSAccess", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "#05/23/2007#" //$NON-NLS-1$
    );

    handleFormula( ordersModel, "PostgreSQL", //$NON-NLS-1$ 
      "DATEVALUE(\"2007-05-23\")", //$NON-NLS-1$
      "date '2007-05-23'" //$NON-NLS-1$
    );
  }

  public void testWhereCondition() {
    handleWhereCondition( ordersModel,
      // mql formula
      "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
        "[BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")", //$NON-NLS-1$

      // expected hypersonic sql
      "( (4 * (2 + 3) - ( BT_CUSTOMERS.COUNTRY  * 2) / 3 <> 1000) AND" + //$NON-NLS-1$
        " ( BT_CUSTOMERS.CUSTOMERNAME  = 'EuroCars') )" //$NON-NLS-1$
    );
  }

  public void testExpectedExceptionCategoryNotFound() {
    String mqlFormula = "[blah_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")"; //$NON-NLS-1$
    try {
      WhereCondition cond = new WhereCondition( ordersModel, "", mqlFormula ); //$NON-NLS-1$
      String sql = cond.getWhereClause( "en_US", false ); //$NON-NLS-1$
      fail();
    } catch ( Exception e ) {
      assertTrue( e instanceof PentahoMetadataException );
      assertTrue( e.getMessage().indexOf( "blah_CUSTOMERS" ) >= 0 ); //$NON-NLS-1$
    }
  }

  public void testWhereConditionWithIN() {
    handleWhereCondition( ordersModel,
      // mql formula
      "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
        "IN([BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME];\"EuroCars1\";\"EuroCars2\";\"EuroCars3\"))", //$NON-NLS-1$

      // expected hypersonic sql
      "( (4 * (2 + 3) - ( BT_CUSTOMERS.COUNTRY  * 2) / 3 <> 1000) AND " + //$NON-NLS-1$
        " BT_CUSTOMERS.CUSTOMERNAME  IN ( 'EuroCars1' , 'EuroCars2' , 'EuroCars3' )  )" //$NON-NLS-1$
    );
  }

  public void testWhereConditionWithLIKE() {
    handleWhereCondition( ordersModel, // mql formula
      "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%US%\")", //$NON-NLS-1$

      // expected hypersonic sql
      "(  BT_CUSTOMERS.COUNTRY  LIKE '%US%' )" //$NON-NLS-1$
    );
  }

  public void testLike() {
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%\")", //$NON-NLS-1$
      "BT_CUSTOMERS.COUNTRY  LIKE '%'" //$NON-NLS-1$
    );
  }

  public void testCase() {
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; " + "\"Japan\")",      //$NON-NLS-1$
      "CASE  WHEN  BT_CUSTOMERS.COUNTRY  = 'US' THEN 'USA' WHEN  BT_CUSTOMERS.COUNTRY  = 'JAPAN' THEN 'Japan' END"      //$NON-NLS-1$
    );
    handleFormula(
      ordersModel,
      "Hypersonic", //$NON-NLS-1$
      "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; " + "\"Japan\"; \"Canada\")",  //$NON-NLS-1$
      "CASE  WHEN  BT_CUSTOMERS.COUNTRY  = 'US' THEN 'USA' WHEN  BT_CUSTOMERS.COUNTRY  = 'JAPAN' THEN 'Japan' ELSE " + "'Canada' END" //$NON-NLS-1$
    );
    handleFormulaFailure( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "CASE()", //$NON-NLS-1$
      "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
    );
    handleFormulaFailure( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "CASE(\"\")", //$NON-NLS-1$
      "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
    );
  }

  public void testCoalesce() {
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]; \"USA\")", //$NON-NLS-1$
      "COALESCE( BT_CUSTOMERS.COUNTRY  , 'USA')" //$NON-NLS-1$
    );
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY])", //$NON-NLS-1$
      "COALESCE( BT_CUSTOMERS.COUNTRY )" //$NON-NLS-1$
    );
    handleFormulaFailure( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "COALESCE()", //$NON-NLS-1$
      "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function COALESCE, expecting 1 params" //$NON-NLS-1$
    );
  }

  public void testNoFunction() {
    handleFormula( ordersModel, "Hypersonic", //$NON-NLS-1$ 
      "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]", //$NON-NLS-1$
      "BT_CUSTOMERS.COUNTRY" //$NON-NLS-1$
    );
  }

  // public void testSingleQuotes() {
  //    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
  //        "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAP'AN\"" //$NON-NLS-1$
  //        ,"Customers.COUNTRY  LIKE '%'" //$NON-NLS-1$
  // );
  // }

  /**
   * In this test we try to see to it :<br> - that the formula engine picks the 2 specified columns from 2 different
   * business tables<br> - hat the aggregation for QUANTITYORDERED is SUM() and NOT for BUYPRICE<br> <br>
   */
  public void testMultiTableColumnFormulas() {
    String formula = "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE]";
    String sql = "SUM(BT_ORDER_DETAILS.QUANTITYORDERED)  *  BT_PRODUCTS.BUYPRICE";

    handleFormula( ordersModel, "Hypersonic", formula, sql );
  }

  /**
   * In this test we try to see to it :<br> - that the formula engine picks the 2 specified columns from 2 different
   * business tables<br> - that we calculate the sum of the multiplication <br>
   */
  public void testMultiTableColumnFormulasAggregate() {
    BusinessColumn quantityOrdered = ordersModel.findBusinessColumn( "BC_ORDER_DETAILS_QUANTITYORDERED" );
    assertNotNull( "Expected to find the business column 'quantity ordered'", quantityOrdered );
    BusinessColumn buyPrice = ordersModel.findBusinessColumn( "BC_PRODUCTS_BUYPRICE" );
    assertNotNull( "Expected to find the business column 'buy price'", buyPrice );

    // let's remove the aggregations of the quantity ordered...
    //
    AggregationSettings qaBackup = quantityOrdered.getAggregationType();
    AggregationSettings paBackup = buyPrice.getAggregationType();
    quantityOrdered.setAggregationType( AggregationSettings.NONE );
    buyPrice.setAggregationType( AggregationSettings.NONE );

    // This changes the expected result...
    //
    String formula = "SUM( [BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE] )";
    String sql = "SUM( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_PRODUCTS.BUYPRICE )";

    handleFormula( ordersModel, "Hypersonic", formula, sql );

    // Set it back to the way it was for further testing.
    quantityOrdered.setAggregationType( qaBackup );
    buyPrice.setAggregationType( paBackup );
  }

  /**
   * In this test we try to test :<br> - if the formula engine picks the 2 specified columns from 2 different business
   * tables<br> - if we calculate the multiplication of the sums <br>
   */
  public void testMultiTableColumnFormulasAggregate2() {
    BusinessColumn quantityOrdered = ordersModel.findBusinessColumn( "BC_ORDER_DETAILS_QUANTITYORDERED" );
    assertNotNull( "Expected to find the business column 'quantity ordered'", quantityOrdered );
    BusinessColumn buyPrice = ordersModel.findBusinessColumn( "BC_PRODUCTS_BUYPRICE" );
    assertNotNull( "Expected to find the business column 'buy price'", buyPrice );

    // let's enable the aggregations of the quantity ordered...
    //
    AggregationSettings qaBackup = quantityOrdered.getAggregationType();
    AggregationSettings paBackup = buyPrice.getAggregationType();
    quantityOrdered.setAggregationType( AggregationSettings.SUM );
    buyPrice.setAggregationType( AggregationSettings.SUM );

    // This changes the expected result...
    //
    String formula = "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE]";
    String sql = "SUM(BT_ORDER_DETAILS.QUANTITYORDERED)  *  SUM(BT_PRODUCTS.BUYPRICE)";

    handleFormula( ordersModel, "Hypersonic", formula, sql );

    // Set it back to the way it was for further testing.
    quantityOrdered.setAggregationType( qaBackup );
    buyPrice.setAggregationType( paBackup );
  }

  public void testMQLQueryFactoryPentahoMetadataException() {
    try {
      MQLQueryFactory.getMQLQuery( null, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      fail();
    } catch ( PentahoMetadataException e ) {
      assertTrue( e.getMessage().indexOf( "ERROR_0017 - " ) >= 0 );
    }
  }

  public void testDatabaseMetaReplacement() {
    // the current sql should have a 'date', while the oracle version should have a DATE()

    String mqlfile = "/mqlquery03.xmql"; //$NON-NLS-1$

    String mqldata = loadXmlFile( mqlfile );

    MQLQuery mqlquery = null;
    try {
      mqlquery = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$

      MappedQuery query = mqlquery.getQuery();
      String sqlQuery = query.getQuery();
      assertTrue( sqlQuery.indexOf( ">= '1-01-2007 00:00:00.0'" ) >= 0 ); //$NON-NLS-1$

      // now replace with oracle database metadata
      DatabaseMeta meta = (DatabaseMeta) ( (MQLQueryImpl) mqlquery ).getDatabaseMeta().clone();

      DatabaseInterface[] di = DatabaseMeta.getDatabaseInterfaces();
      DatabaseInterface oracleDI = null;
      for ( int i = 0; i < di.length; i++ ) {
        if ( di[ i ].getPluginId().toLowerCase().equals( "oracle" ) ) { //$NON-NLS-1$
          oracleDI = (DatabaseInterface) di[ i ].clone();
          break;
        }
      }
      meta.setDatabaseInterface( oracleDI );

      mqlquery = MQLQueryFactory.getMQLQuery( mqldata, meta, "en_US", cwmSchemaFactory ); //$NON-NLS-1$

      query = mqlquery.getQuery();
      sqlQuery = query.getQuery();
      assertTrue( sqlQuery.indexOf( ">= TO_DATE('1-01-2007','YYYY-MM-DD')" ) >= 0 ); //$NON-NLS-1$
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
  }

  public void testToFromXML() throws PentahoMetadataException {

    String mqlfile1 = "/mqlquery01.xmql"; //$NON-NLS-1$
    String mqlfile2 = "/mqlquery02.xmql"; //$NON-NLS-1$ // Distinct is off, but otherwise the same
    String mqlfile3 = "/mqlquery_oldformat.xmql"; //$NON-NLS-1$
    String mqlfile4 = "/mqlquery03.xmql"; //$NON-NLS-1$
    String mqlfile5 = "/mqlquery04.xmql"; //$NON-NLS-1$

    String mqldata = loadXmlFile( mqlfile1 );
    assertNotNull( mqldata );
    MQLQuery mqlquery = null;
    mqlquery = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$

    // Tests parsing the options tag to get the boolean into the MQL query
    mqldata = null;
    mqldata = loadXmlFile( mqlfile2 );
    assertNotNull( mqldata );
    MQLQuery mqlquery2 = null;
    mqlquery2 = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
    assertEquals( ( (MQLQueryImpl) mqlquery2 ).getDisableDistinct(), true );

    // Tests parsing of conditions
    mqldata = null;
    mqldata = loadXmlFile( mqlfile4 );
    assertNotNull( mqldata );
    MQLQuery mqlquery4 = null;
    mqlquery4 = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
    assertEquals( mqlquery4.getConstraints().size(), 1 );

    // Tests parsing an old-format (without options tag) still works, and defaults disableDistinct to false
    mqldata = null;
    mqldata = loadXmlFile( mqlfile3 );
    assertNotNull( mqldata );
    MQLQuery mqlquery3 = null;
    mqlquery3 = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
    assertEquals( ( (MQLQueryImpl) mqlquery3 ).getDisableDistinct(), false );
    assertTrue( ( (MQLQueryImpl) mqlquery3 ).getLimit() < 0 );

    // Tests parsing an old-format (without options tag) still works, and defaults disableDistinct to false
    mqldata = null;
    mqldata = loadXmlFile( mqlfile5 );
    assertNotNull( mqldata );
    MQLQuery mqlquery5 = null;
    mqlquery5 = MQLQueryFactory.getMQLQuery( mqldata, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
    assertEquals( 10, ( (MQLQueryImpl) mqlquery5 ).getLimit() );

    assertNotNull( mqlquery );
    assertNotNull( ( (MQLQueryImpl) mqlquery ).getSchemaMeta() );
    assertEquals( "Orders", ( (MQLQueryImpl) mqlquery ).getSchemaMeta().getDomainName() ); //$NON-NLS-1$
    assertNotNull( mqlquery.getModel() );
    assertEquals( "Orders", mqlquery.getModel().getId() ); //$NON-NLS-1$

    List<? extends Selection> selections = mqlquery.getSelections();

    assertNotNull( selections );
    assertEquals( 2, selections.size() );

    BusinessColumn col1 = selections.get( 0 ).getBusinessColumn();
    assertEquals( "BT_CUSTOMERS", col1.getBusinessTable().getId() ); //$NON-NLS-1$
    assertEquals( "BC_CUSTOMERS_CUSTOMERNAME", col1.getId() ); //$NON-NLS-1$

    BusinessColumn col2 = selections.get( 1 ).getBusinessColumn();
    assertEquals( "BT_CUSTOMERS", col2.getBusinessTable().getId() ); //$NON-NLS-1$
    assertEquals( "BC_CUSTOMERS_COUNTRY", col2.getId() ); //$NON-NLS-1$

    List orders = ( (MQLQueryImpl) mqlquery ).getOrder();

    assertNotNull( orders );
    assertEquals( 1, orders.size() );
    assertTrue( orders.get( 0 ) instanceof OrderBy );
    OrderBy order = (OrderBy) orders.get( 0 );

    assertEquals( true, order.isAscending() );
    assertNotNull( order.getSelection().getBusinessColumn() );
    assertEquals( "BT_CUSTOMERS", order.getSelection().getBusinessColumn().getBusinessTable().getId() ); //$NON-NLS-1$
    assertEquals( "BC_CUSTOMERS_COUNTRY", order.getSelection().getBusinessColumn().getId() ); //$NON-NLS-1$

    // NOW TEST XML OUTPUT
    try {
      String data = IOUtils.toString( getClass().getResourceAsStream( mqlfile1 ) );
      // remove the <?xml version="1.0" encoding="UTF-8"?>, it appears differently in different JVM versions
      data = data.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      data = data.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      String xml = mqlquery.getXML();
      assertNotNull( xml );
      // remove the <?xml version="1.0" encoding="UTF-8"?>, it appears differently in different JVM versions
      xml = xml.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "<limit>-1</limit>", "" );
      /*
       * System.out.println("Generated XML"); System.out.println(xml); System.out.println("File XML");
       * System.out.println(data);
       */
      assertEquals( data, xml );

      // Checks that the rendered XML has the option with true instead of false
      xml = mqlquery2.getXML();
      assertNotNull( xml );
      // remove the <?xml version="1.0" encoding="UTF-8"?>, it appears differently in different JVM versions
      xml = xml.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$

      assertFalse( data.equals( xml ) );

      // Tests that newly generated XML has the correct false flag in it.
      xml = mqlquery3.getXML();
      assertNotNull( xml );
      // remove the <?xml version="1.0" encoding="UTF-8"?>, it appears differently in different JVM versions
      xml = xml.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "<limit>-1</limit>", "" );

      assertEquals( data, xml );

    } catch ( IOException e ) {
      e.printStackTrace();
      fail();
    }

    // TEST CONSTRAINT XML OUTPUT
    try {
      String data = IOUtils.toString( getClass().getResourceAsStream( mqlfile4 ) );
      data = data.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      data = data.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      data = data.replaceAll( "<!--.*[-][-][>]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      String xml = mqlquery4.getXML();
      assertNotNull( xml );
      xml = xml.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "<options><disable_distinct>false</disable_distinct><limit>-1</limit></options>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( data, xml );

    } catch ( IOException e ) {
      e.printStackTrace();
      fail();
    }

    try {
      String data = IOUtils.toString( getClass().getResourceAsStream( mqlfile5 ) );
      data = data.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      data = data.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      data = data.replaceAll( "<!--.*[-][-][>]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      String xml = mqlquery5.getXML();
      assertNotNull( xml );
      xml = xml.replaceAll( "<\\?.*\\?>", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      xml = xml.replaceAll( "[\r\n\t]", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals( data, xml );
    } catch ( IOException e ) {
      e.printStackTrace();
      fail();
    }

    try {
      // Now, look at generated SQL for distinct -vs- no distinct...
      String SQL1 = mqlquery.getQuery().getQuery();
      assertNotNull( SQL1 );
      assertTrue( SQL1.startsWith( "SELECT DISTINCT" ) );
      // System.out.println("SQL1: " + SQL1);

      String SQL2 = mqlquery2.getQuery().getQuery();
      assertNotNull( SQL2 );
      assertFalse( SQL2.startsWith( "SELECT DISTINCT" ) );
      // System.out.println("SQL2: " + SQL2);

      String SQL3 = mqlquery3.getQuery().getQuery();
      assertNotNull( SQL3 );
      assertTrue( SQL3.startsWith( "SELECT DISTINCT" ) );
      // System.out.println("SQL3: " + SQL3);

    } catch ( PentahoMetadataException e ) {
      e.printStackTrace();
      fail();
    }

    // NOW TEST MQL QUERY EXCEPTIONS
    String mqlfaildata01 = mqldata.replaceAll( "<constraints/>", //$NON-NLS-1$
      "<constraints>" + //$NON-NLS-1$
        "   <constraint>" + //$NON-NLS-1$
        "      <condition></condition>" + //$NON-NLS-1$
        "   </constraint>" + //$NON-NLS-1$
        "</constraints>" //$NON-NLS-1$
    );

    try {
      mqlquery = MQLQueryFactory.getMQLQuery( mqlfaildata01, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      fail();
    } catch ( PentahoMetadataException e ) {
      assertEquals( "MQLQuery.ERROR_0001 - Condition not specified in MQL constraint", e.getMessage() ); //$NON-NLS-1$
    }

    String mqlfaildata02 = mqldata.replaceAll( "<constraints/>", //$NON-NLS-1$
      "<constraints>" + //$NON-NLS-1$
        "   <constraint/>" + //$NON-NLS-1$
        "</constraints>" //$NON-NLS-1$
    );

    try {
      mqlquery = MQLQueryFactory.getMQLQuery( mqlfaildata01, null, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      fail();
    } catch ( PentahoMetadataException e ) {
      assertEquals( "MQLQuery.ERROR_0001 - Condition not specified in MQL constraint", e.getMessage() ); //$NON-NLS-1$
    }
  }

  public void testFromXMLDomainNotExisting() {
    try {
      String mqlfile1 = "/mqlquery01.xmql";
      String mqldata = loadXmlFile( mqlfile1 );

      // should work
      MQLQueryFactory.getMQLQuery( "org.pentaho.pms.MQLQueryIT$MQLQueryImplDomainExisting",
        mqldata, null, "en_US", cwmSchemaFactory );

      //should fail
      MQLQueryFactory.getMQLQuery( "org.pentaho.pms.MQLQueryIT$MQLQueryImplDomainNotExisting",
        mqldata, null, "en_US", cwmSchemaFactory );

      fail();
    } catch ( PentahoMetadataException e ) {
      assertEquals( "not exists", e.getMessage() );
    }
  }

  public static class MQLQueryImplDomainExisting extends MQLQueryImpl {

    public MQLQueryImplDomainExisting(String XML, DatabaseMeta databaseMeta, String locale,
        CwmSchemaFactoryInterface factory)
        throws PentahoMetadataException {
      super(XML, databaseMeta, locale, factory);
    }

    @Override
    protected void checkDomainExists(String domainId) {
    }
  }

  public static class MQLQueryImplDomainNotExisting extends MQLQueryImpl {

    public MQLQueryImplDomainNotExisting(String XML, DatabaseMeta databaseMeta, String locale,
        CwmSchemaFactoryInterface factory)
      throws PentahoMetadataException {
      super(XML, databaseMeta, locale, factory);
    }

    @Override
    protected void checkDomainExists(String domainId) throws PentahoMetadataException {
      throw new PentahoMetadataException( "not exists" );
    }
  }
}
