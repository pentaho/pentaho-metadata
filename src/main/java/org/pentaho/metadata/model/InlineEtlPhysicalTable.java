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
    return (IPhysicalModel) getParent();
  }

}
