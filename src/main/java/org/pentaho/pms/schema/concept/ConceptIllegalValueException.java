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
package org.pentaho.pms.schema.concept;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptIllegalValueException extends Exception {
  private static final long serialVersionUID = 4397770344129366440L;

  /**
     * 
     */
  public ConceptIllegalValueException() {
    super();
  }

  /**
   * @param arg0
   * @param arg1
   */
  public ConceptIllegalValueException( String arg0, Throwable arg1 ) {
    super( arg0, arg1 );
  }

  /**
   * @param arg0
   */
  public ConceptIllegalValueException( String arg0 ) {
    super( arg0 );
  }

  /**
   * @param arg0
   */
  public ConceptIllegalValueException( Throwable arg0 ) {
    super( arg0 );
  }

}
