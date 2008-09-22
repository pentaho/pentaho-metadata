/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.example.AdvancedMQLQuery.AliasedSelection;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.mql.Selection;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;

/**
 * This class manages the two types of formulas which appear in the metadata system.  Both of 
 * these types support the conversion of open document formula syntax to RDBMS specific SQL. 
 * 
 * The first formula type appears as a WhereCondition.  WhereConditions may access business columns
 * via the syntax "[<BUSINESS TABLE ID>.<BUSINESS COLUMN ID>]" within the defined formula. 
 * 
 * The first formula type may appear in the "formula" property of physical columns if isExact is 
 * set to true.  These formulas allow for aggregates, and use the syntax "[<PHYSICAL COLUMN NAME>]"
 * to refer to their fields.  They may also use the 
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 * 
 * @see org.pentaho.pms.mql.WhereCondition
 * @see BusinessColumn
 */
public class AliasAwarePMSFormula extends PMSFormula {
  
  private static final Log logger = LogFactory.getLog(AliasAwarePMSFormula.class);
  
  private List<Selection> selections;
  private String aliasName;
  private Map<String, String> businessTableToAliasMap; 
  private Map<String, AdvancedMQLQuery.AliasedSelection> aliasedSelectionMap = new HashMap<String, AdvancedMQLQuery.AliasedSelection>();

  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, DatabaseMeta databaseMeta, String formulaString, List<Selection> selections, String aliasName) throws PentahoMetadataException {
    super(model, databaseMeta, formulaString, null);
    this.selections = selections;
    this.aliasName = aliasName;
  }
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, BusinessTable table, DatabaseMeta databaseMeta, String formulaString, String aliasName) throws PentahoMetadataException {
    super(model, table, databaseMeta, formulaString, null);    
    this.aliasName = aliasName;
  }

  /**
   * constructor which also takes a specific business table for resolving fields
   * 
   * @param model business model for business column lookup
   * @param table business table for resolving fields
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, BusinessTable table, String formulaString, String aliasName) throws PentahoMetadataException {
    super(model, table, formulaString, null);
    this.aliasName = aliasName;
  }
  
  public void setBusinessTableToAliasMap(Map<String, String> businessTableToAlias) {
    this.businessTableToAliasMap = businessTableToAlias;
  }
  
  /**
   * We support unqualified business columns if a business table is provided.
   * This allows physical columns to define a formula which eventually gets
   * used by business table columns.
   * 
   * in addition to supporting the first two options, also support <ALIAS>.<BUSINESS COLUMN ID>
   * 
   * @param fieldName name of field, either "<BUSINESS TABLE ID>.<BUSINESS COLUMN ID>" or "<PHYSICAL COLUMN>"
   * 
   * @throws PentahoMetadataException if field cannot be resolved
   */
  protected void addField(String fieldName) throws PentahoMetadataException {
    // figure out what context we are in, 
    
    // first see if fieldName is an alias
    if (selections != null && fieldName != null && fieldName.indexOf(".") >= 0) {
      String names[] = fieldName.split("\\.");
      for (Selection selection : selections) {
        AdvancedMQLQuery.AliasedSelection aliasedSelection = (AdvancedMQLQuery.AliasedSelection)selection;
        if (aliasedSelection.getAlias() != null && aliasedSelection.getAlias().equals(names[0])) {
          // now search for the business column
          BusinessColumn column = getBusinessModel().findBusinessColumn(names[1]);
          if (column != null) {
            // add to aliased selection map.
            
            // create a new seletion object.  bizcol portion of name may not appear
            // in selections, but alias must appear in selections to be a valid entry.
            
            AdvancedMQLQuery.AliasedSelection sel = new AdvancedMQLQuery.AliasedSelection(column, aliasedSelection.getAlias());
            aliasedSelectionMap.put(fieldName, sel);
            
            // add to the list of business columns which is used for path generation
            getBusinessColumns().add(column);
            return;
          } else {
            throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0011_INVALID_FIELDNAME",fieldName)); //$NON-NLS-1$
          }
        }
      }
    }
    super.addField(fieldName);
  }

  /**
   * need to make this context lookup alias aware
   */
  protected void renderContextLookup(StringBuffer sb, String contextName, String locale) {
    // first see if we are an aliased column
    AdvancedMQLQuery.AliasedSelection sel = aliasedSelectionMap.get(contextName);
    if (sel != null) {
      sb.append(" "); //$NON-NLS-1$
      AdvancedSQLGenerator.SQLAndAliasedTables sqlAndTables = AdvancedSQLGenerator.getSelectionSQL(getBusinessModel(), sel, getDatabaseMeta(), locale);
      sb.append(sqlAndTables.getSql());
      sb.append(" "); //$NON-NLS-1$
      
      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for (AdvancedSQLGenerator.AliasedPathBusinessTable aliasedTable : sqlAndTables.getAliasedBusinessTables()) {
        if (!aliasedTables.contains(aliasedTable)) {
          aliasedTables.add(aliasedTable);
        }
      }
      
      return;
    }

    BusinessColumn column = (BusinessColumn)getBusinessColumnMap().get(contextName);
    if (column == null) {
        
      // we have a physical column function, we need to evaluate it
      // in a special way due to aggregations and such
      
      String tableColumn = ""; //$NON-NLS-1$
      sb.append(" "); //$NON-NLS-1$
      
      // Find the business table related to this contextName.
      // It could be a display name in a certain locale, it could be a column id.
      // Let's find it in the list of tables...
      //
      BusinessTable businessTable = findBusinessTableForContextName(contextName, locale);
      if (businessTable!=null) {
	      sb.append(getDatabaseMeta().quoteField(businessTable.getId()));
	      sb.append("."); //$NON-NLS-1$
      }
      sb.append(getDatabaseMeta().quoteField(contextName));
      sb.append(" "); //$NON-NLS-1$
      
    } else {
      AliasedSelection selection = null;
    
      if (businessTableToAliasMap != null) {
      // render the column sql
        String tmpAliasName = businessTableToAliasMap.get(column.getBusinessTable().getId());
        if (tmpAliasName == null) {
          tmpAliasName = aliasName;
        }
        selection = new AliasedSelection(column, tmpAliasName);
      } else {
        selection = new AliasedSelection(column, aliasName);
      }
      // render the column sql
      sb.append(" "); //$NON-NLS-1$
      AdvancedSQLGenerator.SQLAndAliasedTables sqlAndTables = AdvancedSQLGenerator.getSelectionSQL(getBusinessModel(), selection, getDatabaseMeta(), locale);
      sb.append(sqlAndTables.getSql());
      sb.append(" "); //$NON-NLS-1$
      
      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for (AdvancedSQLGenerator.AliasedPathBusinessTable aliasedTable : sqlAndTables.getAliasedBusinessTables()) {
        if (!aliasedTables.contains(aliasedTable)) {
          aliasedTables.add(aliasedTable);
        }
      }
    }
  }
  
  List<AdvancedSQLGenerator.AliasedPathBusinessTable> aliasedTables = new ArrayList<AdvancedSQLGenerator.AliasedPathBusinessTable>();
  
  public String[] getBusinessTableIDs() {
    throw new UnsupportedOperationException();
  }
  
  public String[] getTableAliasNames() {
    String tables[] = new String[aliasedTables.size()];
    for (int i = 0; i < aliasedTables.size(); i++) {
      tables[i] = aliasedTables.get(i).getAlias();
    }
    return tables;
  }
  
  public List<AdvancedSQLGenerator.AliasedPathBusinessTable> getUsedAliasedTables() {
    return aliasedTables;
  }
}
