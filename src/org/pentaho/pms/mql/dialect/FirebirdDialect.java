package org.pentaho.pms.mql.dialect;

public class FirebirdDialect extends DefaultSQLDialect {

  private static final String TOP_KEYWORD = "FIRST"; //$NON-NLS-1$

  public FirebirdDialect() {
    super("FIREBIRD"); //$NON-NLS-1$
  }

  @Override
  protected void generateSelectPredicate(SQLQueryModel query, StringBuilder sql) {
    generateTopBeforeDistinct(query, sql, TOP_KEYWORD);
  }

}
