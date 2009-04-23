package org.pentaho.metadata.model;

public class LogicalRelationship extends Entity {
  
  private LogicalTable fromTable, toTable;
  private LogicalColumn fromColumn, toColumn;
  /* where this relationship falls in relation to others */
  private long ordinal;
  
  public LogicalTable getFromTable() {
    return fromTable;
  }

  public void setFromTable(LogicalTable fromTable) {
    this.fromTable = fromTable;
  }

  public LogicalTable getToTable() {
    return toTable;
  }

  public void setToTable(LogicalTable toTable) {
    this.toTable = toTable;
  }

  public LogicalColumn getFromColumn() {
    return fromColumn;
  }

  public void setFromColumn(LogicalColumn fromColumn) {
    this.fromColumn = fromColumn;
  }

  public LogicalColumn getToColumn() {
    return toColumn;
  }

  public void setToColumn(LogicalColumn toColumn) {
    this.toColumn = toColumn;
  }

  public long getOrdinal() {
    return ordinal;
  }

  public void setOrdinal(long ordinal) {
    this.ordinal = ordinal;
  }

  //TODO give this enum the correct set of supported join types
  public enum JoinType { OUTER, INNER, LEFT, RIGHT }

}
