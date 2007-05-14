package org.pentaho.pms.jface.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;

public interface ITreeNode {
    public String getName();
    public Image getImage();
    public List getChildren();
    public boolean hasChildren();
    public ITreeNode getParent();
    public void addChild(ITreeNode child);
    public void prune();
    public void addTreeNodeChangeListener(ITreeNodeChangedListener listener);    
    public void removeTreeNodeChangeListener(ITreeNodeChangedListener listener);
    
}
