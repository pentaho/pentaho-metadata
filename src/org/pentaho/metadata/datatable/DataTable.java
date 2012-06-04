package org.pentaho.metadata.datatable;

public class DataTable {

	private Column cols[];
	
	private Row rows[];

	public DataTable() {
		
	}
	
	public DataTable( Row rows[], Column cols[] ) {
		this.rows = rows;
		this.cols = cols;
	}
	
	public Column[] getCols() {
		return cols;
	}

	public void setCols(Column[] cols) {
		this.cols = cols;
	}

	public Row[] getRows() {
		return rows;
	}

	public void setRows(Row[] rows) {
		this.rows = rows;
	}
	
}
