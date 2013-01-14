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
package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.util.CsvDataTypeEvaluator;

public class CsvDataTypeEvaluatorTest {
  
  @Test
  public void testSampleData_String() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("election day");
    columnValues.add("christmas day");
    columnValues.add("1212");
    columnValues.add("2323.33");
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
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
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
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
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
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
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
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
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
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
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator(columnValues);
    DataType guessType = type.evaluateDataType(columnValues);
    Assert.assertEquals(DataType.BOOLEAN, guessType);
  }

  @Test
  public void testGuessBestDateFormat() {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("2008-11-04");
    columnValues.add("2008-11-04 12:12:12");
    columnValues.add("2008-04-04");
    columnValues.add("2008-05-04");
    columnValues.add("2008/11/04");
    columnValues.add("2008/04/04");
    columnValues.add("11/04/2009");
    columnValues.add("11/04/2009 3:30:40");    
    columnValues.add("23-11-2009");
    columnValues.add("2323.33");
    columnValues.add("23.33");
    String guessFormat = CsvDataTypeEvaluator.getBestDateFormat(columnValues);

    Assert.assertEquals("yyyy-MM-dd", guessFormat);

  }

  @Test
  public void testGuessBestDateFormat_Time() {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add("2008-11-04");
    columnValues.add("2008-11-04 12:12:12");
    columnValues.add("2008-11-05 14:42:22");
    String guessFormat = CsvDataTypeEvaluator.getBestDateFormat(columnValues);

    Assert.assertEquals("yyyy-MM-dd HH:mm:ss", guessFormat);
  }


}
