package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class CategoryTreeNode extends ConceptTreeNode {

  protected BusinessCategory category = null;
  protected String locale = null;
  
  public CategoryTreeNode(ITreeNode parent, final BusinessCategory category, final String locale) {
    super(parent);
    this.category = category;
    this.locale = locale; 
  }

  protected void createChildren(List children) {

    for (int c = 0; c < category.nrBusinessColumns(); c++) {
      addDomainChild(category.getBusinessColumn(c));
    }

  }
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessColumn){
      addChild(new ColumnTreeNode(this,(BusinessColumn)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    if (domainObject instanceof BusinessColumn){
        for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
          ColumnTreeNode element = (ColumnTreeNode) iter.next();
          if (element.column.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public Image getImage() {
    // TODO Auto-generated method stub
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return category.getDisplayName(locale);
  }
  
  public ConceptUtilityInterface getDomainObject(){
    return category;
  }
  
  public String getConceptName(){

    ConceptInterface concept = category.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }
}
