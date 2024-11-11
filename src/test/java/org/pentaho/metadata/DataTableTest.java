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
package org.pentaho.metadata;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;
import org.pentaho.metadata.datatable.Cell;
import org.pentaho.metadata.datatable.Column;
import org.pentaho.metadata.datatable.DataTable;
import org.pentaho.metadata.datatable.Row;
import org.pentaho.metadata.datatable.Types;

@SuppressWarnings( { "all" } )
public class DataTableTest {

  @Test
  public void testCell() {

    Cell cell = new Cell();
    Assert.assertNull( "f should be null", cell.getf() );
    Assert.assertNull( "v should be null", cell.getv() );

    cell.setf( "formatted value" );
    Assert.assertNotNull( "f should not be null", cell.getf() );
    Assert.assertEquals( "Wrong value", "formatted value", cell.getf() );

    cell.setv( new BigDecimal( 999 ) );
    Assert.assertNotNull( "v should not be null", cell.getv() );
    Assert.assertEquals( "Wrong value", new BigDecimal( 999 ), cell.getv() );

    cell = new Cell( new BigDecimal( 444 ) );
    Assert.assertNotNull( "v should not be null", cell.getv() );
    Assert.assertEquals( "Wrong value", new BigDecimal( 444 ), cell.getv() );
    Assert.assertNull( "f should be null", cell.getf() );

    cell = new Cell( new BigDecimal( 2222 ), "2,222" );
    Assert.assertNotNull( "v should not be null", cell.getv() );
    Assert.assertNotNull( "f should not be null", cell.getf() );
    Assert.assertEquals( "Wrong value", new BigDecimal( 2222 ), cell.getv() );
    Assert.assertEquals( "Wrong value", "2,222", cell.getf() );

  }

  @Test
  public void testColumn() {

    Column column = new Column();
    Assert.assertNull( "id should be null", column.getId() );
    Assert.assertNull( "label should be null", column.getLabel() );
    Assert.assertNull( "type should be null", column.getType() );

    column.setId( "id1" );
    column.setLabel( "label1" );
    column.setType( "type1" );
    Assert.assertNotNull( "id should not be null", column.getId() );
    Assert.assertNotNull( "label should not be null", column.getLabel() );
    Assert.assertNotNull( "type should not be null", column.getType() );
    Assert.assertEquals( "Wrong value", "id1", column.getId() );
    Assert.assertEquals( "Wrong value", "label1", column.getLabel() );
    Assert.assertEquals( "Wrong value", "type1", column.getType() );

    column = new Column( "id2", "label2", "type2" );
    Assert.assertNotNull( "id should not be null", column.getId() );
    Assert.assertNotNull( "label should not be null", column.getLabel() );
    Assert.assertNotNull( "type should not be null", column.getType() );
    Assert.assertEquals( "Wrong value", "id2", column.getId() );
    Assert.assertEquals( "Wrong value", "label2", column.getLabel() );
    Assert.assertEquals( "Wrong value", "type2", column.getType() );

  }

  @Test
  public void testRow() {

    Row row = new Row();
    Assert.assertNull( "c should be null", row.getc() );

    Cell[] cells = new Cell[] { new Cell() };
    row.setc( cells );
    Assert.assertNotNull( "c should not be null", row.getc() );
    Assert.assertEquals( "Wrong value", cells, row.getc() );

    row = new Row( cells );
    Assert.assertNotNull( "c should not be null", row.getc() );
    Assert.assertEquals( "Wrong value", cells, row.getc() );

  }

  @Test
  public void testDataTable() {

    Row[] rows = new Row[] { new Row() };
    Column[] cols = new Column[] { new Column() };

    DataTable table = new DataTable();
    Assert.assertNull( "Cols should be null", table.getCols() );
    Assert.assertNull( "Rows should be null", table.getRows() );

    table.setCols( cols );
    table.setRows( rows );
    Assert.assertNotNull( "Cols should not be null", table.getCols() );
    Assert.assertNotNull( "Rows should not be null", table.getRows() );
    Assert.assertEquals( "Wrong rows", rows, table.getRows() );
    Assert.assertEquals( "Wrong columns", cols, table.getCols() );

    table = new DataTable( rows, cols );
    Assert.assertNotNull( "Cols should not be null", table.getCols() );
    Assert.assertNotNull( "Rows should not be null", table.getRows() );
    Assert.assertEquals( "Wrong rows", rows, table.getRows() );
    Assert.assertEquals( "Wrong columns", cols, table.getCols() );

  }

  @Test
  public void testTypes() {
    Types types = new Types();
  }

}
