package org.pentaho.pms.service;

import java.util.List;
import java.util.Map;

import org.pentaho.pms.schema.v3.model.Category;
import org.pentaho.pms.schema.v3.model.Column;
import org.pentaho.pms.schema.v3.physical.IDataSource;

public interface IModelManagementService {
  
  /**
   * 
   * @param dataSource
   * @return
   */
  public List<Column> getColumns(IDataSource dataSource);
  
  /**
   * @param metadataConnection
   * @return sample rows of data based on the constraining expression of the datasource 
   */
  public List<List<String>> getDataSample(IDataSource dataSource, int rows);
  
  /**
   * Creates and persists a new category attached to the default domain and model hard-coded
   * by this implementation.  (This method may change in the future to accept model
   * and domain).
   * @param businessVewName The name of the new business view
   * @param businessColumns 
   */
  public Category createCategory(IDataSource dataSource, String businessViewName, List<Column> businessColumns, Map columnCrossRef);
}
