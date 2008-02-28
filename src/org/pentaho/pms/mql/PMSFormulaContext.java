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

import java.util.HashMap;
import java.util.Map;

import org.jfree.formula.DefaultFormulaContext;
import org.jfree.formula.typing.Type;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.dialect.DB2Dialect;
import org.pentaho.pms.mql.dialect.DefaultSQLDialect;
import org.pentaho.pms.mql.dialect.MSAccessDialect;
import org.pentaho.pms.mql.dialect.MSSQLDialect;
import org.pentaho.pms.mql.dialect.MySQLDialect;
import org.pentaho.pms.mql.dialect.OracleDialect;
import org.pentaho.pms.mql.dialect.PostgreSQLDialect;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;

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
  
  private Map<String,SQLDialectInterface> supportedDialects = new HashMap<String,SQLDialectInterface>();
  
  /**
   * private constructor, for now supported functions and operators are hardcoded, in the future
   * this may be moved to a config file
   */
  private PMSFormulaContext() {
    addDialect(new DefaultSQLDialect());
    addDialect(new OracleDialect());
    addDialect(new MSSQLDialect());
    addDialect(new DB2Dialect());
    addDialect(new PostgreSQLDialect());
    addDialect(new MySQLDialect());
    addDialect(new MSAccessDialect());
  }
  
  private void addDialect(SQLDialectInterface dialect) {
     supportedDialects.put(dialect.getDatabaseType(), dialect);
  }
  
  public SQLDialectInterface getSQLDialect(DatabaseMeta databaseMeta) {
    SQLDialectInterface sqlDialect = (SQLDialectInterface)supportedDialects.get(databaseMeta.getDatabaseTypeDesc());
    if (sqlDialect == null) {
      sqlDialect = (SQLDialectInterface)supportedDialects.get("GENERIC"); //$NON-NLS-1$
    }
    return sqlDialect;
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
