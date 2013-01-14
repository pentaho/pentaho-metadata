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
 * Defines a namespace for the registry
 * @author jamesdixon
 *
 */
public class Namespace {

	// define static global namespaces
	public static final Namespace NAMESPACE_GLOBAL = new Namespace("GLOBAL");
	public static final Namespace NAMESPACE_PDI = new Namespace("PDI");
	public static final Namespace NAMESPACE_MODELING = new Namespace("MODELING");
	public static final Namespace NAMESPACE_ANALYZER = new Namespace("ANALYZER");
	public static final Namespace NAMESPACE_REPORTING = new Namespace("REPORTING");
	
	private String id;
	
	private String tool;

	public Namespace() {
	}
	
	public Namespace( String id ) {
		this.id = id;
	}
	
	/**
	 * Returns the id of this namespace
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of this namespace
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the default tool for this namespace
	 * @return
	 */
	public String getTool() {
		return tool;
	}

	/**
	 * Sets the default tool for this namespace
	 * @param tool
	 */
	public void setTool(String tool) {
		this.tool = tool;
	}
	
}
