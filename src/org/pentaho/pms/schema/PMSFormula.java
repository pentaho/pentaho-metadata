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
package org.pentaho.pms.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.formula.EvaluationException;
import org.jfree.formula.Formula;
import org.jfree.formula.lvalues.ContextLookup;
import org.jfree.formula.lvalues.FormulaFunction;
import org.jfree.formula.lvalues.LValue;
import org.jfree.formula.lvalues.StaticValue;
import org.jfree.formula.lvalues.Term;
import org.jfree.formula.operators.InfixOperator;
import org.jfree.formula.parser.ParseException;
import org.jfree.formula.typing.coretypes.TextType;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.dialect.FormulaTraversalInterface;
import org.pentaho.pms.schema.dialect.SQLDialectInterface;
import org.pentaho.pms.schema.dialect.SQLFunctionGeneratorInterface;
import org.pentaho.pms.schema.dialect.SQLOperatorGeneratorInterface;

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
 * @see WhereCondition
 * @see BusinessColumn
 */
public class PMSFormula implements FormulaTraversalInterface {
  
  private static final Log logger = LogFactory.getLog(PMSFormula.class);
  
  /** 
   * if a formula is provided with a business table, 
   * the fields in the formula without a explicit table mentioned 
   * will be mapped to this table.
   */
  private BusinessTable table = null;
  
  /** the model in which the formula will resolve business tables and columns */
  private BusinessModel model = null;
  
  /** reference to kettle's database metadata object for converting to native SQL */
  private DatabaseMeta databaseMeta = null;
  
  /** libFormula formula object reference */
  private Formula formulaObject = null;
  
  /** cache of business columns for lookup during SQL generation */
  private Map businessColumnMap = new HashMap();
  
  /** list of business columns, accessible by other classes */
  private List businessColumnList = new ArrayList();
  
  /** reference to formulaContext singleton */
  private PMSFormulaContext formulaContext = PMSFormulaContext.getInstance();
  private SQLDialectInterface sqlDialect = null;
  
  private boolean isValidated = false;
  
  /** the string to parse */
  private String formulaString;
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public PMSFormula(BusinessModel model, DatabaseMeta databaseMeta, String formulaString) throws PentahoMetadataException {
    
    this.model = model;
    this.formulaString = formulaString;
    this.databaseMeta = databaseMeta;
    
    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED")); //$NON-NLS-1$
    }
    
    if (databaseMeta == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0002_NO_DATABASE_META_PROVIDED")); //$NON-NLS-1$
    }
    
    this.sqlDialect = formulaContext.getSQLDialect(databaseMeta);
    
    if (sqlDialect == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0017_DATABASE_DIALECT_NOT_FOUND", databaseMeta.getDatabaseTypeDesc())); //$NON-NLS-1$
    }
    
    if (formulaString == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0003_NO_FORMULA_STRING_PROVIDED")); //$NON-NLS-1$
    }
  }
  
  
  /**
   * constructor
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public PMSFormula(BusinessModel model, String formulaString) throws PentahoMetadataException {
    
    this.model = model;
    this.formulaString = formulaString;
    
    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED")); //$NON-NLS-1$
    }
    
    if (model.nrBusinessTables() > 0) {
      this.databaseMeta = model.getBusinessTable(0).getPhysicalTable().getDatabaseMeta();
    }
    
    if (databaseMeta == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0002_NO_DATABASE_META_PROVIDED")); //$NON-NLS-1$
    }
    
    this.sqlDialect = formulaContext.getSQLDialect(databaseMeta);
    
    if (sqlDialect == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0017_DATABASE_DIALECT_NOT_FOUND", databaseMeta.getDatabaseTypeDesc())); //$NON-NLS-1$
    }
    
    if (formulaString == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0003_NO_FORMULA_STRING_PROVIDED")); //$NON-NLS-1$
    }
  }
  
  /**
   * constructor which also takes a specific business table for resolving fields
   * 
   * @param model business model for business column lookup
   * @param table business table for resolving fields
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public PMSFormula(BusinessModel model, BusinessTable table, String formulaString) throws PentahoMetadataException {
    
    this(model, formulaString);
    
    this.table = table;
    
    if (table == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0004_NO_BUSINESS_TABLE_PROVIDED")); //$NON-NLS-1$
    }
  }

  /**
   * parse and validate formula, including resolving all fields
   * 
   * @throws PentahoMetadataException
   */
  public void parseAndValidate() throws PentahoMetadataException {
    if (!isValidated) {
      // throws an error if failed to parse and validate condition
      try {
        formulaObject = new Formula(formulaString);
        formulaObject.initialize(formulaContext);
        LValue val = formulaObject.getRootReference();
        validateAndResolveObjectModel(val);
        isValidated = true;
      } catch (ParseException e) {
        logger.error("an exception occurred", e); //$NON-NLS-1$
        // is it possible to provide more detail in this exception to the user?
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0005_FAILED_TO_PARSE_FORMULA", formulaString)); //$NON-NLS-1$
      } catch (EvaluationException e) {
        logger.error("an exception occurred", e); //$NON-NLS-1$
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0006_FAILED_TO_EVALUATE_FORMULA", formulaString)); //$NON-NLS-1$
      } catch (Throwable e) {
        if (e instanceof PentahoMetadataException) {
          throw (PentahoMetadataException)e;         
        } else {
          logger.error("an exception occurred", e); //$NON-NLS-1$
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0007_UNKNOWN_ERROR", formulaString)); //$NON-NLS-1$
        }
      }
      // this should populate the fields object
    }
  }

  /**
   * We support unqualified business columns if a business table is provided.
   * This allows physical columns to define a formula which eventually gets
   * used by business table columns.
   * 
   * @param fieldName name of field, either "<BUSINESS TABLE ID>.<BUSINESS COLUMN ID>" or "<PHYSICAL COLUMN>"
   * 
   * @throws PentahoMetadataException if field cannot be resolved
   */
  private void addField(String fieldName) throws PentahoMetadataException {
    
    if (fieldName == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0008_FIELDNAME_NULL", formulaString)); //$NON-NLS-1$
    }
    
    // we need to validate that "fieldName" actually maps to a field!
    if (!businessColumnMap.containsKey(fieldName)) {
      
      // check if this is a "physicalcolumn" or a "<businesstable>.<businesscolumn>"
      if (fieldName.indexOf(".") < 0) { //$NON-NLS-1$
        
        // expecting <PHYSICAL COLUMN>
        
        if (table == null) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0009_FIELDNAME_ERROR_NO_BUSINESS_TABLE", fieldName)); //$NON-NLS-1$
        }
        
        // note, this column name is the "physical column name" vs. the "business column name"
        // look through all the business columns and verify the column name matches an existing
        // business column.
        for (int i=0;i<table.nrBusinessColumns();i++) {
            BusinessColumn businessColumn = table.getBusinessColumn(i);
            
            // this matches how business column renders it's sql, i'm not a big fan though.
            // instead i would prefer this:
            //   if (businessColumn.getPhysicalColumn().getId().equals(fieldName)) {
            //     break;  
            //   }
            
            if (!businessColumn.isExact() && fieldName.equals(businessColumn.getFormula())) {
              // we've found it, but we don't do anything due to aggregation issues later
              return;
            }
        }
        
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, fieldName ,table.getId()));//$NON-NLS-1$
        
      } else {
      
        // expecting <BUSINESS TABLE ID>.<BUSINESS COLUMN ID>
        String tblcol[] = fieldName.split("\\."); //$NON-NLS-1$
        if (tblcol.length != 2) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0011_INVALID_FIELDNAME",fieldName)); //$NON-NLS-1$
        }
        
        BusinessTable bizTable = model.findBusinessTable(tblcol[0]);
        if (bizTable == null) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0012_FIELDNAME_ERROR_TABLE_NOT_FOUND", fieldName, tblcol[0])); //$NON-NLS-1$
        }
        BusinessColumn column = bizTable.findBusinessColumn(tblcol[1]);
        if (column == null) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, tblcol[1], tblcol[0])); //$NON-NLS-1$
        }
        businessColumnMap.put(fieldName, column);
        businessColumnList.add(column);
        
      }
    }
  }
  
  /*
   *  there should be 3 passes over the formula object model:
   * 
   * 1) a verification pass, which checks the functions for validity and resolves the business columns.
   *    see validateAndResolveObjectModel()
   * 
   * 2) a preprocessing pass, which executes the necessary preprocessing items and generates the SQL
   *    see generateSQL()
   *    
   * Not implemented yet:   
   * 3)  a post processing pass, which executes any necessary post processing items not SQL compatible
   *    this is not implemented yet, it will require some rearchitecting of the metadata system
   *    currently all the metadata system provides is raw SQL.  eventually it will act as its own 
   *    PentahoResultSet allowing post processing 
   * 
   */

  /**
   * Recursive function that traverses the formula object model, resolves the business columns, 
   * and validates the functions and operators specified.
   * 
   * 
   * 
   * @param val the root of the formula object model 
   */
  private void validateAndResolveObjectModel(Object val) throws PentahoMetadataException {
    if (val instanceof Term) {
      Term t = (Term)val;
      validateAndResolveObjectModel(t.getOptimizedHeadValue());
      for (int i = 0; i < t.getOptimizedOperators().length; i++) {
        validateAndResolveObjectModel(t.getOptimizedOperators()[i]);
        validateAndResolveObjectModel(t.getOptimizedOperands()[i]);
      }
    } else if (val instanceof ContextLookup) {
      ContextLookup l = (ContextLookup)val;
      addField(l.getName());
    } else if (val instanceof StaticValue) {
      // everything is fine
      return;
    } else if (val instanceof FormulaFunction) {
      
      FormulaFunction f = (FormulaFunction)val;
      if (sqlDialect.isSupportedFunction(f.getFunctionName())) {
        SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator(f.getFunctionName());
        gen.validateFunction(f);
        // note, if aggregator function, we should make sure it is part of the table formula vs. conditional formula
        if (table == null && sqlDialect.isAggregateFunction(f.getFunctionName())) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0013_AGGREGATE_USAGE_ERROR", f.getFunctionName(), formulaString)); //$NON-NLS-1$
        }
        // validate functions parameters
        if (f.getChildValues() != null && f.getChildValues().length > 0) {
          validateAndResolveObjectModel(f.getChildValues()[0]);
          for (int i = 1; i < f.getChildValues().length; i++) {
            validateAndResolveObjectModel(f.getChildValues()[i]);
          }
        }
      } else {
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0014_FUNCTION_NOT_SUPPORTED", f.getFunctionName())); //$NON-NLS-1$
      }
    } else if (val instanceof InfixOperator) {
      if ( sqlDialect.isSupportedInfixOperator(val.toString())) {
        // everything is fine
        return;
      } else {
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0014_OPERATOR_NOT_SUPPORTED", val.toString())); //$NON-NLS-1$
      }
    } else {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString())); //$NON-NLS-1$
    }
  }
  
  /**
   * Recursive function that executes any preprocessing and generates the correct SQL
   * 
   * @param val the root of the formula object model
   * @param sb the string buffer to append the SQL to 
   * @param locale the current locale
   */
  public void generateSQL(Object val, StringBuffer sb, String locale) throws PentahoMetadataException {
    if (val instanceof Term) {
      Term t = (Term)val;
      sb.append("("); //$NON-NLS-1$
      generateSQL(t.getOptimizedHeadValue(), sb, locale);
      for (int i = 0; i < t.getOptimizedOperators().length; i++) {
        generateSQL(t.getOptimizedOperators()[i], sb, locale);
        generateSQL(t.getOptimizedOperands()[i], sb, locale);
      }
      sb.append(")"); //$NON-NLS-1$
    } else if (val instanceof ContextLookup) {
      ContextLookup l = (ContextLookup)val;
      BusinessColumn column = (BusinessColumn)businessColumnMap.get(l.getName());
      if (column == null) {
        // we have a physical column function, we need to evaluate it
        // in a special way due to aggregations and such
        
        DatabaseMeta databaseMeta = table.getPhysicalTable().getDatabaseMeta();
        String tableColumn = ""; //$NON-NLS-1$
        sb.append(" "); //$NON-NLS-1$
        
        // Todo: WPG: is this correct?  shouldn't we be getting an alias for the table vs. it's display name?
        sb.append(databaseMeta.quoteField(table.getDisplayName(locale)));
        sb.append("."); //$NON-NLS-1$
        sb.append(databaseMeta.quoteField(l.getName()));
        sb.append(" "); //$NON-NLS-1$
        
      } else {
        // render the column sql
        sb.append(" "); //$NON-NLS-1$
        sb.append(column.getFunctionTableAndColumnForSQL(model, locale));
        sb.append(" "); //$NON-NLS-1$
      }
    } else if (val instanceof StaticValue) {
      StaticValue v = (StaticValue)val;
      
      if (v.getValueType() instanceof TextType) {
        sb.append(sqlDialect.quoteStringLiteral(v.getValue()));
      } else {
        sb.append(v.getValue());
      }
    } else if (val instanceof FormulaFunction) {
      
      FormulaFunction f = (FormulaFunction)val;
      SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator(f.getFunctionName());

      // note that generateFunctionSQL calls back into this function for children params if necessary
      gen.generateFunctionSQL(this, sb, locale, f);

    } else if (val instanceof InfixOperator) {
      if ( sqlDialect.isSupportedInfixOperator(val.toString())) {
        SQLOperatorGeneratorInterface gen = sqlDialect.getInfixOperatorSQLGenerator(val.toString());
        sb.append(" " + gen.getOperatorSQL() + " "); //$NON-NLS-1$ //$NON-NLS-2$
      }
    } else {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString())); //$NON-NLS-1$
    }
  }
  
  /**
   * wrapper for recursive generateSQL method
   * 
   * @param locale locale of user
   * 
   * @return sql string
   */
  public String generateSQL(String locale) throws PentahoMetadataException {
    if (!isValidated) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0017_STATE_ERROR_NOT_VALIDATED")); //$NON-NLS-1$
    }
    StringBuffer sb = new StringBuffer();
    generateSQL(formulaObject.getRootReference(), sb, locale);
    return sb.toString();
  }
  
  /** 
   * retrieve the list of business columns
   * 
   * @return list of business columns referenced in the formula
   */
  public List getBusinessColumns() {
    return businessColumnList;
  }
}
