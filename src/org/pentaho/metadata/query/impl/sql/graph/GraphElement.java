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
 * Copyright (c) 2006 - 20011 Pentaho Corporation..  All rights reserved.
 * 
 * Contributed by Nick Coleman
 */
package org.pentaho.metadata.query.impl.sql.graph;

/**
 * Interface implements by all elements of the graph
 */
public interface GraphElement {
	
	/**
	 * Returns true if this <code>GraphElement</code> is required by the graph
	 * 
	 * @return True if <code>GraphElement</code> is required
	 */
	public boolean isRequired();
	
	/**
	 * Returns true if this <code>GraphElement</code> is not required by the graph
	 * 
	 * @return True if <code>GraphElement</code> is not required
	 */
	public boolean isNotRequired();
	
	/**
	 * Returns true if this <code>GraphElement</code> is known to be required 
	 * or not required by the graph
	 * 
	 * @return True if <code>GraphElement</code> requirement is known
	 */
	public boolean isRequirementKnown();
	
	/**
	 * Assigns a requirement value to this element
	 * 
	 * @param required	True if element is required / false if not required
	 * @throws ConsistencyException	When assignment is inconsistent with graph constrains
	 */
	public void setRequirement(boolean required) throws ConsistencyException;
		
	/**
	 * Changes requirement setting to unknown
	 */
	public void clearRequirement();
	
	/**
	 * Returns the status of the <code>queued</code> flag which is used by the graphing
	 * functions to determine if this <code>GraphElement</code> is currently in the queue waiting
	 * to be processed.
	 * 
	 * @return value of <code>queued</code> flag
	 */
	public boolean isQueued();

	/**
	 * Sets value of <code>queued</code> flag
	 * 
	 * @param queued New value of <code>queued</code> flag
	 * @see #isQueued()
	 */
	public void setQueued(boolean queued);
}
