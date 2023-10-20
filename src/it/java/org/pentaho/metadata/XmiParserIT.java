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
 * Copyright (c) 2016-2018 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.metadata;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.io.ReaderInputStream;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.olap.OlapCalculatedMember;
import org.pentaho.metadata.model.olap.OlapCube;
import org.pentaho.metadata.model.olap.OlapDimension;
import org.pentaho.metadata.model.olap.OlapDimensionUsage;
import org.pentaho.metadata.model.olap.OlapHierarchy;
import org.pentaho.metadata.model.olap.OlapHierarchyLevel;
import org.pentaho.metadata.model.olap.OlapMeasure;
import org.pentaho.metadata.model.olap.OlapRole;
import org.pentaho.metadata.query.impl.sql.MappedQuery;
import org.pentaho.metadata.query.impl.sql.SqlGenerator;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.MetadataTestBase;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.pentaho.metadata.util.Util.validateId;

@SuppressWarnings( "nls" )
public class XmiParserIT {

  private XmiParser parser;

  @BeforeClass
  public static void initKettle() throws Exception {
    MetadataTestBase.initKettleEnvironment();
  }

  @Before
  public void setUp() {
    parser = new XmiParser();
  }

  @Test
  public void testXmiParser() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) );
    assertEquals( 6, domain.getConcepts().size() );
    assertEquals( 1, domain.getPhysicalModels().size() );
    assertEquals( 3, domain.getLogicalModels().size() );

    assertEquals( 2, domain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    assertEquals( 8, domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().size() );
    assertEquals( "BC_EMPLOYEES_EMPLOYEENUMBER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    assertEquals( 1, domain.getLogicalModels().get( 0 ).getLogicalRelationships().size() );

    assertEquals( "EMPLOYEENUMBER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getId() );
    assertEquals( "PT_EMPLOYEES", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );
    assertNotNull( domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().get( 0 )
        .getPhysicalColumn().getPhysicalTable().getPhysicalModel() );

    assertEquals( 2, domain.getLogicalModels().get( 0 ).getCategories().size() );
    assertEquals( 9, domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );
    assertEquals( "BC_OFFICES_TERRITORY", domain.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    assertEquals( "TERRITORY", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getId() );
    assertEquals( "PT_OFFICES", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );

    @SuppressWarnings( "unchecked" )
    List<AggregationType> aggTypes =
        (List<AggregationType>) domain.findLogicalModel( "BV_ORDERS" ).findCategory( "CAT_ORDERS" ).findLogicalColumn(
            "BC_ORDERS_ORDERNUMBER" ).getProperty( "aggregation_list" );
    assertNotNull( aggTypes );
    assertEquals( 2, aggTypes.size() );
    assertEquals( AggregationType.COUNT, aggTypes.get( 0 ) );
    assertEquals( AggregationType.COUNT_DISTINCT, aggTypes.get( 1 ) );

    // verify that inheritance is working
    assertEquals( "$#,##0.00;($#,##0.00)", domain.findLogicalModel( "BV_ORDERS" ).findCategory( "CAT_ORDERS" )
        .findLogicalColumn( "BC_ORDERDETAILS_TOTAL" ).getProperty( "mask" ) );
  }

  @Test
  public void testXmiGenerator() throws Exception {
    // String str = new XmiParser().generateXmi(new Domain());
    // System.out.println(str);
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes( StandardCharsets.UTF_8 ) );
    Domain domain2 = parser.parseXmi( is );

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    // note: this does not verify security objects at this time
    assertEquals( xml1, xml2 );
  }

  @Test
  public void testMissingDescriptionRef() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/missing_ref.xmi" ) );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    ByteArrayInputStream is2 = new ByteArrayInputStream( parser.generateXmi( domain2 ).getBytes() );
    Domain domain3 = parser.parseXmi( is2 );

    String xml1 = serializeWithOrderedHashmaps( domain2 );
    String xml2 = serializeWithOrderedHashmaps( domain3 );

    // note: this does not verify security objects at this time
    assertEquals( xml1, xml2 );
  }

  public String serializeWithOrderedHashmaps( Domain domain ) {
    XStream xstream = SerializationService.createXStreamWithAllowedTypes( new DomDriver(), Domain.class );
    xstream.registerConverter( new Converter() {

      public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        // TODO Auto-generated method stub
        writer.startNode( "hashmap" );
        HashMap unknownMap = (HashMap) source;
        if ( !unknownMap.isEmpty() ) {
          if ( unknownMap.keySet().iterator().next() instanceof String ) {
            @SuppressWarnings( "unchecked" )
            HashMap<String, Object> map = (HashMap<String, Object>) source;
            Set<String> ordered = new TreeSet<>( map.keySet() );
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
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/all_concept_properties.xmi" ) );
    assertEquals( 2, domain.getConcepts().size() );
    assertEquals( 1, domain.getPhysicalModels().size() );
    assertEquals( 1, domain.getLogicalModels().size() );

    assertEquals( "http://localhost:8080/pentaho/ServiceAction", domain
        .getChildProperty( "LEGACY_EVENT_SECURITY_SERVICE_URL" ) );

    assertEquals( 1, domain.getLogicalModels().get( 0 ).getLogicalTables().size() );
    assertEquals( 29, domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().size() );
    assertEquals( "BC_CUSTOMER_CUSTOMER_ID", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    assertEquals( 0, domain.getLogicalModels().get( 0 ).getLogicalRelationships().size() );

    assertEquals( "customer_id", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getId() );
    assertEquals( "PT_CUSTOMER", domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 )
        .getLogicalColumns().get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );
    assertNotNull( domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns().get( 0 )
        .getPhysicalColumn().getPhysicalTable().getPhysicalModel() );

    assertEquals( 1, domain.getLogicalModels().get( 0 ).getCategories().size() );
    assertEquals( 29, domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns().size() );
    assertEquals( "BC_CUSTOMER_FULLNAME", domain.getLogicalModels().get( 0 ).getCategories().get( 0 )
        .getLogicalColumns().get( 0 ).getId() );
    assertEquals( "fullname", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getId() );
    assertEquals( "PT_CUSTOMER", domain.getLogicalModels().get( 0 ).getCategories().get( 0 ).getLogicalColumns()
        .get( 0 ).getPhysicalColumn().getPhysicalTable().getId() );

    String xmi = parser.generateXmi( domain );

    Domain domain2 = parser.parseXmi( new ReaderInputStream( new StringReader( xmi ) ) );
    SqlDataSource ds = ( (SqlPhysicalModel) domain.getPhysicalModels().get( 0 ) ).getDatasource();
    SqlDataSource ds2 = ( (SqlPhysicalModel) domain2.getPhysicalModels().get( 0 ) ).getDatasource();

    assertEquals( "http://localhost:8080/pentaho/ServiceAction", domain2
        .getChildProperty( "LEGACY_EVENT_SECURITY_SERVICE_URL" ) );

    assertEquals( "foodmart", ds.getDatabaseName() );
    assertEquals( ds.getDatabaseName(), ds2.getDatabaseName() );

    assertEquals( "MYSQL", ds.getDialectType() );
    assertEquals( ds.getDialectType(), ds2.getDialectType() );

    assertEquals( "NATIVE", ds.getType().toString() );
    assertEquals( ds.getType(), ds2.getType() );

    assertEquals( "localhost", ds.getHostname() );
    assertEquals( ds.getHostname(), ds2.getHostname() );

    assertEquals( "3306", ds.getPort() );
    assertEquals( ds.getPort(), ds2.getPort() );

    assertEquals( "foodmart", ds.getUsername() );
    assertEquals( ds.getUsername(), ds2.getUsername() );

    assertEquals( "foodmart", ds.getPassword() );
    assertEquals( ds.getPassword(), ds2.getPassword() );

    assertEquals( 9, ds.getAttributes().size() );
    assertEquals( ds.getAttributes().size(), ds2.getAttributes().size() );

    assertEquals( "Y", ds2.getAttributes().get( "QUOTE_ALL_FIELDS" ) );

    // test DatabaseMeta conversion
    DatabaseMeta meta = ThinModelConverter.convertToLegacy( "test", ds );

    assertEquals( "test", meta.getName() );
    assertEquals( "MYSQL", meta.getDatabaseTypeDesc() );
    assertEquals( "Native", meta.getAccessTypeDesc() );
    assertEquals( "localhost", meta.getHostname() );
    assertEquals( "3306", meta.getDatabasePortNumberString() );
    assertEquals( "foodmart", meta.getDatabaseName() );
    assertEquals( "foodmart", meta.getUsername() );
    assertEquals( "foodmart", meta.getPassword() );
    assertTrue( meta.isQuoteAllFields() );

    // Verify that RowLevelSecurity is in the xmi
    assertTrue( xmi.contains(
        "&lt;row-level-security type=&quot;global&quot;&gt;&lt;formula&gt;&lt;![CDATA[TRUE()]]&gt;&lt;/formula&gt;"
            + "&lt;entries&gt;&lt;/entries&gt;&lt;/row-level-security&gt;" ) );

    // Verify that the SqlDatasource is to and from successfully
  }

  @Test
  public void testComplexJoinsInXmi() throws Exception {

    // This unit test loads an XMI domain containing
    // a complex join, and also executes a basic query
    // verifying that the complex join is resolved.

    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/samples/complex_join.xmi" ) );
    domain.setId( "test domain" );
    assertTrue( domain.getLogicalModels().get( 0 ).getLogicalRelationships().get( 0 ).isComplex() );
    assertEquals( "[BT_ORDERS_ORDERS.BC_ORDERS_ORDERNUMBER]=[BT_ORDERFACT_ORDERFACT.BC_ORDERFACT_ORDERNUMBER]",
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
    TestHelper
        .assertEqualsIgnoreWhitespaces( "SELECT DISTINCT \n" + "          BT_ORDERS_ORDERS.STATUS AS COL0\n"
        + "         ,BT_ORDERFACT_ORDERFACT.PRODUCTCODE AS COL1\n" + "FROM \n"
        + "          ORDERFACT BT_ORDERFACT_ORDERFACT\n" + "         ,ORDERS BT_ORDERS_ORDERS\n" + "WHERE \n"
            + "          (  BT_ORDERS_ORDERS.ORDERNUMBER  =  BT_ORDERFACT_ORDERFACT.ORDERNUMBER  )\n", queryObj
            .getQuery() );
  }

  @Test
  public void testMissingLocale() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/missing_locale.xmi" ) );
    assertEquals( 0, domain.getLocaleCodes().length );
  }

  @Test
  public void testPartialMetadataFile() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/partial_metadata.xmi" ) );
    assertEquals( 1, domain.getPhysicalModels().size() );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testOlapMetadataFile() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/example_olap.xmi" ) );
    assertEquals( 1, domain.getPhysicalModels().size() );

    assertNotNull( domain.getLogicalModels().get( 0 ).getProperty( "olap_dimensions" ) );
    List<OlapDimension> dimensions =
        (List<OlapDimension>) domain.getLogicalModels().get( 0 ).getProperty( "olap_dimensions" );
    assertEquals( 2, dimensions.size() );

    OlapDimension dim1 = dimensions.get( 0 );
    OlapDimension dim2 = dimensions.get( 1 );

    assertEquals( "fname", dim1.getName() );

    assertEquals( 1, dim1.getHierarchies().size() );
    OlapHierarchy hier1 = dim1.getHierarchies().get( 0 );

    assertEquals( "fname", hier1.getName() );

    assertNotNull( hier1.getLogicalTable() );
    assertEquals( "BT_CUSTOMER2_CUSTOMER2", hier1.getLogicalTable().getId() );
    assertNotNull( hier1.getPrimaryKey() );
    assertEquals( "LC_CUSTOMER2_FNAME", hier1.getPrimaryKey().getId() );

    assertEquals( 1, hier1.getHierarchyLevels().size() );

    OlapHierarchyLevel level = hier1.getHierarchyLevels().get( 0 );

    assertEquals( "fname", level.getName() );
    assertEquals( 0, level.getLogicalColumns().size() );
    assertEquals( "LC_CUSTOMER2_FNAME", level.getReferenceColumn().getId() );
    assertEquals( hier1, level.getOlapHierarchy() );

    assertEquals( "lname - D", dim2.getName() );
    assertEquals( 2, dim2.getHierarchies().size() );

    OlapHierarchy hier2 = dim2.getHierarchies().get( 0 );

    assertEquals( "lname - H", hier2.getName() );

    assertEquals( 2, hier2.getHierarchyLevels().size() );
    OlapHierarchyLevel level2 = hier2.getHierarchyLevels().get( 0 );
    OlapHierarchyLevel level3 = hier2.getHierarchyLevels().get( 1 );

    assertEquals( 4, level3.getLogicalColumns().size() );
    assertFalse( level3.isHavingUniqueMembers() );

    OlapHierarchy hier3 = dim2.getHierarchies().get( 1 );

    assertEquals( "test", hier3.getName() );

    assertNotNull( domain.getLogicalModels().get( 0 ).getProperty( "olap_cubes" ) );
    List<OlapCube> cubes = (List<OlapCube>) domain.getLogicalModels().get( 0 ).getProperty( "olap_cubes" );
    assertEquals( 1, cubes.size() );

    OlapCube cube = cubes.get( 0 );
    assertEquals( "customer2 Table", cube.getName() );
    assertEquals( 1, cube.getOlapDimensionUsages().size() );
    OlapDimensionUsage usage = cube.getOlapDimensionUsages().get( 0 );
    assertEquals( "fname", usage.getName() );
    assertEquals( dim1, usage.getOlapDimension() );

    assertEquals( 1, cube.getOlapMeasures().size() );

    OlapMeasure measure = cube.getOlapMeasures().get( 0 );
    assertEquals( "num_children_at_home", measure.getName() );
    assertEquals( "LC_CUSTOMER2_NUM_CHILDREN_AT_HOME", measure.getLogicalColumn().getId() );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    // note: this does not verify security objects at this time
    assertEquals( xml1, xml2 );
  }

  @Test
  public void collisionAfterCorrectionAreResolved() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/samples/steelwheels.xmi" ) );
    LogicalTable table = domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 );

    assertTrue( table.getLogicalColumns().size() >= 2 );
    LogicalColumn col1 = table.getLogicalColumns().get( 0 );
    col1.setId( "column[x]" );

    LogicalColumn col2 = table.getLogicalColumns().get( 1 );
    col2.setId( "column{x}" );

    assertNotEquals( "Columns have different raw ids", col1.getId(), col2.getId() );
    assertEquals( "Columns have equal validated ids", validateId( col1.getId() ), validateId( col2.getId() ) );

    String xmi = parser.generateXmi( domain );
    domain = parser.parseXmi( new ByteArrayInputStream( xmi.getBytes() ) );

    List<LogicalColumn> columns =
        domain.getLogicalModels().get( 0 ).getLogicalTables().get( 0 ).getLogicalColumns();

    col1 = columns.get( 0 );
    col2 = columns.get( 1 );
    assertTrue( col1.getId(), col1.getId().startsWith( "column" ) );
    assertTrue( col2.getId(), col2.getId().startsWith( "column" ) );
    assertNotEquals( "Columns have different corrected ids", col1.getId(), col2.getId() );
  }

  private static void setInvalidId( String invalidPart, Concept... concepts ) {
    for ( Concept concept : concepts ) {
      concept.setId( concept.getId() + invalidPart );
      assertFalse( validateId( concept.getId() ) );
    }
  }

  private static void assertValidId( Concept concept ) {
    assertNotNull( concept );
    assertTrue( concept.getId(), validateId( concept.getId() ) );
  }

  private static <T extends Concept> T findConceptStartingWith( String id, List<T> concepts ) {
    for ( T concept : concepts ) {
      if ( concept.getId().startsWith( id ) ) {
        return concept;
      }
    }
    return null;
  }

  @Test
  public void testOlapRoles() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/example_olap.xmi" ) );

    List<OlapRole> roles = new ArrayList<>();
    roles.add( new OlapRole( "California Manager", "<SchemaGrant/>" ) );
    roles.add( new OlapRole( "Maryland Manager", "<SchemaGrant/>" ) );

    LogicalModel model = domain.getLogicalModels().get( 0 );
    model.setProperty( LogicalModel.PROPERTY_OLAP_ROLES, roles );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    assertEquals( xml1, xml2 );
  }

  @Test
  public void testOlapCalculatedMembers() throws Exception {
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/example_olap.xmi" ) );

    List<OlapCalculatedMember> members = new ArrayList<>();
    members.add( new OlapCalculatedMember( "Constant One", "Measures", "1", "Currency", false ) );
    members.add( new OlapCalculatedMember( "Constant Two", "Measures", "2", "Currency", true ) );

    List<OlapCube> cubes = (List<OlapCube>) domain.getLogicalModels().get( 0 ).getProperty( "olap_cubes" );
    OlapCube cube = cubes.get( 0 );
    cube.setOlapCalculatedMembers( members );

    String xmi = parser.generateXmi( domain );

    ByteArrayInputStream is = new ByteArrayInputStream( xmi.getBytes() );
    Domain domain2 = parser.parseXmi( is );

    String xml1 = serializeWithOrderedHashmaps( domain );
    String xml2 = serializeWithOrderedHashmaps( domain2 );

    assertEquals( xml1, xml2 );
  }

  @Test
  public void testWriteAndParseLevelFormatter() throws Exception {
    Domain domain = parser.parseXmi( getClass().getResourceAsStream( "/example_olap.xmi" ) );
    LogicalModel analysisModel = domain.getLogicalModels().get( 0 );
    @SuppressWarnings( "unchecked" )
    List<OlapDimension> dims = (List<OlapDimension>) analysisModel.getProperty( LogicalModel.PROPERTY_OLAP_DIMS );

    OlapHierarchyLevel firstLevel = dims.get( 0 ).getHierarchies().get( 0 ).getHierarchyLevels().get( 0 );
    firstLevel.setFormatter( "InlineMemberFormatter" );

    String xmi = parser.generateXmi( domain );
    assertTrue( xmi.contains( "<CWM:TaggedValue tag=\"HIERARCHY_LEVEL_FORMATTER\" value=\"InlineMemberFormatter\"" ) );

    domain = parser.parseXmi( new ByteArrayInputStream( xmi.getBytes() ) );
    analysisModel = domain.getLogicalModels().get( 0 );
    @SuppressWarnings( "unchecked" )
    List<OlapDimension> parsedDims = (List<OlapDimension>) analysisModel.getProperty( LogicalModel.PROPERTY_OLAP_DIMS );
    firstLevel = parsedDims.get( 0 ).getHierarchies().get( 0 ).getHierarchyLevels().get( 0 );
    assertEquals( "InlineMemberFormatter", firstLevel.getFormatter() );
  }
}
