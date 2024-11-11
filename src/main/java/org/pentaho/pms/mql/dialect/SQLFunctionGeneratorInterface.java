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
