/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.model.concept.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This property contains a map of localized strings.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LocalizedString implements Serializable {
  
  private static final long serialVersionUID = 8214549012790547810L;
  
  // TODO: Create a default locale in the metadata instead of just saying
  // English is the default!
  // NOTE: please see http://jira.pentaho.org/browse/PMD-166 for more
  // information
  private static final String DEFAULT_LOCALE = "en_US"; //$NON-NLS-1$
  
  private Map<String,String> localeStringMap;
  
  public LocalizedString() {
    localeStringMap = new HashMap<String,String>();
  }
  
  public LocalizedString(Map<String, String> localeStringMap) {
    this.localeStringMap = localeStringMap;
  }
  
  public LocalizedString(String locale, String value) {
    this();
    localeStringMap.put(locale, value);
  }
  
  /**
   * returns a string from the map, based on locale
   * 
   * @param locale the locale to lookup
   * 
   * @return the localized string value
   */
  public String getString(String locale) {
      return (String) localeStringMap.get(locale);
  }

  public String getLocalizedString(String locale) {
    String str = getString(locale);
    if ((str == null || str.trim().length() == 0) && locale != null && locale.indexOf('_') > 0) {
      str = getLocalizedString(locale.substring(0, locale.lastIndexOf('_')));
    }
    if (str == null || str.trim().length() == 0) {
      str = getLocalizedString(DEFAULT_LOCALE);
    }
    return str;
  }
  
  public void setString(String locale, String string) {
      localeStringMap.put(locale, string);
  }

  /**
   * return a set of locales that are supported.
   * 
   * @return locales
   */
  public Set<String> getLocales() {
      return localeStringMap.keySet();
  }
  
  /**
   * return the map of locales
   * 
   * @return locale string map
   */
  public Map<String, String> getLocaleStringMap() {
    return localeStringMap;
  }
  
  @Override
  public boolean equals(Object object) {
    LocalizedString l = (LocalizedString)object;
    return  
    localeStringMap.equals(l.getLocaleStringMap());
  }
}
