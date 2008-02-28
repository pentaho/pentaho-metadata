package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.util.GUIResource;

public class BusinessViewTreeNode extends ConceptTreeNode {

  BusinessCategory rootCategory;
  protected String locale = null;
  
  public BusinessViewTreeNode(ITreeNode parent, BusinessCategory rootCategory, String locale) {
    super(parent);
    this.rootCategory = rootCategory;

    // TODO: GEM hack, root rootCategory ids are null, and 
    // ConceptUtilityBase overrides the equals methods to compare ids,
    // therefore id comparisons on root categories produce 
    // unexpected results... This alleviates this problem in the tree,
    // but we should investigate why root categories have no ids, and
    // if that is where the problem should be resolved. 

    if (rootCategory.getId()==null){
      try {
        rootCategory.setId(Integer.toString(this.hashCode()));
        // Clear the changed flag, as we don't care if this id gets saved...
        //it's only important at runtime. 
        rootCategory.clearChanged();
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
    this.locale = locale; 
  }

  protected void createChildren(List children) {

    for (int i = 0; i < rootCategory.nrBusinessCategories(); i++) {
      addDomainChild(rootCategory.getBusinessCategory(i));
    }
  }

  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessCategory){
      addChild(new CategoryTreeNode(this,(BusinessCategory)domainObject, locale));
    }
  }
  public void removeDomainChild(Object domainObject){
    List children = new ArrayList();
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    while ( childIter.hasNext() )
      children.add(childIter.next());

    if (domainObject instanceof BusinessCategory){
        for (Iterator iter = children.iterator(); iter.hasNext();) {
          CategoryTreeNode element = (CategoryTreeNode) iter.next();
          if (element.category.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public void sync(){
    sync(rootCategory.getBusinessCategories());
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageBusinessView();
  }   

  public String getName() {
    // TODO Auto-generated method stub
    return Messages.getString("MetaEditor.USER_CATEGORIES"); //$NON-NLS-1$
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_BUSINESS_VIEW;
  }
 
  public Object getDomainObject(){
    return rootCategory;
  }
  
  public String getConceptName(){

    ConceptInterface concept = rootCategory.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }
  
  public BusinessCategory getRootCategory() {
    return rootCategory;
  }
}
