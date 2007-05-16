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
  
  // Hold on to strategic nodes in the subtree, to make accessing these nodes 
  // a bit more efficient
  
  private BusinessTablesTreeNode businessTablesNode;
  private RelationshipsTreeNode relationshipsNode;
  private BusinessViewTreeNode businessViewNode;
  
  public BusinessModelTreeNode(ITreeNode parent, BusinessModel model, String locale) {
    super(parent);
    this.model = model;
    this.locale = locale;
  }

  protected void createChildren(List children) {
    String databaseName = "No database defined";
    if (model.hasConnection()){
      databaseName = model.getConnection().getName();
    }
    LabelTreeNode databaseNode = new LabelTreeNode(this, "Connection: " + databaseName);
    addChild(databaseNode);

    businessTablesNode = new BusinessTablesTreeNode(this, model, locale);
    addChild(businessTablesNode);
    
    relationshipsNode = new RelationshipsTreeNode(this, model);
    addChild(relationshipsNode);
    
    businessViewNode = new BusinessViewTreeNode(this, model.getRootCategory(), locale);
    addChild(businessViewNode);
  }
  
  public void sync(){
    if (fChildren == null){
      getChildren();
    }
    
    businessTablesNode.sync();
    relationshipsNode.sync();
    businessViewNode.sync();
    
    fireTreeNodeUpdated();
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

  public Object getDomainObject(){
    return model;
  }

  public BusinessModel getBusinessModel() {
    return model;
  }
  
  public BusinessTablesTreeNode getBusinessTablesRoot(){
    if (businessTablesNode == null){
      businessTablesNode = new BusinessTablesTreeNode(this, model, locale);
    }
    return businessTablesNode;
  }

  public RelationshipsTreeNode getRelationshipsRoot(){
    if (relationshipsNode == null){
      relationshipsNode = new RelationshipsTreeNode(this, model);      
    }
    return relationshipsNode;
  }
  
  public BusinessViewTreeNode getBusinessViewRoot(){
    if (businessViewNode == null){
      businessViewNode = new BusinessViewTreeNode(this, model.getRootCategory(), locale);
    }
    return businessViewNode;
  }
}
