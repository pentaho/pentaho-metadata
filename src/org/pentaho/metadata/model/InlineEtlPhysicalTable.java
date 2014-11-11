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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * The inline etl physical table simply holds pointers to the physical columns.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class InlineEtlPhysicalTable extends Concept implements IPhysicalTable {

  private static final long serialVersionUID = 587552752354101051L;

  private List<IPhysicalColumn> physicalColumns = new ArrayList<IPhysicalColumn>();

  public InlineEtlPhysicalTable() {
    super();
    // physical table has the following default properties:
    setName( new LocalizedString() );
    setDescription( new LocalizedString() );
  }

  @Override
  public List<IConcept> getChildren() {
    List<IConcept> children = new ArrayList<IConcept>();
    children.addAll( physicalColumns );
    return children;
  }

  public InlineEtlPhysicalTable( InlineEtlPhysicalModel parent ) {
    setParent( parent );
  }

  public List<IPhysicalColumn> getPhysicalColumns() {
    return physicalColumns;
  }

  public IPhysicalModel getPhysicalModel() {
    return ( IPhysicalModel )getParent();
  }

}
