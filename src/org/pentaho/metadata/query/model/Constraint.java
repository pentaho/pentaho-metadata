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
