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
package org.pentaho.pms.mql;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

/**
 * Units tests for the <code>MappedQuery</code> class.
 */
public class MappedQueryTest extends TestCase {

  // Sample SQL statement
  private static final String SAMPLE1_SQL = "SELECT\n          BT_OFFICES_OFFICES.TERRITORY AS COL0\n"
      + "         ,BT_OFFICES_OFFICES.POSTALCODE AS COL1\n         ,BT_OFFICES_OFFICES.COUNTRY AS COL2\n"
      + "         ,BT_OFFICES_OFFICES.STATE AS COL3\n         ,BT_OFFICES_OFFICES.ADDRESSLINE2 AS COL4\n"
      + "         ,BT_OFFICES_OFFICES.ADDRESSLINE1 AS COL5\n         ,BT_OFFICES_OFFICES.PHONE AS COL6\n"
      + "         ,BT_OFFICES_OFFICES.CITY AS COL7\n         ,BT_OFFICES_OFFICES.OFFICECODE AS COL8\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.JOBTITLE AS COL9\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.REPORTSTO AS COL10\n         ,BT_EMPLOYEES_EMPLOYEES.EMAIL AS COL11\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.EXTENSION AS COL12\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.FIRSTNAME AS COL13\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.LASTNAME AS COL14\n"
      + "         ,BT_EMPLOYEES_EMPLOYEES.EMPLOYEENUMBER AS COL15\nFROM \n"
      + "          EMPLOYEES BT_EMPLOYEES_EMPLOYEES\n         ,OFFICES BT_OFFICES_OFFICES\nWHERE \n"
      + "          ( BT_OFFICES_OFFICES.OFFICECODE = BT_EMPLOYEES_EMPLOYEES.OFFICECODE )\n";

  private static final Map<String, String> SAMPLE1_MAP = new HashMap<String, String>();
  static {
    SAMPLE1_MAP.put( "COL0", "BC_OFFICES_TERRITORY" );
    SAMPLE1_MAP.put( "COL1", "BC_OFFICES_POSTALCODE" );
    SAMPLE1_MAP.put( "COL2", "BC_OFFICES_COUNTRY" );
    SAMPLE1_MAP.put( "COL3", "BC_OFFICES_STATE" );
    SAMPLE1_MAP.put( "COL4", "BC_OFFICES_ADDRESSLINE2" );
    SAMPLE1_MAP.put( "COL5", "BC_OFFICES_ADDRESSLINE1" );
    SAMPLE1_MAP.put( "COL6", "BC_OFFICES_PHONE" );
    SAMPLE1_MAP.put( "COL7", "BC_OFFICES_CITY" );
    SAMPLE1_MAP.put( "COL8", "BC_OFFICES_OFFICECODE" );
    SAMPLE1_MAP.put( "COL9", "BC_EMPLOYEES_JOBTITLE" );
    SAMPLE1_MAP.put( "COL10", "BC_EMPLOYEES_REPORTSTO" );
    SAMPLE1_MAP.put( "COL11", "BC_EMPLOYEES_EMAIL" );
    SAMPLE1_MAP.put( "COL12", "BC_EMPLOYEES_EXTENSION" );
    SAMPLE1_MAP.put( "COL13", "BC_EMPLOYEES_FIRSTNAME" );
    SAMPLE1_MAP.put( "COL14", "BC_EMPLOYEES_LASTNAME" );
    SAMPLE1_MAP.put( "COL15", "BC_EMPLOYEES_EMPLOYEENUMBER" );
  }

  /**
   * This test will make sure that the creation of the display query will not accidentally screw up the validity of the
   * query. <br/>
   * BISERVER-2881 was caused by a string replace of COL1 accidentally replacing COL10, COL11, COL12 (etc).
   */
  @SuppressWarnings( "deprecation" )
  public void testGetDisplayQuery() {
    // Get the display query based on the SAMPLE1 set of parameters
    final MappedQuery mappedQuery = new MappedQuery( SAMPLE1_SQL, SAMPLE1_MAP, null );
    final String result = mappedQuery.getDisplayQuery();

    // Each item in the map should be found once (and only once) in the resulting query
    for ( String columnName : SAMPLE1_MAP.values() ) {
      assertEquals( "Error translating [" + columnName + "]", 1, StringUtils.countMatches( result, columnName ) );
    }
  }
}
