/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.metadata.model.concept.util;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.metadata.model.concept.types.DataType;

public class DataFormatterTest {

  private static final String SAMPLE_STRING = "sampleString";

  private static final String SAMPLE_DATE_MASK = "yyyy-MM-dd HH:mm:ss.SSS";

  private static final String SAMPLE_DATE_ERROR = "2000 year 01 month 01 ";

  private String sampleStringDate;

  private Date sampleJavaDate;

  private java.sql.Date sampleSqlDate;

  private java.sql.Timestamp sampleSqlTimeStamp;

  private String expectedDate;

  private static final String SAMPLE_NUMERIC_MASK = "###,###";

  private static final Double SAMPLE_DOUBLE = 123456.789;

  private static final String EXPECTED_DOUBLE = "123,457";

  private static final Long SAMPLE_LONG = 123456l;

  private static final String EXPECTED_LONG = "123,456";

  private static final Integer SAMPLE_INTEGER = 123456;

  private static final String EXPECTED_INTEGER = "123,456";

  @Before
  public void setUp() {
    SimpleDateFormat format = new SimpleDateFormat( SAMPLE_DATE_MASK );
    //add custom seconds
    int seconds = 123456;
    sampleJavaDate = new Date( seconds );
    sampleSqlDate = new java.sql.Date( seconds );
    sampleSqlTimeStamp = new Timestamp( seconds );
    sampleStringDate = format.format( sampleJavaDate );
    expectedDate = format.format( sampleJavaDate );
  }

  @Test
  public void testFormatString_Date() {
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE, SAMPLE_DATE_MASK, sampleJavaDate ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE, SAMPLE_DATE_MASK, sampleSqlDate ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE, SAMPLE_DATE_MASK, sampleSqlTimeStamp ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE, SAMPLE_DATE_MASK, sampleStringDate ) );
    //should return data as string if we pass incorrect date
    assertEquals( SAMPLE_DATE_ERROR, DataFormatter.getFormatedString( DataType.DATE, SAMPLE_DATE_MASK, SAMPLE_DATE_ERROR ) );
  }

  @Test
  public void testFormatString_Numeric() {
    assertEquals( EXPECTED_DOUBLE, DataFormatter.getFormatedString( DataType.NUMERIC, SAMPLE_NUMERIC_MASK, SAMPLE_DOUBLE ) );
    assertEquals( EXPECTED_LONG, DataFormatter.getFormatedString( DataType.NUMERIC, SAMPLE_NUMERIC_MASK, SAMPLE_LONG ) );
    assertEquals( EXPECTED_INTEGER, DataFormatter.getFormatedString( DataType.NUMERIC, SAMPLE_NUMERIC_MASK, SAMPLE_INTEGER ) );
  }

  @Test
  public void testFormatString_String() {
    assertEquals( SAMPLE_STRING, DataFormatter.getFormatedString( DataType.STRING, SAMPLE_NUMERIC_MASK, SAMPLE_STRING ) );
  }

  @Test
  public void testFormatStringDataTypeAsString_Date() {
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE.toString(), SAMPLE_DATE_MASK, sampleJavaDate ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE.toString(), SAMPLE_DATE_MASK, sampleSqlDate ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE.toString(), SAMPLE_DATE_MASK, sampleSqlTimeStamp ) );
    assertEquals( expectedDate, DataFormatter.getFormatedString( DataType.DATE.toString(), SAMPLE_DATE_MASK, sampleStringDate ) );
    //should return data as string if we pass incorrect date
    assertEquals( SAMPLE_DATE_ERROR, DataFormatter.getFormatedString( DataType.DATE.toString(), SAMPLE_DATE_MASK, SAMPLE_DATE_ERROR ) );
  }

  @Test
  public void testFormatStringDataTypeAsString_Numeric() {
    assertEquals( EXPECTED_DOUBLE, DataFormatter.getFormatedString( DataType.NUMERIC.toString(), SAMPLE_NUMERIC_MASK, SAMPLE_DOUBLE ) );
    assertEquals( EXPECTED_LONG, DataFormatter.getFormatedString( DataType.NUMERIC.toString(), SAMPLE_NUMERIC_MASK, SAMPLE_LONG ) );
    assertEquals( EXPECTED_INTEGER, DataFormatter.getFormatedString( DataType.NUMERIC.toString(), SAMPLE_NUMERIC_MASK, SAMPLE_INTEGER ) );
  }

  @Test
  public void testFormatStringDataTypeAsString_String() {
    //should not modify string
    assertEquals( SAMPLE_STRING, DataFormatter.getFormatedString( DataType.STRING.toString(), SAMPLE_NUMERIC_MASK, SAMPLE_STRING ) );
  }

}
