package org.pentaho.pms.schema.v3.physical;

import org.pentaho.di.core.database.DatabaseMeta;

public class SQLDataSource implements IDataSource {

  private DatabaseMeta databaseMeta;

  private String query;
  
  public SQLDataSource(DatabaseMeta databaseMeta, String query) {
    this.databaseMeta = databaseMeta;
    this.query = query;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.schema.v3.physical.IDataSource#getDbMeta()
   */
  public DatabaseMeta getDbMeta() {
    return databaseMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.schema.v3.physical.IDataSource#setDbMeta(org.pentaho.di.core.database.DatabaseMeta)
   */
  public void setDbMeta(DatabaseMeta dbMeta) {
    this.databaseMeta = dbMeta;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.schema.v3.physical.IDataSource#getQuery()
   */
  public String getColumnFilterExpression() {
    return query;
  }

  /* (non-Javadoc)
   * @see org.pentaho.pms.schema.v3.physical.IDataSource#setQuery(java.lang.String)
   */
  public void setColumnFilterExpression(String query) {
    this.query = query;
  }
}
