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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.helpers.SQLDialectHelper;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DefaultSQLDialectIT {

  @Before
  public void setUp() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
  public void testDisplayAsTwoOrMoreDigits() {
    DefaultSQLDialect dialect = new DefaultSQLDialect();

    String singleDigitString = "01";
    int singleDigitInt = 1;
    assertEquals( singleDigitString, dialect.displayAsTwoOrMoreDigits( singleDigitInt ) );

    String twoDigitString = "10";
    int twoDigitInt = 10;
    assertEquals( twoDigitString, dialect.displayAsTwoOrMoreDigits( twoDigitInt ) );

    String negativeDigitString = "-1";
    int negativeDigitInt = -1;
    assertEquals( negativeDigitString, dialect.displayAsTwoOrMoreDigits( negativeDigitInt ) );
  }

  @Test
  public void testTimestampType() throws PentahoMetadataException {
    Timestamp timestamp = new Timestamp( 0 );
    Calendar calendar = Calendar.getInstance( Locale.getDefault() );
    calendar.setTime( timestamp );
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.getDefault() );
    String expected = '\'' + sdf.format( calendar.getTime() ) + '.' + calendar.get( Calendar.MILLISECOND ) + '\'';
    FormulaTraversalInterface formula = mock( FormulaTraversalInterface.class );
    LValue[] values = new LValue[] { new StaticValue( timestamp ) };
    FormulaFunction formulaFunction = mock( FormulaFunction.class );
    when( formulaFunction.getChildValues() ).thenReturn( values );
    StringBuffer sb = new StringBuffer();
    SQLDialectInterface dialect = new DefaultSQLDialect();
    SQLFunctionGeneratorInterface generator = dialect.getFunctionSQLGenerator( "DATEVALUE" );
    generator.generateFunctionSQL( formula, sb, Locale.getDefault().toString(), formulaFunction );
    assertEquals( expected, sb.toString() );
  }

  /**
   * Verify the WHERE condition is generated properly for a "simple" single table query
   */
  @Test
  public void testSimpleWhereCondition_single_table() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t.id", null ); //$NON-NLS-1$
    query.addTable( "TABLE", "t" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addWhereFormula( "t.id is null", null ); //$NON-NLS-1$

    String expected = "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) )"; //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement( query );
    SQLDialectHelper.assertEqualsIgnoreWhitespaces( expected, result );
  }

  /**
   * Verify the WHERE condition is generated properly when model contains an INNER JOIN
   */
  @Test
  public void testSimpleWhereCondition_with_inner_join() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t.id", null ); //$NON-NLS-1$
    query.addTable( "TABLE", "t" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable( "TABLE2", "two" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addJoin( "TABLE", "t", "TABLE2", "two", JoinType.INNER_JOIN, "t.id = two.id",
      null ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    query.addWhereFormula( "t.id is null", null ); //$NON-NLS-1$

    String expected =
      "SELECT DISTINCT t.id FROM TABLE t, TABLE2 two WHERE ( t.id = two.id) AND ( ( t.id is null ) )"; //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement( query );
    SQLDialectHelper.assertEqualsIgnoreWhitespaces( expected, result );
  }

  /**
   * Verify the WHERE condition is generated properly when model contains an OUTER JOIN
   */
  @Test
  public void testSimpleWhereCondition_with_outer_joins() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t.id", null ); //$NON-NLS-1$
    query.addTable( "TABLE", "t" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addTable( "TABLE2", "two" ); //$NON-NLS-1$ //$NON-NLS-2$
    query.addJoin( "TABLE", "t", "TABLE2", "two", JoinType.FULL_OUTER_JOIN, "t.id = two.id",
      null ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    query.addWhereFormula( "t.id is null", null ); //$NON-NLS-1$

    String expected =
      "SELECT DISTINCT t.id FROM TABLE t FULL OUTER JOIN TABLE2 two ON ( t.id = two.id) WHERE ( ( t.id is null ) )";
    //$NON-NLS-1$
    SQLDialectInterface dialect = new DefaultSQLDialect();
    String result = dialect.generateSelectStatement( query );
    SQLDialectHelper.assertEqualsIgnoreWhitespaces( expected, result );
  }

  @Test
  public void testLimitSQL() {
    SQLDialectHelper
      .assertSelect( "SELECT DISTINCT TOP 10 t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
        new DefaultSQLDialect(), SQLDialectHelper.createLimitedQuery() );
  }

  @Test
  public void testNoLimitSQL() {
    SQLDialectHelper.assertSelect( "SELECT DISTINCT t.id FROM TABLE t WHERE ( ( t.id is null ) ) ORDER BY t.id ASC",
      new DefaultSQLDialect(), SQLDialectHelper.createUnlimitedQuery() );
  }

  @Test
  public void testGetDateSQL() {
    String dateExpected = "'2000-01-01'";
    DefaultSQLDialect dialect = new DefaultSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1 ) );
  }

  @Test
  public void testGetDateSQL_withTime() {
    String dateExpected = "'2000-01-01 12:00:00.0'";
    DefaultSQLDialect dialect = new DefaultSQLDialect();
    assertEquals( dateExpected, dialect.getDateSQL( 2000, 1, 1, 12, 0, 0, 0 ) );
  }

}
