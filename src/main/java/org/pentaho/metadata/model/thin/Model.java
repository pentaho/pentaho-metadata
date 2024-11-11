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

package org.pentaho.metadata.model.thin;

import java.util.HashMap;

/**
 * Concrete, lightweight, serializable implementation of an {@see IModel} object
 * 
 * @author jamesdixon
 * 
 */
public class Model extends ModelInfo {

  private static final long serialVersionUID = 6865069259179116876L;

  public static final String CAPABILITY_HAS_ACROSS_AXIS = "across-axis"; // default is true
  public static final String CAPABILITY_IS_ACROSS_CUSTOM = "across-axis-customizable"; // default is true
  public static final String CAPABILITY_HAS_DOWN_AXIS = "down-axis"; // default is false
  public static final String CAPABILITY_IS_DOWN_CUSTOM = "down-axis-customizable"; // default is false
  public static final String CAPABILITY_HAS_FILTERS = "filter-axis"; // default is true
  public static final String CAPABILITY_IS_FILTER_CUSTOM = "filter-axis-customizable"; // default is true
  public static final String CAPABILITY_CAN_SORT = "sortable"; // default is true

  private Element[] elements = new Element[0];

  private HashMap capabilities = new HashMap();

  /**
   * Returns an array of categories for the model
   * 
   * @return
   */
  public Element[] getElements() {
    return elements;
  }

  /**
   * Sets the categories for the model
   * 
   * @param categories
   */
  public void setElements( Element[] elements ) {
    this.elements = elements;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( elements == null ) ? 0 : elements.hashCode() );
    result = prime * result + ( ( getId() == null ) ? 0 : getId().hashCode() );
    result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
    return result;
  }

  /**
   * Determines whether two models are equal to each other
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    // if (!getClass().getName().equals(obj.getClass().getName())) {
    if ( !( obj instanceof Model ) ) {
      return false;
    }
    Model other = (Model) obj;
    if ( elements == null ) {
      if ( other.elements != null ) {
        return false;
      }
    } else if ( elements.length != other.elements.length ) {
      return false;
    } else {
      int idx = 0;
      for ( Element element : elements ) {
        if ( !element.getId().equals( other.elements[idx].getId() ) ) {
          return false;
        }
        idx++;
      }
    }
    String id = getId();
    if ( !id.equals( other.getId() ) ) {
      return false;
    }
    if ( name == null ) {
      if ( other.name != null ) {
        return false;
      }
    } else if ( !name.equals( other.name ) ) {
      return false;
    }
    return true;
  }

}
