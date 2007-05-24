/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
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
package org.pentaho.pms.schema.dialect;

import org.jfree.formula.lvalues.FormulaFunction;
import org.pentaho.pms.core.exception.PentahoMetadataException;

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
   * this is called during the validation phase of the PMSFormula
   * throws a formula exception if there is a validation problem.
   */
  public void validateFunction(FormulaFunction f) throws PentahoMetadataException;
  
  /**
   * This method manages the generation of the SQL for a specific function.
   * 
   * @param formula
   * @param sb
   * @param locale
   * @param f
   */
  public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException;
  
  /**
   * return the type of this specific term
   * 
   * @return type enumerator
   */
  public int getType();
}
