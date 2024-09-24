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

import java.util.List;

import org.pentaho.metadata.model.concept.IConcept;

public interface IPhysicalTable extends IConcept {

  public static final String TABLETYPE_PROPERTY = "tabletype"; //$NON-NLS-1$

  public IPhysicalModel getPhysicalModel();

  public List<IPhysicalColumn> getPhysicalColumns();
}
