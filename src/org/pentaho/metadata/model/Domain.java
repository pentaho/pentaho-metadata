package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

public class Domain extends Concept {
  
  private List<View> views = new ArrayList<View>();
  private List<IPhysicalModel> physicalModels = new ArrayList<IPhysicalModel>();

  public List<View> getViews() {
    return views;
  }

  public void setViews(List<View> views) {
    this.views = views;
  }

  public void addView(View view) {
    views.add(view);
  }
  
  public List<IPhysicalModel> getPhysicalModels() {
    return physicalModels;
  }
  
  public void setPhysicalModels(List<IPhysicalModel> physicalModels) {
    this.physicalModels = physicalModels;
  }
  
  public void addPhysicalModel(IPhysicalModel physicalModel) {
    physicalModels.add(physicalModel);
  }

}
