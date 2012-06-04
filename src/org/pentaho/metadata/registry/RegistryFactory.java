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
 * A factory for providing an implementation of the metadata registry
 * @author jamesdixon
 *
 */
public class RegistryFactory {

	private static RegistryFactory instance;
	
	private IMetadataRegistry metadataRegistry;
	
	/**
	 * Returns the instance of the registry factory
	 * @return
	 */
	public static RegistryFactory getInstance() {
		return instance;
	}
	
	/**
	 * Returns the metadata registry
	 * @return
	 */
	public IMetadataRegistry getMetadataRegistry() {
		return metadataRegistry;
	}
	
	/** 
	 * Sets the metadata registry
	 * @param metadataRegistry
	 */
	public void setMetadataRegistry(IMetadataRegistry metadataRegistry) {
		this.metadataRegistry = metadataRegistry;
	}
	
	static {
		instance = new RegistryFactory();
	}
	
	
}
