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
package org.pentaho.metadata.query.model;

import java.io.Serializable;

/**
 * A constraint within a logical query model. This contains a combination type which defines how it relates to other
 * constraints, and an open formula expression defining the constraint.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class Constraint implements Serializable {

  private static final long serialVersionUID = -8703652534217339403L;

  private CombinationType combinationType;
  private String formula;

  public Constraint( CombinationType combinationType, String formula ) {
    this.combinationType = combinationType;
    this.formula = formula;
  }

  public CombinationType getCombinationType() {
    return combinationType;
  }

  public String getFormula() {
    return formula;
  }

}
