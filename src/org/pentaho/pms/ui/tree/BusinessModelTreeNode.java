package org.pentaho.pms.ui.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.pentaho.pms.jface.tree.ITreeNode;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;


public class BusinessModelTreeNode extends ConceptTreeNode {

  protected BusinessModel model = null;
  protected String locale = null;
  
  public BusinessModelTreeNode(ITreeNode parent, BusinessModel model, String locale) {
    super(parent);
    this.model = model;
    this.locale = locale; 
  }

  protected void createChildren(List children) {
    BusinessTablesTreeNode businessTablesNode = new BusinessTablesTreeNode(fParent, model, locale);
    addChild(businessTablesNode);
    
    RelationshipsTreeNode relationshipsNode = new RelationshipsTreeNode(fParent, model);
    addChild(relationshipsNode);
    
    BusinessViewTreeNode businessViewNode = new BusinessViewTreeNode(fParent, model.getRootCategory(), locale);
    addChild(businessViewNode);
  }

  public Image getImage() {
    return super.getImage();
  }

  public String getName() {
    return model.getDisplayName(locale);
  }

  public String getConceptName(){

    ConceptInterface concept = model.getConcept();
    if (concept != null && concept.findFirstParentConcept() != null) {
      return concept.findFirstParentConcept().getName();
    }
    return null;
  }

  public ConceptUtilityInterface getDomainObject(){
    return model;
  }
}
