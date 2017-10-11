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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
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
