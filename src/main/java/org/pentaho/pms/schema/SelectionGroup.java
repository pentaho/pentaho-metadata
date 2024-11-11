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

import java.util.ArrayList;

/**
 * @deprecated as of metadata 3.0.
 */
public class SelectionGroup {
  private String name;
  private SelectionGroup parent;
  private String description;
  private ArrayList selectionGroups;
  private ArrayList selectionFields;

  public SelectionGroup( String name, SelectionGroup parent ) {
    clear();

    this.name = name;
    this.parent = parent;
  }

  public SelectionGroup( String name ) {
    this( name, null );
  }

  public void clear() {
    name = ""; //$NON-NLS-1$
    parent = null;
    selectionGroups = new ArrayList();
    selectionFields = new ArrayList();
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String desc ) {
    this.description = desc;
  }

  public void setParent( SelectionGroup parent ) {
    this.parent = parent;
  }

  public SelectionGroup getParent() {
    return parent;
  }

  public ArrayList getSelectionFields() {
    return selectionFields;
  }

  public ArrayList getSelectionGroups() {
    return selectionGroups;
  }
}
