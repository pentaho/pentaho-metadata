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

package org.pentaho.metadata.automodel;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.DatabaseMetaInformation;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LoggingObject;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.pms.core.exception.PentahoMetadataException;

public class AutoModelerScenario {

  public AutoModelerScenario() throws KettleDatabaseException, PentahoMetadataException {

    // ////////////////////////////////////////////////////////////
    //
    // A) we create the database metadata object...
    //
    // A1: ask the database type
    //
    String databaseType = "MySQL"; // See: DatabaseMeta.dbAccessTypeCode /
                                   // DatabaseMeta.dbAccessTypeDesc

    // A2: ask the host name and port
    // assume we're using JDBC
    //
    String hostname = "localhost";
    String port = "3306";

    // A3: ask the database name
    //
    String databaseName = "test";

    // A4: ask the username and password
    //
    String username = "matt";
    String password = "abcd";

    // end of A: Create the database meta object
    //
    DatabaseMeta databaseMeta =
        new DatabaseMeta( databaseName, databaseType, "JDBC", hostname, databaseName, port, username, password );

    // ////////////////////////////////////////////////////////////
    //
    // B) we ask for an optional schema name and then get a list of tables from
    // the database
    //
    // B1: look in the database...
    //
    DatabaseMetaInformation dmi = new DatabaseMetaInformation( databaseMeta );
    dmi.getData( new LoggingObject( "Auto Modeler" ), null ); // reads the metadata from the database, optional
    // progress monitor (TODO: roll our own progress monitor)

    // B2: optionally allow the user to select a schema
    //
    String schemaName = dmi.getSchemas()[0].getSchemaName(); // for example, we
                                                             // take the
                                                             // first...

    // B3 : show a list of tables from the database
    //
    String[] tableNames = dmi.getSchemas()[0].getItems(); // for example, all
                                                          // tables from the
                                                          // first schema

    // B4 : the user selects a set of tables
    //
    SchemaTable[] schemaTables =
        new SchemaTable[] { new SchemaTable( "dwh", "d_customer" ), new SchemaTable( "dwh", "d_product" ),
          new SchemaTable( "dwh", "d_date" ), new SchemaTable( "dwh", "f_orderlines" ), };

    // ////////////////////////////////////////////////////////////
    //
    // C) we create the modeler object and generate the schema
    //
    AutoModeler modeler = new AutoModeler( "en_US", "Orders", databaseMeta, schemaTables );
    Domain domain = modeler.generateDomain(); // throws exception

    // ////////////////////////////////////////////////////////////
    //
    // D) At this time, the business model is there, the physical tables
    // and columns as well as the business tables and columns.
    // Let's ask the user for the join definitions
    //
    LogicalModel model = domain.getLogicalModels().get( 0 );

    for ( int i = 0; i < model.getLogicalTables().size(); i++ ) {
      LogicalTable leftTable = model.getLogicalTables().get( i );

      // Present this table to the user and ask for a selection of a second
      // table...
      // The list is assembled like this:
      //
      List<LogicalTable> tables = new ArrayList<LogicalTable>();
      for ( int t = 0; t < model.getLogicalTables().size(); t++ ) {
        LogicalTable table = model.getLogicalTables().get( t );
        if ( leftTable != table ) {
          tables.add( table );
        }
      }

      // Ask the user to make the selection (or SKIP the table!!)
      //
      LogicalTable rightTable = (LogicalTable) tables.get( 0 ); // for example

      // Then list the columns and ask the user to pick 2 columns
      //
      LogicalColumn leftColumn = leftTable.getLogicalColumns().get( 0 ); // for
                                                                         // example
      LogicalColumn rightColumn = rightTable.getLogicalColumns().get( 0 ); // for
                                                                           // example

      // We can add the join to the list...
      //
      model.addLogicalRelationship( new LogicalRelationship( model, leftTable, rightTable, leftColumn, rightColumn ) );
    }

    // ////////////////////////////////////////////////////////////
    //
    // E) The model is complete and can be used to generate SQL, etc.
    //
  }

}
