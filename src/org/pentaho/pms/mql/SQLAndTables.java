package org.pentaho.pms.mql;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * @param sql
	 * @param usedTables
	 */
	public SQLAndTables(String sql, List<BusinessTable> usedTables) {
		this.sql = sql;
		this.usedTables = usedTables;
	}
	
	/**
	 * @param sql
	 * @param usedTable
	 */
	public SQLAndTables(String sql, BusinessTable usedTable) {
		this.sql = sql;
		this.usedTables = new ArrayList<BusinessTable>();
		this.usedTables.add(usedTable);
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

}
