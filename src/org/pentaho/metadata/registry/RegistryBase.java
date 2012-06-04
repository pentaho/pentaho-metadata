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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RegistryBase implements IMetadataRegistry {
	
	private boolean initialized = false;
	
	public RegistryBase() throws Exception {
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void init() throws Exception {

		addNamespace(Namespace.NAMESPACE_GLOBAL);
		addNamespace(Namespace.NAMESPACE_PDI);
		addNamespace(Namespace.NAMESPACE_MODELING);
		addNamespace(Namespace.NAMESPACE_ANALYZER);
		addNamespace(Namespace.NAMESPACE_REPORTING);

		addVerb(Verb.VERB_POPULATES);
		addVerb(Verb.VERB_READS);
		addVerb(Verb.VERB_DEFINES);
		addVerb(Verb.VERB_USES);
		addVerb(Verb.VERB_EXECUTES);

		addType(Type.TYPE_TRANSFORMATION);
		addType(Type.TYPE_JOB);
		addType(Type.TYPE_RESOURCE);
		addType(Type.TYPE_DB_CONNECTION);
		addType(Type.TYPE_PHYSICAL_TABLE);
		addType(Type.TYPE_PHYSICAL_COLUMN);
		addType(Type.TYPE_HOSTED_SOURCE);
		addType(Type.TYPE_PHYSICAL_FILE);
		addType(Type.TYPE_LOGICAL_COLUMN);
		addType(Type.TYPE_ANALYZER_VIEW);
		addType(Type.TYPE_REPORT);
		addType(Type.TYPE_OLAP_MODEL);
		addType(Type.TYPE_REL_MODEL);
		
		addTypeLink(new TypeLink("TRANS","USES","DB_CONNECTION"));
		addTypeLink(new TypeLink("TRANS","POPULATES","PHYSICAL TABLE"));
		addTypeLink(new TypeLink("TRANS","POPULATES","PHYSICAL COLUMN"));
		addTypeLink(new TypeLink("TRANS","POPULATES","PHYSICAL FILE"));
		addTypeLink(new TypeLink("TRANS","READS","PHYSICAL TABLE"));
		addTypeLink(new TypeLink("TRANS","READS","PHYSICAL COLUMN"));
		addTypeLink(new TypeLink("TRANS","READS","PHYSICAL FILE"));
		
		addTypeLink(new TypeLink("JOB","EXECUTES","TRANS"));

		addTypeLink(new TypeLink("OLAP_MOD","USES","DB_CONNECTION"));
		addTypeLink(new TypeLink("OLAP_MOD","USES","PHYS_TABLE"));
		addTypeLink(new TypeLink("OLAP_MOD","USES","PHYS_COLUMN"));
		addTypeLink(new TypeLink("OLAP_MOD","DEFINES","LOGICAL_COLUMN"));

		addTypeLink(new TypeLink("REL_MODEL","USES","DB_CONNECTION"));
		addTypeLink(new TypeLink("REL_MODEL","USES","PHYS_TABLE"));
		addTypeLink(new TypeLink("REL_MODEL","USES","PHYS_COLUMN"));
		addTypeLink(new TypeLink("REL_MODEL","DEFINES","LOGICAL_COLUMN"));

		addTypeLink(new TypeLink("VIEW","USES","LOGICAL_COLUMN"));
		addTypeLink(new TypeLink("REPORT","USES","LOGICAL_COLUMN"));
		
		initialized = true;
	}	
	
	public List<Link> findObjectLinks( String subjectId, String verbId, String typeId ) {
		Set<String> verbIds = null;
		Set<String> typeIds = null;
		if( verbId != null ) {
			verbIds = new HashSet<String>();
			verbIds.add(verbId);
		}
		if( typeId != null ) {
			typeIds = new HashSet<String>();
			typeIds.add(typeId);
		}
		return findObjectLinks( subjectId, verbIds, typeIds );
	}

	public List<Link> findSubjectLinks( String objectId, String verbId, String typeId ) {
		Set<String> verbIds = null;
		Set<String> typeIds = null;
		if( verbId != null ) {
			verbIds = new HashSet<String>();
			verbIds.add(verbId);
		}
		if( typeId != null ) {
			typeIds = new HashSet<String>();
			typeIds.add(typeId);
		}
		return findSubjectLinks( objectId, verbIds, typeIds );
	}

	public List<Link> findDirectLinks( String entityId, String typeId ) {
		Set<String> typeIds = null;
		if( typeId != null ) {
			typeIds = new HashSet<String>();
			typeIds.add(typeId);
		}
		return findDirectLinks( entityId, typeIds );
	}
	
	public List<Entity> findAllLinkedEntities( String entityId, String typeId ) {
		Set<String> typeIds = null;
		if( typeId != null ) {
			typeIds = new HashSet<String>();
			typeIds.add(typeId);
		}
		return findAllLinkedEntities( entityId, typeIds );
	}
	
}
