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
 * Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model.olap;

import java.io.Serializable;

public class OlapCalculatedMember implements Cloneable, Serializable {

  private String name;
  private String dimension;
  private String formula;
  private String formatString;
  private boolean calculateSubtotals;
  private boolean hidden;

  public OlapCalculatedMember() {
  }

  public OlapCalculatedMember(
      String name, String dimension, String formula, String formatString, boolean calculateSubtotals ) {
    super();
    this.name = name;
    this.dimension = dimension;
    this.formula = formula;
    this.formatString = formatString;
    this.calculateSubtotals = calculateSubtotals;
  }

  public OlapCalculatedMember(
      String name, String dimension, String formula, String formatString, boolean calculateSubtotals, boolean hidden ) {
    this( name, dimension, formula, formatString, calculateSubtotals ); // Backwards Compatibility
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getDimension() {
    return dimension;
  }

  public void setDimension( String dimension ) {
    this.dimension = dimension;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula( String formula ) {
    this.formula = formula;
  }

  public String getFormatString() {
    return formatString;
  }

  public void setFormatString( String formatString ) {
    this.formatString = formatString;
  }

  public boolean isCalculateSubtotals() {
    return calculateSubtotals;
  }

  public void setCalculateSubtotals( final boolean calculateSubtotals ) {
    this.calculateSubtotals = calculateSubtotals;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }

  protected Object clone() {
    return new OlapCalculatedMember( name, dimension, formula, formatString, calculateSubtotals, hidden );
  }
}
