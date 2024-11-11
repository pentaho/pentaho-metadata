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

package org.pentaho.metadata.query.model.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.concept.types.DataType;

public class DataTypeDetector {

  public static final String[] COMMON_DATE_FORMATS = new String[] { "MM-dd-yyyy", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy",
    "dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd", "MM-dd-yy", "MM/dd/yy", "dd-MM-yy",
    "dd/MM/yy" };

  /**
   * Class used to detect and modify the column type based on incoming string values. The DataTypeDetector is currently
   * able to detect the following column types:
   * <ul>
   * <li>Strings (default if no other type is found)</li>
   * <li>Booleans</li>
   * <li>Integers</li>
   * <li>Doubles</li>
   * <li>Date</li>
   * <li>Time</li>
   * </ul>
   * And it will also detect if the column is nullable, ie. if null occurs in the values.
   */
  public static DataType getDataType( String valueAsString ) {
    DataType returnType = null;
    boolean _booleanPossible = true;
    boolean _integerPossible = true;
    boolean _doublePossible = true;
    boolean _datePossible = true;
    boolean _timePossible = true;
    boolean _nullPossible = false;

    if ( valueAsString == null ) {
      _nullPossible = true;
    } else {
      if ( _booleanPossible ) {
        try {
          BooleanComparator.parseBoolean( valueAsString );
        } catch ( IllegalArgumentException e ) {
          _booleanPossible = false;
        }
      }
      if ( _doublePossible ) {
        try {
          Double.parseDouble( valueAsString );
        } catch ( NumberFormatException e ) {
          _doublePossible = false;
          _integerPossible = false;
        }
        // If integer is possible, double will always also be possible,
        // but not nescesarily the other way around
        if ( _integerPossible ) {
          try {
            Integer.parseInt( valueAsString );
          } catch ( NumberFormatException e ) {
            _integerPossible = false;
          }
        }
      }
      if ( _datePossible ) {
        DateTimeFormatter fmt = null;
        for ( String mask : COMMON_DATE_FORMATS ) {
          try {
            fmt = DateTimeFormat.forPattern( mask );
            fmt.parseDateTime( valueAsString );
            _datePossible = true;
            break;
          } catch ( IllegalArgumentException e ) {
            _datePossible = false;
          }
        }
      }
      if ( _timePossible ) {
        try {
          new LocalTime( valueAsString );
        } catch ( IllegalArgumentException e ) {
          _timePossible = false;
        }
      }
    }

    if ( _booleanPossible ) {
      returnType = DataType.BOOLEAN;
    } else if ( _integerPossible ) {
      returnType = DataType.NUMERIC;
    } else if ( _doublePossible ) {
      // TODO We need to find a way of passing double in the model as a data type. For now all doubles will be set to
      // NUMERIC
      // returnType = DataType.DOUBLE;
      returnType = DataType.NUMERIC;
    } else if ( _datePossible ) {
      returnType = DataType.DATE;
    } else if ( _timePossible ) {
      // TODO We need to find a way of passing TIME in the model as a data type. For now all TIME will be set to DATE
      // returnType = DataType.TIME;
      returnType = DataType.DATE;
    } else {
      returnType = DataType.STRING;
    }
    return returnType;
  }

  public static Object getValue( String valueAsString ) {
    if ( valueAsString != null ) {
      DataType type = getDataType( valueAsString );
      switch ( type ) {
        case STRING:
          return valueAsString;
        case BOOLEAN:
          return BooleanComparator.parseBoolean( valueAsString );
        case NUMERIC:
          return Integer.parseInt( valueAsString );
          // case DOUBLE:
          // return Double.parseDouble(valueAsString);
        case DATE:
          LocalDate localDate = new LocalDate( valueAsString );
          return new Date( new DateTime( localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth(), 0,
              0, 0, 0 ).getMillis() );
          /*
           * case TIME: LocalTime localTime = new LocalTime(valueAsString); return new Time(new DateTime(1970, 1, 1,
           * localTime .getHourOfDay(), localTime.getMinuteOfHour(), localTime .getSecondOfMinute(),
           * localTime.getMillisOfSecond()) .getMillis());
           */
        default:
          throw new IllegalStateException( Messages.getErrorString(
              "DataTypeDetector.ERROR_0001_UNSUPPORTED_COLUMN_TYPE", type ) ); //$NON-NLS-1$
      }
    }
    return null;
  }

}
