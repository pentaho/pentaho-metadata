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
package org.pentaho.metadata.query.model.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CsvDataReaderIT {

  @Test
  public void testLoadData() throws Exception {
    CsvDataReader reader =
        new CsvDataReader(
          getClass().getResource(
            "/solution/system/metadata/csvfiles/anotherexample.csv" ).getPath(), true, ",", "\"", 5 );
    List<List<String>> data = reader.loadData();
    List<String> originalHeader = new ArrayList<String>();
    originalHeader.add( "Task" );
    originalHeader.add( "Est'd Time" );
    originalHeader.add( "Time Left" );
    originalHeader.add( "Time Spent" );
    originalHeader.add( "Time Left" );
    Assert.assertTrue( compare( reader.getHeader(), originalHeader ) );

    // Create a file with mixed line endings
    final String complexFileName = "target/it-classes/mixed-line-endings.csv";
    final File outputFilename = new File( complexFileName );
    outputFilename.deleteOnExit();
    final FileWriter file = new FileWriter( outputFilename, false );
    file.write( "Col1,Col2,Col3,Col4,Col5\r\n1,2,3,4,5\nA,B,C,D,E\r\n" );
    file.close();

    final CsvDataReader reader2 = new CsvDataReader( complexFileName, true, ",", "\"", 5 );
    final List<List<String>> data2 = reader2.loadData();
    Assert.assertEquals( 2, data2.size() );
    final List<String> validationList1 = new ArrayList<String>( 5 );
    final List<String> validationList2 = new ArrayList<String>( 5 );
    final List<String> validationList3 = new ArrayList<String>( 5 );
    validationList1.add( "Col1" );
    validationList1.add( "Col2" );
    validationList1.add( "Col3" );
    validationList1.add( "Col4" );
    validationList1.add( "Col5" );
    validationList2.add( "1" );
    validationList2.add( "2" );
    validationList2.add( "3" );
    validationList2.add( "4" );
    validationList2.add( "5" );
    validationList3.add( "A" );
    validationList3.add( "B" );
    validationList3.add( "C" );
    validationList3.add( "D" );
    validationList3.add( "E" );
    Assert.assertTrue( compare( reader2.getHeader(), validationList1 ) );
    Assert.assertTrue( compare( data2.get( 0 ), validationList2 ) );
    Assert.assertTrue( compare( data2.get( 1 ), validationList3 ) );
  }

  @Test
  public void testGetDataByColumnName() throws Exception {
    List<String> originalData = new ArrayList<String>();
    originalData.add( "3.5" );
    originalData.add( "8" );
    originalData.add( "13" );
    CsvDataReader reader =
        new CsvDataReader(
          getClass().getResource(
            "/solution/system/metadata/csvfiles/anotherexample.csv" ).getPath(), true, ",", "\"", 5 );
    reader.loadData();
    List<String> data = reader.getColumnData( 1 );
    Assert.assertTrue( compare( data, originalData ) );
  }

  @Test
  public void testGetDataByColumnNumber() throws Exception {
    List<String> originalData = new ArrayList<String>();
    originalData.add( "3.5" );
    originalData.add( "4" );
    originalData.add( "0" );
    CsvDataReader reader =
        new CsvDataReader(
          getClass().getResource(
            "/solution/system/metadata/csvfiles/anotherexample.csv" ).getPath(), true, ",", "\"", 5 );
    reader.loadData();
    List<String> data = reader.getColumnData( 2 );
    Assert.assertTrue( compare( data, originalData ) );
  }

  @Test
  public void testCheckForIndexOutOfBoundException() throws Exception {
    List<String> data = null;
    try {
      CsvDataReader reader =
          new CsvDataReader(
            getClass().getResource(
              "/solution/system/metadata/csvfiles/csv_various_types.csv" ).getPath(), true, ",", "\"", 5 );
      reader.loadData();
      data = reader.getColumnData( 2 );
      Assert.assertTrue( data != null );
    } catch ( IndexOutOfBoundsException ex ) {
      Assert.assertFalse( data != null );
    }
  }

  private boolean compare( List<String> list1, List<String> list2 ) {
    int i = 0;
    for ( String data : list1 ) {
      // System.out.println("comparing " + data + " to " + list2.get(i));
      if ( data.equals( list2.get( i ) ) ) {
        i++;
      } else {
        return false;
      }
    }
    return true;
  }
}
