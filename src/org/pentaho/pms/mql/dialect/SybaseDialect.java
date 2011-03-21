package org.pentaho.pms.mql.dialect;

public class SybaseDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "TOP"; //$NON-NLS-1$

  public SybaseDialect() {
    super("SYBASE"); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate(SQLQueryModel query, StringBuilder sql) {
    generateTopAfterDistinct(query, sql, TOP_KEYWORD);
  }

}
