/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.pms.automodel;

public class SchemaTable {
  private String schemaName;

  private String tableName;

  /**
   * @param schemaName
   * @param tableName
   */
  public SchemaTable( String schemaName, String tableName ) {
    this.schemaName = schemaName;
    this.tableName = tableName;
  }

  public boolean equals( Object obj ) {
    if ( !( obj instanceof SchemaTable ) ) {
      return false;
    }
    if ( obj == null ) {
      return false;
    }
    if ( obj == this ) {
      return true;
    }

    SchemaTable schemaTable = (SchemaTable) obj;
    if ( schemaTable.schemaName == null ) {
      return schemaTable.getTableName().equals( tableName );
    } else {
      return schemaTable.getSchemaName().equals( schemaName ) && schemaTable.getTableName().equals( tableName );
    }
  }

  public int hashCode() {
    if ( schemaName == null ) {
      return tableName.hashCode();
    } else {
      return schemaName.hashCode() ^ tableName.hashCode();
    }
  }

  public String toString() {
    if ( schemaName == null ) {
      return tableName;
    } else {
      return schemaName + "." + tableName;
    }
  }

  /**
   * @return the schemaName
   */
  public String getSchemaName() {
    return schemaName;
  }

  /**
   * @param schemaName
   *          the schemaName to set
   */
  public void setSchemaName( String schemaName ) {
    this.schemaName = schemaName;
  }

  /**
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * @param tableName
   *          the tableName to set
   */
  public void setTableName( String tableName ) {
    this.tableName = tableName;
  }

}
