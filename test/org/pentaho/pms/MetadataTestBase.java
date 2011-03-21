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
package org.pentaho.pms;

import junit.framework.TestCase;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

public class MetadataTestBase extends TestCase {
	
  public void setUp() throws Exception {
    KettleEnvironment.init(false);
  }
  
	public void testOracleDatabaseMeta() {
		assertEquals(createOracleDatabaseMeta().getPluginId(), "ORACLE"); //$NON-NLS-1$
	}
	
	public static DatabaseMeta createOracleDatabaseMeta() {
		return new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	}
		
	public static void assertEqualsIgnoreWhitespaces(String expected, String two) {
		String oneStripped = stripWhiteSpaces(expected);
		String twoStripped = stripWhiteSpaces(two);
		
		assertEquals(oneStripped, twoStripped);
	}
	
	public static void assertEqualsIgnoreWhitespacesAndCase(String expected, String actual) {
		assertEqualsIgnoreWhitespaces(expected.toUpperCase(), actual.toUpperCase());
	}

	private static String stripWhiteSpaces(String one) {
		StringBuilder stripped = new StringBuilder();
		
		boolean previousWhiteSpace = false;
		
		for (char c : one.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (!previousWhiteSpace) {
					stripped.append(' '); // add a single white space, don't add a second
				}
				previousWhiteSpace=true;
			}
			else {
				if (c=='(' || c==')' || c=='|' || c=='-' || c=='+' || c=='/' || c=='*' || c=='{' || c=='}' || c==',' ) {
					int lastIndex = stripped.length()-1;
					if (stripped.charAt(lastIndex)==' ') {
						stripped.deleteCharAt(lastIndex);
					}
					previousWhiteSpace=true;
				} else {
					previousWhiteSpace=false;
				}
				stripped.append(c);
			}
		}
		
		// Trim the whitespace (max 1) at the front and back too...
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(0))) stripped.deleteCharAt(0);
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(stripped.length()-1))) stripped.deleteCharAt(stripped.length()-1);
		
		return stripped.toString();
	}
	
	public SQLQueryModel createLimitedQuery() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("t.id is null", null); //$NON-NLS-1$
    query.setLimit(10);
    query.addOrderBy(null, "t.id", OrderType.ASCENDING);
    return query;
	}
	
	public SQLQueryModel createUnlimitedQuery() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection("t.id", null); //$NON-NLS-1$
    query.addTable("TABLE", "t"); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula("t.id is null", null); //$NON-NLS-1$
    query.addOrderBy(null, "t.id", OrderType.ASCENDING);
    return query;
	}
	
	public void assertSelect(String expected, SQLDialectInterface dialect, SQLQueryModel query) {
    String result = dialect.generateSelectStatement(query);
    assertEqualsIgnoreWhitespacesAndCase(expected, result);
	}
}
