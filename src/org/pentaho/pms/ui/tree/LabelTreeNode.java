package org.pentaho.pms.ui.tree;

import java.util.List;

import org.pentaho.pms.jface.tree.ITreeNode;

public class LabelTreeNode extends ConceptTreeNode {
  
  protected String labelName = null;

  public LabelTreeNode(ITreeNode parent, String name) {
    super(parent);
    
    labelName = name; 
  }

  protected void createChildren(List children) {
    // Labels have no intuitive children, they are added manually as the tree requirements deem necessary
  }

  public void sync(){
    // intentionally do nothing here
  }
  
  public String getName() {
    return labelName;
  }
  
  public Object getDomainObject(){
    return getName();
  }

}
