package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;

public class CategoryTreeNode extends ConceptTreeNode {

  protected BusinessCategory category = null;
  protected String locale = null;
  
  public CategoryTreeNode(ITreeNode parent, final BusinessCategory category, final String locale) {
    super(parent);
    
    this.category = category;

    // TODO: GEM hack, root category ids are null, and 
    // ConceptUtilityBase overrides the equals methods to compare ids,
    // therefore id comparisons on root categories produce 
    // unexpected results... This alleviates this problem in the tree,
    // but we should investigate why root categories have no ids, and
    // if that is where the problem should be resolved. 

    if (category.getId()==null){
      try {
        category.setId(Integer.toString(this.hashCode()));
        // Clear the changed flag, as we don't care if this id gets saved...
        //it's only important at runtime. 
        category.clearChanged();
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
    this.locale = locale; 
  }

  protected void createChildren(List children) {

    for (int c = 0; c < category.nrBusinessColumns(); c++) {
      addDomainChild(category.getBusinessColumn(c));
    }

  }
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessColumn){
      addChild(new BusinessColumnTreeNode(this, (BusinessColumn)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    List children = new ArrayList();
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof BusinessColumn){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          BusinessColumnTreeNode element = (BusinessColumnTreeNode) iter.next();
          if (element.column.equals(domainObject))
            removeChild(element);
        }
    }
  }

//  public void sync(){
//    if (fChildren == null){
//      getChildren();
//    }
//    
//    // make copy of list so removals doesn't cause a problem
//    Iterator childIter = fChildren.iterator();
//    List children = new ArrayList();
//    while ( childIter.hasNext() )
//      children.add(childIter.next());
//    
//    BusinessColumnTreeNode element = null;
//
//    // Check the business model for additions, add children if they exist
//    for (int c = 0; c < category.nrBusinessColumns(); c++) {
//      boolean found = false;
//      for (Iterator iter = children.iterator(); iter.hasNext();) {
//        element = (BusinessColumnTreeNode) iter.next();
//        if (element.getDomainObject().equals(category.getBusinessColumn(c))){
//          found = true;
//          break;
//        }
//      }
//
//      // if not found, then add it to the node...
//      if (!found){
//        addDomainChild(category.getBusinessColumn(c));
//      }
//    }
//
//    // Check the children against the business model to see if any should be removed...
//    // Recursively sync the children that remain
//    for (int c = 0; c < children.size(); c++) {
//      ConceptTreeNode node = (ConceptTreeNode)children.get(c);
//      if (!category.getBusinessColumns().contains(node.getDomainObject())){
//        removeChild(node);
//      }else{
//        node.sync();
//     
//      }
//    }  
//
//    // make copy of list so removals doesn't cause a problem
//    Iterator modelIter = category.getBusinessColumns().iterator();
//    List modelChildren = new ArrayList();
//    while ( modelIter.hasNext() )
//      modelChildren.add(modelIter.next());
//
//    int childIndex = -1, modelIndex = -1;
//
//    for (int i = 0; i < fChildren.size(); i++) {
//      element = (BusinessColumnTreeNode)fChildren.get(i);
//      modelIndex = category.getBusinessColumns().indexOf(element.getDomainObject());
//      
//      // We want to make sure the index is right....
//      // We may have sorted the nodes which would put the tree and the model indexes out of sync
//        if (i != modelIndex){
//          childIndex = i;
//          category.getBusinessColumns().remove(modelIndex);
//
//          try {
//            if (childIndex < category.getBusinessColumns().size())
//              category.getBusinessColumns().add(childIndex, element.getDomainObject());
//            else
//              category.getBusinessColumns().add(element.getDomainObject());
//          } catch (ObjectAlreadyExistsException e) {
//            // Should not happen here...
//          }
//        }
//    }
//    
//    // update this node
//    fireTreeNodeUpdated();
//
//  }


  public void sync(){
    sync(category.getBusinessColumns());
  }
  
  public Image getImage() {
    // TODO Auto-generated method stub
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return category.getDisplayName(locale);
  }

  public int getDragAndDropType() {
    // TODO: Business categories have no draganddroptype; need to add
    // this to the Kettle class DragAndDropContainer.
    return 0;
  }
 
  public Object getDomainObject(){
    return category;
  }
  
  public String getConceptName(){

    ConceptInterface concept = category.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }
  
  public BusinessCategory getCategory() {
    return category;
  }
}
