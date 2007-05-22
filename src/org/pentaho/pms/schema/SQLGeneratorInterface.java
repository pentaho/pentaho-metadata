package org.pentaho.pms.schema;

import be.ibridge.kettle.core.database.DatabaseMeta;

/**
 * this interface is used by PMSFormula and PMSFormulaContext to
 * convert open document format (ODF) functions and operators to native SQL
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public interface SQLGeneratorInterface {
  
  // various types of ODF terms
  public static final int INLINE_FUNCTION = 0;
  public static final int PARAM_FUNCTION = 1;
  public static final int PARAM_AGG_FUNCTION = 2;
  public static final int INFIX_OPERATOR = 3;
  
  /**
   * return the type of this specific term
   * 
   * @return type enumerator
   */
  public int getType();
  
  /**
   * return native sql for this ODF type.  databaseMeta
   * is used to convert ODF to native sql.
   * 
   * @param databaseMeta kettle metadata object for native RDBMS
   * 
   * @return native SQL
   */
  public String getSQL(DatabaseMeta databaseMeta);
  
  /**
   * return native sql for parameter separator. this may be useful if a separator
   * is used other than a traditional comma in an RDBMS, for instance string concat
   * in oracle " A " || " B ".
   * 
   * @param databaseMeta kettle metadata object for native RDBMS
   * 
   * @return native SQL param separator
   */
  public String getFunctionParamSeparator(DatabaseMeta databaseMeta);
}
