/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */
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
