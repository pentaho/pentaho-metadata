package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.Collection;

public class PhysicalSchema extends Entity {

  private PhysicalDataSource source;

  private Collection<IPhysicalTable> tables = new ArrayList<IPhysicalTable>();

  public PhysicalDataSource getSource() {
    return source;
  }

  public void setSource(PhysicalDataSource source) {
    this.source = source;
  }

  public Collection<IPhysicalTable> getTables() {
    return tables;
  }

  public void setTables(Collection<IPhysicalTable> tables) {
    this.tables = tables;
  }
  
  public void addTable(IPhysicalTable table) {
    tables.add(table);
  }

}
