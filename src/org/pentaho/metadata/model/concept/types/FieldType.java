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

public enum FieldType {
  OTHER("FieldType.USER_OTHER_DESC"),
  DIMENSION("FieldType.USER_DIMENSION_DESC"),
  FACT("FieldType.USER_FACT_DESC"),
  KEY("FieldType.USER_KEY_DESC"),
  ATTRIBUTE("FieldType.USER_ATTRIBUTE_DESC");
  
  private String description;
  
  FieldType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static FieldType guessFieldType(String name) {
    String fieldname = name.toLowerCase();
    String ids[] = new String[] { "id", "pk", "tk", "sk" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // Is it a key field?
    boolean isKey = false;
    for (int i=0;i<ids.length && !isKey;i++) {
      if (fieldname.startsWith(ids[i]+"_") || fieldname.endsWith("_"+ids[i])) isKey=true; //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (isKey) return KEY;

    return DIMENSION;
  }
}
