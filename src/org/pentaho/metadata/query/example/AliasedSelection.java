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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
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
