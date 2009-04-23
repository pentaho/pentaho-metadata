package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

public class View extends Entity {
  
  private List<LogicalColumn> columns = new ArrayList<LogicalColumn>();

  public List<LogicalColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<LogicalColumn> columns) {
    this.columns = columns;
  }
  

  public void addColumn(LogicalColumn column) {
    columns.add(column);
  }
}
