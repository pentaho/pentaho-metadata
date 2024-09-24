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
 * DB2 Pentaho Metadata Dialect Class
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 */
public class DB2Dialect extends DefaultSQLDialect {

  public DB2Dialect() {
    super( "DB2" ); //$NON-NLS-1$

    // db2 specific date functions
    supportedFunctions.put(
        "NOW", new DefaultSQLFunctionGenerator( DefaultSQLFunctionGenerator.PARAM_FUNCTION, "( CURRENT DATE )", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( sql );
          }
        } );

    // db2 specific boolean data type

    supportedFunctions.put(
        "TRUE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "TRUE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "1=1" );
          }
        } );

    supportedFunctions.put(
        "FALSE", new DefaultSQLFunctionGenerator( SQLFunctionGeneratorInterface.PARAM_FUNCTION, "FALSE()", 0 ) { //$NON-NLS-1$ //$NON-NLS-2$
          public void generateFunctionSQL( FormulaTraversalInterface formula, StringBuffer sb, String locale,
              FormulaFunction f ) throws PentahoMetadataException {
            sb.append( "1=0" );
          }
        } );
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
  public String getDateSQL( int year, int month, int day ) {
    return "DATE(" + //$NON-NLS-1$
        quoteStringLiteral( year + "-" + displayAsTwoOrMoreDigits( month ) + "-" + displayAsTwoOrMoreDigits( day ) ) + //$NON-NLS-1$ //$NON-NLS-2$
        ")"; //$NON-NLS-1$
  }

  public String getDateSQL( int year, int month, int day, int hour, int minute, int second, int milli ) {
    return "TIMESTAMP("
        + quoteStringLiteral( "" + year + displayAsTwoOrMoreDigits( month ) + displayAsTwoOrMoreDigits( day ) //$NON-NLS-1$
            + displayAsTwoOrMoreDigits( hour ) + displayAsTwoOrMoreDigits( minute ) + displayAsTwoOrMoreDigits( second ) ) //$NON-NLS-1$
        + ")";
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
  protected void generatePostOrderBy( SQLQueryModel query, StringBuilder sql ) {
    if ( query.getLimit() >= 0 ) {
      sql.append( " FETCH FIRST " ); //$NON-NLS-1$
      sql.append( query.getLimit() );
      sql.append( " ROWS ONLY " ); //$NON-NLS-1$
    }
  }

  @Override
  protected void generateSelectPredicate( SQLQueryModel query, StringBuilder sql ) {
    generateDistinct( query, sql );
  }

}
