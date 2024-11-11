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

package org.pentaho.metadata.query.model;

import java.io.Serializable;

/**
 * This class defines the order of the results from a logical query model.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Order implements Serializable {

  private static final long serialVersionUID = 7828692078614137281L;

  public enum Type {
    ASC, DESC
  };

  private Selection selection;
  private Type type;

  public Order( Selection selection, Type type ) {
    this.selection = selection;
    this.type = type;
  }

  public Selection getSelection() {
    return selection;
  }

  public Type getType() {
    return type;
  }

}
