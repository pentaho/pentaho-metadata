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

public class LocaleHelper {

    private static final ThreadLocal threadLocales = new ThreadLocal();

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

}
