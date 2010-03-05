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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.query.model.CombinationType;
import org.pentaho.metadata.query.model.Constraint;
import org.pentaho.metadata.query.model.Order;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.SQLModelGenerator;
import org.pentaho.metadata.util.SQLModelGeneratorException;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.util.Settings;

@SuppressWarnings("deprecation")
public class SQLModelGeneratorTest {

  @BeforeClass
  public static void initKettle() throws Exception {
    KettleEnvironment.init(false);
  }
  
  @Test
  public void testSQLModelGenerator() {
    // basic tests
    try {
      SerializationService service = new SerializationService();

      String xml = service.serializeDomain(generateModel());

      System.out.println(xml);

      Domain domain2 = service.deserializeDomain(xml);

      Assert.assertEquals(1, domain2.getPhysicalModels().size());
    } catch (SQLModelGeneratorException smge) {
      Assert.fail();
    }
  }

  @Test
  public void testToLegacy() {
    if (!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
    Domain domain = null;
    SchemaMeta meta = null;
    try {
      domain = generateModel();
      meta = ThinModelConverter.convertToLegacy(domain);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

    String locale = Locale.getDefault().toString();

    // verify conversion worked.
    BusinessModel model = meta.findModel("MODEL_1");
    Assert.assertNotNull(model);
    String local = model.getName(locale);
    Assert.assertEquals("newdatasource", model.getName(locale));
    BusinessCategory cat = model.getRootCategory().findBusinessCategory(
        Settings.getBusinessCategoryIDPrefix() + "newdatasource");
    Assert.assertNotNull(cat);
    Assert.assertEquals("newdatasource", cat.getName(locale));

    Assert.assertEquals(1, cat.getBusinessColumns().size());

    // this tests the inheritance of physical cols made it through
    BusinessColumn col = cat.getBusinessColumn(0);
    Assert.assertEquals("CUSTOMERNAME", col.getName(locale));
    Assert.assertNotNull(col.getBusinessTable());
    Assert.assertEquals("LOGICAL_TABLE_1", col.getBusinessTable().getId());

    Assert.assertEquals(col.getDataType(), DataTypeSettings.STRING);
    Assert.assertEquals("select customername from customers where customernumber < 171", col.getBusinessTable()
        .getTargetTable());
    Assert.assertEquals("select customername from customers where customernumber < 171", col.getPhysicalColumn()
        .getTable().getTargetTable());
    Assert.assertEquals("CUSTOMERNAME", col.getPhysicalColumn().getFormula());
    Assert.assertEquals(false, col.getPhysicalColumn().isExact());

  }

  @Test
  public void testQueryXmlSerialization() {
    try {
      Domain domain = generateModel();
      LogicalModel model = domain.findLogicalModel("MODEL_1");
      Query query = new Query(domain, model);

      Category category = model.findCategory(Settings.getBusinessCategoryIDPrefix() + "newdatasource");
      LogicalColumn column = category.findLogicalColumn("bc_CUSTOMERNAME");
      query.getSelections().add(new Selection(category, column, null));

      query.getConstraints().add(new Constraint(CombinationType.AND, "[CATEGORY.bc_CUSTOMERNAME] = \"bob\""));

      query.getOrders().add(new Order(new Selection(category, column, null), Order.Type.ASC));

      QueryXmlHelper helper = new QueryXmlHelper();
      String xml = helper.toXML(query);

      InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
      try {
        repo.storeDomain(domain, true);
      } catch (Exception e) {
        e.printStackTrace();
        Assert.fail();
      }
      Query newQuery = null;
      newQuery = helper.fromXML(repo, xml);
      // verify that when we serialize and deserialize, the xml stays the same. 
      Assert.assertEquals(xml, helper.toXML(newQuery));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void testQueryConversion() throws Exception {
    Domain domain = generateModel();
    LogicalModel model = domain.findLogicalModel("MODEL_1");
    Query query = new Query(domain, model);

    Category category = model.findCategory(Settings.getBusinessCategoryIDPrefix() + "newdatasource");
    LogicalColumn column = category.findLogicalColumn("bc_CUSTOMERNAME");
    query.getSelections().add(new Selection(category, column, null));

    query.getConstraints().add(new Constraint(CombinationType.AND, "[bc_newdatasource.bc_CUSTOMERNAME] = \"bob\""));

    query.getOrders().add(new Order(new Selection(category, column, null), Order.Type.ASC));
    MQLQueryImpl impl = null;
    try {
      impl = ThinModelConverter.convertToLegacy(query, null);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertNotNull(impl);
    Assert.assertEquals(

    "SELECT DISTINCT \n" + "          LOGICAL_TABLE_1.CUSTOMERNAME AS COL0\n" + "FROM \n"
        + "          (select customername from customers where customernumber < 171) LOGICAL_TABLE_1\n" + "WHERE \n"
        + "        (\n" + "          (\n" + "              LOGICAL_TABLE_1.CUSTOMERNAME  = 'bob'\n" + "          )\n"
        + "        )\n" + "ORDER BY \n" + "          COL0\n",

    impl.getQuery().getQuery());

  }

  private Connection getDataSourceConnection(String driverClass, String name, String username, String password,
      String url) throws Exception {
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

  private Domain generateModel() throws SQLModelGeneratorException{
    String query = "select customername from customers where customernumber < 171";
    Connection connection = null;
    Boolean securityEnabled = true;
    List<String> users = new ArrayList<String>();
    users.add("suzy");
    List<String> roles = new ArrayList<String>();
    roles.add("Authenticated");
    int defaultAcls = 31;
    String createdBy = "joe";
    String[] columnHeaders = null;
    int[] columnTypes = null;
    Object[][] rawdata = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      connection = getDataSourceConnection("org.hsqldb.jdbcDriver","SampleData"
          ,"pentaho_user", "password"
            ,"jdbc:hsqldb:file:test/solution/system/data/sampledata");
    stmt = connection.createStatement();
    stmt.setMaxRows(5);
    rs = stmt.executeQuery(query);
    ResultSetMetaData metadata = rs.getMetaData();
    columnHeaders = new String[metadata.getColumnCount()];
    columnTypes = new int[metadata.getColumnCount()];
    columnHeaders = getColumnNames(metadata);
    columnTypes = getColumnTypes(metadata);
    } catch(Exception e) {
      e.printStackTrace();
      
    } finally {
      try {
        closeAll(connection, stmt, rs, true);
      } catch (SQLException e) {
      }
    }
    SQLModelGenerator generator = new SQLModelGenerator("newdatasource", "SampleData", columnTypes, columnHeaders, query, securityEnabled, users, roles, defaultAcls, createdBy);
    return generator.generate(); 
  }

  /**
   * The following method returns an array of String(java.sql.Types) containing the column types for
   * a given ResultSetMetaData object.
   */
  private String[] getColumnTypesNames(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String[] columnTypes = new String[columnCount];

    for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
      columnTypes[colIndex - 1] = resultSetMetaData.getColumnTypeName(colIndex);
    }

    return columnTypes;
  }

  /**
   * The following method returns an array of strings containing the column names for
   * a given ResultSetMetaData object.
   */
  public String[] getColumnNames(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String columnNames[] = new String[columnCount];

    for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
      columnNames[colIndex - 1] = resultSetMetaData.getColumnName(colIndex);
    }

    return columnNames;
  }

  /**
   * The following method returns an array of int(java.sql.Types) containing the column types for
   * a given ResultSetMetaData object.
   */
  public int[] getColumnTypes(ResultSetMetaData resultSetMetaData) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    int[] returnValue = new int[columnCount];
    for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
      returnValue[colIndex - 1] = resultSetMetaData.getColumnType(colIndex);
    }

    return returnValue;
  }

  private void closeAll(Connection conn, Statement stmt, ResultSet rs, boolean throwsException) throws SQLException {
    SQLException rethrow = null;
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException ignored) {
        rethrow = ignored;
      }
    }
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException ignored) {
        rethrow = ignored;
      }
    }
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException ignored) {
        rethrow = ignored;
      }
    }
    if (throwsException && rethrow != null) {
      throw rethrow;
    }

  }

}
