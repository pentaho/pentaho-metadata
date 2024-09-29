/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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

  private Map<String, String> localeStringMap;

  public LocalizedString() {
    localeStringMap = new HashMap<String, String>();
  }

  public LocalizedString( Map<String, String> localeStringMap ) {
    this.localeStringMap = localeStringMap;
  }

  public LocalizedString( String locale, String value ) {
    this();
    localeStringMap.put( locale, value );
  }

  /**
   * returns a string from the map, based on locale
   * 
   * @param locale
   *          the locale to lookup
   * 
   * @return the localized string value
   */
  public String getString( String locale ) {
    return (String) localeStringMap.get( locale );
  }

  public String getLocalizedString( final String locale ) {
    String str = getString( locale );
    String tmpLocale = locale;
    while ( stringIsEmpty( str ) && tmpLocale != null && tmpLocale.indexOf( '_' ) > 0 ) {
      tmpLocale = tmpLocale.substring( 0, tmpLocale.lastIndexOf( '_' ) );
      str = getString( tmpLocale );
    }
    if ( !stringIsEmpty( str ) ) {
      return str;
    } else {
      return getString( DEFAULT_LOCALE );
    }
  }

  private boolean stringIsEmpty( String str ) {
    return ( str == null || str.length() == 0 );
  }

  public void setString( String locale, String string ) {
    localeStringMap.put( locale, string );
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
  public boolean equals( Object object ) {
    LocalizedString l = (LocalizedString) object;
    return localeStringMap.equals( l.getLocaleStringMap() );
  }
}
