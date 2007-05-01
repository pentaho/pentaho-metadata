/*
 * Copyright 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 *
 * @created Apr 30, 2007 
 * @author wseyler
 */


package org.pentaho.pms.ui.tree;

import java.util.List;

import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;

import be.ibridge.kettle.core.database.DatabaseMeta;

/**
 * @author wseyler
 *
 */
public class DatabaseMetaTreeNode extends ConceptTreeNode {
  private SchemaMeta schemaMeta;
  private DatabaseMeta databaseMeta;
  private String locale;
  
  /**
   * @param parent
   */
  public DatabaseMetaTreeNode(ITreeNode parent) {
    super(parent);
  }

  /**
   * @param connectionsLabel
   * @param database
   * @param activeLocale
   */
  public DatabaseMetaTreeNode(ITreeNode parent, SchemaMeta schemaMeta, DatabaseMeta databaseMeta, String activeLocale) {
    this(parent);
    this.schemaMeta = schemaMeta;
    this.databaseMeta = databaseMeta;
    this.locale = activeLocale;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    PhysicalTable[] physicalTables = schemaMeta.getTablesOnDatabase(databaseMeta);
    for(int i=0; i<physicalTables.length; i++) {
      PhysicalTable physicalTable = physicalTables[i];
      addChild(new PhysicalTableTreeNode(this, physicalTable, locale));
    }
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return databaseMeta.getName();
  }

}
