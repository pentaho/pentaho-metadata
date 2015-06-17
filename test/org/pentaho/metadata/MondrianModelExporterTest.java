package org.pentaho.metadata;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.model.olap.OlapAnnotation;
import org.pentaho.metadata.model.olap.OlapCalculatedMember;
import org.pentaho.metadata.model.olap.OlapCube;
import org.pentaho.metadata.model.olap.OlapDimension;
import org.pentaho.metadata.model.olap.OlapDimensionUsage;
import org.pentaho.metadata.model.olap.OlapHierarchy;
import org.pentaho.metadata.model.olap.OlapHierarchyLevel;
import org.pentaho.metadata.model.olap.OlapMeasure;
import org.pentaho.metadata.model.olap.OlapRole;
import org.pentaho.metadata.util.MondrianModelExporter;

public class MondrianModelExporterTest {

  @Test
  public void testDegenerateViewGen() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.INLINE_SQL, "select * from customer", "" );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
        + "      </Level>\n" + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <View alias=\"FACT\">\n" + "        <SQL dialect=\"generic\">\n"
        + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n" + "    </View>\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }

  @Test
  public void testUnquotedTargetTable() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.TABLE, "tableName", "schemaName" );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n"
        + "    <Table name=\"tableName\" schema=\"schemaName\" />\n"
        + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n" + "      </Level>\n"
        + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <Table name=\"tableName\" schema=\"schemaName\" />\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }

  @Test
  public void testQuotedTargetTable() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.TABLE, "`tableName`", "`schemaName`" );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n"
        + "    <Table name=\"tableName\" schema=\"schemaName\"/>\n"
        + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n" + "      </Level>\n"
        + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <Table name=\"tableName\" schema=\"schemaName\" />\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }

  @Test
  public void testLevelAnnotations() throws Exception {

    List<OlapDimension> dimensions = new ArrayList<OlapDimension>();
    OlapDimension dimension = new OlapDimension();
    dimension.setName( "Dim1" );

    List<OlapHierarchy> hierarchies = new ArrayList<OlapHierarchy>();
    OlapHierarchy hierarchy = new OlapHierarchy();
    hierarchy.setName( "Hier1" );
    List<OlapHierarchyLevel> hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
    OlapHierarchyLevel level = new OlapHierarchyLevel();
    level.setName( "Lvl1" );

    level.getAnnotations().add( new OlapAnnotation( "GeoRole", "city" ) );
    level.getAnnotations().add( new OlapAnnotation( "RequiredParents", "country,state" ) );

    hierarchyLevels.add( level );

    hierarchy.setHierarchyLevels( hierarchyLevels );
    hierarchies.add( hierarchy );
    dimension.setHierarchies( hierarchies );

    dimensions.add( dimension );

    List<OlapCube> cubes = new ArrayList<OlapCube>();
    OlapCube cube = new OlapCube();
    cube.setName( "Cube1" );
    cubes.add( cube );

    List<OlapMeasure> measures = new ArrayList<OlapMeasure>();
    OlapMeasure measure = new OlapMeasure();
    measure.setName( "Meas1" );
    measures.add( measure );
    cube.setOlapMeasures( measures );

    List<OlapDimensionUsage> dimensionUsages = new ArrayList<OlapDimensionUsage>();

    OlapDimensionUsage dimUsage = new OlapDimensionUsage();
    dimensionUsages.add( dimUsage );
    cube.setOlapDimensionUsages( dimensionUsages );

    dimUsage.setName( "Dim1" );
    dimUsage.setOlapDimension( dimension );

    LogicalModel businessModel = TestHelper.buildDefaultModel();
    LogicalTable logicalTable = businessModel.getLogicalTables().get( 0 );
    hierarchy.setLogicalTable( logicalTable );
    List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
    level.setReferenceColumn( logicalTable.getLogicalColumns().get( 0 ) );
    level.setLogicalColumns( new ArrayList<LogicalColumn>() );
    logicalTable.setProperty( SqlPhysicalTable.TARGET_TABLE, "select * from customer" );
    LogicalColumn m = logicalTable.getLogicalColumns().get( 0 );
    m.setAggregationType( AggregationType.SUM );
    measure.setLogicalColumn( m );
    businessModel.getLogicalTables().get( 0 ).setProperty( SqlPhysicalTable.TARGET_TABLE_TYPE,
        TargetTableType.INLINE_SQL );
    cube.setLogicalTable( businessModel.getLogicalTables().get( 0 ) );

    businessModel.setProperty( "olap_dimensions", dimensions );
    businessModel.setProperty( "olap_cubes", cubes );

    businessModel.setName( new LocalizedString( "en_US", "model" ) );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
        + "        <Annotations>\n" + "          <Annotation name=\"GeoRole\">city</Annotation>\n"
        + "          <Annotation name=\"RequiredParents\">country,state</Annotation>\n" + "        </Annotations>\n"
        + "      </Level>\n" + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <View alias=\"FACT\">\n" + "        <SQL dialect=\"generic\">\n"
        + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n" + "    </View>\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }

  @Test
  public void testLevelProperties() throws Exception {

    List<OlapDimension> dimensions = new ArrayList<OlapDimension>();
    OlapDimension dimension = new OlapDimension();
    dimension.setName( "Dim1" );

    List<OlapHierarchy> hierarchies = new ArrayList<OlapHierarchy>();
    OlapHierarchy hierarchy = new OlapHierarchy();
    hierarchy.setName( "Hier1" );
    List<OlapHierarchyLevel> hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
    OlapHierarchyLevel level = new OlapHierarchyLevel();
    level.setName( "Lvl1" );

    level.getAnnotations().add( new OlapAnnotation( "GeoRole", "city" ) );

    List<LogicalColumn> cols = new ArrayList<LogicalColumn>();
    LogicalColumn lcLat = new LogicalColumn();
    lcLat.setName( new LocalizedString( "en_US", "latitude" ) );
    lcLat.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "Latitude" );
    lcLat.setDataType( DataType.NUMERIC );
    LogicalColumn lcLon = new LogicalColumn();
    lcLon.setName( new LocalizedString( "en_US", "longitude" ) );
    lcLon.setProperty( SqlPhysicalColumn.TARGET_COLUMN, "Longitude" );
    lcLon.setDataType( DataType.NUMERIC );

    cols.add( lcLat );
    cols.add( lcLon );

    level.setLogicalColumns( cols );

    hierarchyLevels.add( level );

    hierarchy.setHierarchyLevels( hierarchyLevels );
    hierarchies.add( hierarchy );
    dimension.setHierarchies( hierarchies );

    dimensions.add( dimension );

    List<OlapCube> cubes = new ArrayList<OlapCube>();
    OlapCube cube = new OlapCube();
    cube.setName( "Cube1" );
    cubes.add( cube );

    List<OlapMeasure> measures = new ArrayList<OlapMeasure>();
    OlapMeasure measure = new OlapMeasure();
    measure.setName( "Meas1" );
    measures.add( measure );
    cube.setOlapMeasures( measures );

    List<OlapDimensionUsage> dimensionUsages = new ArrayList<OlapDimensionUsage>();

    OlapDimensionUsage dimUsage = new OlapDimensionUsage();
    dimensionUsages.add( dimUsage );
    cube.setOlapDimensionUsages( dimensionUsages );

    dimUsage.setName( "Dim1" );
    dimUsage.setOlapDimension( dimension );

    LogicalModel businessModel = TestHelper.buildDefaultModel();
    LogicalTable logicalTable = businessModel.getLogicalTables().get( 0 );
    hierarchy.setLogicalTable( logicalTable );
    List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
    level.setReferenceColumn( logicalTable.getLogicalColumns().get( 0 ) );
    // level.setLogicalColumns(new ArrayList<LogicalColumn>());
    logicalTable.setProperty( SqlPhysicalTable.TARGET_TABLE, "select * from customer" );
    LogicalColumn m = logicalTable.getLogicalColumns().get( 0 );
    m.setAggregationType( AggregationType.SUM );
    measure.setLogicalColumn( m );
    businessModel.getLogicalTables().get( 0 ).setProperty( SqlPhysicalTable.TARGET_TABLE_TYPE,
        TargetTableType.INLINE_SQL );
    cube.setLogicalTable( businessModel.getLogicalTables().get( 0 ) );

    businessModel.setProperty( "olap_dimensions", dimensions );
    businessModel.setProperty( "olap_cubes", cubes );

    businessModel.setName( new LocalizedString( "en_US", "model" ) );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
        + "        <Annotations>\n" + "          <Annotation name=\"GeoRole\">city</Annotation>\n"
        + "        </Annotations>\n" + "        <Property name=\"latitude\" column=\"Latitude\" type=\"Numeric\"/>\n"
        + "        <Property name=\"longitude\" column=\"Longitude\" type=\"Numeric\"/>\n" + "      </Level>\n"
        + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }

  private LogicalModel getTestModel( TargetTableType tableType, String targetTable, String targetSchema ) {
    List<OlapDimension> dimensions = new ArrayList<OlapDimension>();
    OlapDimension dimension = new OlapDimension();
    dimension.setName( "Dim1" );

    List<OlapHierarchy> hierarchies = new ArrayList<OlapHierarchy>();
    OlapHierarchy hierarchy = new OlapHierarchy();
    hierarchy.setName( "Hier1" );
    List<OlapHierarchyLevel> hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
    OlapHierarchyLevel level = new OlapHierarchyLevel();
    level.setName( "Lvl1" );
    hierarchyLevels.add( level );

    hierarchy.setHierarchyLevels( hierarchyLevels );
    hierarchies.add( hierarchy );
    dimension.setHierarchies( hierarchies );

    dimensions.add( dimension );

    List<OlapCube> cubes = new ArrayList<OlapCube>();
    OlapCube cube = new OlapCube();
    cube.setName( "Cube1" );
    cubes.add( cube );

    List<OlapMeasure> measures = new ArrayList<OlapMeasure>();
    OlapMeasure measure = new OlapMeasure();
    measure.setName( "Meas1" );
    measures.add( measure );
    cube.setOlapMeasures( measures );

    List<OlapDimensionUsage> dimensionUsages = new ArrayList<OlapDimensionUsage>();

    OlapDimensionUsage dimUsage = new OlapDimensionUsage();
    dimensionUsages.add( dimUsage );
    cube.setOlapDimensionUsages( dimensionUsages );

    dimUsage.setName( "Dim1" );
    dimUsage.setOlapDimension( dimension );

    LogicalModel businessModel = TestHelper.buildDefaultModel();
    LogicalTable logicalTable = businessModel.getLogicalTables().get( 0 );
    hierarchy.setLogicalTable( logicalTable );
    List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
    level.setReferenceColumn( logicalTable.getLogicalColumns().get( 0 ) );
    level.setLogicalColumns( new ArrayList<LogicalColumn>() );
    logicalTable.setProperty( SqlPhysicalTable.TARGET_TABLE, targetTable );
    logicalTable.setProperty( SqlPhysicalTable.TARGET_SCHEMA, targetSchema );

    LogicalColumn m = logicalTable.getLogicalColumns().get( 0 );
    m.setAggregationType( AggregationType.SUM );
    measure.setLogicalColumn( m );
    businessModel.getLogicalTables().get( 0 ).setProperty( SqlPhysicalTable.TARGET_TABLE_TYPE, tableType );
    cube.setLogicalTable( businessModel.getLogicalTables().get( 0 ) );

    businessModel.setProperty( "olap_dimensions", dimensions );
    businessModel.setProperty( "olap_cubes", cubes );

    businessModel.setName( new LocalizedString( "en_US", "model" ) );
    return businessModel;
  }

  @Test
  public void testDegenerateViewGenDate() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.INLINE_SQL, "select * from customer", "" );
    businessModel.getLogicalTables().get( 0 ).getLogicalColumns().get( 0 ).setDataType( DataType.DATE );
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Date\">\n"
        + "      </Level>\n" + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <View alias=\"FACT\">\n" + "        <SQL dialect=\"generic\">\n"
        + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n" + "    </View>\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "</Schema>", data );
  }
  
  @Test
  public void testRoles() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.INLINE_SQL, "select * from customer", "" );
    List<OlapRole> roles = new ArrayList<OlapRole>();
    roles.add( new OlapRole( "California Manager", "<SchemaGrant></SchemaGrant>" ) );
    businessModel.setProperty( LogicalModel.PROPERTY_OLAP_ROLES,  roles);
    
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
        + "      </Level>\n" + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <View alias=\"FACT\">\n" + "        <SQL dialect=\"generic\">\n"
        + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n" + "    </View>\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + "  </Cube>\n"
        + "    <Role name=\">California Manager\"> <SchemaGrant></SchemaGrant> </Role>\n"
        + "</Schema>", data );
  }
  
  @Test
  public void testCalculatedMembers() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.INLINE_SQL, "select * from customer", "" );
    List<OlapCalculatedMember> members = new ArrayList<OlapCalculatedMember>();
    members.add( new OlapCalculatedMember( "Constant One", "Measures", "1", "Currency" ) );
    
    @SuppressWarnings( "unchecked" )
    List<OlapCube> cubes = (List<OlapCube>) businessModel.getProperty( "olap_cubes" );
    OlapCube cube = cubes.get( 0 );
    cube.setOlapCalculatedMembers( members );
    
    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    String data = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces( "<Schema name=\"model\">\n" + "  <Dimension name=\"Dim1\">\n"
        + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + "    <View alias=\"FACT\">\n"
        + "        <SQL dialect=\"generic\">\n" + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n"
        + "    </View>\n" + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
        + "      </Level>\n" + "    </Hierarchy>\n" + "  </Dimension>\n" + "  <Cube name=\"Cube1\">\n"
        + "    <View alias=\"FACT\">\n" + "        <SQL dialect=\"generic\">\n"
        + "         <![CDATA[select * from customer]]>\n" + "        </SQL>\n" + "    </View>\n"
        + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
        + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" 
        + "    <CalculatedMember name=\"Constant One\" dimension=\"Measures\" formatString=\"Currency\">\n" 
        + "      <Formula><![CDATA[1]]></Formula>\n"
        + "    </CalculatedMember>\n"
        + "  </Cube>\n"
        + "</Schema>", data );
  }

  @Test
  public void testDescriptions() throws Exception {
    LogicalModel businessModel = getTestModel( TargetTableType.TABLE, "table", "schema" );
    @SuppressWarnings( "unchecked" )
    List<OlapCube> cubes = (List<OlapCube>) businessModel.getProperty( LogicalModel.PROPERTY_OLAP_CUBES );
    OlapMeasure measure = cubes.get( 0 ).getOlapMeasures().get( 0 );
    measure.getLogicalColumn().setDescription( new LocalizedString( "en_US", "it's a measure" ) );
    OlapHierarchyLevel level =
        cubes.get( 0 ).getOlapDimensionUsages().get( 0 ).getOlapDimension().getHierarchies().get( 0 )
            .getHierarchyLevels().get( 0 );
    OlapAnnotation description = new OlapAnnotation();
    description.setName( "description.en_US" );
    description.setValue( "description with > in there" );
    level.getAnnotations().add( description );

    MondrianModelExporter exporter = new MondrianModelExporter( businessModel, "en_US" );
    final String schema = exporter.createMondrianModelXML();

    TestHelper.assertEqualsIgnoreWhitespaces(
      "<Schema name=\"model\">\n"
      + "  <Dimension name=\"Dim1\">\n"
      + "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n"
      + "      <Table name=\"table\" schema=\"schema\"/>\n"
      + "      <Level name=\"Lvl1\" uniqueMembers=\"false\" column=\"pc1\" type=\"Numeric\">\n"
      + "        <Annotations>\n"
      + "          <Annotation name=\"description.en_US\">description with &#x3e; in there</Annotation>\n"
      + "        </Annotations>\n"
      + "      </Level>\n"
      + "    </Hierarchy>\n"
      + "  </Dimension>\n"
      + "  <Cube name=\"Cube1\">\n"
      + "    <Table name=\"table\" schema=\"schema\"/>\n"
      + "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n"
      + "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\" description=\"it&#x27;s a measure\"/>\n"
      + "  </Cube>\n" + "</Schema>", schema );
  }

}
