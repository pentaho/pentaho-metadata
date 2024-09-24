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
package org.pentaho.pms.locale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.messages.Messages;

@SuppressWarnings( "deprecation" )
public class Locales extends ChangedFlag {
  public static final String EN_US = "en_US"; //$NON-NLS-1$

  private List<LocaleInterface> localeList;

  public Locales() {
    localeList = new ArrayList<LocaleInterface>();

    setDefault();
  }

  public void setDefault() {
    LocaleInterface locale = new LocaleMeta( EN_US, Messages.getString( "Locales.USER_LOCALE_DESCRIPTION" ), 1, true ); //$NON-NLS-1$
    addLocale( locale );
  }

  /**
   * @return the locales
   */
  public List getLocaleList() {
    return localeList;
  }

  /**
   * @param locales
   *          the locales to set
   */
  public void setLocaleList( List<LocaleInterface> locales ) {
    this.localeList = locales;
  }

  public int nrLocales() {
    return localeList.size();
  }

  public LocaleInterface getLocale( int i ) {
    return (LocaleInterface) localeList.get( i );
  }

  public void removeLocale( int i ) {
    localeList.remove( i );
    setChanged( true );
  }

  public void addLocale( LocaleInterface locale ) {
    localeList.add( locale );
    setChanged( true );
  }

  public void addLocale( int index, LocaleInterface locale ) {
    localeList.add( index, locale );
    setChanged( true );
  }

  public int indexOfLocale( LocaleInterface locale ) {
    return localeList.indexOf( locale );
  }

  public void setLocale( int idx, LocaleInterface locale ) {
    localeList.set( idx, locale );
    setChanged( true );
  }

  /**
   * @return the activeLocale
   */
  public String getActiveLocale() {
    for ( int i = 0; i < nrLocales(); i++ ) {
      if ( getLocale( i ).isActive() ) {
        return getLocale( i ).getCode();
      }
    }
    return EN_US; // Just to get something back :-)
  }

  /**
   * @param activeLocale
   *          the locale code to set as active
   */
  public void setActiveLocale( String activeLocale ) {
    for ( int i = 0; i < nrLocales(); i++ ) {
      LocaleInterface locale = getLocale( i );
      locale.setActive( locale.getCode().equalsIgnoreCase( activeLocale ) );
    }
  }

  public void clearChanged() {
    setChanged( false );
  }

  /**
   * 
   * @return
   */
  public String[] getLocaleCodes() {
    String[] codes = new String[localeList.size()];
    for ( int i = 0; i < codes.length; i++ ) {
      codes[i] = getLocale( i ).getCode();
    }
    return codes;
  }

  public void sortLocales() {
    Collections.sort( localeList );
  }
}
