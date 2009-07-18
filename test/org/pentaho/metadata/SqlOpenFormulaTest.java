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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.query.impl.sql.SqlOpenFormula;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.schema.SchemaMeta;

/**
 * This test exercises the sql open formula code.  right now it converts from the old XMI standard
 * before executing in the new thin model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SqlOpenFormulaTest {


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
  
  public void handleFormula(LogicalModel model, String databaseToTest, String mqlFormula, String expectedSql) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      SqlOpenFormula formula = new SqlOpenFormula(model, databaseMeta, mqlFormula, null, null, false);
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

  public void handleFormulaFailure(LogicalModel model, String databaseToTest, String mqlFormula,
      String expectedException) {
    // retrieve various databases here
    DatabaseMeta databaseMeta = new DatabaseMeta("", databaseToTest, "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    try {
      SqlOpenFormula formula = new SqlOpenFormula(model, databaseMeta, mqlFormula, null, null, false);
      formula.parseAndValidate();
      formula.generateSQL("en_US"); //$NON-NLS-1$
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals(e.getMessage(), expectedException);
    }
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
  public void testDateFunctionMath() {

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
  public void testNestedAndOrs() {
    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "AND(1 <> 2; OR(2<> 3; 3<>4); 4<>5)" //$NON-NLS-1$
        , "(1 <> 2) AND ((2 <> 3) OR (3 <> 4)) AND (4 <> 5)" //$NON-NLS-1$
    );
  }

  @Test
  public void testNotFunction() {
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
  public void testBooleanFunctions() {
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
        ,"1" //$NON-NLS-1$
      );
    handleFormula(getOrdersModel(), "MSSQL", //$NON-NLS-1$ 
        "FALSE()" //$NON-NLS-1$
        ,"0" //$NON-NLS-1$
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
  public void testDateFunctionNow() {
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
  public void testDateFunctionDate() {

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
  public void testDateFunctionDateValue() {
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
  public void testWhereCondition() {
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
  public void testWhereConditionWithIN() {
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
  public void testWhereConditionWithLIKE() {
    handleFormula(getOrdersModel(), "Hypersonic", 
        "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%US%\")", //$NON-NLS-1$

        // expected hypersonic sql
        "BT_CUSTOMERS.COUNTRY  LIKE '%US%'" //$NON-NLS-1$
    );
  }

  @Test
  public void testLike() {
    handleFormula(getOrdersModel(), "Hypersonic", //$NON-NLS-1$ 
        "LIKE([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY];\"%\")" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  LIKE '%'" //$NON-NLS-1$
    );
  }

  @Test
  public void testCase() {
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
  public void testCoalesce() {
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
  public void testNoFunction() {
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
  public void testMultiTableColumnFormulas() {
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
  public void testMultiTableColumnFormulasAggregate() {
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
  public void testMultiTableColumnFormulasAggregate2() {
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
  public void testContainsFunction() {

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
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA' + '%'" //$NON-NLS-1$
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
  public void testBeginsWithFunction() {

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
        , "BT_CUSTOMERS.COUNTRY  LIKE 'AMERICA' + '%'" //$NON-NLS-1$
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
  public void testEndsWithFunction() {

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
        , "BT_CUSTOMERS.COUNTRY  LIKE '%' + 'AMERICA'" //$NON-NLS-1$
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
  public void testISNAFunction() {

    handleFormula(getOrdersModel(), "Oracle", //$NON-NLS-1$
        "ISNA([BT_CUSTOMERS.BC_CUSTOMERS_COUNTRY])" //$NON-NLS-1$
        , "BT_CUSTOMERS.COUNTRY  IS NULL" //$NON-NLS-1$
    );
  }
  
}
