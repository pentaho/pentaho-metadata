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

package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;

/**
 * LucidDb Implementation of Metadata SQL Dialect
 * 
 * @author Dan Kinsley (dan@thinkingphones.com)
 * 
 */
public class LucidDbDialect extends DefaultSQLDialect {

  public LucidDbDialect() {
    super( "LucidDB" ); //$NON-NLS-1$

    // Luciddb specific date functions
    supportedFunctions.put(
        "NOW", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "CURRENT_TIMESTAMP", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( sql );
          }
        } );
  }

  /**
   * return LucidDb formatted date, date'YYYY-MM-DD'
   * 
   * @param year
   * @param month
   * @param day
   * 
   * @return date string
   */
  public String getDateSQL( int year, int month, int day ) {
    return "date" + //$NON-NLS-1$
        quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  protected String getStringConcatOperator() {
    return "||"; //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }

}
