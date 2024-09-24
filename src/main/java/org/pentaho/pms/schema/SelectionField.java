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
package org.pentaho.pms.schema;

/**
 * Created on 6-feb-04
 * 
 * @deprecated as of metadata 3.0.
 */
public class SelectionField {
  private String name;
  private PhysicalColumn field;
  private SelectionGroup group;

  public SelectionField( String name, PhysicalColumn field, SelectionGroup group ) {
    this.name = name;
    this.field = field;
    this.group = group;
  }

  public SelectionField( String name, PhysicalColumn field ) {
    this( name, field, null );
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setField( PhysicalColumn field ) {
    this.field = field;
  }

  public PhysicalColumn getField() {
    return field;
  }

  public void setGroup( SelectionGroup group ) {
    this.group = group;
  }

  public SelectionGroup getGroup() {
    return group;
  }
}
