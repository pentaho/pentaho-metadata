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


/**
 * Concrete, lightweight, serializable implementation of an {@see IModel} object
 * @author jamesdixon
 *
 */
public class Model extends ModelInfo {

  private static final long serialVersionUID = 6865069259179116876L;

  private Category[] categories = new Category[0];

/**
   * Returns an array of categories for the model
   * @return
   */
  public Category[] getCategories() {
    return categories;
  }

  /**
   * Sets the categories for the model
   * @param categories
   */
  public void setCategories(Category[] categories) {
    this.categories = categories;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((categories == null) ? 0 : categories.hashCode());
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
    if (categories == null) {
      if (other.categories != null) {
        return false;
      }
    }
    else if (categories.length != other.categories.length) {
      return false;
    }
    else {
      int idx=0;
      for( Category category : categories ) {
        if(!category.equals(other.categories[idx])) {
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
