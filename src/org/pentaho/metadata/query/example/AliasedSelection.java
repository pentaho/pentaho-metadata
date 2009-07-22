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
package org.pentaho.metadata.query.example;

import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.core.exception.PentahoMetadataException;

/**
 * This is an example of extending the metadata query model.
 * 
 * @author Will Gorman (wgorman@penthao.com)
 *
 */
public class AliasedSelection extends Selection {

  private static final long serialVersionUID = -4952224256796071909L;

  protected String alias;
  protected String formula;
  
  public String toString() {
    return "[bc=" + getLogicalColumn() + "; alias="+ alias + "; formula="+formula+ "]";
  }
  
  public AliasedSelection(Category category, LogicalColumn column, AggregationType agg, String alias) {
    super(category, column, agg);
    this.alias = alias;
  }
  
  public AliasedSelection(String formula) throws PentahoMetadataException {
    super(null, null, null);
    this.formula = formula;
  }

  public String getAlias() {
    return alias;
  }
  
  public String getFormula() {
    return formula;
  }
  
  public boolean hasFormula() {
    return formula != null;
  }
  
  @Override
  public int hashCode() {
    if (getLogicalColumn() != null) {
      return getLogicalColumn().getId().hashCode();
    } else {
      return formula.hashCode();
    }
  }
  
  public boolean equals(Object selection) {
    AliasedSelection sel = (AliasedSelection)selection;
    if (hasFormula() && sel.hasFormula()) {
      return formula.equals(sel.getFormula());
    } else if (!hasFormula() && !sel.hasFormula()){
      return super.equals(selection);
    } else {
      return false;
    }
  }
}
