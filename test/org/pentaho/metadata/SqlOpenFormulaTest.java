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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.impl.sql.SqlOpenFormula;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.schema.SchemaMeta;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test exercises the sql open formula code.  right now it converts from the old XMI standard
 * before executing in the new thin model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
@SuppressWarnings({"deprecation","nls"})
public class SqlOpenFormulaTest {

  private static final String EXPECTED_EXCEPTION_MESSAGE = "<-- EXCEPTION -->";

  @BeforeClass
  public static void initKettle() throws Exception {
    KettleEnvironment.init(false);
  }

  static LogicalModel ordersModel;
  
  static final boolean USE_LEGACY_CWM = false;
  
  public LogicalModel getOrdersModel() {
    if (ordersModel == null) {
      if (USE_LEGACY_CWM) {
  
        CWM cwm = null;
        try {
          cwm = CWM.getInstance("Orders", true); //$NON-NLS-1$
          Assert.assertNotNull("CWM singleton instance is null", cwm);
          cwm.importFromXMI("samples/orders.xmi"); //$NON-NLS-1$      
        } catch (Exception e) {
          e.printStackTrace();
          Assert.fail();
        }
        CwmSchemaFactory cwmSchemaFactory = new CwmSchemaFactory();
    
        SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
        org.pentaho.pms.schema.BusinessModel oldOrdersModel = schemaMeta.findModel("Orders"); //$NON-NLS-1$
        try {
          Domain domain = ThinModelConverter.convertFromLegacy(schemaMeta);
          ordersModel = domain.findLogicalModel("Orders"); //$NON-NLS-1$
          Assert.assertNotNull(ordersModel);
        } catch (Exception e) {
          e.printStackTrace();
        }
        // convert to and then from thin model just for giggles
      } else {
        try {
          Domain domain = new XmiParser().parseXmi(new FileInputStream("samples/orders.xmi"));
          ordersModel = domain.findLogicalModel("Orders");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return ordersModel;
  }
  
  @AfterClass
  public static void deleteFiles() throws Exception {
    deleteFile("mdr.btb");
    deleteFile("mdr.btd");
    deleteFile("mdr.btx");
  }
  
  private static void deleteFile(String filename) {
    File f = new File(filename);
    if(f.exists()) {
      f.delete();
    }
  }
  
  public void handleFormula(LogicalModel model, String databaseToTest, Map<String,  Object> parameters, String mqlFormula, String expectedSql){
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      SqlOpenFormula formula = new SqlOpenFormula(model, databaseMeta, mqlFormula, null, parameters, false);
      formula.parseAndValidate();
      String sql = formula.generateSQL("en_US"); //$NON-NLS-1$
      Assert.assertNotNull(sql);
      sql = sql.trim();
      Assert.assertEquals(expectedSql, sql);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
  public void handleFormula(LogicalModel model, String databaseToTest, String mqlFormula, String expectedSql) {
    handleFormula(model, databaseToTest, null, mqlFormula, expectedSql);
  }

  public void handleFormulaFailure(LogicalModel model, String databaseToTest, Map<String, Object> params, String mqlFormula, String expectedException) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      SqlOpenFormula formula = new SqlOpenFormula(model, databaseMeta, mqlFormula, null, params, false);
      formula.parseAndValidate();
      formula.generateSQL("en_US"); //$NON-NLS-1$
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals(e.getMessage(), expectedException);
    }

  }
  public void handleFormulaFailure(LogicalModel model, String databaseToTest, String mqlFormula, String expectedException) {
    handleFormulaFailure(model, databaseToTest, null, mqlFormula, expectedException);
  }

  public void handleFormula(LogicalModel model, LogicalTable table, String databaseToTest, String mqlFormula,
      String expectedSql) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      SqlOpenFormula formula = new SqlOpenFormula(model, table, databaseMeta, mqlFormula, null, null, false);
      formula.parseAndValidate();
      String sql = formula.generateSQL("en_US"); //$NON-NLS-1$
      Assert.assertNotNull(sql);
      sql = sql.trim();
      Assert.assertEquals(expectedSql, sql);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testMqlDateParams() throws Exception {
    Domain steelWheelsDomain = new XmiParser().parseXmi(new FileInputStream("test-res/steel-wheels.xmi")); 
    
    String mql = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<mql>"
      + "<domain_id>Steel-Wheels</domain_id>"
      + "<model_id>BV_ORDERS</model_id>"
      + "<options>"
      + "<disable_distinct>false</disable_distinct>"
      + "</options>"
      + "<parameters>"
      + "<parameter defaultValue=\"2004-01-01\" name=\"date\" type=\"STRING\"/>"
      + "</parameters>"
      + "<selections>"
      + "<selection>"
      + "<view>BC_CUSTOMER_W_TER_</view>"
      + "<column>BC_CUSTOMER_W_TER_CUSTOMERNUMBER</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "<selection>"
      + "<view>CAT_ORDERS</view>"
      + "<column>BC_ORDERS_ORDERDATE</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "</selections>"
      + "<constraints>"
      + "<constraint>"
      + "<operator/>"
      + "<condition>[CAT_ORDERS.BC_ORDERS_ORDERDATE] "
      + "&gt;DATEVALUE([param:date])</condition>"
      + "</constraint>"
      + "</constraints>"
      + "<orders/>"
      + "</mql>";
    
      QueryXmlHelper helper = new QueryXmlHelper();
      InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
      steelWheelsDomain.setId("Steel-Wheels");
      
      repo.storeDomain(steelWheelsDomain, false);
      Query query = helper.fromXML(repo, mql);
    
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
      
      SqlGenerator generator = new SqlGenerator();
      MappedQuery mappedQuery = generator.generateSql(query, "en_US", repo, databaseMeta);
      
      Assert.assertEquals(
          "SELECT DISTINCT \n" + 
          "          BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER AS COL0\n" + 
          "         ,BT_ORDERS_ORDERS.ORDERDATE AS COL1\n" + 
          "FROM \n" + 
          "          CUSTOMER_W_TER BT_CUSTOMER_W_TER_CUSTOMER_W01\n" + 
          "         ,ORDERS BT_ORDERS_ORDERS\n" + 
          "WHERE \n" + 
          "          ( BT_ORDERS_ORDERS.CUSTOMERNUMBER = BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              BT_ORDERS_ORDERS.ORDERDATE  > TO_DATE('2004-01-01','YYYY-MM-DD')\n" + 
          "          )\n" + 
          "        )\n"
          , mappedQuery.getQuery());
  }
  
  @Test
  public void testMqlDateParams_with_Date_object() throws Exception {
    Domain steelWheelsDomain = new XmiParser().parseXmi(new FileInputStream("test-res/steel-wheels.xmi")); 
    
    String mql = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<mql>"
      + "<domain_id>Steel-Wheels</domain_id>"
      + "<model_id>BV_ORDERS</model_id>"
      + "<options>"
      + "<disable_distinct>false</disable_distinct>"
      + "</options>"
      + "<parameters>"
      + "<parameter defaultValue=\"2004-01-01\" name=\"date\" type=\"STRING\"/>"
      + "</parameters>"
      + "<selections>"
      + "<selection>"
      + "<view>BC_CUSTOMER_W_TER_</view>"
      + "<column>BC_CUSTOMER_W_TER_CUSTOMERNUMBER</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "<selection>"
      + "<view>CAT_ORDERS</view>"
      + "<column>BC_ORDERS_ORDERDATE</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "</selections>"
      + "<constraints>"
      + "<constraint>"
      + "<operator/>"
      + "<condition>[CAT_ORDERS.BC_ORDERS_ORDERDATE] "
      + "&gt;DATEVALUE([param:date])</condition>"
      + "</constraint>"
      + "</constraints>"
      + "<orders/>"
      + "</mql>";
    
      QueryXmlHelper helper = new QueryXmlHelper();
      InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
      steelWheelsDomain.setId("Steel-Wheels");
      
      repo.storeDomain(steelWheelsDomain, false);
      Query query = helper.fromXML(repo, mql);
    
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
      
      SqlGenerator generator = new SqlGenerator();
      Map<String, Object> parameters = new HashMap<String, Object>();
      Date now = new Date();
      parameters.put("date", now);
      MappedQuery mappedQuery = generator.generateSql(query, "en_US", repo, databaseMeta, parameters, false);
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String nowAsString = sdf.format(now);
      
      Assert.assertEquals(
          "SELECT DISTINCT \n" + 
          "          BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER AS COL0\n" + 
          "         ,BT_ORDERS_ORDERS.ORDERDATE AS COL1\n" + 
          "FROM \n" + 
          "          CUSTOMER_W_TER BT_CUSTOMER_W_TER_CUSTOMER_W01\n" + 
          "         ,ORDERS BT_ORDERS_ORDERS\n" + 
          "WHERE \n" + 
          "          ( BT_ORDERS_ORDERS.CUSTOMERNUMBER = BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              BT_ORDERS_ORDERS.ORDERDATE  > TO_DATE('" + nowAsString + "','YYYY-MM-DD')\n" + 
          "          )\n" + 
          "        )\n"
          , mappedQuery.getQuery());
  }
  
  @Test
  public void testMqlConstraints() throws Exception {
    Domain steelWheelsDomain = new XmiParser().parseXmi(new FileInputStream("test-res/steel-wheels.xmi")); 
    
    String mql = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<mql>"
      + "<domain_id>Steel-Wheels</domain_id>"
      + "<model_id>BV_ORDERS</model_id>"
      + "<options>"
      + "<disable_distinct>false</disable_distinct>"
      + "</options>"
      + "<selections>"
      + "<selection>"
      + "<view>BC_CUSTOMER_W_TER_</view>"
      + "<column>BC_CUSTOMER_W_TER_CUSTOMERNUMBER</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "<selection>"
      + "<view>CAT_ORDERS</view>"
      + "<column>BC_ORDERS_ORDERDATE</column>"
      + "<aggregation>NONE</aggregation>"
      + "</selection>"
      + "</selections>"
      + "<constraints>"
      + "<constraint>"
      + "<operator/>"
      + "<condition>[CAT_ORDERS.BC_ORDERS_ORDERDATE] "
      + "&gt;DATEVALUE(\"2009-12-12\")</condition>"
      + "</constraint>"
      + "<constraint>"
      + "<operator>AND NOT</operator>"
      + "<condition>[CAT_ORDERS.BC_ORDERS_ORDERDATE] "
      + "&lt;DATEVALUE(\"2009-12-13\")</condition>"
      + "</constraint>"
      + "</constraints>"
      + "<orders/>"
      + "</mql>";
    
      QueryXmlHelper helper = new QueryXmlHelper();
      InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
      steelWheelsDomain.setId("Steel-Wheels");
      
      repo.storeDomain(steelWheelsDomain, false);
      Query query = helper.fromXML(repo, mql);
    
      DatabaseMeta databaseMeta = new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
      
      SqlGenerator generator = new SqlGenerator();
      MappedQuery mappedQuery = generator.generateSql(query, "en_US", repo, databaseMeta);
      
      TestHelper.printOutJava(mappedQuery.getQuery());
      
      Assert.assertEquals(
          "SELECT DISTINCT \n" + 
          "          BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER AS COL0\n" + 
          "         ,BT_ORDERS_ORDERS.ORDERDATE AS COL1\n" + 
          "FROM \n" + 
          "          CUSTOMER_W_TER BT_CUSTOMER_W_TER_CUSTOMER_W01\n" + 
          "         ,ORDERS BT_ORDERS_ORDERS\n" + 
          "WHERE \n" + 
          "          ( BT_ORDERS_ORDERS.CUSTOMERNUMBER = BT_CUSTOMER_W_TER_CUSTOMER_W01.CUSTOMERNUMBER )\n" + 
          "      AND \n" + 
          "        (\n" + 
          "          (\n" + 
          "              BT_ORDERS_ORDERS.ORDERDATE  > TO_DATE('2009-12-12','YYYY-MM-DD')\n" + 
          "          )\n" + 
          "      AND NOT (\n" + 
          "              BT_ORDERS_ORDERS.ORDERDATE  < TO_DATE('2009-12-13','YYYY-MM-DD')\n" + 
          "          )\n" + 
          "        )\n"
          , mappedQuery.getQuery());
  }
  
  
  @Test
  public void testDateFunctionMath() throws Exception {

    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.DAY_OF_MONTH, 1);
      
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = fmt.format( cal.getTime() );

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "DATEMATH(\"0:MS\")" //$NON-NLS-1$
        , "TO_DATE('"+dateStr+"','YYYY-MM-DD')" //$NON-NLS-1$
    );

  }
  
  @Test
  public void testAggregationFormulas() {
    LogicalTable table = getOrdersModel().findLogicalTable("BT_ORDER_DETAILS"); //$NON-NLS-1$
    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "SUM([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "SUM( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "SUM([QUANTITYORDERED])", //$NON-NLS-1$
        "SUM( BT_ORDER_DETAILS.QUANTITYORDERED )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "COUNT([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "COUNT( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "COUNT([PRICEEACH])", //$NON-NLS-1$
        "COUNT( BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "AVG([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "AVG( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "AVG([QUANTITYORDERED])", //$NON-NLS-1$
        "AVG( BT_ORDER_DETAILS.QUANTITYORDERED )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "MIN([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "MIN( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "MIN([PRICEEACH])", //$NON-NLS-1$
        "MIN( BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "MAX([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "MAX( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_ORDER_DETAILS.PRICEEACH )"); //$NON-NLS-1$

    handleFormula(getOrdersModel(), table, "Oracle", //$NON-NLS-1$
        "MAX([QUANTITYORDERED])", //$NON-NLS-1$
        "MAX( BT_ORDER_DETAILS.QUANTITYORDERED )"); //$NON-NLS-1$

  }
  
  @Test
  public void testNestedAndOrs() throws Exception {
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "AND(1 <> 2; OR(2<> 3; 3<>4); 4<>5)" //$NON-NLS-1$
        , "(1 <> 2) AND ((2 <> 3) OR (3 <> 4)) AND (4 <> 5)" //$NON-NLS-1$
    );
  }

  @Test
  public void testNotFunction() throws Exception {
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "NOT(1 <> 2)" //$NON-NLS-1$
        , "NOT(1 <> 2)" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "NOT(AND(1 <> 2; 2<>3))" //$NON-NLS-1$
        , "NOT((1 <> 2) AND (2 <> 3))" //$NON-NLS-1$
    );

    handleFormulaFailure(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "NOT (1 <> 2; 2  <> 3)", //$NON-NLS-1$
        "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function NOT, expecting 1 params" //$NON-NLS-1$
    );
  }
  
  @Test
  public void testBooleanFunctions() throws Exception {
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"TRUE" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"FALSE" //$NON-NLS-1$
      );
    
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "TRUE()" //$NON-NLS-1$
        ,"1" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "FALSE()" //$NON-NLS-1$
        ,"0" //$NON-NLS-1$
      );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"TRUE" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"FALSE" //$NON-NLS-1$
      );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"1" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"0" //$NON-NLS-1$
      );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"(1=1)" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"(0=1)" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "OR(TRUE();FALSE())" //$NON-NLS-1$
        ,"(1=1) OR (0=1)" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"TRUE" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"FALSE" //$NON-NLS-1$
      );
    
    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "TRUE()" //$NON-NLS-1$
        ,"TRUE" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"FALSE" //$NON-NLS-1$
      );
  }

  @Test
  public void testDateFunctionNow() throws Exception {
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "NOW()" //$NON-NLS-1$
        , "SYSDATE" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "NOW()" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "NOW()" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "( CURRENT DATE )" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "GETDATE()" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "NOW()" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        , "now" //$NON-NLS-1$
    );
  }

  @Test
  public void testDateFunctionDate() throws Exception {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "'2007-05-23'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "'20070523'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "#05/23/2007#" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        , "date '2007-05-23'" //$NON-NLS-1$
    );
  }

  @Test
  public void testDateFunctionDateValue() throws Exception {
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "'2007-05-23'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "DATE('2007-05-23')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "'20070523'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "#05/23/2007#" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        , "date '2007-05-23'" //$NON-NLS-1$
    );
  }

  @Test
  public void testWhereCondition() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", 
    // mql formula
        "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
            "[BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")", //$NON-NLS-1$

        // expected hypersonic sql
        "(4 * (2 + 3) - ( BT_CUSTOMERS.COUNTRY  * 2) / 3 <> 1000) AND" + //$NON-NLS-1$
                " ( BT_CUSTOMERS.CUSTOMERNAME  = 'EuroCars')" //$NON-NLS-1$
    );
  }

  @Test
  public void testExpectedExceptionCategoryNotFound() {
    String mqlFormula = "[blah_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")"; //$NON-NLS-1$
    handleFormulaFailure(getOrdersModel(), "Hypersonic", mqlFormula, "SqlOpenFormula.ERROR_0005 - Failed to parse formula [blah_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")");
  }

  @Test
  public void testWhereConditionWithIN() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", 
    // mql formula
        "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
            "IN([BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME];\"EuroCars1\";\"EuroCars2\";\"EuroCars3\"))", //$NON-NLS-1$

        // expected hypersonic sql
        "(4 * (2 + 3) - ( BT_CUSTOMERS.COUNTRY  * 2) / 3 <> 1000) AND " + //$NON-NLS-1$
            " BT_CUSTOMERS.CUSTOMERNAME  IN ( 'EuroCars1' , 'EuroCars2' , 'EuroCars3' )" //$NON-NLS-1$
    );
  }

  @Test
  public void testWhereConditionWithLIKE() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", 
        "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%US%\")", //$NON-NLS-1$

        // expected hypersonic sql
        "BT_CUSTOMERS.COUNTRY  LIKE '%US%'" //$NON-NLS-1$
    );
  }

  @Test
  public void testLike() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%'" //$NON-NLS-1$
    );
  }

  @Test
  public void testCase() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; \"Japan\")" //$NON-NLS-1$
        , "CASE  WHEN  BT_CUSTOMERS.COUNTRY  = 'US' THEN 'USA' WHEN  BT_CUSTOMERS.COUNTRY  = 'JAPAN' THEN 'Japan' END" //$NON-NLS-1$
    );
    handleFormula(
        getOrdersModel(),
        "Hypersonic", //$NON-NLS-1$ 
        "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; \"Japan\"; \"Canada\")" //$NON-NLS-1$
        ,
        "CASE  WHEN  BT_CUSTOMERS.COUNTRY  = 'US' THEN 'USA' WHEN  BT_CUSTOMERS.COUNTRY  = 'JAPAN' THEN 'Japan' ELSE 'Canada' END" //$NON-NLS-1$
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "CASE()" //$NON-NLS-1$
        , "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "CASE(\"\")" //$NON-NLS-1$
        , "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
    );
  }

  @Test
  public void testCoalesce() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]; \"USA\")" //$NON-NLS-1$
        , "COALESCE( BT_CUSTOMERS.COUNTRY  , 'USA')" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY])" //$NON-NLS-1$
        , "COALESCE( BT_CUSTOMERS.COUNTRY )" //$NON-NLS-1$
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "COALESCE()" //$NON-NLS-1$
        , "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function COALESCE, expecting 1 params" //$NON-NLS-1$
    );
  }

  @Test
  public void testNoFunction() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY" //$NON-NLS-1$
    );
  }

  //  public void testSingleQuotes() {
  //    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
  //        "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAP'AN\"" //$NON-NLS-1$
  //        ,"Customers.COUNTRY  LIKE '%'" //$NON-NLS-1$
  //      );
  //  }

  /**
   * In this test we try to see to it :<br>
   * - that the formula engine picks the 2 specified columns from 2 different business tables<br>
   * - hat the aggregation for QUANTITYORDERED is SUM() and NOT for BUYPRICE<br>
   * <br>
   */
  @Test
  public void testMultiTableColumnFormulas() throws Exception {
    String formula = "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE]";
    String sql = "SUM(BT_ORDER_DETAILS.QUANTITYORDERED)  *  BT_PRODUCTS.BUYPRICE";

    handleFormula(getOrdersModel(), "Hypersonic", formula, sql);
  }

  /**
   * In this test we try to see to it :<br>
   * - that the formula engine picks the 2 specified columns from 2 different business tables<br>
   * - that we calculate the sum of the multiplication
   * <br>
   */
  @Test
  public void testMultiTableColumnFormulasAggregate() throws Exception {
    LogicalColumn quantityOrdered = getOrdersModel().findLogicalColumn("BC_ORDER_DETAILS_QUANTITYORDERED");
    Assert.assertNotNull("Expected to find the business column 'quantity ordered'", quantityOrdered);
    LogicalColumn buyPrice = getOrdersModel().findLogicalColumn("BC_PRODUCTS_BUYPRICE");
    Assert.assertNotNull("Expected to find the business column 'buy price'", buyPrice);

    // let's remove the aggregations of the quantity ordered...
    //
    AggregationType qaBackup = quantityOrdered.getAggregationType();
    AggregationType paBackup = buyPrice.getAggregationType();
    quantityOrdered.setAggregationType(AggregationType.NONE);
    buyPrice.setAggregationType(AggregationType.NONE);

    // This changes the expected result...
    //
    String formula = "SUM( [BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE] )";
    String sql = "SUM( BT_ORDER_DETAILS.QUANTITYORDERED  *  BT_PRODUCTS.BUYPRICE )";

    handleFormula(getOrdersModel(), "Hypersonic", formula, sql);

    // Set it back to the way it was for further testing.
    quantityOrdered.setAggregationType(qaBackup);
    buyPrice.setAggregationType(paBackup);
  }

  /**
   * In this test we try to test :<br>
   * - if the formula engine picks the 2 specified columns from 2 different business tables<br>
   * - if we calculate the multiplication of the sums
   * <br>
   */
  @Test
  public void testMultiTableColumnFormulasAggregate2() throws Exception {
    LogicalColumn quantityOrdered = getOrdersModel().findLogicalColumn("BC_ORDER_DETAILS_QUANTITYORDERED");
    Assert.assertNotNull("Expected to find the business column 'quantity ordered'", quantityOrdered);
    LogicalColumn buyPrice = getOrdersModel().findLogicalColumn("BC_PRODUCTS_BUYPRICE");
    Assert.assertNotNull("Expected to find the business column 'buy price'", buyPrice);

    // let's enable the aggregations of the quantity ordered...
    //
    AggregationType qaBackup = quantityOrdered.getAggregationType();
    AggregationType paBackup = buyPrice.getAggregationType();
    quantityOrdered.setAggregationType(AggregationType.SUM);
    buyPrice.setAggregationType(AggregationType.SUM);

    // This changes the expected result...
    //
    String formula = "[BT_ORDER_DETAILS.BC_ORDER_DETAILS_QUANTITYORDERED] * [BT_PRODUCTS.BC_PRODUCTS_BUYPRICE]";
    String sql = "SUM(BT_ORDER_DETAILS.QUANTITYORDERED)  *  SUM(BT_PRODUCTS.BUYPRICE)";

    handleFormula(getOrdersModel(), "Hypersonic", formula, sql);

    // Set it back to the way it was for further testing.
    quantityOrdered.setAggregationType(qaBackup);
    buyPrice.setAggregationType(paBackup);
  }
  
  @Test
  public void testContainsFunction() throws Exception {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA' || '%'" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE CONCAT('%', 'AMERICA', '%')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA' || '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA' || '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA' + '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA' + '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "CONTAINS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA' || '%'" //$NON-NLS-1$
    );
  }
  
  @Test
  public void testBeginsWithFunction() throws Exception {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' || '%'" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE CONCAT('AMERICA', '%')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' || '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' || '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' + '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' + '%'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "BEGINSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' || '%'" //$NON-NLS-1$
    );
  }
  
  @Test
  public void testEndsWithFunction() throws Exception {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA'" //$NON-NLS-1$
    );
    handleFormula(getOrdersModel(), "MySQL", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE CONCAT('%', 'AMERICA')" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "DB2", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "MSAccess", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA'" //$NON-NLS-1$
    );

    handleFormula(getOrdersModel(), "PostgreSQL", //$NON-NLS-1$ 
        "ENDSWITH([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' || 'AMERICA'" //$NON-NLS-1$
    );
  }

  @Test
  public void testISNAFunction() throws Exception {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "ISNA([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY])" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  IS NULL" //$NON-NLS-1$
    );
  }

  @Test
  public void testEqualsFunction() throws Exception {
    handleFormula(getOrdersModel(), "Hypersonic",
        "EQUALS([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"AMERICA\")",
        "BT_CUSTOMERS.COUNTRY  = 'AMERICA'"
    );

    Map<String,  Object> param = new HashMap<String, Object>();
    param.put("param1", new String[] {"SHIPPED", "DELIVERED"});
    handleFormula(getOrdersModel(), "Hypersonic", param,
        "EQUALS([bc_ORDERS.BC_ORDERS_STATUS];[param:param1])",
        "BT_ORDERS.STATUS  IN ( 'SHIPPED' , 'DELIVERED' )"
    );
  }
  
  @Test
  public void testMultiValuedParamsForNonSupportingFunctions() {
    Map<String,  Object> param = new HashMap<String, Object>();
    param.put("param1", new String[]{"SHIPPED", "DELIVERED"});

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "ENDSWITH([bc_ORDERS.BC_ORDERS_STATUS];[param:param1])",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "ENDSWITH")
    );

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "BEGINSWITH([bc_ORDERS.BC_ORDERS_STATUS];[param:param1])",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "BEGINSWITH")
    );

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "LIKE([bc_ORDERS.BC_ORDERS_STATUS];[param:param1])",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "LIKE")
    );

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "COALESCE([bc_ORDERS.BC_ORDERS_STATUS];[param:param1])",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "COALESCE")
    );

  }

  @Test
  public void testMultiValuedParamsForNonSupportingOperators() {
    Map<String,  Object> param = new HashMap<String, Object>();
    param.put("param1", new String[]{"SHIPPED", "DELIVERED"});

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "[bc_ORDERS.BC_ORDERS_STATUS]=[param:param1]",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "=")
    );

    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "[bc_ORDERS.BC_ORDERS_STATUS]>[param:param1]",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", ">")
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "[bc_ORDERS.BC_ORDERS_STATUS]<[param:param1]",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "<")
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "[bc_ORDERS.BC_ORDERS_STATUS]>=[param:param1]",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", ">=")
    );
    handleFormulaFailure(getOrdersModel(), "Hypersonic", param,
        "[bc_ORDERS.BC_ORDERS_STATUS]<=[param:param1]",
        Messages.getErrorString("SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", "<=")
    );

  }
}
