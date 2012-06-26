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
 * Copyright (c) 2011 Pentaho Corporation.  All rights reserved.
 * 
 * Created Jan, 2011
 * @author jdixon
*/
package org.pentaho.metadata.model.thin;

import java.util.HashMap;


/**
 * Concrete, lightweight, serializable implementation of an {@see IModel} object
 * @author jamesdixon
 *
 */
public class Model extends ModelInfo {

  private static final long serialVersionUID = 6865069259179116876L;

  public static final String CAPABILITY_HAS_ACROSS_AXIS = "across-axis"; // default is true
  public static final String CAPABILITY_IS_ACROSS_CUSTOM = "across-axis-customizable"; // default is true
  public static final String CAPABILITY_HAS_DOWN_AXIS = "down-axis"; // default is false
  public static final String CAPABILITY_IS_DOWN_CUSTOM = "down-axis-customizable"; // default is false
  public static final String CAPABILITY_HAS_FILTERS = "filter-axis";  // default is true
  public static final String CAPABILITY_IS_FILTER_CUSTOM = "filter-axis-customizable"; // default is true
  public static final String CAPABILITY_CAN_SORT = "sortable"; // default is true
  
  private Element[] elements = new Element[0];

  private HashMap capabilities = new HashMap();
  
/**
   * Returns an array of categories for the model
   * @return
   */
  public Element[] getElements() {
    return elements;
  }

  /**
   * Sets the categories for the model
   * @param categories
   */
  public void setElements(Element[] elements) {
    this.elements = elements;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elements == null) ? 0 : elements.hashCode());
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /**
   * Determines whether two models are equal to each other
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Model other = (Model) obj;
    if (elements == null) {
      if (other.elements != null) {
        return false;
      }
    }
    else if (elements.length != other.elements.length) {
      return false;
    }
    else {
      int idx=0;
      for( Element element : elements ) {
        if(!element.equals(other.elements[idx])) {
          return false;
        }
        idx++;
      }
    }
    String id = getId();
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!id.equals(other.getId())) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

}
