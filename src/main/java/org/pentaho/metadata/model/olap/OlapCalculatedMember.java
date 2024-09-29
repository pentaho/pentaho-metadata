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
