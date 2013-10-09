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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.schema.concept;

import java.util.Map;

/**
 * A concept interface describes how a Concept inherits properties from linked Concepts. For now, we will start with a
 * simple Parent/Child relationship.
 * 
 * @author Matt
 * 
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.model.concept.IConcept
 */
public interface ConceptInterface {
  /**
   * @return the name of the concept
   */
  public String getName();

  /**
   * @param name
   *          the name of the concept to set
   */
  public void setName( String name );

  /**
   * @return The parent ConceptInterface, if this is a built-in concept (ConceptString, etc) there is no parent and this
   *         method will return null.
   */
  public ConceptInterface getParentInterface();

  /**
   * @param parentInterface
   *          The parent concept to inherit properties from
   */
  public void setParentInterface( ConceptInterface parentInterface );

  /**
   * @return The interited ConceptInterface, meant to inherit from "related" concepts: business column --> physical
   *         column etc.
   */
  public ConceptInterface getInheritedInterface();

  /**
   * @param parentInterface
   *          The inherited concept to inherit properties from
   */
  public void setInheritedInterface( ConceptInterface inheritedInterface );

  /**
   * @return the securityParentInterface
   */
  public ConceptInterface getSecurityParentInterface();

  /**
   * @param securityParentInterface
   *          the securityParentInterface to set
   */
  public void setSecurityParentInterface( ConceptInterface securityParentInterface );

  /**
   * @return the map of property name/interfaces for this concept interfaces, but also the parent (grand-parent, etc)
   *         property interface(s).
   */
  public Map<String, ConceptPropertyInterface> getPropertyInterfaces();

  /**
   * @return the map of property name/interfaces for the class implementing this concept interface, ONLY the child
   *         properties
   */
  public Map<String, ConceptPropertyInterface> getChildPropertyInterfaces();

  /**
   * @return the map of property name/interfaces for the class implementing this concept interface: ONLY the parents
   *         (grand-parent, etc) properties
   */
  public Map<String, ConceptPropertyInterface> getParentPropertyInterfaces();

  /**
   * @return the map of property name/interfaces for the class implementing this concept interface: ONLY the inherited
   *         (physical column) properties
   */
  public Map<String, ConceptPropertyInterface> getInheritedPropertyInterfaces();

  /**
   * @return the map of property name/interfaces for the class implementing this concept interface: ONLY the inherited
   *         (physical column) properties
   */
  public Map<String, ConceptPropertyInterface> getSecurityPropertyInterfaces();

  /**
   * @param property
   *          the property to add to the map, using the name of the property as the entry in the property interface map.
   */
  public void addProperty( ConceptPropertyInterface property );

  /**
   * Look for a property in the complete set of properties, including the ones from the parent(s)
   * 
   * @param id
   *          the property id to look for
   * @return The concept property or null if nothing could be found.
   */
  public ConceptPropertyInterface getProperty( String id );

  /**
   * Look for a property in the child properties only
   * 
   * @param id
   *          the property id to look for
   * @return The concept property or null if nothing could be found.
   */
  public ConceptPropertyInterface getChildProperty( String id );

  /**
   * Look for a property in the parent properties only
   * 
   * @param id
   *          the property id to look for
   * @return The concept property or null if nothing could be found.
   */
  public ConceptPropertyInterface getParentProperty( String id );

  /**
   * Look for a property in the inherited properties only
   * 
   * @param id
   *          the property id to look for
   * @return The concept property or null if nothing could be found.
   */
  public ConceptPropertyInterface getInheritedProperty( String id );

  /**
   * Look for a property in the security properties only
   * 
   * @param id
   *          the property id to look for
   * @return The concept property or null if nothing could be found.
   */
  public ConceptPropertyInterface getSecurityProperty( String id );

  /**
   * @return the depth from the root concept<br>
   * <br>
   *         Depth 1: child<br>
   *         Depth 2: child --> parent<br>
   *         Depth 3: child --> parent --> parent<br>
   *         etc.<br>
   */
  public int getDepth();

  /**
   * @return the path from root --> .. --> gp --> parent --> concept
   */
  public String[] getPath();

  /**
   * @param path
   *          the path to match with
   * @return true if the path matches the specified path from root --> .. --> gp --> parent --> concept
   */
  public boolean matches( String[] path );

  /**
   * @return an array of all the childs property ids
   */
  public String[] getChildPropertyIDs();

  /**
   * @return an array of all the property ids (including the one from the optional parent(s))
   */
  public String[] getPropertyIDs();

  /**
   * Clears the properties of this concept, not the parents or anything like that.
   */
  public void clearChildProperties();

  /**
   * Find a localized property (type ConceptPropertyType.PROPERTY_TYPE_LOC_STRING)
   * 
   * @param propertyName
   *          the name
   * @param locale
   *          the locale to search for
   * @return the localized value (string) or null if nothing could be found
   */
  public String getLocalizedProperty( String propertyName, String locale );

  /**
   * Add a localized string property (type ConceptPropertyType.PROPERTY_TYPE_LOC_STRING) to this concept
   * 
   * @param propertyName
   *          the name
   * @param locale
   *          the locale
   * @param value
   *          the value to store
   */
  public void addLocalizedProperty( String propertyName, String locale, String value );

  /**
   * @return The locale used in the properties of type Localized String
   */
  public String[] getUsedLocale();

  /**
   * Get an array of the localized concept properties (ONLY type Localized String!!)
   * 
   * @param locale
   *          the locale to look for
   * @return an array of the localized concept properties
   */
  public ConceptPropertyInterface[] getLocalizedProperties( String locale );

  /**
   * Special utility method: save a special localized property indicating the name
   * 
   * @param locale
   *          the locale the name is in
   * @param name
   *          the name of the object that contains this concept
   */
  public void setName( String locale, String name );

  /**
   * special utility method: get the value of a special localized property indicating the name
   * 
   * @param locale
   *          the locale the name is in
   * @return the localized name or null if nothing was found in the specified locale
   */
  public String getName( String locale );

  /**
   * Special utility: save a special localized property indicating the description
   * 
   * @param locale
   *          the locale the description is in
   * @param name
   *          the description of the object that contains this concept
   */
  public void setDescription( String locale, String description );

  /**
   * special utility method: get the value of a special localized property indicating the description
   * 
   * @param locale
   *          the locale the description is in
   * @return the localized description or null if nothing was found in the specified locale
   */
  public String getDescription( String locale );

  /**
   * @return true if the concept has changed
   */
  public boolean hasChanged();

  /**
   * @param changed
   *          the changed flag to set
   */
  public void setChanged( boolean changed );

  /**
   * flag the concept as changed
   */
  public void setChanged();

  /**
   * Clear the changed flag of this concept and the underlying objects
   */
  public void clearChanged();

  /**
   * @return a copy of the concept interface
   */
  public Object clone();

  /**
   * Remove a property from the children.
   * 
   * @param property
   *          The property to remove
   */
  public void removeChildProperty( ConceptPropertyInterface property );

  /**
   * @return true if the concept has a parent concept
   */
  public boolean hasParentConcept();

  /**
   * @return true if the concept has an inherited concept
   */
  public boolean hasInheritedConcept();

  /**
   * @return true if the concept has a security parent concept
   */
  public boolean hasSecurityParentConcept();

  /**
   * Go up to the parent, the grandparent, etc, until you find a parent concept.
   * 
   * @return the first parent concept by going up until the root
   */
  public ConceptInterface findFirstParentConcept();

}
