package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;

public class Category extends Concept {

  public Category() {
    super();
  }

  private static final long serialVersionUID = -2367402604729602739L;

  private LogicalModel parent;
  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();
  
  @Override
  public IConcept getSecurityParentConcept() {
    return parent;
  }
  
  public List<LogicalColumn> getLogicalColumns() {
    return logicalColumns;
  }

  public void setLogicalColumns(List<LogicalColumn> columns) {
    this.logicalColumns = columns;
  }
  
  public void addLogicalColumn(LogicalColumn column) {
    logicalColumns.add(column);
  }
  
  public LogicalColumn findLogicalColumn(String columnId) {
    for (LogicalColumn col : getLogicalColumns()) {
      if (columnId.equals(col.getId())) {
        return col;
      }
    }
    return null;
  }
}
