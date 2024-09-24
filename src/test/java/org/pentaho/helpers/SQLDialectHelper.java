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
package org.pentaho.helpers;

import static org.junit.Assert.*;

import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

public class SQLDialectHelper {

  public static void assertEqualsIgnoreWhitespaces( String expected, String two ) {
    String oneStripped = stripWhiteSpaces( expected );
    String twoStripped = stripWhiteSpaces( two );

    assertEquals( oneStripped, twoStripped );
  }

  public static void assertEqualsIgnoreWhitespacesAndCase( String expected, String actual ) {
    assertEqualsIgnoreWhitespaces( expected.toUpperCase(), actual.toUpperCase() );
  }

  private static String stripWhiteSpaces( String one ) {
    StringBuilder stripped = new StringBuilder();

    boolean previousWhiteSpace = false;

    for ( char c : one.toCharArray() ) {
      if ( Character.isWhitespace( c ) ) {
        if ( !previousWhiteSpace ) {
          stripped.append( ' ' ); // add a single white space, don't add a second
        }
        previousWhiteSpace = true;
      } else {
        if ( c == '(' || c == ')' || c == '|' || c == '-' || c == '+' || c == '/' || c == '*' || c == '{' || c == '}'
            || c == ',' ) {
          int lastIndex = stripped.length() - 1;
          if ( stripped.charAt( lastIndex ) == ' ' ) {
            stripped.deleteCharAt( lastIndex );
          }
          previousWhiteSpace = true;
        } else {
          previousWhiteSpace = false;
        }
        stripped.append( c );
      }
    }

    // Trim the whitespace (max 1) at the front and back too...
    if ( stripped.length() > 0 && Character.isWhitespace( stripped.charAt( 0 ) ) ) {
      stripped.deleteCharAt( 0 );
    }
    if ( stripped.length() > 0 && Character.isWhitespace( stripped.charAt( stripped.length() - 1 ) ) ) {
      stripped.deleteCharAt( stripped.length() - 1 );
    }

    return stripped.toString();
  }

  public static SQLQueryModel createLimitedQuery() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t.id", null ); //$NON-NLS-1$
    query.addTable( "TABLE", "t" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula( "t.id is null", null ); //$NON-NLS-1$
    query.setLimit( 10 );
    query.addOrderBy( null, "t.id", OrderType.ASCENDING );
    return query;
  }

  public static SQLQueryModel createUnlimitedQuery() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t.id", null ); //$NON-NLS-1$
    query.addTable( "TABLE", "t" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula( "t.id is null", null ); //$NON-NLS-1$
    query.addOrderBy( null, "t.id", OrderType.ASCENDING );
    return query;
  }

  public static void assertSelect( String expected, SQLDialectInterface dialect, SQLQueryModel query ) {
    String result = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( expected, result );
  }
}
