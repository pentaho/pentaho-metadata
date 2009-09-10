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
package org.pentaho.metadata.util;

import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;

/**
 * This utility class handles DatabaseMeta related tasks needed by metadata layer users,
 * used today in the BI Platform metadata query component.
 */
public class DatabaseMetaUtil {
  
  public static DatabaseInterface getDatabaseInterface(String productName) {
    
    if (productName == null) {
      return null;
    }
  
    productName = productName.toLowerCase();
  
    // special case to map hsql to hypersonic
    if (productName.indexOf("hsql") >= 0) { //$NON-NLS-1$
      productName = "hypersonic"; //$NON-NLS-1$
    }
  
    // look through all available database dialects for a match
    for (int i = 0; i < DatabaseMeta.getDatabaseInterfaces().length; i++) {
      String typeDesc = DatabaseMeta.getDatabaseInterfaces()[i].getDatabaseTypeDesc().toLowerCase();
      if (productName.indexOf(typeDesc) >= 0) {
        return DatabaseMeta.getDatabaseInterfaces()[i];
      }
    }
    
    return null;
  }
}
