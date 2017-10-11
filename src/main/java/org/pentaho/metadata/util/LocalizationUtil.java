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
 * Copyright (c) 2011 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * This utility class imports and exports all localized strings that exist in a Pentaho Metadata domain.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class LocalizationUtil {

  private static final Log logger = LogFactory.getLog( LocalizationUtil.class );

  protected void exportLocalizedPropertiesRecursively( Properties props, IConcept parent, String locale ) {
    for ( String propName : parent.getChildProperties().keySet() ) {
      if ( parent.getChildProperty( propName ) instanceof LocalizedString ) {
        // externalize string
        String key = stringizeTokens( parent.getUniqueId() ) + ".[" + escapeKey( propName ) + "]";
        LocalizedString lstr = (LocalizedString) parent.getChildProperty( propName );
        String value = lstr.getLocalizedString( locale );
        if ( value == null ) {
          value = "";
        }
        props.setProperty( key, value );
      }
    }
    if ( parent.getChildren() != null ) {
      for ( IConcept child : parent.getChildren() ) {
        exportLocalizedPropertiesRecursively( props, child, locale );
      }
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "concept " + stringizeTokens( parent.getUniqueId() ) + " does not have children" );
      }
    }
  }

  /**
   * This method creates a properties bundle containing all the localized strings for a specific locale in a domain
   * 
   * @param domain
   *          the domain object to extract localized strings from
   * @param locale
   *          the locale to extract
   * @return a properties object containing all the localized strings
   */
  public Properties exportLocalizedProperties( Domain domain, String locale ) {
    Properties props = new Properties();
    exportLocalizedPropertiesRecursively( props, domain, locale );
    return props;
  }

  protected int findCloseBracket( String key, int start ) {
    int end = key.indexOf( "]", start );
    if ( end == -1 ) {
      return -1;
    }
    if ( end != key.length() - 1 ) {
      if ( key.charAt( end + 1 ) == ']' ) {
        return findCloseBracket( key, end + 2 );
      }
    }
    return end;
  }

  protected String escapeKey( String key ) {
    if ( key.indexOf( "]" ) < 0 ) {
      return key;
    }
    return key.replaceAll( "\\]", "\\]\\]" );

  }

  protected String unescapeKey( String key ) {
    if ( key.indexOf( "]" ) < 0 ) {
      return key;
    }
    return key.replaceAll( "\\]\\]", "\\]" );
  }

  protected String stringizeTokens( List<String> keys ) {
    if ( keys.size() == 0 ) {
      return null;
    }
    String key = "[" + escapeKey( keys.get( 0 ) ) + "]";
    for ( int i = 1; i < keys.size(); i++ ) {
      key += ".[" + escapeKey( keys.get( i ) ) + "]";
    }
    return key;
  }

  protected List<String> splitTokens( String key ) {
    List<String> tokens = new ArrayList<String>();
    int start = key.indexOf( "[" );
    while ( start >= 0 ) {
      int end = findCloseBracket( key, start + 1 );
      if ( end == -1 ) {
        // invalid token, return null
        return null;
      }
      tokens.add( unescapeKey( key.substring( start + 1, end ) ) );
      start = key.indexOf( "[", end );
    }
    return tokens;
  }

  /**
   * This method returns a list of missing and extra keys specified in a properties bundle
   * 
   * @param domain
   *          the domain object to analyze
   * @param props
   *          the imported properties to analyze
   * @param locale
   *          the locale to analyze
   * @return messages
   */
  public List<String> analyzeImport( Domain domain, Properties props, String locale ) {
    ArrayList<String> messages = new ArrayList<String>();

    // determine missing strings
    Properties origProps = exportLocalizedProperties( domain, locale );
    Properties cloneOrig = (Properties) origProps.clone();
    for ( Object key : origProps.keySet() ) {
      if ( props.containsKey( key ) ) {
        cloneOrig.remove( key );
      }
    }

    // anything left in cloneOrig was missing
    for ( Object key : cloneOrig.keySet() ) {
      messages.add( Messages.getString( "LocalizationUtil.MISSING_KEY_MESSAGE", key ) );
    }

    // determine extra strings
    Properties cloneProps = (Properties) props.clone();

    for ( Object key : props.keySet() ) {
      if ( origProps.containsKey( key ) ) {
        cloneProps.remove( key );
      }
    }

    // anything left in cloneProps was extra
    for ( Object key : cloneProps.keySet() ) {
      messages.add( Messages.getString( "LocalizationUtil.EXTRA_KEY_MESSAGE", key ) );
    }

    return messages;
  }

  /**
   * This method imports a set of localized properties to a specific locale within the metadata model
   * <p/>
   * TODO: Do more warn logging on missing keys
   * 
   * @param domain
   *          Domain object to populate
   * @param props
   *          Properties object to extract key value pairs from
   * @param locale
   *          the locale in which to populate
   */
  public void importLocalizedProperties( Domain domain, Properties props, String locale ) {
    // Ignore this method call if any parameters are missing
    if ( null == domain || null == props || StringUtils.isEmpty( locale ) ) {
      return;
    }

    // Add new locale if it does not already exist in the domain
    List<LocaleType> localeTypes = domain.getLocales();
    boolean addLocale = true;

    if ( localeTypes != null ) {
      for ( LocaleType localeType : localeTypes ) {
        if ( localeType.getCode().equals( locale ) ) {
          addLocale = false;
          break;
        }
      }
    }

    if ( addLocale ) {
      LocaleType localeType = new LocaleType();
      localeType.setCode( locale );

      domain.addLocale( localeType );
    }

    for ( Object key : props.keySet() ) {
      String k = (String) key;
      if ( logger.isDebugEnabled() ) {
        logger.debug( "importing key " + k + "=" + props.getProperty( k ) );
      }
      List<String> tokens = splitTokens( k );
      if ( tokens != null && tokens.size() >= 1 ) {
        String property = tokens.remove( tokens.size() - 1 );
        IConcept concept = domain.getChildByUniqueId( tokens );
        if ( concept != null ) {
          LocalizedString localizedString = (LocalizedString) concept.getProperty( property );
          if ( localizedString != null ) {
            localizedString.setString( locale, props.getProperty( k ) );
          }
        }
      }
    }
  }
}
