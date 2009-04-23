package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

public class Domain extends Entity {
  
  private List<View> views = new ArrayList<View>();

  public List<View> getViews() {
    return views;
  }

  public void setViews(List<View> views) {
    this.views = views;
  }

  public void addView(View view) {
    views.add(view);
  }
}
