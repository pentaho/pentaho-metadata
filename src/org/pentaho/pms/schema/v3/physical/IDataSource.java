package org.pentaho.pms.schema.v3.physical;

import org.pentaho.di.core.database.DatabaseMeta;

public interface IDataSource {

  public DatabaseMeta getDbMeta();

  public void setDbMeta(DatabaseMeta dbMeta);

  public String getColumnFilterExpression();

  public void setColumnFilterExpression(String columnFilterExpression);

}