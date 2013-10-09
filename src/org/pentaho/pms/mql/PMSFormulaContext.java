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
