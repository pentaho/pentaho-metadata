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
 * DB2 Pentaho Metadata Dialect Class  
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 */
public class DB2Dialect extends DefaultSQLDialect {
  
  public DB2Dialect() {
    super("DB2"); //$NON-NLS-1$
    
    // db2 specific date functions 
    supportedFunctions.put("NOW", new DefaultSQLFunctionGenerator(DefaultSQLFunctionGenerator.PARAM_FUNCTION, "( CURRENT DATE )", 0) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        sb.append(sql);
      }
    });
  }

  /**
   * return DB2 formatted date, DATE('YYYY-MM-DD')
   * 
   * @param year 
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL(int year, int month, int day) {
    return "DATE(" + //$NON-NLS-1$
        quoteStringLiteral(year + "-" + displayAsTwoOrMoreDigits(month) + "-" + displayAsTwoOrMoreDigits(day)) + //$NON-NLS-1$ //$NON-NLS-2$
        ")"; //$NON-NLS-1$
    
  }
}
