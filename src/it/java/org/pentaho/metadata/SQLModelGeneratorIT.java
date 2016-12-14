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

import static org.junit.Assert.*;

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
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
import org.pentaho.pms.MetadataTestBase;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.MQLQueryImpl;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;

@SuppressWarnings( "deprecation" )
public class SQLModelGeneratorIT {

  private static Domain domain;

  @BeforeClass
  public static void initKettle() {
    String query = "select customername from customers where customernumber < 171";
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      MetadataTestBase.initKettleEnvironment();

      connection = DriverManager.getConnection( "jdbc:hsqldb:file:target/it-classes/org/pentaho/metadata/sampledata;readonly=true;files_readonly=true;shutdown=true", "sa", "" );
      stmt = connection.createStatement();
      stmt.setMaxRows( 5 );
      rs = stmt.executeQuery( query );
      ResultSetMetaData metadata = rs.getMetaData();
      String[] columnHeaders = new String[metadata.getColumnCount()];
      int[] columnTypes = new int[metadata.getColumnCount()];
      columnHeaders = getColumnNames( metadata );
      columnTypes = getColumnTypes( metadata );
      SQLModelGenerator generator =
          new SQLModelGenerator( "newdatasource", "SampleData", "Hypersonic", columnTypes, columnHeaders, query,
              true, asList( "suzy" ), asList( "Authenticated" ), 31, "joe" );
      domain = generator.generate();
    } catch ( KettleException e ) {
      e.printStackTrace();
      fail();
    } catch ( SQLException e ) {
      e.printStackTrace();
      fail();
    } catch ( SQLModelGeneratorException e ) {
      e.printStackTrace();
      fail();
    } finally {
      try {
        if ( rs != null ) {
          rs.close();
        }
        if ( stmt != null ) {
          stmt.close();
        }
        if ( connection != null ) {
          connection.close();
        }
      } catch ( SQLException e ) {
        e.printStackTrace();
        fail( "Could not close resource" );
      }
    }
  }

  @Test
  public void testSQLModelGenerator() {
    SerializationService service = new SerializationService();
    String xml = service.serializeDomain( domain );
    Domain domain2 = service.deserializeDomain( xml );
    assertEquals( 1, domain2.getPhysicalModels().size() );
  }

  @Test
  public void testToLegacy() throws ObjectAlreadyExistsException {
    if ( !Props.isInitialized() ) {
      Props.init( Props.TYPE_PROPERTIES_EMPTY );
    }
    SchemaMeta meta = ThinModelConverter.convertToLegacy( domain );
    String locale = Locale.getDefault().toString();
    // verify conversion worked.
    BusinessModel model = meta.findModel( "MODEL_1" );
    assertNotNull( model );
    String local = model.getName( locale );
    assertEquals( "newdatasource", model.getName( locale ) );
    BusinessCategory cat =  model.getRootCategory().findBusinessCategory( Settings.getBusinessCategoryIDPrefix() + "newdatasource" );
    assertNotNull( cat );
    assertEquals( "newdatasource", cat.getName( locale ) );
    assertEquals( 1, cat.getBusinessColumns().size() );
    // this tests the inheritance of physical cols made it through
    BusinessColumn col = cat.getBusinessColumn( 0 );
    assertEquals( "CUSTOMERNAME", col.getName( locale ) );
    assertNotNull( col.getBusinessTable() );
    assertEquals( "LOGICAL_TABLE_1", col.getBusinessTable().getId() );
    assertEquals( col.getDataType(), DataTypeSettings.STRING );
    assertEquals( "select customername from customers where customernumber < 171", col.getBusinessTable().getTargetTable() );
    assertEquals( "select customername from customers where customernumber < 171", col.getPhysicalColumn().getTable().getTargetTable() );
    assertEquals( "CUSTOMERNAME", col.getPhysicalColumn().getFormula() );
    assertEquals( false, col.getPhysicalColumn().isExact() );
  }

  @Test
  public void testQueryXmlSerialization() throws PentahoMetadataException {
    LogicalModel model = domain.findLogicalModel( "MODEL_1" );
    Query query = new Query( domain, model );
    Category category = model.findCategory( Settings.getBusinessCategoryIDPrefix() + "newdatasource" );
    LogicalColumn column = category.findLogicalColumn( "bc_CUSTOMERNAME" );
    query.getSelections().add( new Selection( category, column, null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "[CATEGORY.bc_CUSTOMERNAME] = \"bob\"" ) );
    query.getOrders().add( new Order( new Selection( category, column, null ), Order.Type.ASC ) );

    QueryXmlHelper helper = new QueryXmlHelper();
    String xml = helper.toXML( query );
    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    try {
      repo.storeDomain( domain, true );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
    Query newQuery = null;
    newQuery = helper.fromXML( repo, xml );
    // verify that when we serialize and deserialize, the xml stays the same.
    assertEquals( xml, helper.toXML( newQuery ) );
  }

  @Test
  public void testQueryConversion() throws Exception {
    LogicalModel model = domain.findLogicalModel( "MODEL_1" );
    Query query = new Query( domain, model );

    Category category = model.findCategory( Settings.getBusinessCategoryIDPrefix() + "newdatasource" );
    LogicalColumn column = category.findLogicalColumn( "bc_CUSTOMERNAME" );
    query.getSelections().add( new Selection( category, column, null ) );
    query.getConstraints().add( new Constraint( CombinationType.AND, "[bc_newdatasource.bc_CUSTOMERNAME] = \"bob\"" ) );
    query.getOrders().add( new Order( new Selection( category, column, null ), Order.Type.ASC ) );
    MQLQueryImpl impl = null;
    try {
      impl = ThinModelConverter.convertToLegacy( query, null );
    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }
    assertNotNull( impl );
    String queryString = impl.getQuery().getQuery();
    // System.out.println(queryString);
    TestHelper.assertEqualsIgnoreWhitespaces(
        "SELECT DISTINCT \n  LOGICAL_TABLE_1.CUSTOMERNAME AS COL0\n  FROM \n"
        + " (select customername from customers where customernumber < 171) LOGICAL_TABLE_1\n  WHERE \n"
        + " (\n  (\n  LOGICAL_TABLE_1.CUSTOMERNAME  = 'bob'\n  )\n  )\n  ORDER BY \n  COL0\n",
        queryString );
  }

  /**
   * The following method returns an array of String(java.sql.Types) containing the column types for a given
   * ResultSetMetaData object.
   */
  private String[] getColumnTypesNames( ResultSetMetaData resultSetMetaData ) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String[] columnTypes = new String[columnCount];
    for ( int colIndex = 1; colIndex <= columnCount; colIndex++ ) {
      columnTypes[colIndex - 1] = resultSetMetaData.getColumnTypeName( colIndex );
    }
    return columnTypes;
  }

  /**
   * The following method returns an array of strings containing the column names for a given ResultSetMetaData object.
   */
  public static String[] getColumnNames( ResultSetMetaData resultSetMetaData ) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    String[] columnNames = new String[columnCount];
    for ( int colIndex = 1; colIndex <= columnCount; colIndex++ ) {
      columnNames[colIndex - 1] = resultSetMetaData.getColumnName( colIndex );
    }
    return columnNames;
  }

  /**
   * The following method returns an array of int(java.sql.Types) containing the column types for a given
   * ResultSetMetaData object.
   */
  public static int[] getColumnTypes( ResultSetMetaData resultSetMetaData ) throws SQLException {
    int columnCount = resultSetMetaData.getColumnCount();
    int[] returnValue = new int[columnCount];
    for ( int colIndex = 1; colIndex <= columnCount; colIndex++ ) {
      returnValue[colIndex - 1] = resultSetMetaData.getColumnType( colIndex );
    }
    return returnValue;
  }
}
