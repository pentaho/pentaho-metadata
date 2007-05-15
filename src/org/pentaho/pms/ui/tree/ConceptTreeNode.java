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

  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }


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

    // GEM - this if statement gets us around a bug where
    // some Kettle objects equals() methods are overridden,
    // and they are not able to compare two different classes
    
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
