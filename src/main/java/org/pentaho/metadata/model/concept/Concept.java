/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.metadata.model.concept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * This is the base implementation of a concept, and may be used in generic terms when defining parent concepts or
 * modeling metadata. More concrete implementations extend the Concept class within Pentaho Metadata, found in the
 * org.pentaho.metadata.model package.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class Concept implements IConcept {

  public Concept() {
    super();
  }

  private static final long serialVersionUID = -6912836203678095834L;

  public static String NAME_PROPERTY = "name"; //$NON-NLS-1$
  public static String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
  public static String SECURITY_PROPERTY = "security"; //$NON-NLS-1$

  Map<String, Object> properties = new HashMap<String, Object>();
  String id;
  IConcept parent;
  IConcept parentConcept;
  IConcept inheritedConcept;
  IConcept physicalConcept;
  List<IConcept> children = null;

  public void setParent( IConcept parent ) {
    this.parent = parent;
  }

  public IConcept getParent() {
    if ( parent == null ) {
      return parentConcept;
    }
    return parent;
  }

  public List<String> getUniqueId() {
    List<String> uid = new ArrayList<String>();
    if ( getParent() != null && getParent().getUniqueId() != null ) {
      uid.addAll( getParent().getUniqueId() );
    }
    uid.add( getId() );
    return uid;
  }

  public IConcept getChildByUniqueId( List<String> uid ) {
    return getChildByUniqueId( uid, 0 );
  }

  protected IConcept getChildByUniqueId( List<String> uid, int index ) {
    List<IConcept> children = getChildren();
    for ( IConcept concept : children ) {
      List<String> cuid = concept.getUniqueId();
      if ( cuid.get( cuid.size() - 1 ).equals( uid.get( index ) ) ) {
        if ( index == uid.size() - 1 ) {
          return concept;
        } else {
          return ( (Concept) concept ).getChildByUniqueId( uid, index + 1 );
        }
      }
    }
    return null;
  }

  public Map<String, Object> getChildProperties() {
    return properties;
  }

  public void setChildProperties( Map<String, Object> properties ) {
    this.properties = properties;
  }

  public Object getChildProperty( String name ) {
    return properties.get( name );
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public void setInheritedConcept( IConcept inheritedConcept ) {
    this.inheritedConcept = inheritedConcept;
  }

  public IConcept getInheritedConcept() {
    return inheritedConcept;
  }

  public IConcept getParentConcept() {
    return parentConcept;
  }

  public void setParentConcept( IConcept parentConcept ) {
    this.parentConcept = parentConcept;
  }

  public IConcept getPhysicalConcept() {
    return physicalConcept;
  }

  public void setPhysicalConcept( IConcept physicalConcept ) {
    this.physicalConcept = physicalConcept;
  }

  public IConcept getSecurityParentConcept() {
    return null;
  }

  public Map<String, Object> getProperties() {
    Map<String, Object> all = new HashMap<String, Object>();

    // Properties inherited from the "logical relationship":
    // BusinessColumn inherits from Physical Column, B.Table from Ph.Table
    if ( getInheritedConcept() != null ) {
      all.putAll( getInheritedConcept().getProperties() );
    }

    // Properties inherited from the pre-defined concepts like
    // "Base", "ID", "Name", "Description", etc.
    //
    if ( parentConcept != null ) {
      all.putAll( parentConcept.getProperties() );
    }

    // The security settings from the security parent:
    // Business table inherits from Business model, business column from business table
    if ( getSecurityParentConcept() != null ) {
      // Only take over the security information, nothing else
      Object securityProperty = (Object) getSecurityParentConcept().getProperty( SECURITY_PROPERTY );
      if ( securityProperty != null ) {
        all.put( SECURITY_PROPERTY, securityProperty );
      }
    }

    // The child properties overwrite everything else.
    all.putAll( properties );

    return all;
  }

  public Object getProperty( String name ) {
    return getProperties().get( name );
  }

  public void setProperty( String name, Object property ) {
    properties.put( name, property );
  }

  public void removeChildProperty( String name ) {
    properties.remove( name );
  }

  public LocalizedString getName() {
    return (LocalizedString) getProperty( NAME_PROPERTY );
  }

  public String getName( String locale ) {
    LocalizedString locName = getName();
    if ( locName == null ) {
      return getId();
    }
    String name = locName.getLocalizedString( locale );
    if ( name == null || name.trim().length() == 0 ) {
      return getId();
    }
    return name;
  }

  public void setName( LocalizedString name ) {
    setProperty( NAME_PROPERTY, name );
  }

  public String getDescription( String locale ) {
    LocalizedString locDesc = getDescription();
    if ( locDesc == null ) {
      return getId();
    }
    String name = locDesc.getLocalizedString( locale );
    if ( name == null || name.trim().length() == 0 ) {
      return getId();
    }
    return name;
  }

  public LocalizedString getDescription() {
    return (LocalizedString) getProperty( DESCRIPTION_PROPERTY );
  }

  public void setDescription( LocalizedString description ) {
    setProperty( DESCRIPTION_PROPERTY, description );
  }

  public int compareTo( Object o ) {
    Concept c = (Concept) o;
    return getId().compareTo( c.getId() );
  }

  public Object clone() {
    return clone( new Concept() );
  }

  protected Object clone( Concept clone ) {
    clone.setId( getId() );

    // shallow references
    clone.setChildProperties( getChildProperties() );
    clone.setParentConcept( getParentConcept() );
    return clone;
  }

  public List<IConcept> getChildren() {
    return children;
  }

  public void addChild( IConcept child ) {
    if ( children == null ) {
      children = new ArrayList<IConcept>();
    }
    children.add( child );
  }
}
