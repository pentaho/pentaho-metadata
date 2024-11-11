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

package org.pentaho.metadata.query.model.util;

import java.util.Comparator;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.DefaultTransformer;
import org.pentaho.metadata.messages.Messages;

public class BooleanComparator implements Comparator<Object> {

  private static BooleanComparator _instance = new BooleanComparator();

  private BooleanComparator() {
  }

  public static Comparator<Object> getComparator() {
    return _instance;
  }

  public static Comparable<Object> getComparable( Object object ) {
    final Boolean b = toBoolean( object );
    return new Comparable<Object>() {

      public int compareTo( Object o ) {
        return _instance.compare( b, o );
      }

      @Override
      public String toString() {
        return "BooleanComparable[boolean=" + b + "]"; //$NON-NLS-1$ //$NON-NLS-2$
      }
    };
  }

  public int compare( Object o1, Object o2 ) {
    Boolean b1 = toBoolean( o1 );
    Boolean b2 = toBoolean( o2 );
    return b1.compareTo( b2 );
  }

  private static Boolean toBoolean( Object o ) {
    if ( o != null ) {
      if ( o instanceof Boolean ) {
        return (Boolean) o;
      }
      if ( o instanceof String ) {
        try {
          return parseBoolean( (String) o );
        } catch ( IllegalArgumentException e ) {
          return false;
        }
      }
      if ( o instanceof Number ) {
        try {
          double number = new DefaultTransformer().transform( o );
          if ( number >= 1.0 ) {
            return true;
          }
        } catch ( MathException e ) {
          // ignore
        }
      }
    }
    return false;
  }

  /**
   * Parses a string and returns a boolean representation of it. To parse the string the following values will be
   * accepted, irrespective of case.
   * <ul>
   * <li>true</li>
   * <li>false</li>
   * <li>1</li>
   * <li>0</li>
   * <li>yes</li>
   * <li>no</li>
   * <li>y</li>
   * <li>n</li>
   * </ul>
   * 
   * @param string
   *          the string to parse
   * @return a boolean
   * @throws IllegalArgumentException
   *           if the string provided cannot be parsed as a boolean
   */
  public static boolean parseBoolean( String string ) throws IllegalArgumentException {
    string = string.trim();
    if ( "true".equalsIgnoreCase( string ) || "1".equals( string ) //$NON-NLS-1$ //$NON-NLS-2$
        || "y".equalsIgnoreCase( string ) //$NON-NLS-1$
        || "yes".equalsIgnoreCase( string ) ) { //$NON-NLS-1$
      return true;
    } else if ( "false".equalsIgnoreCase( string ) || "0".equals( string ) //$NON-NLS-1$ //$NON-NLS-2$
        || "n".equalsIgnoreCase( string ) //$NON-NLS-1$
        || "no".equalsIgnoreCase( string ) ) { //$NON-NLS-1$
      return false;
    } else {
      throw new IllegalArgumentException( Messages.getErrorString(
          "BooleanComparator.ERROR_0001_UNABLE_TO_DETECT_BOOLEAN_VAL", string ) ); //$NON-NLS-1$
    }
  }

}
