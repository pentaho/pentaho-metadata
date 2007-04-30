package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class TableTreeNode extends ConceptTreeNode {

  protected BusinessTable table = null;
  protected String locale = null;
  
  public TableTreeNode(ITreeNode parent, final BusinessTable table, final String locale) {
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

  public String getConceptName(){

    ConceptInterface tableConcept = table.getConcept();
    if (tableConcept != null && tableConcept.findFirstParentConcept() != null) {
      return tableConcept.findFirstParentConcept().getName();
    }
    return null;
  }
  
  public Image getImage() {
    // TODO Auto-generated method stub
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return table.getDisplayName(locale);
  }

  public ConceptUtilityInterface getDomainObject(){
    return table;
  }
}
