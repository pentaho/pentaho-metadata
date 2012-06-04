package org.pentaho.metadata.datatable;

import java.math.BigDecimal;

public class Cell {

	private String f;
	
	private BigDecimal v;

	public Cell() {
	}

	public Cell(BigDecimal value) {
		this.v = value;
	}
	
	public Cell(BigDecimal value, String formatted) {
		this.v = value;
		this.f = formatted;
	}
	
	public String getf() {
		return f;
	}

	public void setf(String f) {
		this.f = f;
	}

	public BigDecimal getv() {
		return v;
	}

	public void setv(BigDecimal v) {
		this.v = v;
	}
	
}
