/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.metadata.query.model;

import java.io.Serializable;

/**
 * A constraint within a logical query model.  This contains a combination type which defines
 * how it relates to other constratints, and an open formula expression 
 * defining the constraint.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Constraint implements Serializable {

  private static final long serialVersionUID = -8703652534217339403L;
  
  private CombinationType combinationType;
  private String formula;
  
  public Constraint(CombinationType combinationType, String formula) {
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
