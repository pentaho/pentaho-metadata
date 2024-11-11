/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.locale;

import org.pentaho.di.core.changed.ChangedFlag;

public class LocaleMeta extends ChangedFlag implements LocaleInterface {
  private String code;
  private String description;
  private int order;
  private boolean active;

  /**
   * @param code
   * @param description
   * @param order
   * @param active
   */
  public LocaleMeta( String code, String description, int order, boolean active ) {
    super();
    this.code = code;
    this.description = description;
    this.order = order;
    this.active = active;
  }

  public LocaleMeta() {
    super();
  }

  /**
   * @return the active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * @param active
   *          the active to set
   */
  public void setActive( boolean active ) {
    this.active = active;
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code
   *          the code to set
   */
  public void setCode( String code ) {
    this.code = code;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * @return the order
   */
  public int getOrder() {
    return order;
  }

  /**
   * @param order
   *          the order to set
   */
  public void setOrder( int order ) {
    this.order = order;
  }

  /**
   * Clear the changed flag
   */
  public void clearChanged() {
    setChanged( false );
  }

  public int compareTo( LocaleInterface obj ) {
    LocaleMeta locale = (LocaleMeta) obj;
    if ( order == locale.order ) {
      return code.compareTo( locale.code );
    }
    return new Integer( order ).compareTo( new Integer( locale.order ) );
  }

}
