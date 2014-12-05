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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.metadata.automodel;

import org.junit.Test;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalTable;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.pentaho.metadata.automodel.PhysicalTableImporter.importTableDefinition;

public class PhysicalTableImporterTest {
  @Test
  public void testImportTableDefUsesColumnsFromRowMetaStrategy() throws Exception {
    final Database database = mock( Database.class );
    PhysicalTableImporter.RowMetaStrategy rowMetaStrategy = new PhysicalTableImporter.RowMetaStrategy() {

      @Override public RowMetaInterface rowMeta(
          final Database actualDatabase, final String schemaName, final String tableName )
        throws KettleDatabaseException {
        assertSame( actualDatabase, database );
        assertEquals( "aSchema", schemaName );
        assertEquals( "aTable", tableName );
        RowMeta rowMeta = new RowMeta();
        rowMeta.addValueMeta( new ValueMetaInteger( "quantityordered" ) );
        rowMeta.addValueMeta( new ValueMetaNumber( "totalprice" ) );
        rowMeta.addValueMeta( new ValueMetaString( "productcode" ) );
        rowMeta.addValueMeta( new ValueMetaString( "status" ) );
        return rowMeta;
      }
    };
    SqlPhysicalTable physicalTable =
      importTableDefinition( database, "aSchema", "aTable", "klingon", rowMetaStrategy );
    List<IPhysicalColumn> physicalColumns = physicalTable.getPhysicalColumns();
    assertEquals( 4, physicalColumns.size() );
    assertContainsColumn( "quantityordered", physicalColumns );
    assertContainsColumn( "totalprice", physicalColumns );
    assertContainsColumn( "productcode", physicalColumns );
    assertContainsColumn( "status", physicalColumns );

  }

  private void assertContainsColumn( final String columnName, final List<IPhysicalColumn> physicalColumns ) {
    for ( IPhysicalColumn column : physicalColumns ) {
      if(column.getId().equals( columnName ) ) {
        return;
      }
    }
    fail( "column " + columnName + " not found" );
  }
}