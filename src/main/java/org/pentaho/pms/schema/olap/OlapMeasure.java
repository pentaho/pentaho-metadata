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

import org.pentaho.pms.schema.BusinessColumn;

@SuppressWarnings( "deprecation" )
public class OlapMeasure implements Cloneable {
  private String name;
  private BusinessColumn businessColumn;

  public OlapMeasure() {
  }

  /**
   * @param name
   * @param businessColumn
   */
  public OlapMeasure( String name, BusinessColumn businessColumn ) {
    this();
    this.name = name;
    this.businessColumn = businessColumn;
  }

  public Object clone() {
    return new OlapMeasure( name, businessColumn ); // shallow copy of business column is desired
  }

  /**
   * @return the businessColumn
   */
  public BusinessColumn getBusinessColumn() {
    return businessColumn;
  }

  /**
   * @param businessColumn
   *          the businessColumn to set
   */
  public void setBusinessColumn( BusinessColumn businessColumn ) {
    this.businessColumn = businessColumn;
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

}
