package org.pentaho.pms.mql;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;

/**
 * This is the result of a PMSFormula validation: database specific SQL and a set of used Business Tables (required for query generation in joins, etc)
 * 
 * @author Matt Casters (mcasters@pentaho.org)
 * 
 */
public class SQLAndTables {
	private String sql;

	private List<BusinessTable> usedTables;
	
	private List<Selection> usedColumns;

	/**
	 * @param sql
	 * @param usedTables
	 */
	public SQLAndTables(String sql, List<BusinessTable> usedTables, List<Selection> usedColumns) {
		this.sql = sql;
		this.usedTables = usedTables;
		this.usedColumns = usedColumns;
	}
	
	/**
	 * @param sql
	 * @param usedTable
	 */
	public SQLAndTables(String sql, BusinessTable usedTable, Selection usedColumn) {
		this.sql = sql;
		this.usedTables = new ArrayList<BusinessTable>();
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
	public List<BusinessTable> getUsedTables() {
		return usedTables;
	}

	/**
	 * @param usedTables
	 *            the usedTables to set
	 */
	public void setUsedTables(List<BusinessTable> usedTables) {
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
