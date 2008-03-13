package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.dnd.DragAndDropContainer;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.util.GUIResource;


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
    sync(table.getBusinessColumns());
  }
  
  public String getConceptName(){

    ConceptInterface tableConcept = table.getConcept();
    if (tableConcept != null && tableConcept.findFirstParentConcept() != null) {
      return tableConcept.findFirstParentConcept().getName();
    }
    return null;
  }
  
  public Image getImage() {
    return GUIResource.getInstance().getImageBusinessTable();
  }

  public String getName() {
     return table.getDisplayName(locale);
  }

  public int getDragAndDropType() {
    return DragAndDropContainer.TYPE_BUSINESS_TABLE;
  }

  public Object getDomainObject(){
    return table;
  }
}
