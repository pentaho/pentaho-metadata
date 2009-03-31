package org.pentaho.pms.service;

import java.util.List;

import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.pms.core.SQLPhysicalModel;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.model.Model;

public interface IMetadataService {
  
  public List<Column> getBusinessColumns(SQLPhysicalModel metadataConnection);
  
  /**
   * @param metadataConnection
   * @return sample rows of data based on the provided sql 
   */
  public List<List<String>> getSampleData(SQLPhysicalModel metadataConnection);
  
  /**
   * Creates a new business view attached to the default domain and model hard-coded
   * by this implementation.  (This method may change in the future to accept model
   * and domain).
   * @param businessVewName The name of the new business view
   * @param businessColumns 
   */
  public void createBusinessView(String businessVewName, List<Column> businessColumns);
  
  /**
   * We need to use a lightweight MqlQuery object instead of using the mql string here.
   * @param businessModel
   * @param mqlQuery
   * @return a serializable {@link IPentahoResultSet}
   */
  public IPentahoResultSet executeMqlQuery(Model businessModel, String mqlQuery);

}
