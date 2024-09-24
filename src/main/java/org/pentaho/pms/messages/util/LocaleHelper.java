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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.messages.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Locale;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.messages.LocalHelper
 */
public class LocaleHelper {

  private static final ThreadLocal<Locale> threadLocales = new ThreadLocal<Locale>();

  private static Locale defaultLocale;

  public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
  private static String encoding = UTF_8;

  public static final String LEFT_TO_RIGHT = "LTR"; //$NON-NLS-1$
  private static String textDirection = LEFT_TO_RIGHT;

  public static void setDefaultLocale( Locale newLocale ) {
    defaultLocale = newLocale;
  }

  public static Locale getDefaultLocale() {
    return defaultLocale;
  }

  public static void setLocale( Locale newLocale ) {
    threadLocales.set( newLocale );
  }

  public static Locale getLocale() {
    Locale rtn = (Locale) threadLocales.get();
    if ( rtn != null ) {
      return rtn;
    }
    defaultLocale = Locale.getDefault();
    setLocale( defaultLocale );
    return defaultLocale;
  }

  public static void setSystemEncoding( String encoding ) {
    LocaleHelper.encoding = encoding;
  }

  public static void setTextDirection( String textDirection ) {
    // TODO make this ThreadLocal
    LocaleHelper.textDirection = textDirection;
  }

  public static String getSystemEncoding() {
    return encoding;
  }

  public static String getTextDirection() {
    // TODO make this ThreadLocal
    return textDirection;
  }

  public static String getClosestLocale( String locale, String[] locales ) {
    // see if this locale is supported
    if ( locales == null || locales.length == 0 ) {
      return locale;
    }
    if ( locale == null || locale.length() == 0 ) {
      return locales[0];
    }
    String localeLanguage = locale.substring( 0, 2 );
    String localeCountry = ( locale.length() > 4 ) ? locale.substring( 0, 5 ) : localeLanguage;
    int looseMatch = -1;
    int closeMatch = -1;
    int exactMatch = -1;
    for ( int idx = 0; idx < locales.length; idx++ ) {
      if ( locales[idx].equals( locale ) ) {
        exactMatch = idx;
        break;
      } else if ( locales[idx].length() > 1 && locales[idx].substring( 0, 2 ).equals( localeLanguage ) ) {
        looseMatch = idx;
      } else if ( locales[idx].length() > 4 && locales[idx].substring( 0, 5 ).equals( localeCountry ) ) {
        closeMatch = idx;
      }
    }
    if ( exactMatch == -1 ) {
      if ( closeMatch != -1 ) {
        locale = locales[closeMatch];
      } else if ( looseMatch != -1 ) {
        locale = locales[looseMatch];
      } else {
        // no locale is close , just go with the first?
        locale = locales[0];
      }
    }
    return locale;
  }

  // From the BI-Platform LocaleHelper

  /**
   * This method is called to convert strings from ISO-8859-1 (post/get parameters for example) into the default system
   * locale.
   * 
   * @param isoString
   * @return Re-encoded string
   */
  public static String convertISOStringToSystemDefaultEncoding( String isoString ) {
    return convertEncodedStringToSystemDefaultEncoding( "ISO-8859-1", isoString ); //$NON-NLS-1$
  }

  /**
   * This method converts strings from a known encoding into a string encoded by the system default encoding.
   * 
   * @param fromEncoding
   * @param encodedStr
   * @return Re-encoded string
   */
  public static String convertEncodedStringToSystemDefaultEncoding( String fromEncoding, String encodedStr ) {
    return convertStringEncoding( encodedStr, fromEncoding, LocaleHelper.getSystemEncoding() );
  }

  /**
   * This method converts an ISO-8859-1 encoded string to a UTF-8 encoded string.
   * 
   * @param isoString
   * @return Re-encoded string
   */
  public static String isoToUtf8( String isoString ) {
    return convertStringEncoding( isoString, "ISO-8859-1", "UTF-8" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * This method converts a UTF8-encoded string to ISO-8859-1
   * 
   * @param utf8String
   * @return Re-encoded string
   */
  public static String utf8ToIso( String utf8String ) {
    return convertStringEncoding( utf8String, "UTF-8", "ISO-8859-1" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * This method converts strings between various encodings.
   * 
   * @param sourceString
   * @param sourceEncoding
   * @param targetEncoding
   * @return Re-encoded string.
   */
  public static String convertStringEncoding( String sourceString, String sourceEncoding, String targetEncoding ) {
    String targetString = null;
    if ( null != sourceString && !sourceString.equals( "" ) ) { //$NON-NLS-1$
      try {
        byte[] stringBytesSource = sourceString.getBytes( sourceEncoding );
        targetString = new String( stringBytesSource, targetEncoding );
      } catch ( UnsupportedEncodingException e ) {
        throw new RuntimeException( e );
      }
    } else {
      targetString = sourceString;
    }
    return targetString;
  }

  /**
   * @param aString
   * @return true if the provided string is completely within the US-ASCII character set.
   */
  public static boolean isAscii( String aString ) {
    return isWithinCharset( aString, "US-ASCII" ); //$NON-NLS-1$
  }

  /**
   * @param aString
   * @return true if the provided string is completely within the Latin-1 character set (ISO-8859-1).
   */
  public static boolean isLatin1( String aString ) {
    return isWithinCharset( aString, "ISO-8859-1" ); //$NON-NLS-1$
  }

  /**
   * @param aString
   * @param charsetTarget
   * @return true if the provided string is completely within the target character set.
   */
  public static boolean isWithinCharset( String aString, String charsetTarget ) {
    byte[] stringBytes = aString.getBytes();
    CharsetDecoder decoder = Charset.forName( charsetTarget ).newDecoder();
    try {
      decoder.decode( ByteBuffer.wrap( stringBytes ) );
      return true;
    } catch ( CharacterCodingException ignored ) {
      //ignored
    }
    return false;
  }

}
