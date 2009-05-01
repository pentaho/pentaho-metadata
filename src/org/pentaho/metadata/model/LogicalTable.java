package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.IConcept;
import org.pentaho.metadata.model.LogicalModel;

public class LogicalTable extends Concept {

  public LogicalTable() {
    super();
    // TODO Auto-generated constructor stub
  }

  private LogicalModel logicalModel;
  private IPhysicalTable physicalTable;
  
  // needs the security attribute.
  
  private List<LogicalColumn> logicalColumns = new ArrayList<LogicalColumn>();

  public IPhysicalTable getPhysicalTable() {
    return physicalTable;
  }
  
  public void setPhysicalTable(IPhysicalTable physicalTable) {
    this.physicalTable = physicalTable;
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
  
  @Override
  public IConcept getInheritedConcept() {
    return physicalTable;
  }
  
  @Override
  public IConcept getSecurityParentConcept() {
    return logicalModel;
  }

}
