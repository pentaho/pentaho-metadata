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
package org.pentaho.pms.util;

import java.util.ResourceBundle;

/**
 * 
 * Gets version information from either the version.properties file, or the .jar manifest.
 * 
 * @author mbatchel
 * 
 */
public class VersionHelper implements IVersionHelper {

  public String getVersionInformation() {
    return getVersionInformation( VersionHelper.class );
  }

  public String getVersionInformation( Class clazz ) {
    return getVersionInformation( clazz, true );
  }

  public String getVersionInformation( Class clazz, boolean includeTitle ) {
    // The following two lines read from the MANIFEST.MF
    String implTitle = clazz.getPackage().getImplementationTitle();
    String implVersion = clazz.getPackage().getImplementationVersion();
    if ( implVersion != null ) {
      // If we're in a .jar file, then we can return the version information
      // from the .jar file.
      if ( includeTitle ) {
        return implTitle + " " + implVersion; //$NON-NLS-1$
      } else {
        return implVersion;
      }
    } else {
      // We're not in a .jar file - try to find the build-res/version file and
      // read the version information from that.
      try {
        ResourceBundle bundle = ResourceBundle.getBundle( "build-res.version" ); //$NON-NLS-1$
        StringBuffer buff = new StringBuffer();
        if ( includeTitle ) {
          buff.append( bundle.getString( "impl.title" ) ).append( ' ' ); //$NON-NLS-1$
        }

        buff.append( bundle.getString( "release.major.number" ) ).append( '.' ).append( bundle.getString( "release.minor.number" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        buff.append( '.' )
            .append( bundle.getString( "release.milestone.number" ) ).append( '.' ).append( bundle.getString( "release.build.number" ) ).append( " (class)" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return buff.toString();
      } catch ( Exception ex ) {
        if ( includeTitle ) {
          return "Pentaho Metadata Editor - No Version Information Available"; //$NON-NLS-1$          
        } else {
          return "No Version Information Available"; //$NON-NLS-1$
        }
      }
    }
  }

}
