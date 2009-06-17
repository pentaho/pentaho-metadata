package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.util.SampleDataForDataType;

public class SampleDataForDataTypeTest {
  
  @Test
  public void testSampleData_String() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("election day");
    columnValues.add("christmas day");
    columnValues.add("1212");
    columnValues.add("2323.33");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.STRING, guessType);
  }
  
  @Test
  public void testSampleData_Integer() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("121212");
    columnValues.add("122");
    columnValues.add("1212");
    columnValues.add("2323.33");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.NUMERIC, guessType);
  }
  
  @Test
  public void testSampleData_Double() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("121212");
    columnValues.add("232323.33");
    columnValues.add("3443.33");
    columnValues.add("2323.33");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.NUMERIC, guessType);
  }
  
  @Test
  public void testSampleData_Time() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("16:45");
    columnValues.add("8:30");
    columnValues.add("1212");
    columnValues.add("23:40");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.DATE, guessType);
  }
  @Test
  public void testSampleData_Date() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("2008-11-04");
    columnValues.add("2008-04-04");
    columnValues.add("2008-05-04");
    columnValues.add("2323.33");
    columnValues.add("23.33");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.DATE, guessType);
  }
  
  @Test
  public void testSampleData_Boolean() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("true");
    columnValues.add("1");
    columnValues.add("false");
    columnValues.add("false");
    SampleDataForDataType type = new SampleDataForDataType(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.BOOLEAN, guessType);
  }
}
