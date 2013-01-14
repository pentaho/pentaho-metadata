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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a metadata entity. Id, type, and title must be provided. Attributes are optional.
 * This is a lightweight, serializable class
 * @author jamesdixon
 *
 */
public class Entity implements Serializable, Comparable<Entity> {

	private static final long serialVersionUID = -3474839958774808729L;

	private String id;

	private String typeId;
	
	private String title;
	
	private Map<String,String> attributes = new HashMap<String,String>();
	
	public Entity() {
		
	}
	
	public Entity( String id, String title, String typeId ) {
		this.id = id;
		this.typeId = typeId;
		this.title = title;
	}
	
	/**
	 * Returns the type id of the entity
	 * @return
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Returns the title of the entity
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the entity
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the type id of the entity
	 * @param typeId
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * Gets the id of the entity
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the entity
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the attribute map of the entity
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attribute map of the entity. Arrtibute valuse should be serializable
	 * @param attributes
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Returns an attribute of the entity
	 * @param name
	 * @return
	 */
	public String getAttribute( String name ) {
		return attributes.get(name);
	}
	
	/**
	 * Sets an attribute of the entity
	 * @param name
	 * @param object
	 */
	public void setAttribute( String name, String object ) {
		attributes.put( name, object );
	}

	@Override
	public int compareTo(Entity arg0) {
		return id.compareTo(arg0.getId());
	}
	
}
