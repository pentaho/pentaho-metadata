package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.util.GUIResource;


public class RelationshipTreeNode extends ConceptTreeNode {

  public void addDomainChild(Object obj) {
    // Nothing to do here
  }

  protected RelationshipMeta relationship = null;
  
  public RelationshipTreeNode(ITreeNode parent, final RelationshipMeta relationship) {
    super(parent);
    this.relationship = relationship;
  }

  protected void createChildren(List children) {
    // As of this impementation, relationships have no children
  }
  
  public void sync(){
    // Intentionally do nothing here
  }
  
  public Image getImage() {
    return GUIResource.getInstance().getImageRelationship();
  }

  public String getName() {
    return relationship.toString();
  }
  
  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.TreeNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }

  public String getId() {
    // No ids on relationships?
    return relationship.toString();
  }

  public int getDragAndDropType() {
    return 0;
  }
  
  public Object getDomainObject(){
    return relationship;
  }
}
