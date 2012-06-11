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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jun, 2012
 * @author jdixon
 */
package org.pentaho.metadata.datatable;

/**
 * Defines the metadata for a column in a @see DataTable
 * @author jamesdixon
 *
 */
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
	
	/**
	 * Returns the id of this column
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id of this column
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the type of this column - 'string', 'numeric', 'date', or 'boolean'
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type of this column
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns the label of this column
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the label of this column
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
}
