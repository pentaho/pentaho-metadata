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
 * Defines a common link between two types. For example type "TRANSFORMATION" "POPULATES" "PHYSICAL TABLE"
 * @author jamesdixon
 *
 */
public class TypeLink {

	private String subjectTypeId;
	private String verbId;
	private String objectTypeId;
	
	public TypeLink() {
		
	}
	
	public TypeLink( String subjectTypeId, String verbId, String objectTypeId ) {
		this.subjectTypeId = subjectTypeId;
		this.verbId = verbId;
		this.objectTypeId = objectTypeId;
	}
	
	/**
	 * Returns the type id of the subject
	 * @return
	 */
	public String getSubjectTypeId() {
		return subjectTypeId;
	}
	
	/**
	 * Sets the type id of the subject
	 * @param subjectTypeId
	 */
	public void setSubjectTypeId(String subjectTypeId) {
		this.subjectTypeId = subjectTypeId;
	}
	
	/**
	 * Returns the type id of the object
	 * @return
	 */
	public String getObjectTypeId() {
		return objectTypeId;
	}
	
	/**
	 * Sets the type id of the object
	 * @param objectTypeId
	 */
	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}
	
	/**
	 * Returns the id of the verb that connects the subject and the object
	 * @return
	 */
	public String getVerbId() {
		return verbId;
	}
	
	/**
	 * Sets the id of the verb that connects the subject and the object
	 * @param verbId
	 */
	public void setVerbId(String verbId) {
		this.verbId = verbId;
	}
	
}
