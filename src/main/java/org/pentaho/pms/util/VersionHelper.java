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
 * Copyright (c) 2007 - 2009 Pentaho Corporation..  All rights reserved.
 */
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
