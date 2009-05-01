package org.pentaho.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.metadata.model.concept.Concept;

public class SqlPhysicalModel extends Concept implements IPhysicalModel {
  
  // this property should be replaced with a thin 
  // representation of database meta, which is required
  // for full backward compatibility.
  
  public SqlPhysicalModel() {
    super();
    // TODO Auto-generated constructor stub
  }

  /** returns a pentaho or JNDI datasource **/
  private String datasource;
  
  // this contains a list of the physical tables
  private List<SqlPhysicalTable> physicalTables = new ArrayList<SqlPhysicalTable>();

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String getDatasource() {
    return datasource;
  }
  
  public List<SqlPhysicalTable> getPhysicalTables() {
    return physicalTables;
  }
  

}
