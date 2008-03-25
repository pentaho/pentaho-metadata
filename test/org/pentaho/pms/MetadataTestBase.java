package org.pentaho.pms;

import junit.framework.TestCase;

import org.pentaho.di.core.database.DatabaseMeta;

public class MetadataTestBase extends TestCase {
	
	public void testOracleDatabaseMeta() {
		assertEquals(createOracleDatabaseMeta().getDatabaseType(), DatabaseMeta.TYPE_DATABASE_ORACLE);
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
				stripped.append(c);
				previousWhiteSpace=false;
			}
		}
		
		// Trim the whitespace (max 1) at the front and back too...
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(0))) stripped.deleteCharAt(0);
		if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(stripped.length()-1))) stripped.deleteCharAt(stripped.length()-1);
		
		return stripped.toString();
	}
}
