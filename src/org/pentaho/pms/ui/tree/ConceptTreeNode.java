package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueList;

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
    return null;
  }

  public String getId() {

    if (getDomainObject() instanceof ConceptUtilityBase) {
      return ((ConceptUtilityBase) getDomainObject()).getId();
    }
    return null;
  }

  public int getDragAndDropType() {
    return 0;
  }

  /**
   * 
   * @return
   */
  public String getConceptName() {
    return null;
  }

  /**
   * 
   * @return
   */
  public Image getConceptImage() {
    return null;
  }

  /**
   * 
   * @return the business object that this Concept TreeNode is holdinng
   */
  public abstract Object getDomainObject();

  public abstract void addDomainChild(Object obj);

  /**
   * 
   * @param businessObject
   *            an object from the meta-model - ie., BusinessTable,
   *            BusinessColumn, etc.
   * @return the node that this business object lives in
   */
  public ConceptTreeNode findNode(Object businessObject) {

    ConceptTreeNode node = null;

    // GEM - this if statement gets us around a bug where
    // some Kettle objects equals() methods are overridden,
    // and they are not able to compare two different classes

    if (getDomainObject().getClass().equals(businessObject.getClass())) {
      if (getDomainObject().equals(businessObject))
        node = this;
    }

    Iterator iter = getChildren().iterator();
    while ((iter.hasNext()) && (node == null)) {
      ConceptTreeNode childNode = (ConceptTreeNode) iter.next();
      node = childNode.findNode(businessObject);
    }

    return node;
  }

  /**
   * recursively sort the child nodes in ascending order, then synchronize the
   * changes
   * 
   */
  public void sortChildrenAscending() {

    if (!hasChildren())
      return;

    TreeMap<String, ConceptTreeNode> map = new TreeMap<String, ConceptTreeNode>();
    Collection children = getChildren();

    for (Iterator iter = children.iterator(); iter.hasNext();) {
      ConceptTreeNode element = (ConceptTreeNode) iter.next();
      map.put(element.getName(), element);
      element.sortChildrenAscending();
    }

    fChildren.clear();

    Iterator keyIter = map.keySet().iterator();
    while (keyIter.hasNext()) {
      String key = (String) keyIter.next();
      fChildren.add(map.get(key));
    }
    sync();

  }

  /**
   * Synchronize the node hierarchy with it's business model
   * 
   * @param modelList
   *            the list of business objects from the business model that we
   *            will sync with
   */
  public void sync(UniqueList modelList) {
    if (fChildren == null) {
      getChildren();
    }

    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    List children = new ArrayList();
    while (childIter.hasNext())
      children.add(childIter.next());

    ConceptTreeNode element = null;

    // Check the business model for additions, add children if they exist
    for (int c = 0; c < modelList.size(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        element = (ConceptTreeNode) iter.next();
        if (element.getDomainObject().equals(modelList.get(c))) {
          found = true;
          break;
        }
      }

      // if not found, then add it to the node...
      if (!found) {
        addDomainChild(modelList.get(c));
      }
    }

    // Check the children against the business model to see if any should be
    // removed...
    // Recursively sync the children that remain
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode) children.get(c);

      if (!modelList.contains(node.getDomainObject())) {
        removeChild(node);
      } else {
        node.sync();
      }
    }
    syncIndexing(modelList);
    // update this node
    fireTreeNodeUpdated();

  }

  protected void syncIndexing(UniqueList modelList) {

    // make copy of list so removals doesn't cause a problem
    Iterator modelIter = modelList.iterator();
    List modelChildren = new ArrayList();
    while (modelIter.hasNext())
      modelChildren.add(modelIter.next());

    int childIndex = -1, modelIndex = -1;

    for (int i = 0; i < fChildren.size(); i++) {
      ConceptTreeNode element = (ConceptTreeNode) fChildren.get(i);
      modelIndex = modelList.indexOf(element.getDomainObject());

      // We want to make sure the index is right....
      // We may have sorted the nodes which would put the tree and the
      // model indexes out of sync
      if (i != modelIndex) {
        childIndex = i;
        modelList.remove(modelIndex);
        try {
          if (childIndex < modelList.size())
            modelList.add(childIndex, element.getDomainObject());
          else
            modelList.add(element.getDomainObject());
        } catch (ObjectAlreadyExistsException e) {
          //should not happen!
        }
      }
    }

  }

}
