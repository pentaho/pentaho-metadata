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
 * Copyright (c) 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.util.validation;

/**
 * Created by Yury_Bakhmutski on 10/26/2017.
 */
public class ValidationStatus {
  public final StatusEnum statusEnum;
  private String localizedMessage;

  private ValidationStatus() {
    statusEnum = StatusEnum.VALID;
  }

  private ValidationStatus( String message ) {
    statusEnum = StatusEnum.INVALID;
    this.localizedMessage = message;
  }

  public static ValidationStatus valid() {
    return new ValidationStatus();
  }

  public static ValidationStatus invalid( String localizedValidationFailMessage ) {
    return new ValidationStatus( localizedValidationFailMessage );
  }

  public String getLocalizedMessage() {
    return localizedMessage;
  }

  public enum StatusEnum {
    VALID, INVALID;
  }
}
