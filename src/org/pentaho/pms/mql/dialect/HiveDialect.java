package org.pentaho.pms.mql.dialect;

import org.pentaho.pms.mql.dialect.SQLQueryModel.SQLTable;
import org.pentaho.pms.util.Const;

/**
 * Apache Hadoop Hive Implementation of Metadata SQL Dialect
 * 
 * @author Jordan Ganoff (jganoff@pentaho.com)
 * 
 */
public class HiveDialect extends DefaultSQLDialect {

  public HiveDialect() {
    super("HADOOP HIVE"); //$NON-NLS-1$
  }

  @Override
  protected void generateFrom(SQLQueryModel query, StringBuilder sql) {
    sql.append("FROM ").append(Const.CR); //$NON-NLS-1$
    boolean first = true;
    for (SQLTable table : query.getTables()) {
      if (first) {
        sql.append("          "); //$NON-NLS-1$
        appendTableAndAlias(sql, table);
        first = false;
      } else {
        // Hive does not support more than one table reference.  When more than one table is 
        // used we must explicitly join it.
        sql.append("     JOIN "); //$NON-NLS-1$
        appendTableAndAlias(sql, table);
        // TODO Optimize this so the conditions are added as ON conditions.
      }
      sql.append(Const.CR);
    }
  }

  /**
   * Append a table's name and alias, if defined, to the end of {@code sql}.
   * 
   * @param sql  
   * @param table
   */
  private void appendTableAndAlias(StringBuilder sql, SQLTable table) {
    sql.append(table.getTableName());
    if (table.getAlias() != null) {
      sql.append(" ").append(table.getAlias()); //$NON-NLS-1$
    }
  }
}
