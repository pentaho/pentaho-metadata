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
package org.pentaho.metadata.messages;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "org.pentaho.metadata.messages.messages"; //$NON-NLS-1$

  private static final Map<Locale, ResourceBundle> locales = Collections
      .synchronizedMap( new HashMap<Locale, ResourceBundle>() );

  protected static Map<Locale, ResourceBundle> getLocales() {
    return locales;
  }

  private static ResourceBundle getBundle() {
    Locale locale = LocaleHelper.getLocale();
    ResourceBundle bundle = (ResourceBundle) locales.get( locale );
    if ( bundle == null ) {
      bundle = ResourceBundle.getBundle( BUNDLE_NAME, locale );
      locales.put( locale, bundle );
    }
    return bundle;
  }

  public static String getString( String key, Object... params ) {
    return getString( getBundle(), key, params );
  }

  public static String getErrorString( String key, Object... params ) {
    return getErrorString( getBundle(), key, params );
  }

  /**
   * Get a formatted error message. The message consists of two parts. The first part is the error numeric Id associated
   * with the key used to identify the message in the resource file. For instance, suppose the error key is
   * MyClass.ERROR_0068_FILE_NOT_FOUND. The first part of the error msg would be "0068". The second part of the returned
   * string is simply the <code>msg</code> parameter.
   * 
   * Currently the format is: error key - error msg For instance: "0068 - File not found"
   * 
   * @param key
   *          String containing the key that was used to obtain the <code>msg</code> parameter from the resource file.
   * @param msg
   *          String containing the message that was obtained from the resource file using the <code>key</code>
   *          parameter.
   * @return String containing the formatted error message.
   */
  public static String formatErrorMessage( String key, String msg ) {
    int end = key.indexOf( ".ERROR_" ); //$NON-NLS-1$
    end = ( end < 0 ) ? key.length() : Math.min( end + ".ERROR_0000".length(), key.length() ); //$NON-NLS-1$
    return Messages.getString( "Messages.ERROR_FORMAT_MASK", key.substring( 0, end ), msg ); //$NON-NLS-1$
  }

  public static String getErrorString( ResourceBundle bundle, String key, Object... params ) {
    return formatErrorMessage( key, getString( bundle, key, params ) );
  }

  public static String getString( ResourceBundle bundle, String key, Object... params ) {
    try {
      return MessageFormat.format( bundle.getString( key ), params );
    } catch ( Exception e ) {
      return '!' + key + '!';
    }
  }
}
