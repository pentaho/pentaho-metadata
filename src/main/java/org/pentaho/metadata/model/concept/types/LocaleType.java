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

  public LocaleType( String code, String description ) {
    this.code = code;
    this.description = description;
  }

  public void setCode( String code ) {
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

  public void setDescription( String description ) {
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
  public boolean equals( Object object ) {
    LocaleType l = (LocaleType) object;
    return ( ( getDescription() == null && l.getDescription() == null ) || ( getDescription() != null && getDescription()
        .equals( l.getDescription() ) ) )
        && ( ( getCode() == null && l.getCode() == null ) || ( getCode() != null && getCode().equals( l.getCode() ) ) );
  }
}
