package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

public class Domain extends Concept {
  
  private List<IPhysicalModel> physicalModels = new ArrayList<IPhysicalModel>();
  private List<LogicalModel> logicalModels = new ArrayList<LogicalModel>();  
  
  public List<IPhysicalModel> getPhysicalModels() {
    return physicalModels;
  }
  
  public void setPhysicalModels(List<IPhysicalModel> physicalModels) {
    this.physicalModels = physicalModels;
  }
  
  public void addPhysicalModel(IPhysicalModel physicalModel) {
    physicalModels.add(physicalModel);
  }

  public List<LogicalModel> getLogicalModels() {
    return logicalModels;
  }
  
  public void setLogicalModels(List<LogicalModel> logicalModels) {
    this.logicalModels = logicalModels;
  }
  
  public void addLogicalModel(LogicalModel physicalModel) {
    logicalModels.add(physicalModel);
  }

  
}
