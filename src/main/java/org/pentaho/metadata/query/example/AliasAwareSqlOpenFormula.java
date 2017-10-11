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
 * Copyright (c) 2009 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.query.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.query.impl.sql.SqlOpenFormula;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.core.exception.PentahoMetadataException;

/**
 * This class manages the two types of formulas which appear in the metadata system. Both of these types support the
 * conversion of open document formula syntax to RDBMS specific SQL.
 * 
 * The first formula type appears as a WhereCondition. WhereConditions may access business columns via the syntax
 * "[<BUSINESS TABLE ID>.<BUSINESS COLUMN ID>]" within the defined formula.
 * 
 * The first formula type may appear in the "formula" property of physical columns if isExact is set to true. These
 * formulas allow for aggregates, and use the syntax "[<PHYSICAL COLUMN NAME>]" to refer to their fields. They may also
 * use the
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 */
public class AliasAwareSqlOpenFormula extends SqlOpenFormula {

  private static final Log logger = LogFactory.getLog( AliasAwareSqlOpenFormula.class );

  private List<Selection> selections;
  private String aliasName;
  private Map<String, String> LogicalTableToAliasMap;
  private Map<String, AliasedSelection> aliasedSelectionMap = new HashMap<String, AliasedSelection>();

  /**
   * constructor, currently used for testing
   * 
   * @param model
   *          business model for business column lookup
   * @param formulaString
   *          formula string
   * @throws PentahoMetadataException
   *           throws an exception if we're missing anything important
   */
  public AliasAwareSqlOpenFormula( LogicalModel model, DatabaseMeta databaseMeta, String formulaString,
      List<Selection> selections, String aliasName ) throws PentahoMetadataException {
    super( model, databaseMeta, formulaString, null, null, false );
    this.selections = selections;
    this.aliasName = aliasName;
  }

  /**
   * constructor, currently used for testing
   * 
   * @param model
   *          business model for business column lookup
   * @param formulaString
   *          formula string
   * @throws PentahoMetadataException
   *           throws an exception if we're missing anything important
   */
  public AliasAwareSqlOpenFormula( LogicalModel model, LogicalTable table, DatabaseMeta databaseMeta,
      String formulaString, String aliasName ) throws PentahoMetadataException {
    super( model, table, databaseMeta, formulaString, null, null, false );
    this.aliasName = aliasName;
  }

  // /**
  // * constructor which also takes a specific business table for resolving fields
  // *
  // * @param model business model for business column lookup
  // * @param table business table for resolving fields
  // * @param formulaString formula string
  // * @throws PentahoMetadataException throws an exception if we're missing anything important
  // */
  // public AliasAwareSqlOpenFormula(LogicalModel model, LogicalTable table, String formulaString, String aliasName)
  // throws PentahoMetadataException {
  // super(model, table, formulaString, null);
  // this.aliasName = aliasName;
  // }

  public void setLogicalTableToAliasMap( Map<String, String> LogicalTableToAlias ) {
    this.LogicalTableToAliasMap = LogicalTableToAlias;
  }

  /**
   * We support unqualified business columns if a business table is provided. This allows physical columns to define a
   * formula which eventually gets used by business table columns.
   * 
   * in addition to supporting the first two options, also support <ALIAS>.<BUSINESS COLUMN ID>
   * 
   * @param fieldName
   *          name of field, either "<BUSINESS TABLE ID>.<BUSINESS COLUMN ID>" or "<PHYSICAL COLUMN>"
   * 
   * @throws PentahoMetadataException
   *           if field cannot be resolved
   */
  protected void addField( String fieldName ) throws PentahoMetadataException {
    // figure out what context we are in,

    // first see if fieldName is an alias
    if ( selections != null && fieldName != null && fieldName.indexOf( "." ) >= 0 ) {
      String[] names = fieldName.split( "\\." );
      for ( Selection selection : selections ) {
        AliasedSelection aliasedSelection = (AliasedSelection) selection;
        if ( aliasedSelection.getAlias() != null && aliasedSelection.getAlias().equals( names[0] ) ) {
          // now search for the business column
          LogicalColumn column = getLogicalModel().findLogicalColumn( names[1] );
          if ( column != null ) {
            // add to aliased selection map.

            // create a new seletion object. bizcol portion of name may not appear
            // in selections, but alias must appear in selections to be a valid entry.

            AliasedSelection sel = new AliasedSelection( null, column, null, aliasedSelection.getAlias() );
            aliasedSelectionMap.put( fieldName, sel );

            // add to the list of business columns which is used for path generation
            getSelections().add( sel );
            return;
          } else {
            throw new PentahoMetadataException( Messages.getErrorString(
                "SqlOpenFormula.ERROR_0011_INVALID_FIELDNAME", fieldName ) ); //$NON-NLS-1$
          }
        }
      }
    }
    super.addField( fieldName );
  }

  /**
   * need to make this context lookup alias aware
   */
  protected void renderContextLookup( StringBuffer sb, String contextName, String locale ) {
    // first see if we are an aliased column
    AliasedSelection sel = aliasedSelectionMap.get( contextName );
    if ( sel != null ) {
      sb.append( " " ); //$NON-NLS-1$
      AdvancedSqlGenerator.SQLAndAliasedTables sqlAndTables =
          AdvancedSqlGenerator.getSelectionSQL( getLogicalModel(), sel, getDatabaseMeta(), locale );
      sb.append( sqlAndTables.getSql() );
      sb.append( " " ); //$NON-NLS-1$

      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for ( AdvancedSqlGenerator.AliasedPathLogicalTable aliasedTable : sqlAndTables.getAliasedLogicalTables() ) {
        if ( !aliasedTables.contains( aliasedTable ) ) {
          aliasedTables.add( aliasedTable );
        }
      }

      return;
    }

    Selection column = (Selection) getSelectionMap().get( contextName );
    if ( column == null ) {

      // we have a physical column function, we need to evaluate it
      // in a special way due to aggregations and such

      String tableColumn = ""; //$NON-NLS-1$
      sb.append( " " ); //$NON-NLS-1$

      // Find the business table related to this contextName.
      // It could be a display name in a certain locale, it could be a column id.
      // Let's find it in the list of tables...
      //
      LogicalTable LogicalTable = findLogicalTableForContextName( contextName, locale );
      if ( LogicalTable != null ) {
        sb.append( getDatabaseMeta().quoteField( LogicalTable.getId() ) );
        sb.append( "." ); //$NON-NLS-1$
      }
      sb.append( getDatabaseMeta().quoteField( contextName ) );
      sb.append( " " ); //$NON-NLS-1$

    } else {
      AliasedSelection selection = null;

      if ( LogicalTableToAliasMap != null ) {
        // render the column sql
        String tmpAliasName = LogicalTableToAliasMap.get( column.getLogicalColumn().getLogicalTable().getId() );
        if ( tmpAliasName == null ) {
          tmpAliasName = aliasName;
        }
        selection = new AliasedSelection( null, column.getLogicalColumn(), null, tmpAliasName );
      } else {
        selection = new AliasedSelection( null, column.getLogicalColumn(), null, aliasName );
      }
      // render the column sql
      sb.append( " " ); //$NON-NLS-1$
      AdvancedSqlGenerator.SQLAndAliasedTables sqlAndTables =
          AdvancedSqlGenerator.getSelectionSQL( getLogicalModel(), selection, getDatabaseMeta(), locale );
      sb.append( sqlAndTables.getSql() );
      sb.append( " " ); //$NON-NLS-1$

      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for ( AdvancedSqlGenerator.AliasedPathLogicalTable aliasedTable : sqlAndTables.getAliasedLogicalTables() ) {
        if ( !aliasedTables.contains( aliasedTable ) ) {
          aliasedTables.add( aliasedTable );
        }
      }
    }
  }

  List<AdvancedSqlGenerator.AliasedPathLogicalTable> aliasedTables =
      new ArrayList<AdvancedSqlGenerator.AliasedPathLogicalTable>();

  public String[] getLogicalTableIDs() {
    throw new UnsupportedOperationException();
  }

  public String[] getTableAliasNames() {
    String[] tables = new String[aliasedTables.size()];
    for ( int i = 0; i < aliasedTables.size(); i++ ) {
      tables[i] = aliasedTables.get( i ).getAlias();
    }
    return tables;
  }

  public List<AdvancedSqlGenerator.AliasedPathLogicalTable> getUsedAliasedTables() {
    return aliasedTables;
  }
}
