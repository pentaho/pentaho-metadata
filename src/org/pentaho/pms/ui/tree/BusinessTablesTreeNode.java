package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class BusinessTablesTreeNode extends ConceptTreeNode {

  

  protected BusinessModel model = null;
  protected String locale = null;
  
  public BusinessTablesTreeNode(ITreeNode parent, BusinessModel model, String locale) {
    super(parent);
    this.model = model;
    this.locale = locale; 
  }

  protected void createChildren(List children) {
    
    for (int c = 0; c < model.nrBusinessTables(); c++) {
        addDomainChild(model.getBusinessTable(c));
    }
  }
  
  public void addDomainChild(Object domainObject){
    if (domainObject instanceof BusinessTable){
      addChild(new TableTreeNode(this,(BusinessTable)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    if (domainObject instanceof BusinessTable){
        for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
          TableTreeNode element = (TableTreeNode) iter.next();
          if (element.table.equals(domainObject))
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
    return "Business Tables";
  }

  public ConceptUtilityInterface getDomainObject(){
    return model;
  }

}
