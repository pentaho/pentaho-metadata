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
