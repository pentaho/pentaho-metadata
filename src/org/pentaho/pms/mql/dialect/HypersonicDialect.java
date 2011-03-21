package org.pentaho.pms.mql.dialect;


public class HypersonicDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$
  
  public HypersonicDialect() {
    super("HYPERSONIC"); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate(SQLQueryModel query, StringBuilder sql) {
    generateTopBeforeDistinct(query, sql, TOP_KEYWORD);
  }

}