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
 * @created May 2, 2007 
 * @author wseyler
 */


package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.GUIResource;

import be.ibridge.kettle.core.database.DatabaseMeta;

/**
 * @author wseyler
 *
 */
public class ConnectionsTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
  
  public ConnectionsTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
  }
  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getDatabases().iterator();
    while(iter.hasNext()) {
      DatabaseMeta databaseMeta = (DatabaseMeta) iter.next();
      addChild(new DatabaseMetaTreeNode(this, schemaMeta, databaseMeta));
    }
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return Messages.getString("MetaEditor.USER_CONNECTIONS");
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageConnection();
  }

}
