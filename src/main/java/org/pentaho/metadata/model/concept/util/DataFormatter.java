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
 * Copyright (c) 2016 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.model.concept.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.pentaho.di.core.util.DateDetector;
import org.pentaho.metadata.model.concept.types.DataType;

public class DataFormatter {

  static final Logger log = Logger.getLogger( DataFormatter.class );

  /**
   * 
   * @param datatype {@link String} representation of {@link DataType}
   * @param mask
   * @param data
   * @return {@link String} - data which was formatted by mask
   */
  public static String getFormatedString( String datatype, String mask, Object data ) {
    return getFormatedString( DataType.valueOf( datatype ), mask, data );
  }

  /**
   * 
   * @param datatype
   *          should be one from {@link DataType}
   * @param mask
   * @param data
   * @return {@link String} - data which was formatted by mask or data if we have not correct mask
   */
  public static String getFormatedString( DataType dataType, String mask, Object data ) {
    try {
      switch ( dataType ) {
        case NUMERIC:
          DecimalFormat decimalFormat = new DecimalFormat( mask );
          decimalFormat.setParseBigDecimal( true );
          return decimalFormat.format( data  );
        case DATE:
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat( mask );
          if ( data instanceof Date ) {
            return simpleDateFormat.format( data );
          }
          Date dateFromstring = DateDetector.getDateFromString( String.valueOf( data  ) );
          return simpleDateFormat.format( dateFromstring );
        case STRING:
        case UNKNOWN:
        case BOOLEAN:
        case BINARY:
        case IMAGE:
        case URL:
        default:
          return String.valueOf( data );
      }
    } catch ( Exception e ) {
      log.debug( DataFormatter.class.getName() + " could not apply mask to data. The original data was returned" ); //$NON-NLS-1$  //$NON-NLS-2$
      return String.valueOf( data );
    }
  }

}
