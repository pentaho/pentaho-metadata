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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
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
    getChildren();
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.ui.tree.ConceptTreeNode#createChildren(java.util.List)
   */
  protected void createChildren(List children) {
    Iterator iter = schemaMeta.getBusinessModels().iterator();
    while(iter.hasNext()) {
      BusinessModel businessModel = (BusinessModel) iter.next();
      addDomainChild(businessModel);
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessModel){
      addChild(new BusinessModelTreeNode(this, (BusinessModel) domainObject, schemaMeta.getActiveLocale()));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List children = new ArrayList();
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof BusinessModel){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          BusinessModelTreeNode element = (BusinessModelTreeNode) iter.next();
          if (element.model.equals(domainObject))
            removeChild(element);
        }
    }
  }
  
  public void sync(){
    if (fChildren == null)
      return;
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    List children = new ArrayList();
    while ( childIter.hasNext() )
      children.add(childIter.next());
    
    for (int c = 0; c < schemaMeta.nrBusinessModels(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        BusinessModelTreeNode element = (BusinessModelTreeNode) iter.next();
        if (element.getDomainObject().equals(schemaMeta.getBusinessModels().get(c)))
          found = true;
      }
      if (!found){
        addDomainChild(schemaMeta.getBusinessModels().get(c));
      }
    }
    
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode)children.get(c);
      if (!schemaMeta.getBusinessModels().contains(node.getDomainObject())){
        removeChild(node);
      }else{
        node.sync();
      }
    }
    // update this node
    fireTreeNodeUpdated();
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
  
  public Object getDomainObject(){
    return schemaMeta;
  }
}
