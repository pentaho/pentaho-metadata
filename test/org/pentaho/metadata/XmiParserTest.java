package org.pentaho.metadata;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.SerializationService;
import org.pentaho.metadata.util.XmiParser;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmiParserTest {
  
  @Test
  public void testXmiParser() throws Exception {
    Domain domain = new XmiParser().parseXmi(new FileInputStream("samples/steelwheels.xmi"));
    Assert.assertEquals(6, domain.getConcepts().size());
    Assert.assertEquals(1, domain.getPhysicalModels().size());
    Assert.assertEquals(3, domain.getLogicalModels().size());
    
    Assert.assertEquals(2, domain.getLogicalModels().get(0).getLogicalTables().size());
    Assert.assertEquals(8, domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().size());
    Assert.assertEquals("BC_EMPLOYEES_JOBTITLE", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getId());
    Assert.assertEquals(1, domain.getLogicalModels().get(0).getLogicalRelationships().size());
    
    Assert.assertEquals("JOBTITLE", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getId());
    Assert.assertEquals("PT_EMPLOYEES", domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getId());
    Assert.assertNotNull(domain.getLogicalModels().get(0).getLogicalTables().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getPhysicalModel());
    
    Assert.assertEquals(2, domain.getLogicalModels().get(0).getCategories().size());
    Assert.assertEquals(9, domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().size());
    Assert.assertEquals("BC_OFFICES_TERRITORY", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getId());
    Assert.assertEquals("TERRITORY", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getPhysicalColumn().getId());
    Assert.assertEquals("PT_OFFICES", domain.getLogicalModels().get(0).getCategories().get(0).getLogicalColumns().get(0).getPhysicalColumn().getPhysicalTable().getId());
    
  }
  
  @Test
  public void testXmiGenerator() throws Exception {
    // String str = new XmiParser().generateXmi(new Domain());
    // System.out.println(str);
    XmiParser parser = new XmiParser();
    Domain domain = parser.parseXmi(new FileInputStream("samples/steelwheels.xmi"));
    
    String xmi = parser.generateXmi(domain);
    
    ByteArrayInputStream is = new ByteArrayInputStream(xmi.getBytes());
    Domain domain2 = parser.parseXmi(is);
    
    SerializationService serializer = new SerializationService();
   
    String xml1 = serializeWithOrderedHashmaps(domain);
    String xml2 = serializeWithOrderedHashmaps(domain2);

    // note: this does not verify security objects at this time
    Assert.assertEquals(xml1, xml2);
  }
  
  public String serializeWithOrderedHashmaps(Domain domain) {
    XStream xstream = new XStream(new DomDriver());
    xstream.registerConverter(new Converter() {

      public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        // TODO Auto-generated method stub
        writer.startNode("hashmap");
        HashMap unknownMap = (HashMap)source;
        if (unknownMap.size() > 0) {
          if (unknownMap.keySet().iterator().next() instanceof String) { 
            HashMap<String, Object> map = (HashMap<String, Object>)source;
            Set<String> ordered = new TreeSet<String>(map.keySet());
            for (String key : ordered) {
              writer.startNode("entry");
              writer.addAttribute("key", key);
              Object obj = map.get(key);
              if (obj == null) {
                System.out.println("NULL OBJ FOR " + key);
              } else {
                context.convertAnother(map.get(key));
              }
              writer.endNode();
            }
          }/* else {
            
            System.out.println(unknownMap.keySet().iterator().next());
          }*/
        }
        writer.endNode();
      }

      public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        // TODO Auto-generated method stub
        return null;
      }

      public boolean canConvert(Class type) {
        return type.getName().equals("java.util.HashMap");
      }
      
    });
    return xstream.toXML(domain);

  }
}
