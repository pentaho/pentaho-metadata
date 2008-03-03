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
package org.pentaho.pms.mql;

import org.jfree.formula.DefaultFormulaContext;
import org.jfree.formula.typing.Type;
import org.pentaho.pms.messages.Messages;

/**
 * This is a singleton class that manages PMSFormula's context.
 * the PMSFormula uses this class to validate operators and
 * functions, and it also uses this class to convert Formula
 * terms to SQL.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class PMSFormulaContext extends DefaultFormulaContext {
  
  /** singleton instance, one per classloader */
  private static PMSFormulaContext singleton = new PMSFormulaContext();
  
  /**
   * static, thread safe singleton retrieval
   * 
   * @return PMSFormulaContext singleton object
   */
  public static PMSFormulaContext getInstance() {
    return singleton;
  }

  /**
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional
   * manner of executing a formula.  Instead they generate the necessary SQL to be 
   * executed from an RDBMS.  
   */
  public Object resolveReference(Object name) {
    throw new UnsupportedOperationException(Messages.getErrorString("PMSFormulaContext.ERROR_0001_INVALID_USE")); //$NON-NLS-1$
  }

  /**
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional
   * manner of executing a formula.  Instead they generate the necessary SQL to be 
   * executed from an RDBMS.  
   */
  public Type resolveReferenceType(Object name) {
    throw new UnsupportedOperationException(Messages.getErrorString("PMSFormulaContext.ERROR_0001_INVALID_USE")); //$NON-NLS-1$
  }
}
