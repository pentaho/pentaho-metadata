package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.RelationshipMeta;


public class RelationshipTreeNode extends ConceptTreeNode {

  protected RelationshipMeta relationship = null;
  
  public RelationshipTreeNode(ITreeNode parent, final RelationshipMeta relationship) {
    super(parent);
    this.relationship = relationship;
  }

  protected void createChildren(List children) {
    // As of this impementation, relationships have no children
  }
  
  public void sync(){
    
  }
  
  public Image getImage() {
    // TODO Auto-generated method stub
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return relationship.toString();
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.TreeNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }
  
  public Object getDomainObject(){
    return relationship;
  }
}
