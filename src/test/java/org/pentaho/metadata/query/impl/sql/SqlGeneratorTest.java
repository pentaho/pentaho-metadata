/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.metadata.query.impl.sql;
import java.util.*;
import org.junit.Test;
import org.junit.Assert;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.pms.mql.dialect.SQLQueryModel;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.di.core.database.DatabaseMeta;

import org.pentaho.metadata.model.LogicalModel;

import org.pentaho.metadata.model.LogicalTable;

import org.pentaho.metadata.query.model.Constraint;
import org.mockito.Mockito;

public class SqlGeneratorTest {
  @Test
  public void testGenerateFromAndWhere() throws Exception {
    String TABLE_NAME = "TABLE1";
    String INLINE_SQL = "select idt1 as f1,field2 as f2,field3 as f3,field4 as f4,field5 as f5,field6 as f6,field7 as f7,idt1,field2,field3,field4,field5,field6,field7 from test.t1";
    SqlGenerator sqlg = new SqlGenerator();
    SQLQueryModel query = new SQLQueryModel();
    List<LogicalTable> userBusinessTales = new ArrayList<LogicalTable>();

    LogicalTable logicalTable = new LogicalTable();
    SqlPhysicalTable physicalTable = new SqlPhysicalTable();
    physicalTable.setTargetTable( TABLE_NAME );
    physicalTable.setTargetTableType( TargetTableType.INLINE_SQL );
    logicalTable.setPhysicalTable( physicalTable );
    userBusinessTales.add( logicalTable );

    logicalTable = new LogicalTable();
    physicalTable = new SqlPhysicalTable();
    physicalTable.setTargetTable( TABLE_NAME );
    physicalTable.setTargetTableType( TargetTableType.TABLE );
    logicalTable.setPhysicalTable( physicalTable );
    userBusinessTales.add( logicalTable );

    logicalTable = new LogicalTable();
    physicalTable = new SqlPhysicalTable();
    physicalTable.setTargetTable( INLINE_SQL );
    physicalTable.setTargetTableType( TargetTableType.TABLE );
    logicalTable.setPhysicalTable( physicalTable );
    userBusinessTales.add( logicalTable );

    LogicalModel model = new LogicalModel();
    Path path = null;
    List<Constraint> conditions = null;//new ArrayList<Constraint>();
    Map<LogicalTable, String> tableAliases = new HashMap<LogicalTable, String>();
    Map<Constraint, SqlOpenFormula> constraintFormulaMap = new HashMap<Constraint, SqlOpenFormula>();
    Map<String, Object> parameters = new HashMap<String, Object>();
    boolean genAsPreparedStatement = false;
    DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
    String schemaName = null;
    Mockito.doReturn( TABLE_NAME ).when(databaseMeta).getQuotedSchemaTableCombination( Mockito.any(), Mockito.anyString() );
    String locale  = "en_US";
    sqlg.generateFromAndWhere( query, userBusinessTales, model, path, conditions,  tableAliases,
       constraintFormulaMap, parameters, genAsPreparedStatement,  databaseMeta,  locale );
    Assert.assertEquals("(" + TABLE_NAME + ")",query.getTables().get(0).getTableName() );
    Assert.assertEquals(TABLE_NAME ,query.getTables().get(1).getTableName() );
    Assert.assertEquals("(" + INLINE_SQL + ")",query.getTables().get(2).getTableName() );
  }
}
