package org.pentaho.pms.ui.tree;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
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
      addChild(new BusinessTableTreeNode(this,(BusinessTable)domainObject, locale));
    }
  }
  
  public void removeDomainChild(Object domainObject){
    if (domainObject instanceof BusinessTable){
        for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
          BusinessTableTreeNode element = (BusinessTableTreeNode) iter.next();
          if (element.table.equals(domainObject))
            removeChild(element);
        }
    }
  }

  public Image getImage() {
    return super.getImage();
  }

  public String getName() {
    return Messages.getString("MetaEditor.USER_BUSINESS_TABLES");
  }

  public ConceptUtilityInterface getDomainObject(){
    return model;
  }

}
