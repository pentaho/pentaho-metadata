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
package org.pentaho.metadata.model.olap;

public class OlapDimensionUsage implements Cloneable {
  private String name;
  private OlapDimension olapDimension;

  public OlapDimensionUsage() {
  }

  /**
   * @param name
   * @param olapDimension
   */
  public OlapDimensionUsage(String name, OlapDimension olapDimension) {
    super();
    this.name = name;
    this.olapDimension = olapDimension;
  }

  public Object clone() {
    try {
      // shallow copy of the dimension is fine.
      OlapDimensionUsage usage = (OlapDimensionUsage) super.clone();
      return usage;
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

  public boolean equals(Object obj) {
    return name.equals(((OlapDimensionUsage) obj).name);
  }

  public int hashCode() {
    return name.hashCode();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the olapDimension
   */
  public OlapDimension getOlapDimension() {
    return olapDimension;
  }

  /**
   * @param olapDimension
   *          the olapDimension to set
   */
  public void setOlapDimension(OlapDimension olapDimension) {
    this.olapDimension = olapDimension;
  }

}
