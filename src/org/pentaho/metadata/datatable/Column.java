package org.pentaho.metadata.datatable;

public class Column {

	private String id;
	private String type;
	private String label;
	
	public Column() {
	}
	
	public Column( String id, String label, String type ) {
		this.id = id;
		this.label = label;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
