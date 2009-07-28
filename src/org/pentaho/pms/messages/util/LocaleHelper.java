/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.messages.util;

import java.util.Locale;

/**
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.messages.LocalHelper
 */
public class LocaleHelper {

    private static final ThreadLocal<Locale> threadLocales = new ThreadLocal<Locale>();

    private static Locale defaultLocale;

    public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
    private static String encoding = UTF_8;

    public static final String LEFT_TO_RIGHT = "LTR"; //$NON-NLS-1$
    private static String textDirection = LEFT_TO_RIGHT;

    public static void setDefaultLocale(Locale newLocale) {
        defaultLocale = newLocale;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setLocale(Locale newLocale) {
        threadLocales.set(newLocale);
    }

    public static Locale getLocale() {
        Locale rtn = (Locale) threadLocales.get();
        if (rtn != null) {
            return rtn;
        }
        defaultLocale = Locale.getDefault();
        setLocale(defaultLocale);
        return defaultLocale;
    }

    public static void setSystemEncoding(String encoding) {
    	LocaleHelper.encoding = encoding;
    }

    public static void setTextDirection(String textDirection) {
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

    public static String getClosestLocale( String locale, String locales[] ) {
        // see if this locale is supported
      if( locales == null || locales.length == 0 ) {
        return locale;
      }
      if( locale == null || locale.length() == 0 ) {
        return locales[ 0 ];
      }
      String localeLanguage = locale.substring(0, 2);
      String localeCountry = (locale.length() > 4) ? locale.substring(0, 5) : localeLanguage;
        int looseMatch = -1;
        int closeMatch = -1;
        int exactMatch = -1;
        for( int idx=0; idx<locales.length; idx++ ) {
          if( locales[idx].equals( locale ) ) {
            exactMatch = idx;
            break;
          }
          else if( locales[idx].length() > 1 && locales[idx].substring(0, 2).equals( localeLanguage ) ) {
            looseMatch = idx;
          }
          else if( locales[idx].length() > 4 && locales[idx].substring(0, 5).equals( localeCountry ) ) {
            closeMatch = idx;
          }
        }
        if( exactMatch != -1 ) {
          // do nothing we have an exact match
        }
        else if( closeMatch != - 1) {
          locale = locales[ closeMatch ];
        }
        else if( looseMatch != - 1) {
          locale = locales[ looseMatch ];
        }
        else {
          // no locale is close , just go with the first?
          locale = locales[ 0 ];
        }
        return locale;
    }
}
