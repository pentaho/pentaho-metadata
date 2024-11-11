/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.schema;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

/**
 * Contains a list of the required properties per object class. These property values can change, but the properties
 * themselves cannot be removed.
 * 
 * @author Matt (updated gmoran)
 * 
 */
public class RequiredProperties extends ChangedFlag implements ChangedFlagInterface {
  private Map<Class, List> map;

  public RequiredProperties() {
    this.map = new Hashtable<Class, List>();

    setDefault(); // TODO: For testing purposes, persist in CWM later on.
  }

  public Map getMap() {
    return map;
  }

  public void setMap( Map<Class, List> map ) {
    this.map = map;
  }

  public void setDefaultProperties( Class subject, List propertyTypes ) {
    map.put( subject, propertyTypes );
    setChanged();
  }

  public List getDefaultProperties( Class subject ) {
    return (List) map.get( subject );
  }

  public Class[] getSubjects() {
    Set<Class> keySet = map.keySet();
    return (Class[]) keySet.toArray( new Class[keySet.size()] );
  }

  @SuppressWarnings( "deprecation" )
  public void setDefault() {
    // First we do the Physical Table...
    Class subject = PhysicalTable.class;
    List<DefaultProperty> propertyTypes = new ArrayList<DefaultProperty>();
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.TARGET_TABLE ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.NAME ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.DESCRIPTION ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.TABLE_TYPE ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.RELATIVE_SIZE ) );
    setDefaultProperties( subject, propertyTypes );

    subject = BusinessTable.class;
    propertyTypes = new ArrayList<DefaultProperty>();
    // mlowery: PMD-112: commenting out props for business table
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.NAME));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.DESCRIPTION));
    setDefaultProperties( subject, propertyTypes );

    // Physical Column...
    subject = PhysicalColumn.class;
    propertyTypes = new ArrayList<DefaultProperty>();
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.FORMULA ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.NAME ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.DESCRIPTION ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.FIELD_TYPE ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.DATA_TYPE ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.EXACT ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.HIDDEN ) );
    // gmoran: These are defaulted but not required
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.FONT));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.MASK));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.COLOR_FG));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.COLOR_BG));
    setDefaultProperties( subject, propertyTypes );

    // Business Column...
    subject = BusinessColumn.class;
    propertyTypes = new ArrayList<DefaultProperty>();
    // mlowery: PMD-112: commenting out props for business column
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.FORMULA));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.NAME));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.DESCRIPTION));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.FIELD_TYPE));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.DATA_TYPE));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.AGGREGATION));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.EXACT));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.HIDDEN));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.FONT));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.MASK));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.COLOR_FG));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.COLOR_BG));
    // propertyTypes.add(new DefaultProperty(subject, DefaultPropertyID.SECURITY));
    setDefaultProperties( subject, propertyTypes );

    // The business Categories
    subject = BusinessCategory.class;
    propertyTypes = new ArrayList<DefaultProperty>();
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.NAME ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.DESCRIPTION ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.SECURITY ) );
    setDefaultProperties( subject, propertyTypes );

    // The business models
    subject = BusinessModel.class;
    propertyTypes = new ArrayList<DefaultProperty>();
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.NAME ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.DESCRIPTION ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.SECURITY ) );
    propertyTypes.add( new DefaultProperty( subject, DefaultPropertyID.ROW_LEVEL_SECURITY ) );
    setDefaultProperties( subject, propertyTypes );

    setChanged( false );
  }
}
