/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

/**
 * The inline etl physical table simply holds pointers to the physical columns.
 *  
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class InlineEtlPhysicalTable extends Concept implements IPhysicalTable {

  private static final long serialVersionUID = 587552752354101051L;
  
  private InlineEtlPhysicalModel parent;
  
  private List<IPhysicalColumn> physicalColumns = new ArrayList<IPhysicalColumn>();

  public InlineEtlPhysicalTable() {
    super();
  
  }

  public InlineEtlPhysicalTable(InlineEtlPhysicalModel parent) {
    this.parent = parent;
  }
  
  public List<IPhysicalColumn> getPhysicalColumns() {
    return physicalColumns;
  }

  public IPhysicalModel getPhysicalModel() {
    return parent;
  }

}
