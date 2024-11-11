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

/**
 * This interface defines the API for all physical models.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public interface IPhysicalModel extends IConcept {
  public List<? extends IPhysicalTable> getPhysicalTables();

  public String getQueryExecName();

  public String getDefaultQueryClassname();

}
