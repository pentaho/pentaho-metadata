/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.schema.concept.types.localstring;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @deprecated as of metadata 3.0. please see org.pentaho.metadata.model.concept.types.LocalizedString
 */
public class LocalizedStringSettings implements Cloneable {
  public static final LocalizedStringSettings EMPTY = new LocalizedStringSettings();

  private Map<String, String> localeStringMap;

  /**
   * @param localeStringMap
   */
  public LocalizedStringSettings() {
    localeStringMap = new Hashtable<String, String>( 5 );
  }

  public Object clone() throws CloneNotSupportedException {
    LocalizedStringSettings settings = new LocalizedStringSettings();
    settings.localeStringMap.putAll( localeStringMap );
    /*
     * String locales[] = getLocales(); for (int i=0;i<locales.length;i++) { settings.setLocaleString(locales[i],
     * getString(locales[i])); }
     */
    return settings;
  }

  /**
   * @param localeStringMap
   */
  public LocalizedStringSettings( Map<String, String> localeStringMap ) {
    this.localeStringMap = localeStringMap;
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof LocalizedStringSettings == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }
    LocalizedStringSettings rhs = (LocalizedStringSettings) obj;
    return new EqualsBuilder().append( localeStringMap, rhs.localeStringMap ).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder( 19, 163 ).append( localeStringMap ).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE ).append( localeStringMap ).toString();
  }

  /**
   * @return the localeStringMap
   */
  public Map getLocaleStringMap() {
    return localeStringMap;
  }

  /**
   * @param localeStringMap
   *          the localeStringMap to set
   */
  public void setLocaleStringMap( Map<String, String> localeStringMap ) {
    this.localeStringMap = localeStringMap;
  }

  public String getString( String locale ) {
    return (String) localeStringMap.get( locale );
  }

  public void setLocaleString( String locale, String string ) {
    localeStringMap.put( locale, string );
  }

  public String[] getLocales() {
    return (String[]) localeStringMap.keySet().toArray( new String[localeStringMap.keySet().size()] );
  }

  public void clear() {
    localeStringMap.clear();
  }

}
