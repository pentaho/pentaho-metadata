package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.util.GUIResource;

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
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof RelationshipMeta){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          RelationshipTreeNode element = (RelationshipTreeNode) iter.next();
          if (element.relationship.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync(){
    if (fChildren == null){
      getChildren();
    }
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    while ( childIter.hasNext() )
      children.add(childIter.next());
    
    RelationshipTreeNode element = null;

    // Check the business model for additions, add children if they exist
    for (int c = 0; c < model.nrRelationships(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        element = (RelationshipTreeNode) iter.next();
        if (element.getDomainObject().equals(model.getRelationship(c))){
          found = true;
          break;
        }
      }

      // if not found, then add it to the node...
      if (!found){
        addDomainChild(model.getRelationship(c));
      }
    }
    
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode)children.get(c);

      if (!model.getRelationships().contains(node.getDomainObject())){
        removeChild(node);
      }else{
        node.sync();
      }
    } 
    // update this node
    fireTreeNodeUpdated();

  }

 
  public Image getImage(){
    return GUIResource.getInstance().getImageRelationshipsParent();
  }   

  public String getName() {
    return Messages.getString("MetaEditor.USER_RELATIONSHIPS"); //$NON-NLS-1$
  }

  public Object getDomainObject(){
    return model;
  }

}
