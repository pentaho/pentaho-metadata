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

package org.pentaho.metadata.automodel;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.automodel.PhysicalTableImporter.ImportStrategy;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.util.ThinModelConverter;
import org.pentaho.metadata.util.Util;
import org.pentaho.pms.core.exception.PentahoMetadataException;

import static org.pentaho.metadata.automodel.PhysicalTableImporter.defaultImportStrategy;

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

    if ( !Props.isInitialized() ) {
      Props.init( Props.TYPE_PROPERTIES_EMPTY );
    }
  }

  public Domain generateDomain() throws PentahoMetadataException {
    return generateDomain( defaultImportStrategy );
  }

  public Domain generateDomain( final ImportStrategy importStrategy ) throws PentahoMetadataException {
    Domain domain = new Domain();
    domain.setId( modelName );

    List<LocaleType> locales = new ArrayList<LocaleType>();
    locales.add( new LocaleType( "en_US", "English (US)" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    domain.setLocales( locales );

    SqlPhysicalModel physicalModel = new SqlPhysicalModel();
    physicalModel.setId( databaseMeta.getName() );
    physicalModel.setDatasource( ThinModelConverter.convertFromLegacy( databaseMeta ) );

    Database database = database();

    try {
      // Add the database connection to the empty schema...
      //
      domain.addPhysicalModel( physicalModel );

      // Also add a model with the same name as the model name...
      //
      String bmID = Util.getLogicalModelIdPrefix() + "_" + modelName.replaceAll( " ", "_" ).toUpperCase(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      LogicalModel logicalModel = new LogicalModel();
      logicalModel.setId( bmID );
      domain.addLogicalModel( logicalModel );

      // Connect to the database...
      //
      database.connect();

      // clear the cache
      DBCache.getInstance().clear( databaseMeta.getName() );

      for ( int i = 0; i < tableNames.length; i++ ) {
        SchemaTable schemaTable = tableNames[i];

        // Import the specified tables and turn them into PhysicalTable
        // objects...
        //
        SqlPhysicalTable physicalTable =
            PhysicalTableImporter.importTableDefinition( database, schemaTable.getSchemaName(), schemaTable
            .getTableName(), locale, importStrategy );
        physicalModel.addPhysicalTable( physicalTable );

        // At the same time, we will create a business table and add that to the
        // business model...
        //
        LogicalTable businessTable = createBusinessTable( physicalTable, locale );
        logicalModel.addLogicalTable( businessTable );
      }
    } catch ( Exception e ) {
      // For the unexpected stuff, just throw the exception upstairs.
      //
      throw new PentahoMetadataException( e );
    } finally {
      // Make sure to close the connection
      //
      database.disconnect();
    }

    return domain;
  }

  Database database() {
    return new Database( databaseMeta );
  }

  private LogicalColumn findBusinessColumn( LogicalTable logicalTable, String columnName ) {
    for ( LogicalColumn logicalColumn : logicalTable.getLogicalColumns() ) {
      if ( columnName.equals( ( (SqlPhysicalColumn) logicalColumn.getPhysicalColumn() ).getTargetColumn() ) ) {
        return logicalColumn;
      }
    }
    return null;
  }

  private LogicalTable createBusinessTable( SqlPhysicalTable physicalTable, String locale ) {

    // Create a business table with a new ID and localized name
    //
    LogicalTable businessTable = new LogicalTable( null, physicalTable );

    // Try to set the name of the business table to something nice (beautify)
    //
    String tableName = PhysicalTableImporter.beautifyName( physicalTable.getTargetTable() );
    businessTable.setName( new LocalizedString( locale, tableName ) );

    businessTable.setId( Util.proposeSqlBasedLogicalTableId( locale, businessTable, physicalTable ) );

    // Add columns to this by copying the physical columns to the business
    // columns...
    //
    for ( IPhysicalColumn physicalColumn : physicalTable.getPhysicalColumns() ) {

      LogicalColumn businessColumn = new LogicalColumn();
      businessColumn.setPhysicalColumn( physicalColumn );
      businessColumn.setLogicalTable( businessTable );

      // We're done, add the business column.
      //
      // Propose a new ID
      businessColumn.setId( Util.proposeSqlBasedLogicalColumnId( locale, businessTable,
          (SqlPhysicalColumn) physicalColumn ) );
      businessTable.addLogicalColumn( businessColumn );
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
