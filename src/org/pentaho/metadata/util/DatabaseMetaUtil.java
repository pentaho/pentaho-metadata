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
