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
package org.pentaho.pms.mql;

import org.pentaho.pms.messages.Messages;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * This is a singleton class that manages PMSFormula's context. the PMSFormula uses this class to validate operators and
 * functions, and it also uses this class to convert Formula terms to SQL.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.impl.sql.SqlOpenFormulaContext
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
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional manner of executing a formula. Instead
   * they generate the necessary SQL to be executed from an RDBMS.
   */
  public Object resolveReference( Object name ) {
    throw new UnsupportedOperationException( Messages.getErrorString( "PMSFormulaContext.ERROR_0001_INVALID_USE" ) ); //$NON-NLS-1$
  }

  /**
   * PMSFormulaContext and PMSFormula do not use libFormula in the traditional manner of executing a formula. Instead
   * they generate the necessary SQL to be executed from an RDBMS.
   */
  public Type resolveReferenceType( Object name ) {
    throw new UnsupportedOperationException( Messages.getErrorString( "PMSFormulaContext.ERROR_0001_INVALID_USE" ) ); //$NON-NLS-1$
  }
}
