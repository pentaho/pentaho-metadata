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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.query.model.Selection;

/**
 * This is the result of a SqlOpenFormula validation: database specific SQL and a set of used Business Tables (required
 * for query generation in joins, etc)
 * 
 * @author Matt Casters (mcasters@pentaho.org)
 * 
 */
public class SqlAndTables {
  private String sql;

  private List<LogicalTable> usedTables;

  private List<Selection> usedColumns;

  /**
   * @param sql
   * @param usedTables
   */
  public SqlAndTables( String sql, List<LogicalTable> usedTables, List<Selection> usedColumns ) {
    this.sql = sql;
    this.usedTables = usedTables;
    this.usedColumns = usedColumns;
  }

  /**
   * @param sql
   * @param usedTable
   */
  public SqlAndTables( String sql, LogicalTable usedTable, Selection usedColumn ) {
    this.sql = sql;
    this.usedTables = new ArrayList<LogicalTable>();
    this.usedTables.add( usedTable );
    this.usedColumns = new ArrayList<Selection>();
    this.usedColumns.add( usedColumn );
  }

  /**
   * @return the sql
   */
  public String getSql() {
    return sql;
  }

  /**
   * @param sql
   *          the sql to set
   */
  public void setSql( String sql ) {
    this.sql = sql;
  }

  /**
   * @return the usedTables
   */
  public List<LogicalTable> getUsedTables() {
    return usedTables;
  }

  /**
   * @param usedTables
   *          the usedTables to set
   */
  public void setUsedTables( List<LogicalTable> usedTables ) {
    this.usedTables = usedTables;
  }

  /**
   * @return the usedColumns
   */
  public List<Selection> getUsedColumns() {
    return usedColumns;
  }

  /**
   * @param usedColumns
   *          the usedColumns to set
   */
  public void setUsedColumns( List<Selection> usedColumns ) {
    this.usedColumns = usedColumns;
  }

}
