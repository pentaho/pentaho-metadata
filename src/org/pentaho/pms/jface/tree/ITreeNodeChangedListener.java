package org.pentaho.pms.jface.tree;

public interface ITreeNodeChangedListener {

  public void onUpdate(ITreeNode node);
  
  public void onAddChild(ITreeNode parent, ITreeNode child);
 
  public void onDelete(ITreeNode node);
}
