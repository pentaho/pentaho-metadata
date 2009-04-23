package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.Collection;

import org.pentaho.pms.schema.PhysicalTable;

public class PhysicalSchema extends Entity {

  private PhysicalDataSource source;

  private Collection<PhysicalTable> tables = new ArrayList<PhysicalTable>();

  public PhysicalDataSource getSource() {
    return source;
  }

  public void setSource(PhysicalDataSource source) {
    this.source = source;
  }

  public Collection<PhysicalTable> getTables() {
    return tables;
  }

  public void setTables(Collection<PhysicalTable> tables) {
    this.tables = tables;
  }
  
  public void addTable(PhysicalTable table) {
    tables.add(table);
  }

}
