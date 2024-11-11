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
