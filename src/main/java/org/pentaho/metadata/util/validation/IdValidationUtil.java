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


import org.pentaho.pms.messages.Messages;

/**
 * Created by Yury_Bakhmutski on 10/25/2017.
 */
public class IdValidationUtil {

  /**
   * List of unacceptable characters which should corresponds to the regexp's inside the Util#toId method.
   */
  public static final String MQL_RESERVED_CHARS = " .,:(){}[]\"`'*/+-";

  /**
   * @param id proposed id for column or table
   * @return IdValidationStatus
   */
  public static ValidationStatus validateId( CharSequence id ) {
    if ( id == null || id.length() == 0 ) {
      return ValidationStatus.invalid( Messages.getString( "Util.ERROR_0001_ID_EMPTY_OR_NULL" ) );
    }

    for ( int i = 0, len = id.length(); i < len; i++ ) {
      char ch = id.charAt( i );
      if ( isUnacceptableCharacter( ch ) ) {
        return ValidationStatus.invalid( Messages.getString(
          "Util.ERROR_0002_ID_CONTAINS_RESERVED_SYMBOLS", id.toString(), "" ) );
      }
    }

    return ValidationStatus.valid();
  }

  /**
   * Check if character is unacceptable for MQL and need to be converted.
   *
   * @param ch character to check
   * @return true if character is unacceptable for MQL, false otherwise
   */
  private static boolean isUnacceptableCharacter( char ch ) {
    return MQL_RESERVED_CHARS.indexOf( ch ) != -1;
  }

  public static IllegalArgumentException idValidationFailed( String id ) {
    return new IllegalArgumentException(
      "Cannot set id '" + id + "'. Please use Util.toId() to create a well-formed identifier" );
  }

}
