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
 * Copyright (c) 20011 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.pms.mql.dialect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pentaho.metadata.messages.Messages;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLOrderBy;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLSelection;
import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLWhereFormula;
import org.pentaho.pms.util.Const;

/**
 * Cloudera Impala Implementation of Metadata SQL Dialect
 * 
 */
public class ImpalaDialect extends BaseHiveDialect {

  protected final static String HIVE_DIALECT_TYPE = "IMPALA";
  
  protected final static String DRIVER_CLASS_NAME = "org.apache.hive.jdbc.HiveDriver";
  
  public ImpalaDialect() {
    super(HIVE_DIALECT_TYPE); //$NON-NLS-1$
  }
  
  @Override
  protected String getDriverClassName() {
    return DRIVER_CLASS_NAME;
  }
  
  protected static String getHiveDialectType() {
    return HIVE_DIALECT_TYPE;
  }
  
  @Override
  protected List<SQLWhereFormula> generateOuterJoin(SQLQueryModel query, StringBuilder sql) {
    throw new RuntimeException(Messages.getErrorString("ImpalaDialect.ERROR_0001_OUTER_JOIN_NOT_SUPPORTED")); //$NON-NLS-1$
  }

  @Override
  protected void generateHaving(SQLQueryModel query, StringBuilder sql) {
    if (!query.getHavings().isEmpty()) {
      throw new RuntimeException(Messages.getErrorString("ImpalaDialect.ERROR_0004_HAVING_NOT_SUPPORTED")); //$NON-NLS-1$
    }
  }

  @Override
  protected void generateSelect(SQLQueryModel query, StringBuilder sql) {
    sql.append("SELECT ");
    generateSelectPredicate(query, sql);
    sql.append(Const.CR);
    boolean first = true;
    for (SQLSelection selection : query.getSelections()) {
      if (first) {
        first = false;
        sql.append("          "); //$NON-NLS-1$
      } else {
        sql.append("         ,"); //$NON-NLS-1$
      }
      sql.append(selection.getFormula());
      
      if (selection.getAlias() != null) {
        sql.append(" AS "); //$NON-NLS-1$
        sql.append(selection.getAlias());
      }
      
      sql.append(Const.CR);
    }
  }

  
  /**
   * Creates a FROM clause by joining tables and their WHERE conditions optimized for Impala.  The basic logic is:
   * 
   * 1.  Honor the user defined Join Order if possible.
   * 2.  Include WHERE condition if only equalities are used.
   * 3.  Joins with WHERE conditions that contain operators other than '=' should be joined without a condition and the 
   *     conditions be placed in the WHERE clause of the query.
   * 
   * @param query Query Model
   * @return String representing FROM and WHERE clause based on the Inner Joins of the query model.
   */
  protected String getFromAndWhereClauseWithInnerJoins(SQLQueryModel query) {
    StringBuilder sql = new StringBuilder();
    // Copy of joins so we can manipulate the list
    List<SQLJoin> joins = new ArrayList<SQLJoin>(query.getJoins());
    // Tables already used in join conditions (this is required to ensure tables are not duplicated)
    Set<String> usedTables = new HashSet<String>();
    // SQLJoins with WHERE conditions that must be included in the WHERE clause of the query
    List<SQLJoin> joinsForWhereClause = new LinkedList<SQLJoin>();
    // Honor the sorting order given by Join Order Key 
    Collections.sort(joins, InnerJoinComparator.getInstance());
    SQLJoin join = joins.get(0);
    // Use the LHS of the first join as the anchor table to start the query
    String firstTable = getTableAndAlias(join.getLeftTablename(), join.getLeftTableAlias());
    sql.append("          ").append(firstTable); //$NON-NLS-1$
    sql.append(Const.CR);
    // The first table has now been used in the query
    usedTables.add(firstTable);
    // Connect SQLJoin nodes until we can't connect any more
    connectNode(sql, usedTables, joins, joinsForWhereClause);
    // If there are joins left after we're done connecting nodes they are unreachable
    if (!joins.isEmpty()) {
      throw new RuntimeException(String.format(Messages.getErrorString(
          "ImpalaDialect.ERROR_0002_JOIN_PATH_NOT_FOUND", //$NON-NLS-1$
          getTableAndAlias(join.getLeftTablename(), join.getLeftTableAlias()),
          getTableAndAlias(join.getRightTablename(), join.getRightTableAlias()))));
    }
    // Add any joins that have where conditions that cannot be put into the ON clause because of Impala's join syntax
    generateInnerJoinWhereConditions(query, sql, joinsForWhereClause);
    return sql.toString();
  }

  /**
   * Attempt to connect another {@link SQLJoin} to the query.
   * 
   * @param sql In-progress query string being built
   * @param usedTables Tables already used in this query
   * @param unusedJoins Remaining, unused {@link SQLJoin}s.
   * @param joinsForWhereClause {@link SQLJoin}s with WHERE conditions that have not been used in any ON conditions
   */
  protected void connectNode(StringBuilder sql, Set<String> usedTables, List<SQLJoin> unusedJoins,
      List<SQLJoin> joinsForWhereClause) {
    Iterator<SQLJoin> iter = unusedJoins.iterator();
    while (iter.hasNext()) {
      SQLJoin join = iter.next();
      String lhs = getTableAndAlias(join.getLeftTablename(), join.getLeftTableAlias());
      String rhs = getTableAndAlias(join.getRightTablename(), join.getRightTableAlias());
      boolean lhsUsed = usedTables.contains(lhs);
      boolean rhsUsed = usedTables.contains(rhs);
      // Determine if we should reject this join condition, skip it, flip it, or keep it as is
      if (lhsUsed && rhsUsed) {
        // Multiple joins against the same tables.  This is assumed to be not possible.
        throw new RuntimeException(Messages.getErrorString(
            "ImpalaDialect.ERROR_0003_ADDITIONAL_JOIN_CONDITIONS_FOUND", lhs, rhs)); //$NON-NLS-1$
      } else if (!lhsUsed && !rhsUsed) {
        // If neither of the tables have been used yet skip this join for now.
        continue;
      } else if (!lhsUsed && rhsUsed) {
        // Swap the lhs and rhs so we join unused tables to the chain of used tables
        String t = lhs;
        lhs = rhs;
        rhs = t;
      } else {
        // Keep original order, used on left, unused on right.
      }
      // We've found a join to be included, remove it from the list of unused joins
      iter.remove();
      // Join the RHS table
      usedTables.add(rhs);
      sql.append("          JOIN "); //$NON-NLS-1$
      sql.append(rhs);
      // Check for a valid join formula
      if (!isValidJoinFormula(join.getSqlWhereFormula().getFormula())) {
        // SQLJoins with invalid Impala ON clause join formulas will be added in the WHERE clause
        joinsForWhereClause.add(join);
      } else {
        // Use the Impala-valid join condition in the ON clause of this join
        sql.append(" ON ( ").append(join.getSqlWhereFormula().getFormula()).append(" )"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      sql.append(Const.CR);
      // We successfully found a new SQLJoin node to attach to the query, attempt to connect another
      connectNode(sql, usedTables, unusedJoins, joinsForWhereClause);
      // And stop looking here, call to connectNode(..) above will start from the top
      break;
    }
  }

  
  protected void generateOrderBy(SQLQueryModel query, StringBuilder sql) {
    
    if (query.getOrderBys().size() > 0) {
      sql.append("ORDER BY ").append(Const.CR); //$NON-NLS-1$
      boolean first = true;
      for (SQLOrderBy orderby : query.getOrderBys()) {
        if (first) {
          first = false;
          sql.append("          "); //$NON-NLS-1$
        } else {
          sql.append("         ,"); //$NON-NLS-1$
        }
        
        if (orderby.getSelection().getAlias() != null) {
          sql.append(orderby.getSelection().getAlias());
        } else {
          sql.append(orderby.getSelection().getFormula());
        }
      
        if (orderby.getOrder() != null) {
          sql.append(" "); //$NON-NLS-1$
          switch (orderby.getOrder()) {
            case ASCENDING:
              sql.append("ASC"); //$NON-NLS-1$
              break;
            case DESCENDING:
              sql.append("DESC"); //$NON-NLS-1$
              break;
            default:
              throw new RuntimeException("unsupported order type: " + orderby.getOrder()); //$NON-NLS-1$
          }
        }
        sql.append(Const.CR);
      }
    }
  }
}
