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

package org.pentaho.metadata.query.impl.sql;

import org.pentaho.metadata.messages.Messages;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * This is a singleton class that manages SqlOpenFormula's context. the SqlOpenFormula uses this class to validate
 * operators and functions, and it also uses this class to convert Formula terms to SQL.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
public class SqlOpenFormulaContext extends DefaultFormulaContext {

  /** singleton instance, one per classloader */
  private static SqlOpenFormulaContext singleton = new SqlOpenFormulaContext();

  /**
   * static, thread safe singleton retrieval
   * 
   * @return SqlOpenFormulaContext singleton object
   */
  public static SqlOpenFormulaContext getInstance() {
    return singleton;
  }

  /**
   * SqlOpenFormulaContext and SqlOpenFormula do not use libFormula in the traditional manner of executing a formula.
   * Instead they generate the necessary SQL to be executed from an RDBMS.
   */
  public Object resolveReference( Object name ) {
    throw new UnsupportedOperationException( Messages.getErrorString( "SqlOpenFormulaContext.ERROR_0001_INVALID_USE" ) ); //$NON-NLS-1$
  }

  /**
   * SqlOpenFormulaContext and SqlOpenFormula do not use libFormula in the traditional manner of executing a formula.
   * Instead they generate the necessary SQL to be executed from an RDBMS.
   */
  public Type resolveReferenceType( Object name ) {
    throw new UnsupportedOperationException( Messages.getErrorString( "SqlOpenFormulaContext.ERROR_0001_INVALID_USE" ) ); //$NON-NLS-1$
  }
}
