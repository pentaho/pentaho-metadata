package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.jface.tree.TreeNode;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class ColumnTreeNode extends ConceptTreeNode {

  protected BusinessColumn column = null;
  private String locale = null;
  
  public ColumnTreeNode(ITreeNode parent, final BusinessColumn column, final String locale) {
    super(parent);
    this.column = column;
    this.locale = locale; 
    // TODO Auto-generated constructor stub
  }

  protected void createChildren(List children) {
    // Category columns have no children under the default implementation
  }

  public Image getImage() {
    return super.getImage();
  }

  public String getName() {
    // TODO Auto-generated method stub
    return column.getDisplayName(locale);
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.jface.tree.TreeNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }
  
  public ConceptUtilityInterface getDomainObject(){
    return column;
  }
  
  public String getConceptName(){

    ConceptInterface concept = column.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }

}
