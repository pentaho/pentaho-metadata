package org.pentaho.metadata;

import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.XmiParser;

public class XmiParserTest {
  
  @Test
  public void testXmiParser() throws Exception {
    Domain domain = new XmiParser().parseXmi(new FileInputStream("samples/steelwheels.xmi"));
    Assert.assertEquals(8, domain.getConcepts().size());
    Assert.assertEquals(1, domain.getPhysicalModels().size());
    Assert.assertEquals(3, domain.getLogicalModels().size());
    
    Assert.assertEquals(2, domain.getLogicalModels().get(0).getLogicalTables().size());
    Assert.assertEquals(8, domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().size());
    Assert.assertEquals("BC_EMPLOYEES_JOBTITLE", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getId());
    Assert.assertEquals("JOBTITLE", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getId());
    Assert.assertEquals("PT_EMPLOYEES", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getId());
    Assert.assertNotNull(domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getPhysicalModel());
    
    Assert.assertEquals(2, domain.getLogicalModels().get(0).getCategories().size());
    Assert.assertEquals(9, domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().size());
    Assert.assertEquals("BC_OFFICES_TERRITORY", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getId());
    Assert.assertEquals("TERRITORY", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getPhysicalColumn().getId());
    Assert.assertEquals("PT_OFFICES", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getId());
    
  }
}
