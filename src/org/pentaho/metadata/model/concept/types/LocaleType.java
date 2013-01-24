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
