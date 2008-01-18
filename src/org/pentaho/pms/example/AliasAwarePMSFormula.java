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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.PMSFormula;
import org.pentaho.pms.mql.SQLGenerator;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;

import be.ibridge.kettle.core.database.DatabaseMeta;

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
  
  private List<AdvancedMQLQuery.AliasedSelection> selections;
  private String aliasName;
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, DatabaseMeta databaseMeta, String formulaString, List<AdvancedMQLQuery.AliasedSelection> selections) throws PentahoMetadataException {
    super(model, databaseMeta, formulaString);
    this.selections = selections;
  }
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, BusinessTable table, DatabaseMeta databaseMeta, String formulaString, String aliasName) throws PentahoMetadataException {
    super(model, table, databaseMeta, formulaString);    
    this.aliasName = aliasName;
  }
  
//  
//  
//  /**
//   * constructor
//   * 
//   * @param model business model for business column lookup
//   * @param formulaString formula string
//   * @throws PentahoMetadataException throws an exception if we're missing anything important
//   */
//  public AliasAwarePMSFormula(BusinessModel model, String formulaString) throws PentahoMetadataException {
//    super(model, formulaString);
//  }
  
  /**
   * constructor which also takes a specific business table for resolving fields
   * 
   * @param model business model for business column lookup
   * @param table business table for resolving fields
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public AliasAwarePMSFormula(BusinessModel model, BusinessTable table, String formulaString, String aliasName) throws PentahoMetadataException {
    super(model, table, formulaString);
    this.aliasName = aliasName;
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
    boolean aliasFound = false;
    for (AdvancedMQLQuery.AliasedSelection selection : selections) {
      //selection.getAlias()
    }
//    if (fieldName != null && fieldName.indexOf("\\.")) {
//      
//    }
    if (!aliasFound) {
      super.addField(fieldName);
    }
  }

  
  
  /**
   * need to make this context lookup alias aware
   */
  protected void renderContextLookup(StringBuffer sb, String contextName, String locale) {
    BusinessColumn column = (BusinessColumn)getBusinessColumnMap().get(contextName);
    if (column == null) {
      // we have a physical column function, we need to evaluate it
      // in a special way due to aggregations and such
      
      String tableColumn = ""; //$NON-NLS-1$
      sb.append(" "); //$NON-NLS-1$
      
      // Todo: WPG: is this correct?  shouldn't we be getting an alias for the table vs. it's display name?
      sb.append(getDatabaseMeta().quoteField(getBusinessTable().getDisplayName(locale)));
      sb.append("."); //$NON-NLS-1$
      sb.append(getDatabaseMeta().quoteField(contextName));
      sb.append(" "); //$NON-NLS-1$
      
    } else {
      // render the column sql
      sb.append(" "); //$NON-NLS-1$
      sb.append(SQLGenerator.getBusinessColumnSQL(getBusinessModel(), column, getDatabaseMeta(), locale));
      sb.append(" "); //$NON-NLS-1$
    }
  }
}
