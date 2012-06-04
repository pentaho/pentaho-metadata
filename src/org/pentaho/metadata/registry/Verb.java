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
 * Defines a verb that is used to link entities in the metadata registry. Also defines static common verbs
 * @author jamesdixon
 *
 */
public class Verb {

	public static final Verb VERB_POPULATES = new Verb(  "POPULATES", "GLOBAL", "POPULATES" );
	public static final Verb VERB_READS = new Verb(  "READS", "GLOBAL", "READS" );
	public static final Verb VERB_DEFINES = new Verb(  "DEFINES", "GLOBAL", "DEFINES" );
	public static final Verb VERB_USES = new Verb(  "USES", "GLOBAL", "USES" );
	public static final Verb VERB_EXECUTES = new Verb(  "EXECUTES", "GLOBAL", "EXECUTES" );
	public static final Verb VERB_CREATED = new Verb(  "CREATED", "GLOBAL", "CREATED" );
			
	private String id;
	
	private String namespaceId;
	
	private String verbId;

	public Verb( ) {
	}
	
	public Verb( String id, String namespaceId, String verbId ) {
		this.id = id;
		this.namespaceId = namespaceId;
		this.verbId = verbId;
	}
	
	/**
	 * Returns the id of the verb
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the verb
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of the namespace of the verb
	 * @return
	 */
	public String getNamespaceId() {
		return namespaceId;
	}

	/**
	 * Sets the id of the namespace of the verb
	 * @param namespaceId
	 */
	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}

	/**
	 * Returns the id of the verb
	 * @return
	 */
	public String getVerbId() {
		return verbId;
	}

	/**
	 * Sets the verb id
	 * @param verbId
	 */
	public void setVerbId(String verbId) {
		this.verbId = verbId;
	}
	
}
