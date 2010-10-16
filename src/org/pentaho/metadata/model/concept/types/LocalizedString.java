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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
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
  public static final String DEFAULT_LOCALE = "en_US"; //$NON-NLS-1$
  
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
    if ((str == null || str.trim().length() == 0) && !DEFAULT_LOCALE.startsWith(locale)) {
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
