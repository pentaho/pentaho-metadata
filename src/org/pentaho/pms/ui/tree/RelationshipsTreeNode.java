package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class RelationshipsTreeNode extends ConceptTreeNode {

  protected BusinessModel model = null;
  
  public RelationshipsTreeNode(ITreeNode parent, BusinessModel model) {
    super(parent);
    this.model = model;
  }

  protected void createChildren(List children) {
    
    for (int c = 0; c < model.nrRelationships(); c++) {
        addDomainChild(model.getRelationship(c));
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof RelationshipMeta){
      addChild(new RelationshipTreeNode(this,(RelationshipMeta)domainObject));
    }

  }
  
  public void removeDomainChild(Object domainObject){
    if (domainObject instanceof RelationshipMeta){
        for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
          RelationshipTreeNode element = (RelationshipTreeNode) iter.next();
          if (element.relationship.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public Image getImage() {
    // TODO Auto-generated method stub
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return "Relationships";
  }

  public ConceptUtilityInterface getDomainObject(){
    return model;
  }

}
