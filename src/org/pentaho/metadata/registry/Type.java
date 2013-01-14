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
 */
package org.pentaho.metadata.registry;

/**
 * Defines an entity types and a static set of common types
 * @author jamesdixon
 *
 */
public class Type {

	public static final Type TYPE_PROJECT = new Type( "PROJECT", "GLOBAL", "PROJECT" );
	public static final Type TYPE_TRANSFORMATION = new Type( "TRANSFORMATION", "PDI", "KTR" );
	public static final Type TYPE_JOB = new Type( "JOB", "PDI", "KJB" );
	public static final Type TYPE_RESOURCE = new Type( "RESOURCE", "GLOBAL", "RESOURCE" );
	public static final Type TYPE_DB_CONNECTION = new Type( "DB_CONNECTION", "GLOBAL", "DATABASE CONNECTION" );
	public static final Type TYPE_PHYSICAL_TABLE = new Type( "PHYS_TABLE", "GLOBAL", "PHYSICAL TABLE" );
	public static final Type TYPE_PHYSICAL_COLUMN = new Type( "PHYS_COLUMN", "GLOBAL", "PHYSICAL COLUMN" );
	public static final Type TYPE_HOSTED_SOURCE = new Type( "HOSTED_SOURCE", "GLOBAL", "HOSTED DATA SOURCE" );
	public static final Type TYPE_PHYSICAL_FILE = new Type( "PHYSICAL_FILE", "GLOBAL", "PHYSICAL FILE" );
	public static final Type TYPE_LOGICAL_COLUMN = new Type( "LOGICAL_COLUMN", "GLOBAL", "LOGICAL COLUMN" );
	public static final Type TYPE_ANALYZER_VIEW = new Type( "ANALYZER_VIEW", "ANALYZER", "VIEW" );
	public static final Type TYPE_REPORT = new Type( "REPORT", "REPORTING", "REPORT" );
	public static final Type TYPE_OLAP_MODEL = new Type( "OLAP_MODEL", "MODELING", "OLAP MODEL" );
	public static final Type TYPE_REL_MODEL = new Type( "REL_MODEL", "MODELING", "RELATIONAL MODEL" );
	public static final Type TYPE_USER = new Type( "USER", "GLOBAL", "USER" );
	
	private String id;
	
	private String namespaceId;
	
	private String type;

	public Type() {
		
	}
	
	public Type( String id, String namespaceId, String type ) {
		this.id = id;
		this.namespaceId = namespaceId;
		this.type = type;
	}

	/**
	 * Returns the id of this type
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of this type
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of the namespace of this type
	 * @return
	 */
	public String getNamespaceId() {
		return namespaceId;
	}

	/**
	 * Sets the id of the namespace of this type
	 * @param namespaceId
	 */
	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}

	/**
	 * Returns the name of this type
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the name of this type
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
