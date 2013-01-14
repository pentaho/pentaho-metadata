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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * A subclass of the simple registry that persists the registry in an XML document on the file system
 * @author jamesdixon
 *
 */
public class SimpleFileRegistry extends SimpleRegistry {

	private String filePath;

	public SimpleFileRegistry() throws Exception {
		super();
	}
	
	/**
	 * Returns the file path for the persistance file
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the file path for the persistance file
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @see org.pentaho.metadata.registry.SimpleRegistry#load()
	 */
	@Override
	protected void load() throws InvalidPropertiesFormatException, IOException {

		// empty the registry
		clear();

		File file = new File(filePath);
		if( file.exists() ) {
			InputStream in = new FileInputStream( file );
			Properties props = new Properties();
			props.loadFromXML(in);
			int idx = 0;
			List<Link> links = new ArrayList<Link>();
			while( true ) {
				String sub = (String) props.get("link-"+idx+"-subId");
				String subType = (String) props.get("link-"+idx+"-subType");
				String obj = (String) props.get("link-"+idx+"-objId");
				String objType = (String) props.get("link-"+idx+"-objType");
				String verb = (String) props.get("link-"+idx+"-verb");
				if( sub != null && obj != null && verb != null ) {
					Link link = new Link(sub, subType, verb, obj, objType);
					links.add(link);
					idx++;
				} else {
					break;
				}
			}
			setLinks(links);

			idx = 0;
			while( true ) {
				String id = (String) props.get("entity-"+idx+"-id");
				String title = (String) props.get("entity-"+idx+"-title");
				String typeId = (String) props.get("entity-"+idx+"-type");
				if( id != null && typeId != null ) {
					Entity entity = new Entity(id, title, typeId);
					addEntity(entity);
					
					int idx2 = 0;
					while(true) {
						String key = (String) props.get("entity-"+idx+"-attrkey-"+idx2);
						String value = (String) props.get("entity-"+idx+"-attrvalue-"+idx2);
						if( key != null && value != null ) {
							entity.setAttribute(key, StringEscapeUtils.unescapeJava(value));
							idx2++;
						} else {
							break;
						}
					}
					idx++;
				} else {
					break;
				}
			}
		}
	
	}

	/**
	 * @see org.pentaho.metadata.registry.IMetadataRegistry#commit()
	 */
	@Override
	public void commit() throws Exception {

		// save the links
		List<Link> links = getLinks();

		Properties props = new Properties();
		int idx = 0;
		for( Link link : links ) {
			props.put("link-"+idx+"-subId", link.getSubjectId());
			props.put("link-"+idx+"-subType", link.getSubjectTypeId());
			props.put("link-"+idx+"-verb", link.getVerbId());
			props.put("link-"+idx+"-objId", link.getObjectId());
			props.put("link-"+idx+"-objType", link.getObjectTypeId());
			idx++;
		}
		
		idx = 0;
		Map<String,Map<String,Entity>> entities = getEntities();
		for( Entry<String,Map<String,Entity>> entry : entities.entrySet()) {
			Map<String,Entity> typeMap = entry.getValue();
			Set<Entry<String,Entity>> typeSet = typeMap.entrySet();
			for( Entry<String,Entity> typeEntry : typeSet ) {
				Entity entity = typeEntry.getValue();
				props.put("entity-"+idx+"-id", entity.getId());
				props.put("entity-"+idx+"-type", entity.getTypeId());
				if(entity.getTitle() != null) {
					props.put("entity-"+idx+"-title", entity.getTitle());
				} else {
					props.put("entity-"+idx+"-title", entity.getId());
				}
				
				Map<String,String> attrMap = entity.getAttributes();

				int idx2 = 0;
				for( Entry<String,String> attrEntry : attrMap.entrySet() ) {
					String key = attrEntry.getKey();
					String value = attrEntry.getValue();
					props.put("entity-"+idx+"-attrkey-"+idx2, key);
					props.put("entity-"+idx+"-attrvalue-"+idx2, StringEscapeUtils.escapeJava(value));
					idx2++;
				}
				
				idx++;
			}
		}
		
		File file = new File(filePath);
		OutputStream out = new FileOutputStream(file);
		try {
			props.storeToXML(out, null);
		} finally {
			out.close();
		}
	}

}
