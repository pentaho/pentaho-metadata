package org.pentaho.pms.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessCategory;
import org.pentaho.pms.util.GUIResource;

public class BusinessViewTreeNode extends CategoryTreeNode {

  
  public BusinessViewTreeNode(ITreeNode parent, BusinessCategory rootCategory, String locale) {
    super(parent, rootCategory, locale);
  }

  protected void createChildren(List children) {

    for (int i = 0; i < category.nrBusinessCategories(); i++) {
      addDomainChild(category.getBusinessCategory(i));
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
    sync(category.getBusinessCategories());
  }
  
  public Image getImage(){
    return GUIResource.getInstance().getImageBusinessView();
  }   

  public String getName() {
    // TODO Auto-generated method stub
    return Messages.getString("MetaEditor.USER_CATEGORIES"); //$NON-NLS-1$
  }
}
