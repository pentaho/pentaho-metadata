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
    
    // db2 specific boolean data type
    
    supportedFunctions.put("TRUE", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "TRUE()", 0) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        sb.append("1");
      }
    });

    supportedFunctions.put("FALSE", new DefaultSQLFunctionGenerator(SQLFunctionGeneratorInterface.PARAM_FUNCTION, "FALSE()", 0) { //$NON-NLS-1$ //$NON-NLS-2$
      public void generateFunctionSQL(FormulaTraversalInterface formula, StringBuffer sb, String locale, FormulaFunction f) throws PentahoMetadataException {
        sb.append("0");
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
  
  /**
   * DB2 has a 30 character limit on table name length
   * 
   * @return max table name length
   */
  public int getMaxTableNameLength() {
    return 30;
  }
  
  @Override
  protected String getStringConcatOperator() {
    return "||"; //$NON-NLS-1$
  }

  @Override
  protected void generatePostOrderBy(SQLQueryModel query, StringBuilder sql) {
    if (query.getLimit() >= 0) {
      sql.append(" FETCH FIRST "); //$NON-NLS-1$
      sql.append(query.getLimit());
      sql.append(" ROWS ONLY "); //$NON-NLS-1$
    }
  }
  
}
