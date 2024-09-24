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
