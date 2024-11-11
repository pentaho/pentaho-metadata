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

package org.pentaho.metadata.model.concept.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.pentaho.di.core.util.DateDetector;
import org.pentaho.metadata.model.concept.types.DataType;

public class DataFormatter {

  static final Logger log = LogManager.getLogger( DataFormatter.class );

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
