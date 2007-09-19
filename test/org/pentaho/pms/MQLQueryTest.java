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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.pentaho.pms.core.CWM;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.OrderBy;
import org.pentaho.pms.schema.PMSFormula;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.util.Const;

import be.ibridge.kettle.core.database.DatabaseMeta;

import junit.framework.TestCase;

public class MQLQueryTest extends TestCase {
  
  BusinessModel ordersModel = null;
  
  public void setUp() {
    if (ordersModel == null) {
      ordersModel = getOrdersModel();
    }
  }
  
  public String loadXmlFile(String filename) {
    try {
      File file = new File(filename);
      FileInputStream fileInputStream = new FileInputStream(file);
      byte bytes[] = new byte[(int)file.length()];
      fileInputStream.read(bytes);
      fileInputStream.close();
      String data = new String(bytes, Const.XML_ENCODING);
      return data;
    } catch (Throwable t) {
      t.printStackTrace();
      fail();
    }
    return null;
  }
  
  public BusinessModel getOrdersModel() {
    CWM cwm = null;
    try {
      cwm = CWM.getInstance("Orders", true); //$NON-NLS-1$
      cwm.importFromXMI("samples/orders.xmi"); //$NON-NLS-1$      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    CwmSchemaFactory cwmSchemaFactory = new CwmSchemaFactory();
    
    SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
    return schemaMeta.findModel("Orders"); //$NON-NLS-1$
  }
  
  public void handleFormula(BusinessModel model, String databaseToTest, String mqlFormula, String expectedSql) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula(model, databaseMeta, mqlFormula);
      formula.parseAndValidate();
      String sql = formula.generateSQL("en_US"); //$NON-NLS-1$
      assertNotNull(sql);
      sql = sql.trim();
      assertEquals(expectedSql, sql);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  public void handleFormulaFailure(BusinessModel model, String databaseToTest, String mqlFormula, String expectedException) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula(model, databaseMeta, mqlFormula);
      formula.parseAndValidate();
      formula.generateSQL("en_US"); //$NON-NLS-1$
      fail();
    } catch (Exception e) {
      assertEquals(e.getMessage(), expectedException);
    }
  }
  
  public void handleFormula(BusinessModel model, BusinessTable table, String databaseToTest, String mqlFormula, String expectedSql) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      PMSFormula formula = new PMSFormula(model, table, databaseMeta, mqlFormula);
      formula.parseAndValidate();
      String sql = formula.generateSQL("en_US"); //$NON-NLS-1$
      assertNotNull(sql);
      sql = sql.trim();
      assertEquals(expectedSql, sql);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  public void handleWhereCondition(BusinessModel model, String mqlFormula, String expectedSql) {
    try {
      WhereCondition cond = new WhereCondition(model, "", mqlFormula); //$NON-NLS-1$
      String sql = cond.getWhereClause("en_US", false); //$NON-NLS-1$
      assertNotNull(sql);
      sql = sql.trim();
      /*
      System.out.println("-----------");
      System.out.println("Expected SQL: [" + expectedSql + "]");
      System.out.println("-----------");
      System.out.println("Actual SQL: [" + sql + "]");
      System.out.println("-----------");
      */
      assertEquals(expectedSql, sql);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  public void testAggregationFormulas() {
    BusinessTable table = ordersModel.findBusinessTable("BT_ORDER_DETAILS"); //$NON-NLS-1$
    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "SUM([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "SUM( \"Order Details\".QUANTITYORDERED  *  \"Order Details\".PRICEEACH )"); //$NON-NLS-1$

    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "SUM([QUANTITYORDERED])", //$NON-NLS-1$
        "SUM( \"Order Details\".QUANTITYORDERED )"); //$NON-NLS-1$

    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "COUNT([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "COUNT( \"Order Details\".QUANTITYORDERED  *  \"Order Details\".PRICEEACH )"); //$NON-NLS-1$

    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "COUNT([PRICEEACH])", //$NON-NLS-1$
        "COUNT( \"Order Details\".PRICEEACH )"); //$NON-NLS-1$
    
    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "AVG([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "AVG( \"Order Details\".QUANTITYORDERED  *  \"Order Details\".PRICEEACH )"); //$NON-NLS-1$

    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "AVG([QUANTITYORDERED])", //$NON-NLS-1$
        "AVG( \"Order Details\".QUANTITYORDERED )"); //$NON-NLS-1$
    
    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "MIN([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "MIN( \"Order Details\".QUANTITYORDERED  *  \"Order Details\".PRICEEACH )"); //$NON-NLS-1$
    
    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "MIN([PRICEEACH])", //$NON-NLS-1$
        "MIN( \"Order Details\".PRICEEACH )"); //$NON-NLS-1$
    
    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "MAX([QUANTITYORDERED]*[PRICEEACH])", //$NON-NLS-1$
        "MAX( \"Order Details\".QUANTITYORDERED  *  \"Order Details\".PRICEEACH )"); //$NON-NLS-1$

    handleFormula(ordersModel, table, "Oracle", //$NON-NLS-1$
        "MAX([QUANTITYORDERED])", //$NON-NLS-1$
        "MAX( \"Order Details\".QUANTITYORDERED )"); //$NON-NLS-1$

  }
  
  public void testNestedAndOrs() {
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "AND(1 <> 2; OR(2<> 3; 3<>4); 4<>5)" //$NON-NLS-1$
        ,"(1 <> 2) AND ((2 <> 3) OR (3 <> 4)) AND (4 <> 5)" //$NON-NLS-1$
      );
  }
  
  public void testNotFunction() {
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "NOT(1 <> 2)" //$NON-NLS-1$
        ,"NOT(1 <> 2)" //$NON-NLS-1$
      );
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "NOT(AND(1 <> 2; 2<>3))" //$NON-NLS-1$
        ,"NOT((1 <> 2) AND (2 <> 3))" //$NON-NLS-1$
      );
    
    handleFormulaFailure(ordersModel, "Oracle", //$NON-NLS-1$
        "NOT (1 <> 2; 2  <> 3)", //$NON-NLS-1$
        "PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function NOT, expecting 1 params" //$NON-NLS-1$
      );
  }
  
  public void testDateFunctionNow() {
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "NOW()" //$NON-NLS-1$
        ,"SYSDATE" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "MySQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"NOW()" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"NOW()" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "DB2", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"( CURRENT DATE )" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "MSSQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"GETDATE()" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "MSAccess", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"NOW()" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "PostgreSQL", //$NON-NLS-1$ 
        "NOW()" //$NON-NLS-1$
        ,"now" //$NON-NLS-1$
      );  
  }
  
  public void testDateFunctionDate() {
   
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
      );
    handleFormula(ordersModel, "MySQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"DATE('2007-05-23')" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"'2007-05-23'" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "DB2", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"DATE('2007-05-23')" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "MSSQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"'20070523'" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "MSAccess", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"#05/23/2007#" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "PostgreSQL", //$NON-NLS-1$ 
        "DATE(2007;5;23)" //$NON-NLS-1$
        ,"date '2007-05-23'" //$NON-NLS-1$
      ); 
  }
  
  public void testDateFunctionDateValue() {
    handleFormula(ordersModel, "Oracle", //$NON-NLS-1$
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"TO_DATE('2007-05-23','YYYY-MM-DD')" //$NON-NLS-1$
      );
    handleFormula(ordersModel, "MySQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"DATE('2007-05-23')" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"'2007-05-23'" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "DB2", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"DATE('2007-05-23')" //$NON-NLS-1$
      );

    handleFormula(ordersModel, "MSSQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"'20070523'" //$NON-NLS-1$
      );
    
    handleFormula(ordersModel, "MSAccess", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"#05/23/2007#" //$NON-NLS-1$
      );   
    
    handleFormula(ordersModel, "PostgreSQL", //$NON-NLS-1$ 
        "DATEVALUE(\"2007-05-23\")" //$NON-NLS-1$
        ,"date '2007-05-23'" //$NON-NLS-1$
      ); 
  }
  
  public void testWhereCondition() {
    handleWhereCondition(ordersModel,
      // mql formula
      "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
      "[BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")", //$NON-NLS-1$
      
      // expected hypersonic sql
      "( (((4 * (2 + 3)) - (( Customers.COUNTRY  * 2) / 3)) <> 1000) AND " + //$NON-NLS-1$
      "( Customers.CUSTOMERNAME  = 'EuroCars') )" //$NON-NLS-1$
    );
  }
  
  
  public void testExpectedExceptionCategoryNotFound() {
    String mqlFormula = "[blah_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME] = \"EuroCars\")"; //$NON-NLS-1$
    try {
      WhereCondition cond = new WhereCondition(ordersModel, "", mqlFormula); //$NON-NLS-1$
      String sql = cond.getWhereClause("en_US", false); //$NON-NLS-1$
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof PentahoMetadataException);
      assertTrue(e.getMessage().indexOf("blah_CUSTOMERS") >= 0); //$NON-NLS-1$
    }
  }
  
  public void testWhereConditionWithIN() {
    handleWhereCondition(ordersModel,
      // mql formula
      "AND(4 * (2 + 3) - ([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY] * 2) / 3 <> 1000;" + //$NON-NLS-1$
      "IN([BT_CUSTOMERS.BC_CUSTOMERS_CUSTOMERNAME];\"EuroCars1\";\"EuroCars2\";\"EuroCars3\"))", //$NON-NLS-1$
      
      // expected hypersonic sql
      "( (((4 * (2 + 3)) - (( Customers.COUNTRY  * 2) / 3)) <> 1000) AND " + //$NON-NLS-1$
      " Customers.CUSTOMERNAME  IN ( 'EuroCars1' , 'EuroCars2' , 'EuroCars3' )  )" //$NON-NLS-1$
    );
  }
  
  public void testWhereConditionWithLIKE() {
    handleWhereCondition(ordersModel,
      // mql formula
      "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%US%\")", //$NON-NLS-1$

      // expected hypersonic sql
      "(  Customers.COUNTRY  LIKE '%US%' )" //$NON-NLS-1$
    );
  }
  
  public void testLike() {
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%\")" //$NON-NLS-1$
        ,"Customers.COUNTRY  LIKE '%'" //$NON-NLS-1$
      );
  }

  public void testCase() {
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; \"Japan\")" //$NON-NLS-1$
        ,"CASE  WHEN  Customers.COUNTRY  = 'US' THEN 'USA' WHEN  Customers.COUNTRY  = 'JAPAN' THEN 'Japan' END" //$NON-NLS-1$
      );
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "CASE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"US\"; \"USA\";[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAPAN\"; \"Japan\"; \"Canada\")" //$NON-NLS-1$
        ,"CASE  WHEN  Customers.COUNTRY  = 'US' THEN 'USA' WHEN  Customers.COUNTRY  = 'JAPAN' THEN 'Japan' ELSE 'Canada' END" //$NON-NLS-1$
      );
    handleFormulaFailure(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "CASE()" //$NON-NLS-1$
        ,"PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
      );
    handleFormulaFailure(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "CASE(\"\")" //$NON-NLS-1$
        ,"PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function CASE, expecting 2 params" //$NON-NLS-1$
      );
  }
  
  public void testCoalesce() {
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]; \"USA\")" //$NON-NLS-1$
        ,"COALESCE( Customers.COUNTRY  , 'USA')" //$NON-NLS-1$
      );
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "COALESCE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY])" //$NON-NLS-1$
        ,"COALESCE( Customers.COUNTRY )" //$NON-NLS-1$
      );
    handleFormulaFailure(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "COALESCE()" //$NON-NLS-1$
        ,"PMSFormulaContext.ERROR_0002 - Invalid number of parameters for function COALESCE, expecting 1 params" //$NON-NLS-1$
      );
  }

  
  public void testNoFunction() {
    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
        "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]" //$NON-NLS-1$
        ,"Customers.COUNTRY" //$NON-NLS-1$
      );
  }
  
//  public void testSingleQuotes() {
//    handleFormula(ordersModel, "Hypersonic", //$NON-NLS-1$ 
//        "[BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY]=\"JAP'AN\"" //$NON-NLS-1$
//        ,"Customers.COUNTRY  LIKE '%'" //$NON-NLS-1$
//      );
//  }

  public void testToFromXML() {

    CWM cwm = CWM.getInstance("Orders", false); //$NON-NLS-1$
    try {
      cwm.importFromXMI("samples/orders.xmi"); //$NON-NLS-1$
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    CwmSchemaFactory cwmSchemaFactory = new CwmSchemaFactory();
    
   
		String mqlfile1 = "test/org/pentaho/pms/mqlquery01.xmql"; //$NON-NLS-1$
    String mqlfile2 = "test/org/pentaho/pms/mqlquery02.xmql"; //$NON-NLS-1$ // Distinct is off, but otherwise the same
    String mqlfile3 = "test/org/pentaho/pms/mqlquery_oldformat.xmql"; //$NON-NLS-1$
    
    String mqldata = loadXmlFile(mqlfile1);
    assertNotNull(mqldata);
    MQLQuery mqlquery = null;
    try {
      mqlquery = new MQLQuery(mqldata, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
    } catch (PentahoMetadataException e) {
      e.printStackTrace();
      fail();
    }

    // Tests parsing the options tag to get the boolean into the MQL query
    mqldata = null;
    mqldata = loadXmlFile(mqlfile2);
    assertNotNull(mqldata);
    MQLQuery mqlquery2 = null;
    try {
      mqlquery2 = new MQLQuery(mqldata, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      assertEquals(mqlquery2.getDisableDistinct(), true);
    } catch (PentahoMetadataException e) {
      e.printStackTrace();
      fail();
    }
    
    // Tests parsing an old-format (without options tag) still works, and defaults disableDistinct to false
    mqldata = null;
    mqldata = loadXmlFile(mqlfile3);
    assertNotNull(mqldata);
    MQLQuery mqlquery3 = null;
    try {
      mqlquery3 = new MQLQuery(mqldata, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      assertEquals(mqlquery3.getDisableDistinct(), false);
    } catch (PentahoMetadataException e) {
      e.printStackTrace();
      fail();
    }
    
    assertNotNull(mqlquery);
    assertNotNull(mqlquery.getSchemaMeta());
    assertEquals("Orders", mqlquery.getSchemaMeta().getDomainName()); //$NON-NLS-1$
    assertNotNull(mqlquery.getModel());
    assertEquals("Orders", mqlquery.getModel().getId() ); //$NON-NLS-1$
    
    List selections = mqlquery.getSelections();
    
    assertNotNull(selections);
    assertEquals(2, selections.size());
    
    assertTrue(selections.get(0) instanceof BusinessColumn);
    BusinessColumn col1 = (BusinessColumn)selections.get(0);
    assertEquals("BT_CUSTOMERS", col1.getBusinessTable().getId()); //$NON-NLS-1$
    assertEquals("BC_CUSTOMERS_CUSTOMERNAME", col1.getId()); //$NON-NLS-1$
    
    assertTrue(selections.get(1) instanceof BusinessColumn);
    BusinessColumn col2 = (BusinessColumn)selections.get(1);
    assertEquals("BT_CUSTOMERS", col2.getBusinessTable().getId()); //$NON-NLS-1$
    assertEquals("BC_CUSTOMERS_COUNTRY", col2.getId()); //$NON-NLS-1$
    
    List orders = mqlquery.getOrder();
    
    assertNotNull(orders);
    assertEquals(1, orders.size());
    assertTrue(orders.get(0) instanceof OrderBy);
    OrderBy order = (OrderBy)orders.get(0);
    
    assertEquals(true, order.isAscending());
    assertNotNull(order.getBusinessColumn());
    assertEquals("BT_CUSTOMERS", order.getBusinessColumn().getBusinessTable().getId()); //$NON-NLS-1$
    assertEquals("BC_CUSTOMERS_COUNTRY", order.getBusinessColumn().getId()); //$NON-NLS-1$
    
    // NOW TEST XML OUTPUT
    try {
      File file = new File(mqlfile1);
      FileInputStream fileInputStream = new FileInputStream(file);
      byte bytes[] = new byte[(int)file.length()];
      fileInputStream.read(bytes);
      fileInputStream.close();
      String data = new String(bytes, Const.XML_ENCODING);
      data = data.replaceAll("[\n\t]", ""); //$NON-NLS-1$ //$NON-NLS-2$
      String xml = mqlquery.getXML();
      assertNotNull(xml);
      xml = xml.replaceAll("[\n\t]",""); //$NON-NLS-1$ //$NON-NLS-2$
      /*
      System.out.println("Generated XML");
      System.out.println(xml);
      System.out.println("File XML");
      System.out.println(data);
      */
      assertEquals(data, xml);
      
      // Checks that the rendered XML has the option with true instead of false
      xml = mqlquery2.getXML();
      assertFalse(data.equals(xml));
      
      // Tests that newly generated XML has the correct false flag in it.
      xml = mqlquery3.getXML();
      assertEquals(data, xml);
      
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    try {
      // Now, look at generated SQL for distinct -vs- no distinct...
      String SQL1 = mqlquery.getQuery().getQuery();
      assertNotNull(SQL1);
      assertTrue(SQL1.startsWith("SELECT DISTINCT"));
      // System.out.println("SQL1: " + SQL1);
     
      String SQL2 = mqlquery2.getQuery().getQuery();
      assertNotNull(SQL2);
      assertFalse(SQL2.startsWith("SELECT DISTINCT"));
      // System.out.println("SQL2: " + SQL2);

      String SQL3 = mqlquery3.getQuery().getQuery();
      assertNotNull(SQL3);
      assertTrue(SQL3.startsWith("SELECT DISTINCT"));
      // System.out.println("SQL3: " + SQL3);
      
    } catch (PentahoMetadataException e) {
      e.printStackTrace();
      fail();
    }
    
    
    // NOW TEST MQL QUERY EXCEPTIONS
    String mqlfaildata01 = mqldata.replaceAll(
          "<constraints/>", //$NON-NLS-1$
          "<constraints>" + //$NON-NLS-1$
          "   <constraint>" + //$NON-NLS-1$
          "      <condition></condition>" + //$NON-NLS-1$
          "   </constraint>" + //$NON-NLS-1$
          "</constraints>" //$NON-NLS-1$
        );
    
    try {
      mqlquery = new MQLQuery(mqlfaildata01, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      fail();
    } catch (PentahoMetadataException e) {
      assertEquals("MQLQuery.ERROR_0001 - Condition not specified in MQL constraint", e.getMessage() ); //$NON-NLS-1$
    }
    
    String mqlfaildata02 = mqldata.replaceAll(
          "<constraints/>", //$NON-NLS-1$
          "<constraints>" + //$NON-NLS-1$
          "   <constraint/>" + //$NON-NLS-1$
          "</constraints>" //$NON-NLS-1$
        );

    try {
      mqlquery = new MQLQuery(mqlfaildata01, "en_US", cwmSchemaFactory ); //$NON-NLS-1$
      fail();
    } catch (PentahoMetadataException e) {
      assertEquals("MQLQuery.ERROR_0001 - Condition not specified in MQL constraint", e.getMessage() ); //$NON-NLS-1$
    }
	}
}
