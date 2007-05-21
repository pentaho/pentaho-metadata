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

  public void sync(){
    if (fChildren == null){
      getChildren();
    }
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    List children = new ArrayList();
    while ( childIter.hasNext() )
      children.add(childIter.next());
    
    for (int c = 0; c < category.nrBusinessColumns(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        BusinessColumnTreeNode element = (BusinessColumnTreeNode) iter.next();
        if (element.getDomainObject().equals(category.getBusinessColumn(c)))
          found = true;
      }
      if (!found){
        addDomainChild(category.getBusinessColumn(c));
      }
    }
    
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode)children.get(c);

      if (!category.getBusinessColumns().contains(node.getDomainObject())){
        removeChild(node);
      }else{
        node.sync();
      }
    }  
    // update this node
    fireTreeNodeUpdated();

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
