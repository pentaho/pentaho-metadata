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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A simple implementation of a metadata registry that uses in-memory maps to store the entities and links.
 * @author jamesdixon
 *
 */
public abstract class SimpleRegistry extends RegistryBase {

	private List<Namespace> namespaces = new ArrayList<Namespace>();
	
	private List<Type> types = new ArrayList<Type>();
	
	private Map<String,Map<String,Entity>> entities = new HashMap<String,Map<String,Entity>>();
	
	private List<Verb> verbs = new ArrayList<Verb>();
	
	private List<Link> links = new ArrayList<Link>();
	
	private List<TypeLink> typeLinks = new ArrayList<TypeLink>();
	
	/**
	 * Loads the registry from its storage location
	 * @throws Exception
	 */
	protected abstract void load() throws Exception;
	
	public SimpleRegistry() throws Exception {
		super();
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#init
	 */
	@Override
	public void init() throws Exception {
		namespaces = new ArrayList<Namespace>();
		
		types = new ArrayList<Type>();
		
		entities = new HashMap<String,Map<String,Entity>>();
		
		verbs = new ArrayList<Verb>();
		
		links = new ArrayList<Link>();
		
		typeLinks = new ArrayList<TypeLink>();
		
		super.init();
		
		load();
	}
	
	/**
	 * Returns the map of all entities
	 * @return
	 */
	protected Map<String, Map<String,Entity>> getEntities() {
		return entities;
	}

	/**
	 * Sets the map of all entities
	 * @param entities
	 */
	protected void setEntities(Map<String, Map<String,Entity>> entities) {
		this.entities = entities;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#clear()
	 */
	@Override
	public void clear() {
		
		entities = new HashMap<String,Map<String,Entity>>();
		links = new ArrayList<Link>();
		
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getEntitiesOfType(String, String, boolean)
	 */
	@Override
	public List<Entity> getEntitiesOfType( String typeId, String match, boolean exactMatch ) {
		
		List<Entity> results = new ArrayList<Entity>();
		for( Entry<String,Map<String,Entity>> entry : entities.entrySet() ) {
			
			Map <String,Entity> typeMap = entry.getValue();
			Entity entity = typeMap.get(typeId);
			if( entity != null ) {
				if( match == null ) {
					results.add(entity);
				}
				else if( exactMatch && entity.getId().equals(match)) {
					results.add(entity);
				}
				else if(entity.getId().contains(match)) {
					results.add(entity);
				}
			}
		}
		return results;
		
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#findObjectLinks(String, Set, Set)
	 */
	@Override
	public List<Link> findObjectLinks( String subjectId, Set<String> verbIds, Set<String> typeIds ) {
		
		List<Link> results = new ArrayList<Link>();
		for( Link link : links ) {
			if( link.getSubjectId().equalsIgnoreCase(subjectId) ) {
				if( ( verbIds == null || verbIds.contains(link.getVerbId() ) ) && 
					( typeIds == null || typeIds.contains(link.getObjectTypeId())) ) {
					results.add(link);
				}
			}
		}
		return results;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#findSubjectLinks(String, Set, Set)
	 */
	@Override
	public List<Link> findSubjectLinks( String objectId, Set<String> verbIds, Set<String> typeIds ) {
		
		List<Link> results = new ArrayList<Link>();
		for( Link link : links ) {
			if( link.getObjectId().equalsIgnoreCase(objectId) ) {
				if( ( verbIds == null || verbIds.contains(link.getVerbId() ) ) && 
					( typeIds == null || typeIds.contains(link.getSubjectTypeId())) ) {
					results.add(link);
				}
			}
		}
		return results;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#findDirectLinks(String, Set)
	 */
	@Override
	public List<Link> findDirectLinks( String entityId, Set<String> typeIds ) {
		List<Link> results = findObjectLinks(entityId, null, typeIds);
		List<Link> results2 = findSubjectLinks(entityId, null, typeIds);
		for( Link link: results2 ) {
			results.add(link);
		}
		return results;
		
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#findAllLinkedEntities(String, Set)
	 */
	@Override
	public List<Entity> findAllLinkedEntities( String entityId, Set<String> typeIds ) {
		
		List<Entity> found = new ArrayList<Entity>();
		Set<String> foundIds = new HashSet<String>();
		List<Link> targets = findDirectLinks(entityId, (String) null);
		Set<String> done = new HashSet<String>(); 
		done.add(entityId);
		// prevent the source entity from being returned
		foundIds.add(entityId);
		findAllLinks( typeIds, found, foundIds, targets, done);
		return found;
	}

	/**
	 * Finds all the links between the entities found so far of the specified types
	 * @param typeIds
	 * @param found
	 * @param foundIds
	 * @param targets
	 * @param done
	 */
	protected void findAllLinks( Set<String> typeIds, List<Entity> found, Set<String> foundIds, List<Link> targets, Set<String> done ) {
		
		// check to see if any of the targets match
		for( Link target : targets ) {
			done.add(target.getObjectId());
			done.add(target.getSubjectId());
			if( !foundIds.contains(target.getObjectId()) && (typeIds == null || typeIds.contains( target.getObjectTypeId() ))) {
				found.add( getEntity(target.getObjectId(), target.getObjectTypeId()) );
				foundIds.add(target.getObjectId());
			}
			if( !foundIds.contains(target.getSubjectId()) && (typeIds == null || typeIds.contains( target.getSubjectTypeId() ))) {
				found.add(getEntity( target.getSubjectId(), target.getSubjectTypeId() ));
				foundIds.add(target.getSubjectId());
			}
		}
		// create the new target list
		List<Link> newTargets = new ArrayList<Link>();
		for( Link target : targets ) {
			List<Link> results = findDirectLinks(target.getObjectId(), (String) null);
			for( Link link : results ) {
				if( !(done.contains(link.getObjectId()) && done.contains(link.getSubjectId()) ) ) {
					newTargets.add(link);
				}
			}
			results = findDirectLinks(target.getSubjectId(), (String) null);
			for( Link link : results ) {
				if( !(done.contains(link.getObjectId()) && done.contains(link.getSubjectId()) ) ) {
					newTargets.add(link);
				}
			}
		}	
		if( newTargets.size() == 0 ) {
			// nothing new to examine, we are done
			return;
		} else {
			findAllLinks( typeIds, found, foundIds, newTargets, done );
		}
		
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addLink(Link)
	 */
	@Override
	public void addLink(Link link) {
		links.add(link);
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addLink(Link)
	 */
	@Override
	public boolean removeLink(Link link) {
		if( links.contains(link) ) {
			links.remove(link);
			return true;
		}
		return false;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getLinks()
	 */
	@Override
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * Sets the list of links
	 * @param links
	 */
	protected void setLinks(List<Link> links) {
		this.links = links;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getVerbs()
	 */
	@Override
	public List<Verb> getVerbs() {
		return verbs;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#setVerbs(List)
	 */
	@Override
	public void setVerbs(List<Verb> verbs) {
		this.verbs = verbs;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getNamespaces()
	 */
	@Override
	public List<Namespace> getNamespaces() {
		return namespaces;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addNamespace(Namespace)
	 */
	@Override
	public void addNamespace( Namespace namespace ) {
		namespaces.add(namespace);
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getTypes()
	 */
	@Override
	public List<Type> getTypes() {
		return types;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#setTypes(List)
	 */
	@Override
	public void setTypes(List<Type> types) {
		this.types = types;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#setNamespaces(List)
	 */
	@Override
	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addType(Type)
	 */
	@Override
	public void addType( Type type ){
		types.add( type );
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addEntity(Entity)
	 */
	@Override
	public void addEntity( Entity entity ) {
		Map<String,Entity> typeMap = entities.get(entity.getId());
		if( typeMap == null ) {
			typeMap = new HashMap<String,Entity>();
			entities.put(entity.getId(), typeMap);
		}
		typeMap.put(entity.getTypeId(), entity);
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#deleteEntity(String)
	 */
	@Override
	public boolean removeEntity( Entity entity ) {
		
		Map<String,Entity> typeMap = entities.get(entity.getId());
		if( typeMap == null || typeMap.get(entity.getTypeId()) == null ) {
			// the entity could not be found
			return false;
		}
		typeMap.remove(entity.getTypeId());
		if( typeMap.size() == 0 ) {
			// there are no entities of any type with this id
			entities.remove(entity.getId());
		}
		return true;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getEntity(String, String)
	 */
	@Override
	public Entity getEntity( String id, String typeId ) {
		Map<String,Entity> typeMap = entities.get(id);
		if( typeMap == null ) {
			return null;
		}
		return typeMap.get(typeId);
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getEntities(String)
	 */
	@Override
	public List<Entity> getEntities( String id ) {
		List<Entity> entityList = new ArrayList<Entity>();
		Map<String,Entity> typeMap = entities.get(id);
		if( typeMap != null ) {
			Set<Entry<String,Entity>> entries = typeMap.entrySet();
			for( Entry<String,Entity> entry : entries ) {
				Entity entity = entry.getValue();
				entityList.add(entity);
			}
		}
		return entityList;
	}
	
	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addVerb(Verb)
	 */
	@Override
	public void addVerb( Verb verb ) {
		verbs.add( verb );
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#addTypeLink(TypeLink)
	 */
	@Override
	public void addTypeLink(TypeLink link) {
		typeLinks.add(link);
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#getTypeLinks()
	 */
	@Override
	public List<TypeLink> getTypeLinks() {
		return typeLinks;
	}
	
}
