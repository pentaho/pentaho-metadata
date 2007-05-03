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
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.GUIResource;

/**
 * @author wseyler
 *
 */
public class BusinessModelsTreeNode extends ConceptTreeNode {
  protected SchemaMeta schemaMeta = null;
 
  /**
   * @param node
   * @param schemaMeta
   */
  public BusinessModelsTreeNode(ITreeNode parent, SchemaMeta schemaMeta) {
    super(parent);
    
    this.schemaMeta = schemaMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getBusinessModels().iterator();
    while(iter.hasNext()) {
      BusinessModel businessModel = (BusinessModel) iter.next();
      addChild(new BusinessModelTreeNode(this, businessModel, schemaMeta.getActiveLocale()));
    }
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.ITreeNode#getName()
   */
  public String getName() {
    return Messages.getString("MetaEditor.USER_BUSINESS_MODELS");
  }

  public Image getImage(){
    return GUIResource.getInstance().getImageBol();
  }   
}
