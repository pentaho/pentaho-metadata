package org.pentaho.pms.mql.dialect;


public class NetezzaDialect extends DefaultSQLDialect {

  public NetezzaDialect() {
    super("NETEZZA"); //$NON-NLS-1$
  }
  
  @Override
  protected void generatePostOrderBy(SQLQueryModel query, StringBuilder sql) {
    generateLimit(query, sql);
  }
  
  @Override
  protected void generateSelectPredicate(SQLQueryModel query, StringBuilder sql) {
    generateDistinct(query, sql);
  }

}
