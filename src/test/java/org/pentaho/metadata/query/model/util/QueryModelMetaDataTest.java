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
 * Copyright (c) 2016 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.query.model.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.query.model.Selection;

public class QueryModelMetaDataTest {

  private int columnNumber;

  private int columnNumberIncorrect;

  private static final int ROW_NUMBER_CORRECT = 0;

  //we able to get attribute only for 0 line
  private static final int ROW_NUMBER_INCORRECT = 1;

  private String[][] columnHeaders;
  private String[][] rowHeaders;
  private String columnNameFormat;
  private String[] columnTypes;
  private String[] columnNames;
  private String[] rowHeaderNames;
  private List<? extends Selection> columns;

  private Object property;

  @Before
  public void setUp() {
    property = new Object();

    LogicalColumn column = mock( LogicalColumn.class );
    when( column.getProperty( anyString() ) ).thenReturn( property );

    Selection selection = new Selection( null, column, null );

    columnHeaders = new String[][] { { "col1", "col2" } };
    rowHeaders = new String[][] { { "row1", "row2" } };
    columnNameFormat = "columnNameFormat";
    columnTypes = new String[] { "columnType1", "columnType2" };
    columnNames = new String[] { "columnName1", "columnName2" };
    rowHeaderNames = new String[] { "rowHeaderName1", "rowHeaderName2" };
    columns = Collections.singletonList( selection );
    columnNumber = 0; // we could use max column number according list of columns
    columnNumberIncorrect = columnNumber + 1;
  }

  @Test
  public void testColumnHeadersWorkWithMixedCaseColumnMapAndLowerCasedAlias() {
    // This tests BISERVER-11022 (Impala with dashboards)
    Map<String, String> columnsMap = new HashMap<String, String>();
    columnsMap.put( "CamelCase", "test" );
    assertEquals( "test", new QueryModelMetaData( columnsMap, new Object[][] { new Object[] { "camelcase" } },
        new Object[][] {}, new ArrayList<Selection>() ).getColumnHeaders()[0][0] );
  }

  @Test
  public void testCheckContent() {
    QueryModelMetaData metadataSample =
        new QueryModelMetaData( columnHeaders, rowHeaders, columnNameFormat, columnTypes, columnNames, rowHeaderNames, columns );
    QueryModelMetaData metadataActual = new QueryModelMetaData( metadataSample );
    testCheckContent( metadataActual );
  }

  @Test
  public void testCheckContent2() {
    QueryModelMetaData metadata =
      new QueryModelMetaData( columnHeaders, rowHeaders, columnNameFormat, columnTypes, columnNames, rowHeaderNames, columns );
    testCheckContent( metadata );
  }

  private void testCheckContent( QueryModelMetaData metadata ) {
    assertTrue( Arrays.equals( columnHeaders, metadata.getColumnHeaders() ) );
    assertTrue( Arrays.equals( rowHeaders, metadata.getRowHeaders() ) );
    assertEquals( columnNameFormat, metadata.getColumnNameFormat() );
    assertTrue( Arrays.equals( columnTypes, metadata.getColumnTypes() ) );
    assertTrue( Arrays.equals( columnNames, metadata.getFlattenedColumnNames() ) );
    assertTrue( Arrays.equals( rowHeaderNames, metadata.getRowHeaderNames() ) );
    assertEquals( columns, metadata.getColumns() );
  }

  @Test
  public void testGetAttribute() {
    QueryModelMetaData metadataSample =
        new QueryModelMetaData( columnHeaders, rowHeaders, columnNameFormat, columnTypes, columnNames, rowHeaderNames, columns );
    assertNull( metadataSample.getAttribute( ROW_NUMBER_INCORRECT, columnNumber, "attributeName" ) );
    assertNull( metadataSample.getAttribute( ROW_NUMBER_CORRECT, columnNumberIncorrect, "attributeName" ) );
    assertNull( metadataSample.getAttribute( ROW_NUMBER_INCORRECT, columnNumberIncorrect, "attributeName" ) );
    assertEquals( property,  metadataSample.getAttribute( ROW_NUMBER_CORRECT, columnNumber, "attributeName" ) );
  }

}
