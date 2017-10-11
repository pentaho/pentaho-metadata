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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.DatabaseMetaInformation;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LoggingObject;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.schema.SchemaMeta;

@SuppressWarnings( "deprecation" )
public class AutoModelerScenario {

  public AutoModelerScenario() throws KettleDatabaseException, PentahoMetadataException {

    // ////////////////////////////////////////////////////////////
    //
    // A) we create the database metadata object...
    //
    // A1: ask the database type
    //
    String databaseType = "MySQL"; // See: DatabaseMeta.dbAccessTypeCode / DatabaseMeta.dbAccessTypeDesc

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
    // B) we ask for an optional schema name and then get a list of tables from the database
    //
    // B1: look in the database...
    //
    DatabaseMetaInformation dmi = new DatabaseMetaInformation( databaseMeta );
    dmi.getData( new LoggingObject( "Auto Modeler" ), null ); // reads the metadata from the database, optional progress
                                                              // monitor (TODO: roll our own progress monitor)

    // B2: optionally allow the user to select a schema
    //
    String schemaName = dmi.getSchemas()[0].getSchemaName(); // for example, we take the first...

    // B3 : show a list of tables from the database
    //
    String[] tableNames = dmi.getSchemas()[0].getItems(); // for example, all tables from the first schema

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
    SchemaMeta schemaMeta = modeler.generateSchemaMeta(); // throws exception

    // ////////////////////////////////////////////////////////////
    //
    // D) At this time, the business model is there, the physical tables
    // and columns as well as the business tables and columns.
    // Let's ask the user for the join definitions
    //
    BusinessModel model = schemaMeta.getActiveModel();

    for ( int i = 0; i < model.nrBusinessTables(); i++ ) {
      BusinessTable leftTable = model.getBusinessTable( i );

      // Present this table to the user and ask for a selection of a second table...
      // The list is assembled like this:
      //
      List<BusinessTable> tables = new ArrayList<BusinessTable>();
      for ( int t = 0; t < model.nrBusinessTables(); t++ ) {
        BusinessTable table = model.getBusinessTable( t );
        if ( leftTable != table ) {
          tables.add( table );
        }
      }

      // Ask the user to make the selection (or SKIP the table!!)
      //
      BusinessTable rightTable = (BusinessTable) tables.get( 0 ); // for example

      // Then list the columns and ask the user to pick 2 columns
      //
      BusinessColumn leftColumn = leftTable.getBusinessColumn( 0 ); // for example
      BusinessColumn rightColumn = rightTable.getBusinessColumn( 0 ); // for example

      // We can add the join to the list...
      //
      model.addRelationship( new RelationshipMeta( leftTable, rightTable, leftColumn, rightColumn ) );
    }

    // ////////////////////////////////////////////////////////////
    //
    // E) The model is complete and can be used to generate SQL, etc.
    //
  }

}
