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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class is used to define a number of default values for various settings throughout Pentaho Metadata. It also
 * contains a number of static final methods to make your life easier.
 * 
 * @author Matt
 * @since 07-05-2003
 * 
 */
public class Const {

  /**
   * CR: operating systems specific Cariage Return
   */
  public static final String CR = System.getProperty( "line.separator" ); //$NON-NLS-1$

  /**
   * The margin between the text of a note and its border.
   */
  public static final int NOTE_MARGIN = 5;

  /**
   * The base name of the Pentaho metadata editor logfile
   */
  public static final String META_EDITOR_LOG_FILE = "pentaho-meta"; //$NON-NLS-1$

  /**
   * Default we store our information in Unicode UTF-8 character set.
   */
  public static final String XML_ENCODING = "UTF-8"; //$NON-NLS-1$

  /**
   * Convert a String into an integer. If the conversion fails, assign a default value.
   * 
   * @param str
   *          The String to convert to an integer
   * @param def
   *          The default value
   * @return The converted value or the default.
   */
  public static final int toInt( String str, int def ) {
    int retval;
    try {
      retval = Integer.parseInt( str );
    } catch ( Exception e ) {
      retval = def;
    }
    return retval;
  }

  /**
   * Convert a String into a long integer. If the conversion fails, assign a default value.
   * 
   * @param str
   *          The String to convert to a long integer
   * @param def
   *          The default value
   * @return The converted value or the default.
   */
  public static final long toLong( String str, long def ) {
    long retval;
    try {
      retval = Long.parseLong( str );
    } catch ( Exception e ) {
      retval = def;
    }
    return retval;
  }

  /**
   * Convert a String into a double. If the conversion fails, assign a default value.
   * 
   * @param str
   *          The String to convert to a double
   * @param def
   *          The default value
   * @return The converted value or the default.
   */
  public static final double toDouble( String str, double def ) {
    double retval;
    try {
      retval = Double.parseDouble( str );
    } catch ( Exception e ) {
      retval = def;
    }
    return retval;
  }

  /**
   * Convert a String into a date. The date format is <code>yyyy/MM/dd HH:mm:ss.SSS</code>. If the conversion fails,
   * assign a default value.
   * 
   * @param str
   *          The String to convert into a Date
   * @param def
   *          The default value
   * @return The converted value or the default.
   */
  public static final Date toDate( String str, Date def ) {
    SimpleDateFormat df = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss.SSS", Locale.US ); //$NON-NLS-1$
    try {
      return df.parse( str );
    } catch ( ParseException e ) {
      return def;
    }
  }

  /**
   * Right trim: remove spaces to the right of a string
   * 
   * @param str
   *          The string to right trim
   * @return The trimmed string.
   */
  public static final String rtrim( String str ) {
    int max = str.length();
    while ( max > 0 && isSpace( str.charAt( max - 1 ) ) ) {
      max--;
    }

    return str.substring( 0, max );
  }

  /**
   * Determines whether or not a character is considered a space. A character is considered a space in Kettle if it is a
   * space, a tab, a newline or a cariage return.
   * 
   * @param c
   *          The character to verify if it is a space.
   * @return true if the character is a space. false otherwise.
   */
  public static final boolean isSpace( char c ) {
    return c == ' ' || c == '\t' || c == '\r' || c == '\n';
  }

  /**
   * Left trim: remove spaces to the left of a String.
   * 
   * @param str
   *          The String to left trim
   * @return The left trimmed String
   */
  public static final String ltrim( String str ) {
    int from = 0;
    while ( from < str.length() && isSpace( str.charAt( from ) ) ) {
      from++;
    }

    return str.substring( from );
  }

  /**
   * Trims a string: removes the leading and trailing spaces of a String.
   * 
   * @param str
   *          The string to trim
   * @return The trimmed string.
   */
  public static final String trim( String str ) {
    int max = str.length() - 1;
    int min = 0;

    while ( min <= max && isSpace( str.charAt( min ) ) ) {
      min++;
    }
    while ( max >= 0 && isSpace( str.charAt( max ) ) ) {
      max--;
    }

    if ( max < min ) {
      return ""; //$NON-NLS-1$
    }

    return str.substring( min, max + 1 );
  }

  /**
   * Right pad a string: adds spaces to a string until a certain length. If the length is smaller then the limit
   * specified, the String is truncated.
   * 
   * @param ret
   *          The string to pad
   * @param limit
   *          The desired length of the padded string.
   * @return The padded String.
   */
  public static final String rightPad( String ret, int limit ) {
    if ( ret == null ) {
      return rightPad( new StringBuffer(), limit );
    } else {
      return rightPad( new StringBuffer( ret ), limit );
    }
  }

  /**
   * Right pad a StringBuffer: adds spaces to a string until a certain length. If the length is smaller then the limit
   * specified, the String is truncated.
   * 
   * @param ret
   *          The StringBuffer to pad
   * @param limit
   *          The desired length of the padded string.
   * @return The padded String.
   */
  public static final String rightPad( StringBuffer ret, int limit ) {
    int len = ret.length();
    int l;

    if ( len > limit ) {
      ret.setLength( limit );
    } else {
      for ( l = len; l < limit; l++ ) {
        ret.append( ' ' );
      }
    }
    return ret.toString();
  }

  /**
   * Replace values in a String with another.
   * 
   * @param string
   *          The original String.
   * @param repl
   *          The text to replace
   * @param with
   *          The new text bit
   * @return The resulting string with the text pieces replaced.
   */
  public static final String replace( String string, String repl, String with ) {
    StringBuffer str = new StringBuffer( string );
    for ( int i = str.length() - 1; i >= 0; i-- ) {
      if ( str.substring( i ).startsWith( repl ) ) {
        str.delete( i, i + repl.length() );
        str.insert( i, with );
      }
    }
    return str.toString();
  }

  /**
   * Alternate faster version of string replace using a stringbuffer as input.
   * 
   * @param str
   *          The string where we want to replace in
   * @param code
   *          The code to search for
   * @param repl
   *          The replacement string for code
   */
  public static void repl( StringBuffer str, String code, String repl ) {
    int clength = code.length();

    int i = str.length() - clength;

    while ( i >= 0 ) {
      String look = str.substring( i, i + clength );
      // Look for a match!
      if ( look.equalsIgnoreCase( code ) ) {
        str.replace( i, i + clength, repl );
      }
      i--;
    }
  }

  /**
   * Count the number of spaces to the left of a text. (leading)
   * 
   * @param field
   *          The text to examine
   * @return The number of leading spaces found.
   */
  public static final int nrSpacesBefore( String field ) {
    int nr = 0;
    int len = field.length();
    while ( nr < len && field.charAt( nr ) == ' ' ) {
      nr++;
    }
    return nr;
  }

  /**
   * Count the number of spaces to the right of a text. (trailing)
   * 
   * @param field
   *          The text to examine
   * @return The number of trailing spaces found.
   */
  public static final int nrSpacesAfter( String field ) {
    int nr = 0;
    int len = field.length();
    while ( nr < len && field.charAt( field.length() - 1 - nr ) == ' ' ) {
      nr++;
    }
    return nr;
  }

  /**
   * Checks whether or not a String consists only of spaces.
   * 
   * @param str
   *          The string to check
   * @return true if the string has nothing but spaces.
   */
  public static final boolean onlySpaces( String str ) {
    for ( int i = 0; i < str.length(); i++ ) {
      if ( !isSpace( str.charAt( i ) ) ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Implements Oracle style NVL function
   * 
   * @param source
   *          The source argument
   * @param def
   *          The default value in case source is null or the length of the string is 0
   * @return source if source is not null, otherwise return def
   */
  public static final String NVL( String source, String def ) {
    if ( source == null || source.length() == 0 ) {
      return def;
    }
    return source;
  }

  /**
   * Search for a string in an array of strings and return the index.
   * 
   * @param lookup
   *          The string to search for
   * @param array
   *          The array of strings to look in
   * @return The index of a search string in an array of strings. -1 if not found.
   */
  public static final int indexOfString( String lookup, String[] array ) {
    if ( array == null ) {
      return -1;
    }
    if ( lookup == null ) {
      return -1;
    }

    for ( int i = 0; i < array.length; i++ ) {
      if ( lookup.equalsIgnoreCase( array[i] ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Search for a string in a list of strings and return the index.
   * 
   * @param lookup
   *          The string to search for
   * @param list
   *          The ArrayList of strings to look in
   * @return The index of a search string in an array of strings. -1 if not found.
   */
  public static final int indexOfString( String lookup, List list ) {
    if ( list == null ) {
      return -1;
    }

    for ( int i = 0; i < list.size(); i++ ) {
      String compare = (String) list.get( i );
      if ( lookup.equalsIgnoreCase( compare ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Sort the strings of an array in alphabetical order.
   * 
   * @param input
   *          The array of strings to sort.
   * @return The sorted array of strings.
   */
  public static final String[] sortStrings( String[] input ) {
    Arrays.sort( input );
    return input;
  }

  /**
   * Convert strings separated by a string into an array of strings.
   * <p>
   * <code>
	 Example: a;b;c;d    ==  new String[] { a, b, c, d }
	 * </code>
   * 
   * @param string
   *          The string to split
   * @param separator
   *          The separator used.
   * @return the string split into an array of strings
   * 
   * @deprecated
   */
  public static final String[] splitString( String string, String separator ) {
    /*
     * 0123456 Example a;b;c;d --> new String[] { a, b, c, d }
     */
    List<String> list = new ArrayList<String>();

    if ( string == null || string.length() == 0 ) {
      return new String[] {};
    }

    int sepLen = separator.length();
    int from = 0;
    int end = string.length() - sepLen + 1;

    for ( int i = from; i < end; i += sepLen ) {
      if ( string.substring( i, i + sepLen ).equalsIgnoreCase( separator ) ) {
        // OK, we found a separator, the string to add to the list
        // is [from, i[
        list.add( NVL( string.substring( from, i ), "" ) ); //$NON-NLS-1$
        from = i + sepLen;
      }
    }

    // Wait, if the string didn't end with a separator, we still have information at the end of the string...
    // In our example that would be "d"...
    if ( from + sepLen <= string.length() ) {
      list.add( NVL( string.substring( from, string.length() ), "" ) ); //$NON-NLS-1$
    }

    return (String[]) list.toArray( new String[list.size()] );
  }

  /**
   * Convert strings separated by a character into an array of strings.
   * <p>
   * <code>
	 Example: a;b;c;d    ==  new String[] { a, b, c, d }
	 * </code>
   * 
   * @param string
   *          The string to split
   * @param separator
   *          The separator used.
   * @return the string split into an array of strings
   */
  public static final String[] splitString( String string, char separator ) {
    /*
     * 0123456 Example a;b;c;d --> new String[] { a, b, c, d }
     */
    List<String> list = new ArrayList<String>();

    if ( string == null || string.length() == 0 ) {
      return new String[] {};
    }

    int from = 0;
    int end = string.length();

    for ( int i = from; i < end; i += 1 ) {
      if ( string.charAt( i ) == separator ) {
        // OK, we found a separator, the string to add to the list
        // is [from, i[
        list.add( NVL( string.substring( from, i ), "" ) ); //$NON-NLS-1$
        from = i + 1;
      }
    }

    // Wait, if the string didn't end with a separator, we still have information at the end of the string...
    // In our example that would be "d"...
    if ( from + 1 <= string.length() ) {
      list.add( NVL( string.substring( from, string.length() ), "" ) ); //$NON-NLS-1$
    }

    return (String[]) list.toArray( new String[list.size()] );
  }

  /**
   * Convert strings separated by a string into an array of strings.
   * <p>
   * <code>
   *   Example /a/b/c --> new String[] { a, b, c }
   * </code>
   * 
   * @param path
   *          The string to split
   * @param separator
   *          The separator used.
   * @return the string split into an array of strings
   */
  public static final String[] splitPath( String path, String separator ) {
    /*
     * 012345 Example /a/b/c --> new String[] { a, b, c }
     */
    if ( path == null || path.length() == 0 || path.equals( separator ) ) {
      return new String[] {};
    }
    int sepLen = separator.length();
    int nr_separators = 0;
    int from = path.startsWith( separator ) ? sepLen : 0;
    if ( from != 0 ) {
      nr_separators++;
    }

    for ( int i = from; i < path.length(); i += sepLen ) {
      if ( path.substring( i, i + sepLen ).equalsIgnoreCase( separator ) ) {
        nr_separators++;
      }
    }

    String[] spath = new String[nr_separators];
    int nr = 0;
    for ( int i = from; i < path.length(); i += sepLen ) {
      if ( path.substring( i, i + sepLen ).equalsIgnoreCase( separator ) ) {
        spath[nr] = path.substring( from, i );
        nr++;

        from = i + sepLen;
      }
    }
    if ( nr < spath.length ) {
      spath[nr] = path.substring( from );
    }

    //
    // a --> { a }
    //
    if ( spath.length == 0 && path.length() > 0 ) {
      spath = new String[] { path };
    }

    return spath;
  }

  /**
   * Sorts the array of Strings, determines the uniquely occuring strings.
   * 
   * @param strings
   *          the array that you want to do a distinct on
   * @return a sorted array of uniquely occuring strings
   */
  public static final String[] getDistinctStrings( String[] strings ) {
    if ( strings == null ) {
      return null;
    }
    if ( strings.length == 0 ) {
      return new String[] {};
    }

    String[] sorted = sortStrings( strings );
    List<String> result = new ArrayList<String>();
    String previous = ""; //$NON-NLS-1$
    for ( int i = 0; i < sorted.length; i++ ) {
      if ( !sorted[i].equalsIgnoreCase( previous ) ) {
        result.add( sorted[i] );
      }
      previous = sorted[i];
    }

    return (String[]) result.toArray( new String[result.size()] );
  }

  /**
   * Returns a string of the stack trace of the specified exception
   */
  public static final String getStackTracker( Throwable e ) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter( stringWriter );
    e.printStackTrace( printWriter );
    String string = stringWriter.getBuffer().toString();
    try {
      stringWriter.close();
    } catch ( IOException ioe ) {
    } // is this really required?
    return string;
  }

  /**
   * Check if the string supplied is empty. A String is empty when it is null or when the length is 0
   * 
   * @param string
   *          The string to check
   * @return true if the string supplied is empty
   */
  public static final boolean isEmpty( String string ) {
    return string == null || string.length() == 0;
  }

  /**
   * Check if the stringBuffer supplied is empty. A StringBuffer is empty when it is null or when the length is 0
   * 
   * @param string
   *          The stringBuffer to check
   * @return true if the stringBuffer supplied is empty
   */
  public static final boolean isEmpty( StringBuffer string ) {
    return string == null || string.length() == 0;
  }

  /**
   * Convert a normal name with spaces into an ID: with underscores replacing the spaces, etc.
   * 
   * @param name
   *          the name to convert to an ID
   * @return The ID-ified name
   */
  public static final String toID( String name ) {
    name = Const.replace( name, " ", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, ".", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, ",", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, ":", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "(", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, ")", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "{", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "}", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "[", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "]", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "*", "_TIMES_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "/", "_DIVIDED_BY_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "+", "_PLUS_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "-", "_HYPHEN_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "____", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "___", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "__", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "\"", "" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "`", "" ); //$NON-NLS-1$ //$NON-NLS-2$
    name = Const.replace( name, "'", "" ); //$NON-NLS-1$ //$NON-NLS-2$
    // this line replaces any non ascii chars with an underscore
    name = name.replaceAll( "[^a-zA-Z_0-9]", "_" ); //$NON-NLS-1$ //$NON-NLS-2$

    return name;
  }
}
