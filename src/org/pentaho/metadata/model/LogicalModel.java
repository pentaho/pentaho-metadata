package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

public class LogicalModel extends Concept {
  private List<LogicalTable> logicalTables = new ArrayList<LogicalTable>();
  
  // TODO: add security
  
  public List<LogicalTable> getLogicalTables() {
    return logicalTables;
  }
  
  // should the categories go here? 
}
