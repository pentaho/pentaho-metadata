package org.pentaho.pms.mql.dialect;

public class H2Dialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$
  
  public H2Dialect() {
    super("H2"); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate(SQLQueryModel query, StringBuilder sql) {
    generateTopBeforeDistinct(query, sql, TOP_KEYWORD);
  }

}
