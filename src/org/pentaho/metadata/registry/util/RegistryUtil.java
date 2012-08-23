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
package org.pentaho.metadata.registry.util;

import java.util.regex.Pattern;

import org.pentaho.metadata.registry.Entity;
import org.pentaho.metadata.registry.IMetadataRegistry;
import org.pentaho.metadata.registry.RegistryFactory;
import org.pentaho.metadata.registry.Type;

/**
 * Collection of utility methods for interacting with the Metadata Registry.
 */
public class RegistryUtil {
  /**
   * Separator for parts of a composite id
   */
  public static final String COMPOUND_ID_SEPARATOR = "~";

  /**
   * Placeholder for null values in a composite id
   */
  public static final String NULL = "$NULL$";
  /**
   * Placeholder for empty strings (String's of length 0) in a composite id
   */
  public static final String EMPTY = "$EMPTY$";

  private static final Pattern COMPOUND_ID_SEPARATOR_PATTERN = Pattern.compile(COMPOUND_ID_SEPARATOR);

  /**
   * Generates a composite id for a typed entity
   * @param name The name of the entity
   * @param type the type of the entity
   * @return the composite id
   */
  public String generateTypedId( String name, String type ) {
	  return generateCompositeId(type, name);
  }
  
  /**
   * Generates a composite id for a database table
   * @param databaseName
   * @param schemaName
   * @param tableName
   * @return the composite id
   */
  public String generateTableId( String databaseName, String schemaName, String tableName ) {
	  return generateCompositeId(databaseName, schemaName, tableName);
  }
  
  /**
   * Generates a composite id for a document
   * @param documentName The name of the document
   * @param documentIdOrPath The document id or document path
   * @return
   */
  public String generateDocumentId( String documentName, String documentIdOrPath ) {
	  return generateCompositeId(documentName, documentIdOrPath);
  }
  
  /**
   * Generate an composite id by concatenating the provided parts together with the {@link #COMPOUND_ID_SEPARATOR}.
   * <p>
   * See {@link #splitCompositeId} to break a composite id apart.
   * </p>
   *
   * @param parts Parts of an id to be joined together to form a single id
   * @return Id created from the provided parts.
   */
  public String generateCompositeId(String... parts) {
    if (parts == null || parts.length == 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i != 0) {
        sb.append(COMPOUND_ID_SEPARATOR);
      }
      // Replace nulls and empty strings with a placeholder so we can parse them back out properly
      String s = parts[i];
      if (s == null) {
        s = NULL;
      } else if (s.length() == 0) {
        s = EMPTY;
      }
      sb.append(s);
    }
    return sb.toString();
  }

  /**
   * Split apart a composite id into it's individual parts. Converse to {@link #generateCompositeId(String...)}.
   *
   * @param id
   * @return
   */
  public String[] splitCompositeId(String id) {
    if (id == null) {
      return null;
    }
    String[] parts = COMPOUND_ID_SEPARATOR_PATTERN.split(id);
    // Replace placeholders with actual values so we have a lossless conversion
    for (int i = 0; i < parts.length; i++) {
      if (NULL.equals(parts[i])) {
        parts[i] = null;
      } else if (EMPTY.equals(parts[i])) {
        parts[i] = "";
      }
    }
    return parts;
  }
	
	/**
	 * Returns an entity representing a database table. The entity is retrieved from the registry. If 'create' is false, returns
	 * null if the entity does not exist in the registry. If it is true, the entity will be created if it does not exist. If the
	 * entity is created the registry will be be committed, the caller needs to commit the registry.
	 * @param databaseName
	 * @param schemaName
	 * @param tableName
	 * @param create
	 * @return
	 */
	public Entity getTableEntity( String databaseName, String schemaName, String tableName, boolean create ) {
		String id = generateTableId(databaseName, schemaName, tableName);
	    RegistryFactory factory = RegistryFactory.getInstance();
	    IMetadataRegistry registry = factory.getMetadataRegistry();
	    Entity entity = registry.getEntity(id, Type.TYPE_PHYSICAL_TABLE.getId());
	    if( entity == null && create ) {
	    	entity = new Entity(id, tableName, Type.TYPE_PHYSICAL_TABLE.getId());
	    	registry.addEntity(entity);
	    }
		return entity;
	}

	/**
	 * Modifies a numeric entity attribute by adding an offset to it. If the attribute does not exist, its initial
	 * value is set to the offset. The entity must exist in the registry.
	 * @param entityId
	 * @param typeId
	 * @param attrId
	 * @param offset
	 * @return true if the update succeeded.
	 */
	public boolean updateAttribute( String entityId, String typeId, String attrId, long offset ) {
	    RegistryFactory factory = RegistryFactory.getInstance();
	    IMetadataRegistry registry = factory.getMetadataRegistry();
	    Entity entity = registry.getEntity(entityId, typeId);
	    if( entity == null ) {
	    	return false;
	    }
	    return updateAttribute(entity, attrId, offset);
	}
	
	/**
	 * Modifies a numeric entity attribute by adding an offset to it. If the attribute does not exist, its initial
	 * value is set to the offset. The entity must exist in the registry.
	 * @param entity
	 * @param attrId
	 * @param offset
	 * @return true if the update succeeded.
	 */
	public boolean updateAttribute( Entity entity, String attrId, long offset ) {
		String attrValue = entity.getAttribute(attrId);
		long newValue = 0;
		if( attrValue == null ) {
			newValue = offset;
		} else {
			long value = Long.parseLong(attrValue);
			newValue = value + offset;
		}
		return setAttribute(entity, attrId, newValue);
	}	

	/**
	 * Sets an entity attribute
	 * @param attrId
	 * @param boost
	 * @return true if the update succeeded.
	 */
	public boolean setAttribute( String entityId, String typeId, String attrId, long value ) {
	    RegistryFactory factory = RegistryFactory.getInstance();
	    IMetadataRegistry registry = factory.getMetadataRegistry();
	    Entity entity = registry.getEntity(entityId, typeId);
	    if( entity == null ) {
	    	return false;
	    }
		return setAttribute( entity, attrId, Long.toString(value));
	}

	/**
	 * Sets an entity attribute
	 * @param attrId
	 * @param boost
	 * @return true if the update succeeded.
	 */
	public boolean setAttribute( String entityId, String typeId, String attrId, String value ) {
	    RegistryFactory factory = RegistryFactory.getInstance();
	    IMetadataRegistry registry = factory.getMetadataRegistry();
	    Entity entity = registry.getEntity(entityId, typeId);
	    if( entity == null ) {
	    	return false;
	    }
		return setAttribute( entity, attrId, value);
	}

	/**
	 * Sets an entity attribute
	 * @param attrId
	 * @param boost
	 * @return true if the update succeeded.
	 */
	public boolean setAttribute( Entity entity, String attrId, long value ) {
		return setAttribute( entity, attrId, Long.toString(value));
	}
	
	/**
	 * Sets an entity attribute
	 * @param attrId
	 * @param boost
	 * @return true if the update succeeded.
	 */
	public boolean setAttribute( Entity entity, String attrId, String value ) {
		entity.setAttribute(attrId, value);
	    return true;
	}	
}
