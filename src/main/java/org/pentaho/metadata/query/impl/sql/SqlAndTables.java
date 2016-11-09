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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
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
