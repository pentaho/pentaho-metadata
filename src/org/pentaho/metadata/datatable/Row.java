package org.pentaho.metadata.datatable;

public class Row {

	private Cell c[];

	public Row() {
	}
	
	public Row( Cell cells[] ) {
		c = cells;
	}
	
	public Cell[] getc() {
		return c;
	}

	public void setc(Cell[] c) {
		this.c = c;
	}

}
