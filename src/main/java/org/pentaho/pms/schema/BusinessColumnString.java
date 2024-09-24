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
package org.pentaho.pms.schema;

import java.util.List;

/**
 * Proxy class to tie together a business column in a categories tree and a string representation.
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0.
 */
public class BusinessColumnString {
  private String flatRepresentation;
  private int index;
  private BusinessColumn businessColumn;

  /**
   * @param flatRepresentation
   * @param index
   * @param businessColumn
   */
  public BusinessColumnString( String flatRepresentation, int index, BusinessColumn businessColumn ) {
    super();
    this.flatRepresentation = flatRepresentation;
    this.index = index;
    this.businessColumn = businessColumn;
  }

  public BusinessColumn getBusinessColumn() {
    return businessColumn;
  }

  public void setBusinessColumn( BusinessColumn businessColumn ) {
    this.businessColumn = businessColumn;
  }

  public String getFlatRepresentation() {
    return flatRepresentation;
  }

  public void setFlatRepresentation( String flatRepresentation ) {
    this.flatRepresentation = flatRepresentation;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex( int index ) {
    this.index = index;
  }

  /**
   * @param businessColumnStrings
   *          A List of {@link BusinessColumnString}
   * @return just an array of flat representation strings
   */
  public static final String[] getFlatRepresentations( List businessColumnStrings ) {
    String[] strings = new String[businessColumnStrings.size()];

    for ( int i = 0; i < businessColumnStrings.size(); i++ ) {
      BusinessColumnString businessColumnString = (BusinessColumnString) businessColumnStrings.get( i );
      strings[i] = businessColumnString.getFlatRepresentation();
    }

    return strings;
  }

  public static final int getBusinessColumnIndex( List businessColumnStrings, BusinessColumn businessColumn ) {
    for ( int i = 0; i < businessColumnStrings.size(); i++ ) {
      BusinessColumnString businessColumnString = (BusinessColumnString) businessColumnStrings.get( i );
      BusinessColumn column = businessColumnString.getBusinessColumn();
      if ( column != null && column.equals( businessColumn ) ) {
        return i;
      }
    }
    return -1;
  }
}
