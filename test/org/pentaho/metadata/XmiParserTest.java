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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.io.ReaderInputStream;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.olap.OlapCube;
import org.pentaho.metadata.model.olap.OlapDimension;
import org.pentaho.metadata.model.olap.OlapDimensionUsage;
import org.pentaho.metadata.model.olap.OlapHierarchy;
import org.pentaho.metadata.model.olap.OlapHierarchyLevel;
import org.pentaho.metadata.model.olap.OlapMeasure;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.MetadataTestBase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings( "nls" )
public class XmiParserTest {

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Test
  public void testXmiParser() throws Exception {
    Domain domain = new XmiParser().parseXmi( new FileInputStream( "samples/steelwheels.xmi" ) );
    Assert.assertEquals( 6, domain.getConcepts().size() );
    Assert.assertEquals( 1, domain.getPhysicalModels().size() );
    Assert.assertEquals( 3, domain.getLogicalModels().size() );

    Assert.assertEquals( 2, domain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    Assert.assertEquals( 8, domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().size() );
    Assert.assertEquals( "BC_EMPLOYEES_EMPLOYEENUMBER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    Assert.assertEquals( 1, domain.getLogicalModels().get( 0 ).getLogicalRelationships().size() );

    Assert.assertEquals( "EMPLOYEENUMBER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getId() );
    Assert.assertEquals( "PT_EMPLOYEES", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );
    Assert.assertNotNull( domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().get( 0 )
        .getPhysicalColumn().getPhysicalTable().getPhysicalModel() );

    Assert.assertEquals( 2, domain.getLogicalModels().get( 0 ).getCategories().size() );
    Assert.assertEquals( 9, domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );
    Assert.assertEquals( "BC_OFFICES_TERRITORY", domain.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    Assert.assertEquals( "TERRITORY", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getId() );
    Assert.assertEquals( "PT_OFFICES", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );

    @SuppressWarnings( "unchecked" )
    List<AggregationType> aggTypes =
        (List<AggregationType>) domain.findLogicalModel( "BV_ORDERS" ).findCategory( "CAT_ORDERS" ).findLogicalColumn(
            "BC_ORDERS_ORDERNUMBER" ).getProperty( "aggregation_list" );
    Assert.assertNotNull( aggTypes );
    Assert.assertEquals( 2, aggTypes.size() );
    Assert.assertEquals( aggTypes.get( 0 ), AggregationType.COUNT );
    Assert.assertEquals( aggTypes.get( 1 ), AggregationType.COUNT_DISTINCT );

    // verify that inheritance is working
    Assert.assertEquals( "$#,##0.00;($#,##0.00)", domain.findLogicalModel( "BV_ORDERS" ).findCategory( "CAT_ORDERS" )
        .findLogicalColumn( "BC_ORDERDETAILS_TOTAL" ).getProperty( "mask" ) );

  }

  @Test
  public void testXmiGenerator() throws Exception {
    // String str = new XmiParser().generateXmi(new Domain());
    // System.out.println(str);
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "samples/steelwheels.xmi" ) );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes( "UTF-8" ) );
    Domain domain2 = parser.parseXmi( is );

    SerializationService serializer = new SerializationService();

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    // note: this does not verify security objects at this time
    Assert.assertEquals( xml1, xml2 );
  }

  @Test
  public void testMissingDescriptionRef() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "test-res/missing_ref.xmi" ) );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    ByteArrayInputStream is2 = new ByteArrayInputStream( parser.generateXmi( domain2 ).getBytes() );
    Domain domain3 = parser.parseXmi( is2 );

    SerializationService serializer = new SerializationService();

    String xml1 = serializeWithOrderedHashmaps( domain2 );
    String xml2 = serializeWithOrderedHashmaps( domain3 );

    // note: this does not verify security objects at this time
    Assert.assertEquals( xml1, xml2 );
  }

  public String serializeWithOrderedHashmaps( Domain domain ) {
    XStream xstream = new XStream( new DomDriver() );
    xstream.registerConverter( new Converter() {

      public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        // TODO Auto-generated method stub
        writer.startNode( "hashmap" );
        HashMap unknownMap = (HashMap) source;
        if ( unknownMap.size() > 0 ) {
          if ( unknownMap.keySet().iterator().next() instanceof String ) {
            @SuppressWarnings( "unchecked" )
            HashMap<String, Object> map = (HashMap<String, Object>) source;
            Set<String> ordered = new TreeSet<String>( map.keySet() );
            for ( String key : ordered ) {
              writer.startNode( "entry" );
              writer.addAttribute( "key", key );
              Object obj = map.get( key );
              if ( obj == null ) {
                System.out.println( "NULL OBJ FOR " + key );
              } else {
                context.convertAnother( map.get( key ) );
              }
              writer.endNode();
            }
          }
        }
        writer.endNode();
      }

      public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        // TODO Auto-generated method stub
        return null;
      }

      public boolean canConvert( Class type ) {
        return type.getName().equals( "java.util.HashMap" );
      }

    } );
    return xstream.toXML( domain );

  }

  @Test
  public void testXmiLegacyConceptProperties() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "test-res/all_concept_properties.xmi" ) );
    Assert.assertEquals( 2, domain.getConcepts().size() );
    Assert.assertEquals( 1, domain.getPhysicalModels().size() );
    Assert.assertEquals( 1, domain.getLogicalModels().size() );

    Assert.assertEquals( "http://localhost:8080/pentaho/ServiceAction", domain
        .getChildProperty( "LEGACY_EVENT_SECURITY_SERVICE_URL" ) );

    Assert.assertEquals( 1, domain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    Assert.assertEquals( 29, domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().size() );
    Assert.assertEquals( "BC_CUSTOMER_CUSTOMER_ID", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    Assert.assertEquals( 0, domain.getLogicalModels().get( 0 ).getLogicalRelationships().size() );

    Assert.assertEquals( "customer_id", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getId() );
    Assert.assertEquals( "PT_CUSTOMER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );
    Assert.assertNotNull( domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().get( 0 )
        .getPhysicalColumn().getPhysicalTable().getPhysicalModel() );

    Assert.assertEquals( 1, domain.getLogicalModels().get( 0 ).getCategories().size() );
    Assert.assertEquals( 29, domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );
    Assert.assertEquals( "BC_CUSTOMER_FULLNAME", domain.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    Assert.assertEquals( "fullname", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getId() );
    Assert.assertEquals( "PT_CUSTOMER", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );

    String xmi = parser.generateXmi( domain );

    Domain domain2 = parser.parseXmi( new ReaderInputStream( new StringReader( xmi ) ) );
    SqlDataSource ds = ( (SqlPhysicalModel) domain.getPhysicalModels().get( 0 ) ).getDatasource();
    SqlDataSource ds2 = ( (SqlPhysicalModel) domain2.getPhysicalModels().get( 0 ) ).getDatasource();

    Assert.assertEquals( "http://localhost:8080/pentaho/ServiceAction", domain2
        .getChildProperty( "LEGACY_EVENT_SECURITY_SERVICE_URL" ) );

    Assert.assertEquals( "foodmart", ds.getDatabaseName() );
    Assert.assertEquals( ds.getDatabaseName(), ds2.getDatabaseName() );

    Assert.assertEquals( "MYSQL", ds.getDialectType() );
    Assert.assertEquals( ds.getDialectType(), ds2.getDialectType() );

    Assert.assertEquals( "NATIVE", ds.getType().toString() );
    Assert.assertEquals( ds.getType(), ds2.getType() );

    Assert.assertEquals( "localhost", ds.getHostname() );
    Assert.assertEquals( ds.getHostname(), ds2.getHostname() );

    Assert.assertEquals( "3306", ds.getPort() );
    Assert.assertEquals( ds.getPort(), ds2.getPort() );

    Assert.assertEquals( "foodmart", ds.getUsername() );
    Assert.assertEquals( ds.getUsername(), ds2.getUsername() );

    Assert.assertEquals( "foodmart", ds.getPassword() );
    Assert.assertEquals( ds.getPassword(), ds2.getPassword() );

    Assert.assertEquals( 9, ds.getAttributes().size() );
    Assert.assertEquals( ds.getAttributes().size(), ds2.getAttributes().size() );

    Assert.assertEquals( "Y", ds2.getAttributes().get( "QUOTE_ALL_FIELDS" ) );

    // test DatabaseMeta conversion
    DatabaseMeta meta = ThinModelConverter.convertToLegacy( "test", ds );

    Assert.assertEquals( "test", meta.getName() );
    Assert.assertEquals( "MYSQL", meta.getDatabaseTypeDesc() );
    Assert.assertEquals( "Native", meta.getAccessTypeDesc() );
    Assert.assertEquals( "localhost", meta.getHostname() );
    Assert.assertEquals( "3306", meta.getDatabasePortNumberString() );
    Assert.assertEquals( "foodmart", meta.getDatabaseName() );
    Assert.assertEquals( "foodmart", meta.getUsername() );
    Assert.assertEquals( "foodmart", meta.getPassword() );
    Assert.assertTrue( meta.isQuoteAllFields() );

    // Verify that RowLevelSecurity is in the xmi
    Assert
        .assertTrue( xmi
            .indexOf( "&lt;row-level-security type=&quot;global&quot;&gt;&lt;formula&gt;&lt;![CDATA[TRUE()]]&gt;&lt;/formula&gt;&lt;entries&gt;&lt;/entries&gt;&lt;/row-level-security&gt;" ) >= 0 );

    // Verify that the SqlDatasource is to and from successfully
  }

  @Test
  public void testComplexJoinsInXmi() throws Exception {

    // This unit test loads an XMI domain containing
    // a complex join, and also executes a basic query
    // verifying that the complex join is resolved.

    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "samples/complex_join.xmi" ) );
    domain.setId( "test domain" );
    Assert.assertTrue( domain.getLogicalModels().get( 0 ).getLogicalRelationships().get( 0 ).isComplex() );
    Assert.assertEquals( "[BT_ORDERS_ORDERS.BC_ORDERS_ORDERNUMBER]=[BT_ORDERFACT_ORDERFACT.BC_ORDERFACT_ORDERNUMBER]",
        domain.getLogicalModels().get( 0 ).getLogicalRelationships().get( 0 ).getComplexJoin() );

    String mql =
        "<mql>" + "<domain_type>relational</domain_type>" + "<domain_id>test domain</domain_id>"
            + "<model_id>BV_MODEL_1</model_id>" + "<model_name>Model 1</model_name>" + "<options>"
            + "  <disable_distinct>false</disable_distinct>" + "</options>" + "<selections>" + "  <selection>"
            + "    <view>BC_ORDERS</view>" + "    <column>BC_ORDERS_STATUS</column>"
            + "    <aggregation>none</aggregation>" + "  </selection>" + "  <selection>"
            + "    <view>BC_ORDERFACT</view>" + "    <column>BC_ORDERFACT_PRODUCTCODE</column>"
            + "    <aggregation>none</aggregation>" + "  </selection>" + "</selections>" + "<constraints/>"
            + "<orders/>" + "</mql>";

    InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
    repo.storeDomain( domain, false );
    QueryXmlHelper helper = new QueryXmlHelper();
    Query query = helper.fromXML( repo, mql );

    SqlGenerator generator = new SqlGenerator();
    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    MappedQuery queryObj = generator.generateSql( query, "en_US", repo, databaseMeta );
    // TestHelper.printOutJava(queryObj.getQuery());
    TestHelper.assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          BT_ORDERS_ORDERS.STATUS AS COL0\n"
        + "         ,BT_ORDERFACT_ORDERFACT.PRODUCTCODE AS COL1\n" + "FROM \n"
        + "          ORDERFACT BT_ORDERFACT_ORDERFACT\n" + "         ,ORDERS BT_ORDERS_ORDERS\n" + "WHERE \n"
        + "          (  BT_ORDERS_ORDERS.ORDERNUMBER  =  BT_ORDERFACT_ORDERFACT.ORDERNUMBER  )\n", queryObj.getQuery() );

  }

  @Test
  public void testMissingLocale() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "test-res/missing_locale.xmi" ) );
    Assert.assertEquals( 0, domain.getLocaleCodes().length );
  }

  @Test
  public void testPartialMetadataFile() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "test-res/partial_metadata.xmi" ) );
    Assert.assertEquals( 1, domain.getPhysicalModels().size() );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testOlapMetadataFile() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( new FileInputStream( "test-res/example_olap.xmi" ) );
    Assert.assertEquals( 1, domain.getPhysicalModels().size() );

    Assert.assertNotNull( domain.getLogicalModels().get( 0 ).getProperty( "olap_dimensions" ) );
    List<OlapDimension> dimensions =
        (List<OlapDimension>) domain.getLogicalModels().get( 0 ).getProperty( "olap_dimensions" );
    Assert.assertEquals( 2, dimensions.size() );

    OlapDimension dim1 = dimensions.get( 0 );
    OlapDimension dim2 = dimensions.get( 1 );

    Assert.assertEquals( "fname", dim1.getName() );

    Assert.assertEquals( 1, dim1.getHierarchies().size() );
    OlapHierarchy hier1 = dim1.getHierarchies().get( 0 );

    Assert.assertEquals( "fname", hier1.getName() );

    Assert.assertNotNull( hier1.getLogicalTable() );
    Assert.assertEquals( "BT_CUSTOMER2_CUSTOMER2", hier1.getLogicalTable().getId() );
    Assert.assertNotNull( hier1.getPrimaryKey() );
    Assert.assertEquals( "LC_CUSTOMER2_FNAME", hier1.getPrimaryKey().getId() );

    Assert.assertEquals( 1, hier1.getHierarchyLevels().size() );

    OlapHierarchyLevel level = hier1.getHierarchyLevels().get( 0 );

    Assert.assertEquals( "fname", level.getName() );
    Assert.assertEquals( 0, level.getLogicalColumns().size() );
    Assert.assertEquals( "LC_CUSTOMER2_FNAME", level.getReferenceColumn().getId() );
    Assert.assertEquals( hier1, level.getOlapHierarchy() );

    Assert.assertEquals( "lname - D", dim2.getName() );
    Assert.assertEquals( 2, dim2.getHierarchies().size() );

    OlapHierarchy hier2 = dim2.getHierarchies().get( 0 );

    Assert.assertEquals( "lname - H", hier2.getName() );

    Assert.assertEquals( 2, hier2.getHierarchyLevels().size() );
    OlapHierarchyLevel level2 = hier2.getHierarchyLevels().get( 0 );
    OlapHierarchyLevel level3 = hier2.getHierarchyLevels().get( 1 );

    Assert.assertEquals( 4, level3.getLogicalColumns().size() );
    Assert.assertEquals( false, level3.isHavingUniqueMembers() );

    OlapHierarchy hier3 = dim2.getHierarchies().get( 1 );

    Assert.assertEquals( "test", hier3.getName() );

    Assert.assertNotNull( domain.getLogicalModels().get( 0 ).getProperty( "olap_cubes" ) );
    List<OlapCube> cubes = (List<OlapCube>) domain.getLogicalModels().get( 0 ).getProperty( "olap_cubes" );
    Assert.assertEquals( 1, cubes.size() );

    OlapCube cube = cubes.get( 0 );
    Assert.assertEquals( "customer2 Table", cube.getName() );
    Assert.assertEquals( 1, cube.getOlapDimensionUsages().size() );
    OlapDimensionUsage usage = cube.getOlapDimensionUsages().get( 0 );
    Assert.assertEquals( "fname", usage.getName() );
    Assert.assertEquals( dim1, usage.getOlapDimension() );

    Assert.assertEquals( 1, cube.getOlapMeasures().size() );

    OlapMeasure measure = cube.getOlapMeasures().get( 0 );
    Assert.assertEquals( "num_children_at_home", measure.getName() );
    Assert.assertEquals( "LC_CUSTOMER2_NUM_CHILDREN_AT_HOME", measure.getLogicalColumn().getId() );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    SerializationService serializer = new SerializationService();

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    // note: this does not verify security objects at this time
    Assert.assertEquals( xml1, xml2 );

  }

}
