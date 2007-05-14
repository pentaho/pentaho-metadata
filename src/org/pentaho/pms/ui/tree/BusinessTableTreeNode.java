package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class BusinessTableTreeNode extends ConceptTreeNode {

  protected BusinessTable table = null;
  protected String locale = null;
  
  public BusinessTableTreeNode(ITreeNode parent, final BusinessTable table, final String locale) {
    super(parent);
    this.table = table;
    this.locale = locale; 
  }

  protected void createChildren(List children) {

    for (int c = 0; c < table.nrBusinessColumns(); c++) {
      addDomainChild(table.getBusinessColumn(c));
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessColumn){
      addChild(new BusinessColumnTreeNode(this,(BusinessColumn)domainObject, locale));
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
    
    // no children, nothing to synchronize
    if (fChildren == null)
      return;
    
    // make copy of list so removals doesn't cause a problem
    Iterator childIter = fChildren.iterator();
    List children = new ArrayList();
    while ( childIter.hasNext() )
      children.add(childIter.next());
    
    // Check the business model for additions, add children if they exist
    for (int c = 0; c < table.nrBusinessColumns(); c++) {
      boolean found = false;
      for (Iterator iter = children.iterator(); iter.hasNext();) {
        BusinessColumnTreeNode element = (BusinessColumnTreeNode) iter.next();
        if (element.getDomainObject().equals(table.getBusinessColumn(c)))
          found = true;
      }
      if (!found){
        addDomainChild(table.getBusinessColumn(c));
      }
    }
    
    // Check the children against the business model to see if any should be removed...
    // Recursively sync the children that remain
    for (int c = 0; c < children.size(); c++) {
      ConceptTreeNode node = (ConceptTreeNode)children.get(c);

      if (!table.getBusinessColumns().contains(node.getDomainObject())){
        removeChild(node);
      }else{
        node.sync();
      }
    }
    // update this node
    fireTreeNodeUpdated();
  }

  public String getConceptName(){

    ConceptInterface tableConcept = table.getConcept();
    if (tableConcept != null && tableConcept.findFirstParentConcept() != null) {
      return tableConcept.findFirstParentConcept().getName();
    }
    return null;
  }
  
  public Image getImage() {
    return super.getImage();
  }

  public String getName() {
     return table.getDisplayName(locale);
  }

  public Object getDomainObject(){
    return table;
  }
}
