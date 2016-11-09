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
package org.pentaho.pms.automodel;

import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.Settings;

/**
 * This class will help in the automatic generation of a metadata model.<br>
 * We will only receive :
 * <p>
 * - a locale (from the environment)<br>
 * - a model name (any user defined string)<br>
 * - a database connection<br>
 * - a set of input schema-table combinations<br>
 * <p>
 * This information should be enough to get a minimal model going.
 * <p>
 * The model WILL NOT CONTAIN RELATIONSHIPS / JOINS!! Those need to be added later.
 * 
 * @author matt
 * 
 */
@SuppressWarnings( "deprecation" )
public class AutoModeler {
  private DatabaseMeta databaseMeta;
  private SchemaTable[] tableNames;
  private String modelName;
  private String locale;

  /**
   * @param locale
   * @param modelName
   * @param databaseMeta
   * @param tableNames
   */
  public AutoModeler( String locale, String modelName, DatabaseMeta databaseMeta, SchemaTable[] tableNames ) {
    this.locale = locale;
    this.modelName = modelName;
    this.databaseMeta = databaseMeta;
    this.tableNames = tableNames;
  }

  // public AutoModeler(String locale, String modelName, SQLPhysicalModel sqlPhysicalModel) {
  // this.locale = locale;
  // this.modelName = modelName;
  // this.databaseMeta = sqlPhysicalModel.getDbMeta();
  // updateSchemaTables();
  // }
  //
  // private void updateSchemaTables() {
  // Database database = new Database(databaseMeta);
  // database.
  //
  // }

  public SchemaMeta generateSchemaMeta() throws PentahoMetadataException {
    SchemaMeta schemaMeta = new SchemaMeta();
    schemaMeta.setName( modelName );

    Database database = new Database( databaseMeta );
    try {
      // Add the database connection to the empty schema...
      //
      schemaMeta.addDatabase( databaseMeta );

      // Also add a model with the same name as the model name...
      //
      String bmID = Settings.getBusinessModelIDPrefix() + "_" + Const.replace( modelName, " ", "_" ).toUpperCase();
      BusinessModel businessModel = new BusinessModel( bmID );
      schemaMeta.addModel( businessModel );

      // Connect to the database...
      //
      database.connect();

      // clear the cache
      DBCache.getInstance().clear( databaseMeta.getName() );

      for ( int i = 0; i < tableNames.length; i++ ) {
        SchemaTable schemaTable = tableNames[i];

        // Import the specified tables and turn them into PhysicalTable objects...
        //
        PhysicalTable physicalTable =
            PhysicalTableImporter.importTableDefinition( database, schemaTable.getSchemaName(), schemaTable
                .getTableName(), locale );
        schemaMeta.addTable( physicalTable );

        // At the same time, we will create a business table and add that to the business model...
        //
        BusinessTable businessTable = createBusinessTable( physicalTable, locale );
        businessModel.addBusinessTable( businessTable );
      }

      // Set the model as active
      //
      schemaMeta.setActiveModel( businessModel );
    } catch ( Exception e ) {
      // For the unexpected stuff, just throw the exception upstairs.
      //
      throw new PentahoMetadataException( e );
    } finally {
      // Make sure to close the connection
      //
      database.disconnect();
    }

    return schemaMeta;
  }

  private BusinessColumn findBusinessColumn( BusinessTable businessTable, String columnName ) {
    for ( int i = 0; i < businessTable.nrBusinessColumns(); i++ ) {
      BusinessColumn businessColumn = businessTable.getBusinessColumn( i );
      if ( columnName.equals( businessColumn.getPhysicalColumn().getFormula() ) ) {
        return businessColumn;
      }
    }
    return null;
  }

  private BusinessTable createBusinessTable( PhysicalTable physicalTable, String locale )
    throws ObjectAlreadyExistsException {

    // Create a business table with a new ID and localized name
    //
    BusinessTable businessTable = new BusinessTable( null, physicalTable );

    // Try to set the name of the business table to something nice (beautify)
    //
    String tableName = PhysicalTableImporter.beautifyName( physicalTable.getTargetTable() );
    businessTable.getConcept().setName( locale, tableName );

    businessTable.setId( BusinessTable.proposeId( locale, businessTable, physicalTable ) );

    // Add columns to this by copying the physical columns to the business columns...
    //
    for ( int i = 0; i < physicalTable.nrPhysicalColumns(); i++ ) {

      PhysicalColumn physicalColumn = physicalTable.getPhysicalColumn( i );
      BusinessColumn businessColumn = new BusinessColumn( physicalColumn.getId(), physicalColumn, businessTable );

      // We're done, add the business column.
      //
      // Propose a new ID
      businessColumn.setId( BusinessColumn.proposeId( locale, businessTable, physicalColumn ) );
      businessTable.addBusinessColumn( businessColumn );
    }

    return businessTable;
  }

  /**
   * @return the databaseMeta
   */
  public DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }

  /**
   * @param databaseMeta
   *          the databaseMeta to set
   */
  public void setDatabaseMeta( DatabaseMeta databaseMeta ) {
    this.databaseMeta = databaseMeta;
  }

  /**
   * @return the tableNames
   */
  public SchemaTable[] getTableNames() {
    return tableNames;
  }

  /**
   * @param tableNames
   *          the tableNames to set
   */
  public void setTableNames( SchemaTable[] tableNames ) {
    this.tableNames = tableNames;
  }

  /**
   * @return the modelName
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * @param modelName
   *          the modelName to set
   */
  public void setModelName( String modelName ) {
    this.modelName = modelName;
  }

  /**
   * @return the locale
   */
  public String getLocale() {
    return locale;
  }

  /**
   * @param locale
   *          the locale to set
   */
  public void setLocale( String locale ) {
    this.locale = locale;
  }
}
