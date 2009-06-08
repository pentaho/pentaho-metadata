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
  }
  
  
}
