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

package org.pentaho.pms.schema.olap;

public class OlapDimensionUsage implements Cloneable {
  private String name;
  private OlapDimension olapDimension;

  public OlapDimensionUsage() {
  }

  /**
   * @param name
   * @param olapDimension
   */
  public OlapDimensionUsage( String name, OlapDimension olapDimension ) {
    super();
    this.name = name;
    this.olapDimension = olapDimension;
  }

  public Object clone() {
    try {
      OlapDimensionUsage usage = (OlapDimensionUsage) super.clone(); // shallow copy of the dimension is fine.
      return usage;
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public boolean equals( Object obj ) {
    return name.equals( ( (OlapDimensionUsage) obj ).name );
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
  public void setName( String name ) {
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
  public void setOlapDimension( OlapDimension olapDimension ) {
    this.olapDimension = olapDimension;
  }

}
