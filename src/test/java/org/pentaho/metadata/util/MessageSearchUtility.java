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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSearchUtility {

  static Properties existingMessages = new Properties();
  static String existingMessageBundle = null;
  static String messageBundle = null;
  static boolean writeFound = false;
  static boolean appendToEnd = true;
  static TreeSet<String> keys = new TreeSet<String>();
  static TreeSet<String> towrite = new TreeSet<String>();
  static PrintWriter pw = null;

  public static void main( String[] args ) throws Exception {

    File f = null;
    if ( args.length > 0 ) {
      f = new File( args[0] );
    } else {
      f = new File( "src/org/pentaho/metadata" );
    }
    if ( args.length > 1 ) {
      existingMessageBundle = args[1];
    } else {
      existingMessageBundle = "src/org/pentaho/pms/locale/messages.properties";
    }

    existingMessages.load( new FileInputStream( existingMessageBundle ) );

    if ( args.length > 2 ) {
      messageBundle = args[2];
    } else {
      messageBundle = "src/org/pentaho/metadata/messages/messages.properties";
    }

    writeFound = !messageBundle.equals( existingMessageBundle );

    if ( args.length > 3 ) {
      appendToEnd = "true".equals( args[2] );
    }

    traverse( f );

    pw = new PrintWriter( new FileWriter( messageBundle, appendToEnd ) );
    for ( String str : towrite ) {
      pw.println( str );
    }

    pw.close();
    // System.out.println(val + " messages found");
  }

  public static void traverse( File f ) throws Exception {
    File[] fs = f.listFiles();
    for ( File file : fs ) {
      if ( file.isDirectory() ) {
        traverse( file );
      } else {
        if ( file.getName().equals( "MessageSearchUtility.java" ) ) {
          continue;
        }
        if ( file.getName().endsWith( ".java" ) ) {
          parseJavaFile( file );
        }
      }
    }
  }

  static int val = 0;

  public static void parseJavaFile( File f ) throws Exception {
    // System.out.println("Parsing " + f.getCanonicalPath());

    StringBuffer sb = new StringBuffer();
    BufferedReader br = new BufferedReader( new FileReader( f ) );
    String line = br.readLine();
    while ( line != null ) {
      sb.append( line ); // .append("\n");
      line = br.readLine();
    }

    int v = 0;

    // it would be ideal if this could tell the difference between a commented out or an active message

    Pattern p = Pattern.compile( "Messages\\.(getString|getErrorString)\\((.*?)\\)" );
    Pattern p2 = Pattern.compile( "\"(.*?)\"" );
    Matcher m = p.matcher( sb );
    while ( m.find() ) {
      Matcher m2 = p2.matcher( m.group( 2 ) );
      if ( m2.find() ) {
        boolean found = ( existingMessages.get( m2.group( 1 ) ) != null );
        String messageKey = m2.group( 1 );
        String messageVal = found ? existingMessages.getProperty( messageKey ) : "__NOT_FOUND__";

        // System.out.println(
        // "" +
        // found
        // + " " + m2.group(1) + "="
        // + messageVal
        // );
        if ( writeFound || !found ) {
          if ( !keys.contains( messageKey ) ) {
            towrite.add( messageKey + "=" + messageVal );
            keys.add( messageKey );
          }

        }
      }
      val++;
      v++;
    }
    if ( v > 0 ) {
      System.out.println( "VAL for file: " + v );
    }
  }
}
