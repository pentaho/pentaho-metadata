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

package org.pentaho.metadata.model.olap;

import java.io.Serializable;

public class OlapDimensionUsage implements Cloneable, Serializable {
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
    // shallow copy of the dimension is fine.
    OlapDimensionUsage usage = new OlapDimensionUsage();
    usage.name = this.name;
    usage.olapDimension = this.olapDimension;
    return usage;
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
