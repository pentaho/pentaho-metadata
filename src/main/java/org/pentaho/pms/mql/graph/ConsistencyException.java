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

package org.pentaho.pms.mql.graph;

public class ConsistencyException extends Exception {
  private static final long serialVersionUID = 1L;

  private GraphElement element;

  public ConsistencyException() {

  }

  public ConsistencyException( GraphElement element ) {
    this.element = element;
  }

  public GraphElement getElement() {
    return element;
  }
}
