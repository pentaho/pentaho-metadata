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
package org.pentaho.metadata.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.util.DataTypeDetector;

/**
 * Created by IntelliJ IDEA. User: rfellows Date: Aug 30, 2010 Time: 1:39:20 PM To change this template use File |
 * Settings | File Templates.
 */
public class DataTypeDetectorTest {

  private static final String[] validDateSamples = new String[] { "1999/02/22", "1999/10/30", "2000/04/13",
    "1800/03/16", "1900/04/28", // yyyy/MM/dd
    "1999-02-22 12:00:00", "1999-10-30 1:00:00", "2000-04-13 02:00:59", "1800-03-16 12:59:12", "1900-04-28 22:59:59", // yyyy-MM-dd
                                                                                                                      // hh:mm:ss
    "1999-02-22", "1999-10-30", "2000-04-13", "1800-03-16", "1900-04-28", // yyyy-MM-dd
    "02/22/1932 1:01:01", "10/30/1935 23:00:12", "04/13/1943 12:59:29", "03/16/1951 01:23:21", "04/28/1958 23:59:59", // MM/dd/yyyy
                                                                                                                      // HH:mm:ss
    "02/22/1932", "10/30/1935", "04/13/1943", "03/16/1951", "04/28/1958", // MM/dd/yyyy
    "02-22-1932", "10-30-1935", "04-13-1943", "03-16-1951", "04-28-1958", // MM-dd-yyyy
    "22/02/1932", "30/10/1935", "13/04/1943", "16/03/1951", "28/04/1958", // dd/MM/yyyy
    "22-02-1932", "30-10-1935", "13-04-1943", "16-03-1951", "28-04-1958", // dd-MM-yyyy
    "02/22/32", "10/30/35", "04/13/43", "03/16/51", "04/28/58", // MM/dd/yy
    "02-22-32", "10-30-35", "04-13-43", "03-16-51", "04-28-58", // MM-dd-yy
    "22/02/32", "30/10/35", "13/04/43", "16/03/51", "28/04/58", // dd/MM/yy
    "22-02-32", "30-10-35", "13-04-43", "16-03-51", "28-04-58", // dd-MM-yy
  };
  private static final String[] nonValidDateSamples = new String[] { "2000-10-10 25:66:59", "20/20/2000", "01-01/2000",
    "true", "false", "300", "300.99", "hello world", null };

  @Test
  public void testGetDataType_Dates() {
    for ( String d : validDateSamples ) {
      DataType dt = DataTypeDetector.getDataType( d );
      assertEquals( "sample tested is not a recognized date format [" + d + "].", DataType.DATE, dt );
    }
  }

  @Test
  public void testGetDataType_NotDates() {
    for ( String d : nonValidDateSamples ) {
      DataType dt = DataTypeDetector.getDataType( d );
      assertFalse( "sample tested is a recognized date format [" + d + "]... it shouldn't be", DataType.DATE == dt );
    }
  }

}
