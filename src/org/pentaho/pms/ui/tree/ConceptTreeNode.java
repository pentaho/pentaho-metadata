package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;

public abstract class ConceptTreeNode extends TreeNode {

  public ConceptTreeNode(ITreeNode parent) {
    super(parent);
  }

  protected abstract void createChildren(List children);
  

  public String getConceptName(){
    return null;
  }

  public Image getConceptImage(){
    return null;
  }
}
