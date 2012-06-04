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
 * Defines a link between two existing entities. Links are defined between a subject and an bject using a verb.
 * @author jamesdixon
 *
 */
public class Link {

	private String subjectId;
	private String subjectTypeId;
	private String verbId;
	private String objectId;
	private String objectTypeId;
	
	public Link() {
	}

	public Link( Entity subject, Verb verb, Entity object ) {
		this(subject.getId(), subject.getTypeId(), verb.getId(), object.getId(), object.getTypeId());
	}
	
	public Link( String subjectId, String subjectTypeId, String verbId, String objectId, String objectTypeId ) {
		this.subjectId = subjectId;
		this.subjectTypeId = subjectTypeId;
		this.verbId = verbId;
		this.objectId = objectId;
		this.objectTypeId = objectTypeId;
	}
	
	/**
	 * Returns the type id of the subject entity
	 * @return
	 */
	public String getSubjectTypeId() {
		return subjectTypeId;
	}

	/**
	 * Sets the type of the subject entity
	 * @param subjectTypeId
	 */
	public void setSubjectTypeId(String subjectTypeId) {
		this.subjectTypeId = subjectTypeId;
	}

	/**
	 * Returns the type id of the object entity
	 * @return
	 */
	public String getObjectTypeId() {
		return objectTypeId;
	}

	/**
	 * Sets the type of the object entity
	 * @param objectTypeId
	 */
	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	/**
	 * Returns the id of the subject entity
	 * @return
	 */
	public String getSubjectId() {
		return subjectId;
	}
	
	/**
	 * Sets the id of the subject entity
	 * @param subjectId
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	/**
	 * Returns the id of the object entity
	 * @return
	 */
	public String getObjectId() {
		return objectId;
	}
	
	/**
	 * Sets the id of the object entity
	 * @param objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	/**
	 * Returns the id of the verb
	 * @return
	 */
	public String getVerbId() {
		return verbId;
	}
	
	/**
	 * Sets the id of the verb
	 * @param verbId
	 */
	public void setVerbId(String verbId) {
		this.verbId = verbId;
	}
	
}
