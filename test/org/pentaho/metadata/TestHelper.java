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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.metadata;

import org.junit.Assert;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.messages.util.LocaleHelper;

@SuppressWarnings("deprecation")
public class TestHelper {
  public static LogicalModel buildDefaultModel() {
    try {
      final LogicalModel model = new LogicalModel();
      
      final LogicalTable bt1 = new LogicalTable();
      bt1.setId("bt1"); //$NON-NLS-1$
      bt1.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt1"); //$NON-NLS-1$
      final LogicalColumn bc1 = new LogicalColumn();
      bc1.setId("bc1"); //$NON-NLS-1$
      bc1.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc1"); //$NON-NLS-1$
      bc1.setLogicalTable(bt1);
      bc1.setDataType(DataType.NUMERIC);
      bt1.addLogicalColumn(bc1);
      bt1.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
      
      final LogicalTable bt2 = new LogicalTable();
      bt2.setId("bt2"); //$NON-NLS-1$
      bt2.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt2"); //$NON-NLS-1$
      final LogicalColumn bc2 = new LogicalColumn();
      bc2.setId("bc2"); //$NON-NLS-1$
      bc2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc2"); //$NON-NLS-1$
      bc2.setLogicalTable(bt2);
      bt2.addLogicalColumn(bc2);

      final LogicalColumn bce2 = new LogicalColumn();
      bce2.setId("bce2"); //$NON-NLS-1$
      bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN_TYPE, TargetColumnType.OPEN_FORMULA);
      bce2.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "[bt2.bc2] * 2"); //$NON-NLS-1$
      bce2.setLogicalTable(bt2);
      bt2.addLogicalColumn(bce2);
      bt2.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
      
      final LogicalTable bt3 = new LogicalTable();
      bt3.setId("bt3"); //$NON-NLS-1$
      bt3.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt3"); //$NON-NLS-1$
      final LogicalColumn bc3 = new LogicalColumn();
      bc3.setId("bc3"); //$NON-NLS-1$
      bc3.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc3"); //$NON-NLS-1$
      bc3.setLogicalTable(bt3);
      bt3.addLogicalColumn(bc3);
      bt3.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
      
      final LogicalTable bt4 = new LogicalTable();
      bt4.setId("bt4"); //$NON-NLS-1$
      bt4.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt4"); //$NON-NLS-1$
      final LogicalColumn bc4 = new LogicalColumn();
      bc4.setId("bc4"); //$NON-NLS-1$
      bc4.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc4"); //$NON-NLS-1$
      bc4.setLogicalTable(bt4);
      bt4.addLogicalColumn(bc4);
      bt4.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
      
      final LogicalTable bt5 = new LogicalTable();
      bt5.setId("bt5"); //$NON-NLS-1$
      bt5.setProperty(SqlPhysicalTable.TARGET_TABLE, "pt5"); //$NON-NLS-1$
      final LogicalColumn bc5 = new LogicalColumn();
      bc5.setId("bc5"); //$NON-NLS-1$
      bc5.setProperty(SqlPhysicalColumn.TARGET_COLUMN, "pc5"); //$NON-NLS-1$
      bc5.setLogicalTable(bt5);
      bt5.addLogicalColumn(bc5);
      bt5.setProperty(SqlPhysicalTable.RELATIVE_SIZE, 1);
      final LogicalRelationship rl1 = new LogicalRelationship();
      
      rl1.setFromTable(bt1);
      rl1.setFromColumn(bc1);
      rl1.setToTable(bt2);
      rl1.setToColumn(bc2);
      
      final LogicalRelationship rl2 = new LogicalRelationship();
      
      rl2.setFromTable(bt2);
      rl2.setFromColumn(bc2);
      rl2.setToTable(bt3);
      rl2.setToColumn(bc3);
  
      final LogicalRelationship rl3 = new LogicalRelationship();
      
      rl3.setFromTable(bt3);
      rl3.setFromColumn(bc3);
      rl3.setToTable(bt4);
      rl3.setToColumn(bc4);
  
      final LogicalRelationship rl4 = new LogicalRelationship();
      
      rl4.setFromTable(bt4);
      rl4.setFromColumn(bc4);
      rl4.setToTable(bt5);
      rl4.setToColumn(bc5);
      
      model.getLogicalTables().add(bt1);
      model.getLogicalTables().add(bt2);
      model.getLogicalTables().add(bt3);
      model.getLogicalTables().add(bt4);
      model.getLogicalTables().add(bt5);
      
      model.getLogicalRelationships().add(rl1);
      model.getLogicalRelationships().add(rl2);
      model.getLogicalRelationships().add(rl3);
      model.getLogicalRelationships().add(rl4);
      
      return model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void assertEqualsIgnoreWhitespaces(String expected, String two) {
    String oneStripped = stripWhiteSpaces(expected);
    String twoStripped = stripWhiteSpaces(two);
    
    Assert.assertEquals(oneStripped, twoStripped);
  }
  
  private static String stripWhiteSpaces(String one) {
    StringBuilder stripped = new StringBuilder();
    
    boolean previousWhiteSpace = false;
    
    for (char c : one.toCharArray()) {
      if (Character.isWhitespace(c)) {
        if (!previousWhiteSpace) {
          stripped.append(' '); // add a single white space, don't add a second
        }
        previousWhiteSpace=true;
      }
      else {
        if (c=='(' || c==')' || c=='|' || c=='-' || c=='+' || c=='/' || c=='*' || c=='{' || c=='}' || c==',' ) {
          int lastIndex = stripped.length()-1;
          if (stripped.charAt(lastIndex)==' ') {
            stripped.deleteCharAt(lastIndex);
          }
          previousWhiteSpace=true;
        } else {
          previousWhiteSpace=false;
        }
        stripped.append(c);
      }
    }
    
    // Trim the whitespace (max 1) at the front and back too...
    if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(0))) stripped.deleteCharAt(0);
    if (stripped.length() > 0 && Character.isWhitespace(stripped.charAt(stripped.length()-1))) stripped.deleteCharAt(stripped.length()-1);
    
    return stripped.toString();
  }
  
  public static void printOutJava(String sql) {
    String lines[] = sql.split("\n");
    for (int i = 0; i < lines.length; i++) {
      System.out.print("        \"" +lines[i].replaceAll("\\\"", "\\\\\""));
      if (i == lines.length - 1) {
        System.out.println("\\n\"");
      } else {
        System.out.println("\\n\" + ");
      }
    }
  }
  
  public static DatabaseMeta createOracleDatabaseMeta() {
    return new DatabaseMeta("", "ORACLE", "Native", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
  }
  
  public static Domain getBasicDomain() {
    
    String locale = LocaleHelper.getLocale().toString();
    
    SqlPhysicalModel model = new SqlPhysicalModel();
    SqlDataSource dataSource = new SqlDataSource();
    dataSource.setDatabaseName("SampleData");
    model.setDatasource(dataSource);
    SqlPhysicalTable table = new SqlPhysicalTable(model);
    table.setId("PT1");
    model.getPhysicalTables().add(table);
    table.setTargetTableType(TargetTableType.INLINE_SQL);
    table.setTargetTable("select * from customers");
    
    SqlPhysicalColumn column = new SqlPhysicalColumn(table);
    column.setId("PC1");
    column.setTargetColumn("customername");
    column.setName(new LocalizedString(locale, "Customer Name"));
    column.setDescription(new LocalizedString(locale, "Customer Name Desc"));
    column.setDataType(DataType.STRING);
    table.getPhysicalColumns().add(column);
    
    LogicalModel logicalModel = new LogicalModel();
    logicalModel.setId("MODEL");
    logicalModel.setName(new LocalizedString(locale, "My Model"));
    logicalModel.setDescription(new LocalizedString(locale, "A Description of the Model"));
    
    LogicalTable logicalTable = new LogicalTable();
    logicalTable.setId("LT");
    logicalTable.setPhysicalTable(table);
    logicalTable.setLogicalModel(logicalModel);
    
    logicalModel.getLogicalTables().add(logicalTable);
    
    LogicalColumn logicalColumn = new LogicalColumn();
    logicalColumn.setId("LC_CUSTOMERNAME");
    logicalColumn.setPhysicalColumn(column);
    logicalColumn.setLogicalTable(logicalTable);
    logicalTable.addLogicalColumn(logicalColumn);
    
    Category mainCategory = new Category(logicalModel);
    mainCategory.setId("CATEGORY");
    mainCategory.setName(new LocalizedString(locale, "Category"));
    mainCategory.addLogicalColumn(logicalColumn);
    
    logicalModel.getCategories().add(mainCategory);
    
    Domain domain = new Domain();
    domain.setId("DOMAIN");
    domain.addPhysicalModel(model);
    domain.addLogicalModel(logicalModel);
    
    return domain;
  }

}
