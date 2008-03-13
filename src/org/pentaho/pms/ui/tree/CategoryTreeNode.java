package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.util.GUIResource;

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
    List<ITreeNode> children = new ArrayList<ITreeNode>();
    
    // make copy of list so removals doesn't cause a problem
    Iterator<ITreeNode> childIter = fChildren.iterator();
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
    sync(category.getBusinessColumns());
  }
  
  public Image getImage() {
    return GUIResource.getInstance().getImageCatagory();
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
