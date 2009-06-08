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

import java.io.Serializable;

/**
 * The LocaleType contains metadata information about a locale.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LocaleType implements Serializable {

  private static final long serialVersionUID = 5282520977042081601L;

  private String code;
  private String description;
  
  public LocaleType() {
  }
  
  public LocaleType(String code, String description) {
    this.code = code;
    this.description = description;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  /**
   * the locale code
   * 
   * @return the locale code
   */
  public String getCode() {
    return code;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * a user friendly description of the locale
   * 
   * @return description
   */
  public String getDescription() {
    return description;
  }
  
  @Override
  public boolean equals(Object object) {
    LocaleType l = (LocaleType)object;
    return  
    ((getDescription() == null && l.getDescription() == null) ||
     (getDescription() != null && getDescription().equals(l.getDescription()))) &&
    ((getCode() == null && l.getCode() == null) ||
         (getCode() != null && getCode().equals(l.getCode())));
  }
}
