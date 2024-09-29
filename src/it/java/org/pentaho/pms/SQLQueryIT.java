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

package org.pentaho.pms;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.pms.mql.dialect.SQLQueryModel.OrderType;

/**
 * Tests SQLQueryModel and sql query generation.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 */
@SuppressWarnings( "nls" )
public class SQLQueryIT extends MetadataTestBase {
  public static final String lineSep = System.getProperty( "line.separator" );

  public void testSQL() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.addHavingFormula( "col2 > 10", "AND" );
    query.addWhereFormula( "col1 < 3", "AND" );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep
        + "         ,sum(t1.c2) AS col2" + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4"
        + lineSep + "FROM " + lineSep + "          t1" + lineSep + "WHERE " + lineSep + "          (" + lineSep
        + "          (" + lineSep + "             col1 < 3" + lineSep + "          )" + lineSep + "          )"
        + lineSep + "GROUP BY " + lineSep + "          col1" + lineSep + "HAVING " + lineSep + "          (" + lineSep
        + "             col2 > 10" + lineSep + "          )" + lineSep + "ORDER BY " + lineSep + "          col2"
        + lineSep + "         ,col3 ASC" + lineSep + "         ,col4 DESC" + lineSep, sql );

    // printOutJava(sql);
  }

  public void testNotWhereHavingSQL() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.addHavingFormula( "col2 > 10", "AND NOT" );
    query.addWhereFormula( "col1 < 3", "AND NOT" );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );
    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep
        + "         ,sum(t1.c2) AS col2" + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4"
        + lineSep + "FROM " + lineSep + "          t1" + lineSep + "WHERE " + lineSep + "          (" + lineSep
        + "      NOT (" + lineSep + "             col1 < 3" + lineSep + "          )" + lineSep + "          )"
        + lineSep + "GROUP BY " + lineSep + "          col1" + lineSep + "HAVING " + lineSep + "      NOT (" + lineSep
        + "             col2 > 10" + lineSep + "          )" + lineSep + "ORDER BY " + lineSep + "          col2"
        + lineSep + "         ,col3 ASC" + lineSep + "         ,col4 DESC" + lineSep, sql );

    // printOutJava(sql);
  }

  public void testSQLWithSecurityInWhere() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.addHavingFormula( "col2 > 10", "AND" );
    query.addWhereFormula( "col1 < 3", "AND" );
    query.setSecurityConstraint( "'role' in ('role', 'role2')", false );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );

    // printOutJava(sql);

    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep
        + "         ,sum(t1.c2) AS col2" + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4"
        + lineSep + "FROM " + lineSep + "          t1" + lineSep + "WHERE " + lineSep + "        (" + lineSep
        + "          'role' in ('role', 'role2')" + lineSep + "        ) AND (" + lineSep + "          (" + lineSep
        + "             col1 < 3" + lineSep + "          )" + lineSep + "        )" + lineSep + "GROUP BY " + lineSep
        + "          col1" + lineSep + "HAVING " + lineSep + "          (" + lineSep + "             col2 > 10"
        + lineSep + "          )" + lineSep + "ORDER BY " + lineSep + "          col2" + lineSep + "         ,col3 ASC"
        + lineSep + "         ,col4 DESC" + lineSep, sql );

  }

  public void testSQLWithSecurityInWhereNoAddlWheres() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.setSecurityConstraint( "'role' in ('role', 'role2')", false );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );

    // printOutJava(sql);

    assertEquals( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep + "         ,sum(t1.c2) AS col2"
        + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4" + lineSep + "FROM " + lineSep
        + "          t1" + lineSep + "WHERE " + lineSep + "        (" + lineSep
        + "          'role' in ('role', 'role2')" + lineSep + "        )" + lineSep + "GROUP BY " + lineSep
        + "          col1" + lineSep + "ORDER BY " + lineSep + "          col2" + lineSep + "         ,col3 ASC"
        + lineSep + "         ,col4 DESC" + lineSep, sql );
  }

  public void testSQLWithSecurityInHavingNoAddlHavings() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.setSecurityConstraint( "'role' in ('role', 'role2')", true );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );

    // printOutJava(sql);

    assertEquals( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep + "         ,sum(t1.c2) AS col2"
        + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4" + lineSep + "FROM " + lineSep
        + "          t1" + lineSep + "GROUP BY " + lineSep + "          col1" + lineSep + "HAVING " + lineSep
        + "        (" + lineSep + "          'role' in ('role', 'role2')" + lineSep + "        )" + lineSep
        + "ORDER BY " + lineSep + "          col2" + lineSep + "         ,col3 ASC" + lineSep + "         ,col4 DESC"
        + lineSep, sql );
  }

  public void testSQLWithSecurityInHaving() {
    SQLQueryModel query = new SQLQueryModel();
    query.addSelection( "t1.c1", "col1" );
    query.addSelection( "sum(t1.c2)", "col2" );
    query.addSelection( "t1.c3", "col3" );
    query.addSelection( "t1.c4", "col4" );

    query.addTable( "t1", null );
    query.addGroupBy( null, "col1" );
    query.addHavingFormula( "col2 > 10", "AND" );
    query.addWhereFormula( "col1 < 3", "AND" );
    query.setSecurityConstraint( "'role' in ('role', 'role2')", true );
    query.addOrderBy( null, "col2", null );
    query.addOrderBy( null, "col3", OrderType.ASCENDING );
    query.addOrderBy( null, "col4", OrderType.DESCENDING );

    DatabaseMeta databaseMeta = new DatabaseMeta( "", "ORACLE", "Native", "", "", "", "", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    SQLDialectInterface dialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    String sql = dialect.generateSelectStatement( query );

    // printOutJava(sql);

    assertEqualsIgnoreWhitespacesAndCase( "SELECT DISTINCT " + lineSep + "          t1.c1 AS col1" + lineSep
        + "         ,sum(t1.c2) AS col2" + lineSep + "         ,t1.c3 AS col3" + lineSep + "         ,t1.c4 AS col4"
        + lineSep + "FROM " + lineSep + "          t1" + lineSep + "WHERE " + lineSep + "          (" + lineSep
        + "          (" + lineSep + "             col1 < 3" + lineSep + "          )" + lineSep + "          )"
        + lineSep + "GROUP BY " + lineSep + "          col1" + lineSep + "HAVING " + lineSep + "        (" + lineSep
        + "          'role' in ('role', 'role2')" + lineSep + "        ) AND (" + lineSep + "          (" + lineSep
        + "             col2 > 10" + lineSep + "          )" + lineSep + "        )" + lineSep + "ORDER BY " + lineSep
        + "          col2" + lineSep + "         ,col3 ASC" + lineSep + "         ,col4 DESC" + lineSep, sql );

  }

  public static void printOutJava( String sql ) {
    String[] lines = sql.split( "\n" );
    for ( int i = 0; i < lines.length; i++ ) {
      System.out.print( "        \"" + lines[i] );
      if ( i == lines.length - 1 ) {
        System.out.println( "\\n\"" );
      } else {
        System.out.println( "\\n\" + " );
      }
    }
  }
}
