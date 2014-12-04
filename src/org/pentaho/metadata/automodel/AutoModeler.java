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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.automodel.PhysicalTableImporter.RowMetaStrategy;
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

import static org.pentaho.metadata.automodel.PhysicalTableImporter.defaultRowMetaStrategy;

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
  }

  public Domain generateDomain() throws PentahoMetadataException {
    return generateDomain( defaultRowMetaStrategy );
  }

  public Domain generateDomain( final RowMetaStrategy rowMetaStrategy ) throws PentahoMetadataException {
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
            .getTableName(), locale, rowMetaStrategy );
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
