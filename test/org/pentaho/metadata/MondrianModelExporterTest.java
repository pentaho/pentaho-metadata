package org.pentaho.metadata;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.metadata.model.olap.OlapCube;
import org.pentaho.metadata.model.olap.OlapDimension;
import org.pentaho.metadata.model.olap.OlapDimensionUsage;
import org.pentaho.metadata.model.olap.OlapHierarchy;
import org.pentaho.metadata.model.olap.OlapHierarchyLevel;
import org.pentaho.metadata.model.olap.OlapMeasure;
import org.pentaho.metadata.util.MondrianModelExporter;

public class MondrianModelExporterTest {

  @Test
  public void testDegenerateViewGen() throws Exception {
    
    List<OlapDimension> dimensions = new ArrayList<OlapDimension>();
    OlapDimension dimension = new OlapDimension();
    dimension.setName("Dim1");
    
    List<OlapHierarchy> hierarchies = new ArrayList<OlapHierarchy>();
    OlapHierarchy hierarchy = new OlapHierarchy();
    hierarchy.setName("Hier1");
    List<OlapHierarchyLevel> hierarchyLevels = new ArrayList<OlapHierarchyLevel>();
    OlapHierarchyLevel level = new OlapHierarchyLevel();
    level.setName("Lvl1");
    hierarchyLevels.add(level);
    
    hierarchy.setHierarchyLevels(hierarchyLevels);
    hierarchies.add(hierarchy);
    dimension.setHierarchies(hierarchies);
    
    dimensions.add(dimension);
    
    List<OlapCube> cubes = new ArrayList<OlapCube>();
    OlapCube cube = new OlapCube();
    cube.setName("Cube1");
    cubes.add(cube);
    
    List<OlapMeasure> measures = new ArrayList<OlapMeasure>();
    OlapMeasure measure = new OlapMeasure();
    measure.setName("Meas1");
    measures.add(measure);
    cube.setOlapMeasures(measures);
    
    List<OlapDimensionUsage> dimensionUsages = new ArrayList<OlapDimensionUsage>();

    OlapDimensionUsage dimUsage = new OlapDimensionUsage();
    dimensionUsages.add(dimUsage);
    cube.setOlapDimensionUsages(dimensionUsages);
    
    dimUsage.setName("Dim1");
    dimUsage.setOlapDimension(dimension);
    
    LogicalModel businessModel = TestHelper.buildDefaultModel();
    LogicalTable logicalTable = businessModel.getLogicalTables().get(0); 
    hierarchy.setLogicalTable(logicalTable);
    List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
    level.setReferenceColumn(logicalTable.getLogicalColumns().get(0));
    level.setLogicalColumns(new ArrayList<LogicalColumn>());
    logicalTable.setProperty(SqlPhysicalTable.TARGET_TABLE, "select * from customer");
    LogicalColumn m = logicalTable.getLogicalColumns().get(0);
    m.setAggregationType(AggregationType.SUM);
    measure.setLogicalColumn(m);
    businessModel.getLogicalTables().get(0).setProperty(SqlPhysicalTable.TARGET_TABLE_TYPE, TargetTableType.INLINE_SQL);
    cube.setLogicalTable(businessModel.getLogicalTables().get(0));
    
    businessModel.setProperty("olap_dimensions", dimensions);
    businessModel.setProperty("olap_cubes", cubes);
    
    businessModel.setName(new LocalizedString("en_US", "model"));
    MondrianModelExporter exporter = new MondrianModelExporter(businessModel, "en_US");
    String data = exporter.createMondrianModelXML();
    
    Assert.assertEquals(
        "<Schema name=\"model\">\n" + 
        "  <Dimension name=\"Dim1\">\n" + 
        "    <Hierarchy name=\"Hier1\" hasAll=\"false\">\n" + 
        "    <View alias=\"FACT\">\n" + 
        "        <SQL dialect=\"generic\">\n" + 
        "         <![CDATA[select * from customer]]>\n" + 
        "        </SQL>\n" + 
        "    </View>\n" + 
        "      <Level name=\"Lvl1\" column=\"pc1\" uniqueMembers=\"false\">\n" + 
        "      </Level>\n" + 
        "    </Hierarchy>\n" + 
        "  </Dimension>\n" + 
        "  <Cube name=\"Cube1\">\n" + 
        "    <View alias=\"FACT\">\n" + 
        "        <SQL dialect=\"generic\">\n" + 
        "         <![CDATA[select * from customer]]>\n" + 
        "        </SQL>\n" + 
        "    </View>\n" + 
        "    <DimensionUsage name=\"Dim1\" source=\"Dim1\" foreignKey=\"pc2\"/>\n" + 
        "    <Measure name=\"bc1\" column=\"pc1\" aggregator=\"sum\" formatString=\"Standard\"/>\n" + 
        "  </Cube>\n" + 
        "</Schema>",
        data
    );
  }
}
