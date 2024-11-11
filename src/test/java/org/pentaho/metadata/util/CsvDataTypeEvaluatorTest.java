/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

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
    columnValues.add( "election day" );
    columnValues.add( "christmas day" );
    columnValues.add( "1212" );
    columnValues.add( "2323.33" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.STRING, guessType );
  }

  @Test
  public void testSampleData_Integer() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "121212" );
    columnValues.add( "122" );
    columnValues.add( "1212" );
    columnValues.add( "2323.33" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.NUMERIC, guessType );
  }

  @Test
  public void testSampleData_Double() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "121212" );
    columnValues.add( "232323.33" );
    columnValues.add( "3443.33" );
    columnValues.add( "2323.33" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.NUMERIC, guessType );
  }

  @Test
  public void testSampleData_Time() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "16:45" );
    columnValues.add( "8:30" );
    columnValues.add( "1212" );
    columnValues.add( "23:40" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.DATE, guessType );
  }

  @Test
  public void testSampleData_Date() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "2008-11-04" );
    columnValues.add( "2008-04-04" );
    columnValues.add( "2008-05-04" );
    columnValues.add( "2323.33" );
    columnValues.add( "23.33" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.DATE, guessType );
  }

  @Test
  public void testSampleData_Boolean() throws Exception {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "true" );
    columnValues.add( "1" );
    columnValues.add( "false" );
    columnValues.add( "false" );
    CsvDataTypeEvaluator type = new CsvDataTypeEvaluator( columnValues );
    DataType guessType = type.evaluateDataType( columnValues );
    Assert.assertEquals( DataType.BOOLEAN, guessType );
  }

  @Test
  public void testGuessBestDateFormat() {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "2008-11-04" );
    columnValues.add( "2008-11-04 12:12:12" );
    columnValues.add( "2008-04-04" );
    columnValues.add( "2008-05-04" );
    columnValues.add( "2008/11/04" );
    columnValues.add( "2008/04/04" );
    columnValues.add( "11/04/2009" );
    columnValues.add( "11/04/2009 3:30:40" );
    columnValues.add( "23-11-2009" );
    columnValues.add( "2323.33" );
    columnValues.add( "23.33" );
    String guessFormat = CsvDataTypeEvaluator.getBestDateFormat( columnValues );

    Assert.assertEquals( "yyyy-MM-dd", guessFormat );

  }

  @Test
  public void testGuessBestDateFormat_Time() {
    List<String> columnValues = new ArrayList<String>();
    columnValues.add( "2008-11-04" );
    columnValues.add( "2008-11-04 12:12:12" );
    columnValues.add( "2008-11-05 14:42:22" );
    String guessFormat = CsvDataTypeEvaluator.getBestDateFormat( columnValues );

    Assert.assertEquals( "yyyy-MM-dd HH:mm:ss", guessFormat );
  }

}
