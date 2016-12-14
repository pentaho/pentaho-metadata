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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;

/**
 * this interface defines how a function validates and renders it's sql
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public interface SQLFunctionGeneratorInterface {

  // various types of ODF terms
  public static final int INLINE_FUNCTION = 0;
  public static final int PARAM_FUNCTION = 1;
  public static final int PARAM_AGG_FUNCTION = 2;

  /**
   * this is called during the validation phase of the PMSFormula throws a formula exception if there is a validation
   * problem.
   */
  public void validateFunction( FormulaFunction f ) throws PentahoMetadataException;

  /**
   * This method manages the generation of the SQL for a specific function.
   * 
   * @param formula
   * @param sb
   * @param locale
   * @param f
   */
  public void
    generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f )
      throws PentahoMetadataException;

  /**
   * return the type of this specific term
   * 
   * @return type enumerator
   */
  public int getType();

  /**
   * This method indicates whether or not the function supports multi-valued parameters
   * 
   * @return
   */
  public boolean isMultiValuedParamAware();

}
