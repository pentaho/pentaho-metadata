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

package org.pentaho.pms.test;

import java.util.Collection;
import java.util.Iterator;

import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmExpression;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmPackage;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmColumn;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmTable;

/**
 * Test program for our meta-model driver meta-data implementation...
 * 
 * @author Matt
 * 
 */
public class TestCWM {
  public static final String DOMAIN = "SomeDomain"; // The domain name //$NON-NLS-1$

  private static final String TEST_TABLE_NAME = "PentahoTable1"; //$NON-NLS-1$

  private static CWM cwm = CWM.getInstance( DOMAIN );

  /**
   * @param args
   */
  public static void main( String[] args ) throws Exception {
    TestCWM testCWM = new TestCWM();
    testCWM.storeTable();
    testCWM.readBack();
    // testCWM.removeTable();
    // testCWM.readBack();

    String[] domainNames = CWM.getDomainNames();
    for ( int i = 0; i < domainNames.length; i++ ) {
      System.out.println( "Package #" + ( i + 1 ) + " found : " + domainNames[i] ); //$NON-NLS-1$ //$NON-NLS-2$
      cwm.removePackage( domainNames[i] );
    }

    CWM.quitAndSync();
  }

  public void storeTable() {
    CwmTable table = cwm.getTable( TEST_TABLE_NAME );
    if ( table == null ) {
      System.out.println( "Table [" + TEST_TABLE_NAME + "] not found: creating..." ); //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      System.out.println( "Table [" + TEST_TABLE_NAME + "] found: overwriting..." ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    cwm.beginTransaction();

    RowMetaInterface fields = new RowMeta();
    ValueMetaInterface field1 = new ValueMeta( "field1", ValueMetaInterface.TYPE_STRING ); //$NON-NLS-1$
    field1.setLength( 35 );
    field1.setOrigin( "field1 description" ); //$NON-NLS-1$
    fields.addValueMeta( field1 );
    ValueMetaInterface field2 = new ValueMeta( "field2", ValueMetaInterface.TYPE_NUMBER ); //$NON-NLS-1$
    field2.setLength( 7, 2 );
    field2.setOrigin( "field2 description" ); //$NON-NLS-1$
    fields.addValueMeta( field2 );
    ValueMetaInterface field3 = new ValueMeta( "field3", ValueMetaInterface.TYPE_INTEGER );
    field3.setLength( 5 );
    field3.setOrigin( "field3 description" );
    fields.addValueMeta( field3 );
    ValueMetaInterface field4 = new ValueMeta( "field4", ValueMetaInterface.TYPE_DATE );
    field4.setOrigin( "field4 description" );
    fields.addValueMeta( field4 );
    ValueMetaInterface field5 = new ValueMeta( "field5", ValueMetaInterface.TYPE_BIGNUMBER );
    field5.setLength( 52, 16 );
    field5.setOrigin( "field5 description" );
    fields.addValueMeta( field5 );
    ValueMetaInterface field6 = new ValueMeta( "field6", ValueMetaInterface.TYPE_BOOLEAN );
    field6.setOrigin( "field6 description" );
    fields.addValueMeta( field6 );

    table = cwm.createTable( TEST_TABLE_NAME, fields );

    // Add descriptions to table and columns...

    CwmDescription description = cwm.createDescription( "This is a table description" ); //$NON-NLS-1$
    cwm.setDescription( table, description );
    @SuppressWarnings( "unchecked" )
    Collection<CwmColumn> collection = table.getOwnedElement();
    CwmColumn[] columns = (CwmColumn[]) collection.toArray( new CwmColumn[collection.size()] );

    for ( int i = 0; i < fields.size(); i++ ) {
      ValueMetaInterface field = fields.getValueMeta( i );
      CwmColumn column = columns[i];

      // Add a description to the column
      //
      description = cwm.createDescription( field.getOrigin() );
      cwm.setDescription( column, description );
    }

    // Try to create a package here...
    CwmPackage p = cwm.createPackage( DOMAIN + " package" ); //$NON-NLS-1$
    @SuppressWarnings( "unchecked" )
    Collection<CwmTable> ca = p.getImportedElement();
    ca.add( table );
    cwm.setDescription( p, cwm.createDescription( "This is a package description for [" + DOMAIN + "]" ) ); //$NON-NLS-1$ //$NON-NLS-2$

    cwm.endTransaction();

    System.out.println( "Finished writing to table [" + TEST_TABLE_NAME + "]." ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void readBack() {
    CwmTable table = cwm.getTable( TEST_TABLE_NAME );
    if ( table != null ) {
      System.out.println( "Readback found table : " + table.getName() ); //$NON-NLS-1$
      CwmDescription[] tableDescription = cwm.getDescription( table );
      for ( int i = 0; i < tableDescription.length; i++ ) {
        System.out.println( "Table description #" + ( i + 1 ) + " : " + tableDescription[i].getBody() ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      Collection collection = table.getOwnedElement();
      for ( Iterator iter = collection.iterator(); iter.hasNext(); ) {
        CwmColumn column = (CwmColumn) iter.next();
        System.out
            .print( "Column: " + column.getName() + ", type=" + column.getType().getName() + ", length=" + column.getLength() + ", precision=" + column.getPrecision() ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        // The formula
        CwmExpression expression = column.getInitialValue();
        if ( expression != null ) {
          System.out.print( " Formula: " + expression.getBody() + " (" + expression.getLanguage() + ")" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // The descriptions
        CwmDescription[] columnDescription = cwm.getDescription( column );
        for ( int i = 0; i < columnDescription.length; i++ ) {
          System.out.print( " [" + columnDescription[i].getBody() + "]" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        System.out.println();
      }

      CwmPackage p = cwm.getPackage( DOMAIN );
      if ( p != null ) {
        System.out.println( "Package found ! --> " + p.getName() ); //$NON-NLS-1$
        // do we have a package description?
        CwmDescription[] description = cwm.getDescription( p );
        for ( int i = 0; i < description.length; i++ ) {
          System.out.println( "Package description #" + ( i + 1 ) + " --> " + description[i].getBody() ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    } else {
      System.out.println( "Couldn't find table " + TEST_TABLE_NAME ); //$NON-NLS-1$
    }
  }

  public void removeTable() {
    cwm.removeTable( TEST_TABLE_NAME );
  }
}
