package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.query.model.util.CsvDataReader;

public class CsvDataReaderTest {

  @Test
  public void testLoadData() throws Exception {
    CsvDataReader reader = new CsvDataReader("test/solution/system/metadata/csvfiles/anotherexample.csv", true, ",", "\"", 5);
    List<List<String>> data = reader.loadData();
    List<String> originalHeader = new ArrayList<String>();
    originalHeader.add("Task");
    originalHeader.add("Est'd Time");
    originalHeader.add("Time Left");
    originalHeader.add("Time Spent");
    originalHeader.add("Time Left");
    Assert.assertTrue(compare(reader.getHeader(), originalHeader ));
    
  }
  
  @Test
  public void testGetDataByColumnName() throws Exception {
    List<String> originalData = new ArrayList<String>();
    originalData.add("3.5");
    originalData.add("8");
    originalData.add("13");    
    CsvDataReader reader = new CsvDataReader("test/solution/system/metadata/csvfiles/anotherexample.csv", true, ",", "\"", 5);
    reader.loadData();
    List<String> data = reader.getColumnData(1);
    Assert.assertTrue(compare(data,originalData));
  }
  
  @Test
  public void testGetDataByColumnNumber() throws Exception {
    List<String> originalData = new ArrayList<String>();
    originalData.add("3.5");
    originalData.add("4");
    originalData.add("0");
    CsvDataReader reader = new CsvDataReader("test/solution/system/metadata/csvfiles/anotherexample.csv", true, ",", "\"", 5);
    reader.loadData();
    List<String> data = reader.getColumnData(2);
    Assert.assertTrue(compare(data, originalData));    
  }
  
  private boolean compare(List<String> list1, List<String> list2) {
    int i=0;
    for(String data : list1) {
      // System.out.println("comparing " + data + " to " + list2.get(i));
      if (data.equals(list2.get(i))) {
        i++;
      } else {
        return false;
      }
    }
    return true;
  }
}
