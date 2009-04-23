package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.Collection;

public class LogicalSchema extends Entity {

  private Collection<LogicalTable> tables = new ArrayList<LogicalTable>();

  private Collection<LogicalRelationship> relationships = new ArrayList<LogicalRelationship>();

  public Collection<LogicalTable> getTables() {
    return tables;
  }

  public void setTables(Collection<LogicalTable> tables) {
    this.tables = tables;
  }

  public void addTable(LogicalTable table) {
    tables.add(table);
  }

  public Collection<LogicalRelationship> getRelationships() {
    return relationships;
  }

  public void setRelationships(Collection<LogicalRelationship> relationships) {
    this.relationships = relationships;
  }

  public void addLogicalRelationship(LogicalRelationship relationship) {
    relationships.add(relationship);
  }

}
