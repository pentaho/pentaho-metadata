/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
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
package org.pentaho.metadata.query.impl.sql;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.query.model.Selection;

/**
 * This is the result of a SqlOpenFormula validation: database specific SQL and a set of used Business Tables (required for query generation in joins, etc)
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
	public SqlAndTables(String sql, List<LogicalTable> usedTables, List<Selection> usedColumns) {
		this.sql = sql;
		this.usedTables = usedTables;
		this.usedColumns = usedColumns;
	}
	
	/**
	 * @param sql
	 * @param usedTable
	 */
	public SqlAndTables(String sql, LogicalTable usedTable, Selection usedColumn) {
		this.sql = sql;
		this.usedTables = new ArrayList<LogicalTable>();
		this.usedTables.add(usedTable);
		this.usedColumns = new ArrayList<Selection>();
		this.usedColumns.add(usedColumn);
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
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
	 *            the usedTables to set
	 */
	public void setUsedTables(List<LogicalTable> usedTables) {
		this.usedTables = usedTables;
	}

	/**
	 * @return the usedColumns
	 */
	public List<Selection> getUsedColumns() {
		return usedColumns;
	}

	/**
	 * @param usedColumns the usedColumns to set
	 */
	public void setUsedColumns(List<Selection> usedColumns) {
		this.usedColumns = usedColumns;
	}

}
