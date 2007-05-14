package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;

public abstract class ConceptTreeNode extends TreeNode {

  public ConceptTreeNode(ITreeNode parent) {
    super(parent);
  }

  /**
   * 
   */
  protected abstract void createChildren(List children);
  
  /**
   * 
   *
   */
  public abstract void sync();

  /**
   * 
   * @return
   */
  public String getConceptName(){
    return null;
  }

  /**
   * 
   * @return
   */
  public Image getConceptImage(){
    return null;
  }
  
  /**
   * 
   * @return
   */
  public abstract Object getDomainObject();
  
  /**
   * 
   * @param businessObject
   * @return
   */
  public ConceptTreeNode findNode(Object businessObject){
    
    ConceptTreeNode node = null;

    if (getDomainObject().getClass().equals(businessObject.getClass())){
      if (getDomainObject().equals(businessObject))
        node = this;
    }
    
    Iterator iter = getChildren().iterator();
    while((iter.hasNext()) && (node == null)){
      ConceptTreeNode childNode = (ConceptTreeNode)iter.next();
      node = childNode.findNode(businessObject);
    }
    
    return node;
  }

  
}
