package org.pentaho.pms.jface.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public abstract class TreeNode implements ITreeNode
{
  protected ITreeNode fParent;
  protected List<ITreeNode> fChildren;
  private transient List<ITreeNodeChangedListener> fModelChangeListeners;
  
  public TreeNode(ITreeNode parent) {
    fParent = parent;
    
  }
  
  public Image getImage() {
    return null; /* TODO */
  }
  
  public boolean hasChildren() {    
    return ((getChildren() != null) && (getChildren().size()>0));
  }
 
  public ITreeNode getParent() {    
    return fParent;
  }
  
  public List<ITreeNode> getChildren() 
  {
    if( fChildren != null )
      return fChildren;
    
    fChildren = new ArrayList<ITreeNode>();
    createChildren(fChildren);
      
    return fChildren;
  }

  public void prune(){
    fChildren = null;
    this.fireTreeNodeUpdated();
  }
  
  public void addChild(ITreeNode node){
    if (fChildren == null)
      fChildren = new ArrayList<ITreeNode>();

    if (!fChildren.contains(node)){
      fChildren.add(node);
    }
    this.fireChildAdded(node);
  }

  public void addChild(int index, ITreeNode node){
    if (fChildren == null)
      fChildren = new ArrayList<ITreeNode>();

    if (!fChildren.contains(node)){
      fChildren.add(index, node);
    }
    this.fireChildAdded(node);
  }
 

  public void removeChild(ITreeNode node){
    if (fChildren.contains(node)){
      fChildren.remove(node);
    }
    ((TreeNode)node).fireTreeNodeDeleted();
    
  }
  
  /* subclasses should override this method and add the child nodes */
  protected abstract void createChildren(List children);
  
  public void addTreeNodeChangeListener(ITreeNodeChangedListener listener)
  {
    if( fModelChangeListeners == null )
      fModelChangeListeners = new ArrayList<ITreeNodeChangedListener>();
 
    /* if listener already exists, then do not add */   
    if( fModelChangeListeners.contains(listener) )
      return;
 
    fModelChangeListeners.add(listener);
  }
 
  public void removeTreeNodeChangeListener(ITreeNodeChangedListener listener)
  {
    if( fModelChangeListeners == null )
      return;
    
    fModelChangeListeners.remove(listener);
  }
 
  protected List<ITreeNodeChangedListener> getModelChangedListeners()
  {
    if( fModelChangeListeners == null )
      return Collections.<ITreeNodeChangedListener>emptyList();
    
    return fModelChangeListeners;
  }
  
  /** Fire methods need to be called in appropriate subclasses of treenode */
   
  protected void fireTreeNodeUpdated()
  {
    Iterator listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      ((ITreeNodeChangedListener)listenerIter.next()).onUpdate(this);
  }
 
  protected void fireChildAdded(ITreeNode child)
  {
    Iterator listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      ((ITreeNodeChangedListener)listenerIter.next()).onAddChild(this, child);
  }
 
  protected void fireTreeNodeDeleted()
  {
    List<ITreeNodeChangedListener> listeners = new ArrayList<ITreeNodeChangedListener>();
    
    // make copy of listener list so removals of listeners 
    // doesn't cause a problem
    Iterator<ITreeNodeChangedListener> listenerIter = getModelChangedListeners().iterator();
    while ( listenerIter.hasNext() )
      listeners.add(listenerIter.next());
    
    Iterator i = listeners.iterator();
    while ( i.hasNext() )
      ((ITreeNodeChangedListener)i.next()).onDelete(this);
    
    
  }

}
