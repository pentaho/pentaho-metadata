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
 * Copyright (c) 2017 Hitachi Vantara.  All rights reserved.
 */
package org.pentaho.metadata.automodel;

import org.junit.Test;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pentaho.metadata.automodel.PhysicalTableImporter.importTableDefinition;

public class PhysicalTableImporterTest {
  @Test
  public void testImportTableCanExcludeColumns() throws Exception {
    final Database database = mock( Database.class );
    PhysicalTableImporter.ImportStrategy importStrategy = new PhysicalTableImporter.ImportStrategy() {

      @Override public boolean shouldInclude( final ValueMetaInterface valueMeta ) {
        return !valueMeta.getName().equals( "totalprice" );
      }

      @Override public String displayName( final ValueMetaInterface valueMeta ) {
        return valueMeta.getName();
      }
    };
    RowMeta rowMeta = new RowMeta();
    rowMeta.addValueMeta( new ValueMetaInteger( "quantityordered" ) );
    rowMeta.addValueMeta( new ValueMetaNumber( "totalprice" ) );
    rowMeta.addValueMeta( new ValueMetaString( "productcode" ) );
    rowMeta.addValueMeta( new ValueMetaString( "status" ) );
    DatabaseMeta databaseMeta = mock( DatabaseMeta.class );
    when( database.getDatabaseMeta() ).thenReturn( databaseMeta );
    when( databaseMeta.quoteField( "aSchema" ) ).thenReturn( "aSchema" );
    when( databaseMeta.quoteField( "aTable" ) ).thenReturn( "aTable" );
    when( databaseMeta.getSchemaTableCombination( "aSchema", "aTable" ) ).thenReturn( "aSchema.aTable" );
    when( database.getTableFields( "aSchema.aTable" ) ).thenReturn( rowMeta );
    SqlPhysicalTable physicalTable =
      importTableDefinition( database, "aSchema", "aTable", "klingon", importStrategy );
    List<IPhysicalColumn> physicalColumns = physicalTable.getPhysicalColumns();
    assertEquals( 3, physicalColumns.size() );

    // Depending on the class the formatting values are set or unset. If NULL formatting values were set on the
    // property map, this would cause an XmiParsing Error. Testing that values that are NULL are not set in the
    // property map.
    IPhysicalColumn column = assertContainsColumn( "quantityordered", physicalColumns );
    assertNotNullFormattingValues( column.getProperties() );

    column = assertContainsColumn( "productcode", physicalColumns );
    assertNotNullFormattingValues( column.getProperties() );

    column = assertContainsColumn( "status", physicalColumns );
    assertNotNullFormattingValues( column.getProperties() );
  }

  @Test
  public void testGetDataTypeTimestamp() throws Exception {
    final Database database = mock( Database.class );
    RowMeta rowMeta = new RowMeta();
    rowMeta.addValueMeta( new ValueMetaTimestamp( "ordertime" ) );
    DatabaseMeta databaseMeta = mock( DatabaseMeta.class );
    when( database.getDatabaseMeta() ).thenReturn( databaseMeta );
    when( databaseMeta.quoteField( "aSchema" ) ).thenReturn( "aSchema" );
    when( databaseMeta.quoteField( "aTable" ) ).thenReturn( "aTable" );
    when( databaseMeta.getSchemaTableCombination( "aSchema", "aTable" ) ).thenReturn( "aSchema.aTable" );
    when( database.getTableFields( "aSchema.aTable" ) ).thenReturn( rowMeta );

    SqlPhysicalTable physicalTable =
      importTableDefinition( database, "aSchema", "aTable", "klingon" );
    List<IPhysicalColumn> physicalColumns = physicalTable.getPhysicalColumns();

    assertEquals( 1, physicalColumns.size() );

    IPhysicalColumn column = physicalColumns.get( 0 );
    DataType dt = (DataType) column.getProperty( "datatype" );
    assertEquals( "Timestamp", dt.getName() );
  }

  @Test
  public void testBeautifyName() {
    assertEquals( "TestName", PhysicalTableImporter.beautifyName( "\"TestName\"" ) );
    assertEquals( "TestName", PhysicalTableImporter.beautifyName( "'TestName'" ) );
    assertEquals( "Test Name", PhysicalTableImporter.beautifyName( "Test_Name" ) );
    assertEquals( "TestName", PhysicalTableImporter.beautifyName( "TestName\\" ) );
    assertEquals( "TestName", PhysicalTableImporter.beautifyName( "`TestName`" ) );
  }

  private IPhysicalColumn assertContainsColumn( final String columnName, final List<IPhysicalColumn> physicalColumns ) {
    for ( IPhysicalColumn column : physicalColumns ) {
      if ( column.getId().equals( columnName ) ) {
        return column;
      }
    }

    fail( "column " + columnName + " not found" );
    return null;
  }

  private void assertNotNullFormattingValues( Map properties ) {
    assertNotNullPropertyValue( "source_mask", properties );
    assertNotNullPropertyValue( "source_decimalSymbol", properties );
    assertNotNullPropertyValue( "source_groupingSymbol", properties );
    assertNotNullPropertyValue( "source_currencySymbol", properties );
  }

  private void assertNotNullPropertyValue( String key, Map properties ) {
    if ( properties.containsKey( key ) ) {
      assertNotNull( properties.get( key ) );
    }
  }
}
