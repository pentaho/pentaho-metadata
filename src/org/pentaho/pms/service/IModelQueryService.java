package org.pentaho.pms.service;

import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.pms.schema.v3.model.Model;

public interface IModelQueryService {
  /**
   * We need to use a lightweight MqlQuery object instead of using the mql string here.
   * @param businessModel
   * @param mqlQuery
   * @return a serializable {@link IPentahoResultSet}
   */
  public IPentahoResultSet executeMqlQuery(Model model, String mqlQuery);
}
