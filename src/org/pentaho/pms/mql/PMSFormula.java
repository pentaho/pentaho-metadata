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
package org.pentaho.pms.mql;

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
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.dialect.FormulaTraversalInterface;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLFunctionGeneratorInterface;
import org.pentaho.pms.mql.dialect.SQLOperatorGeneratorInterface;
import org.pentaho.pms.schema.BusinessCategory;
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
public class PMSFormula implements FormulaTraversalInterface {
  
  private static final Log logger = LogFactory.getLog(PMSFormula.class);
  
  /** 
   * if a formula is provided with a business table, 
   * the fields in the formula without a explicit table mentioned 
   * will be mapped to this table.
   */
  private List<BusinessTable> tables;
  
  /** the model in which the formula will resolve business tables and columns */
  private BusinessModel model = null;
  
  /** reference to kettle's database metadata object for converting to native SQL */
  private DatabaseMeta databaseMeta = null;
  
  /** libFormula formula object reference */
  private Formula formulaObject = null;
  
  /** cache of business columns for lookup during SQL generation */
  private Map<String,BusinessColumn> businessColumnMap = new HashMap<String,BusinessColumn>();
  
  /** table alias map **/
  private Map<BusinessTable, String> tableAliases;
  
  /** list of business columns, accessible by other classes */
  private List<BusinessColumn> businessColumnList = new ArrayList<BusinessColumn>();
  
  /** reference to formulaContext singleton */
  private PMSFormulaContext formulaContext = PMSFormulaContext.getInstance();
  private SQLDialectInterface sqlDialect = null;
  
  private boolean isValidated = false;
  
  private boolean allowAggregateFunctions = false;
  
  private boolean hasAggregateFunction = false;
  
  /** the string to parse */
  private String formulaString;
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public PMSFormula(BusinessModel model, DatabaseMeta databaseMeta, String formulaString, Map<BusinessTable, String> tableAliases) throws PentahoMetadataException {
    
    this.model = model;
    this.formulaString = formulaString;
    this.databaseMeta = databaseMeta;
    this.tableAliases = tableAliases;
    this.tables = new ArrayList<BusinessTable>();
    
    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED")); //$NON-NLS-1$
    }
    
    if (databaseMeta == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0002_NO_DATABASE_META_PROVIDED")); //$NON-NLS-1$
    }
    
    this.sqlDialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    
    if (sqlDialect == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0018_DATABASE_DIALECT_NOT_FOUND", databaseMeta.getDatabaseTypeDesc())); //$NON-NLS-1$
    }
    
    if (formulaString == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0003_NO_FORMULA_STRING_PROVIDED")); //$NON-NLS-1$
    }
  }
  
  /**
   * constructor, currently used for testing
   * 
   * @param model business model for business column lookup
   * @param formulaString formula string
   * @throws PentahoMetadataException throws an exception if we're missing anything important
   */
  public PMSFormula(BusinessModel model, BusinessTable table, DatabaseMeta databaseMeta, String formulaString, Map<BusinessTable, String> tableAliases) throws PentahoMetadataException {
    
    this.model = model;
    this.formulaString = formulaString;
    this.databaseMeta = databaseMeta;
    this.tableAliases = tableAliases;
    this.tables = new ArrayList<BusinessTable>();
    this.tables.add(table);
    
    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED")); //$NON-NLS-1$
    }
    
    if (databaseMeta == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0002_NO_DATABASE_META_PROVIDED")); //$NON-NLS-1$
    }
    
    if (table == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0004_NO_BUSINESS_TABLE_PROVIDED")); //$NON-NLS-1$
    }
    
    this.sqlDialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    
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
  public PMSFormula(BusinessModel model, String formulaString, Map<BusinessTable, String> tableAliases) throws PentahoMetadataException {
    
    this.model = model;
    this.formulaString = formulaString;
    this.tableAliases = tableAliases;
    this.tables = new ArrayList<BusinessTable>();
    
    if (model == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED")); //$NON-NLS-1$
    }
    
    if (model.nrBusinessTables() > 0) {
      this.databaseMeta = model.getBusinessTable(0).getPhysicalTable().getDatabaseMeta();
    }
    
    if (databaseMeta == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0002_NO_DATABASE_META_PROVIDED")); //$NON-NLS-1$
    }
    
    this.sqlDialect = SQLDialectFactory.getSQLDialect(databaseMeta);
    
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
  public PMSFormula(BusinessModel model, BusinessTable table, String formulaString, Map<BusinessTable, String> tableAliases) throws PentahoMetadataException {
    
    this(model, formulaString, tableAliases);
    
    this.tables.add(table);
    
    if (table == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0004_NO_BUSINESS_TABLE_PROVIDED")); //$NON-NLS-1$
    }
  }

  public void setTableAliases(Map<BusinessTable, String> tableAliases) {
    this.tableAliases = tableAliases;
  }
  
  protected DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }
  
  public List<BusinessTable> getBusinessTables() {
    return tables;
  }
  
  protected BusinessModel getBusinessModel() {
    return model;
  }
  
  protected Map getBusinessColumnMap() {
    return businessColumnMap;
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
  protected void addField(String fieldName) throws PentahoMetadataException {
    
    if (fieldName == null) {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0008_FIELDNAME_NULL", formulaString)); //$NON-NLS-1$
    }
    
    // we need to validate that "fieldName" actually maps to a field!
    if (!businessColumnMap.containsKey(fieldName)) {
      
      // check if this is a "physicalcolumn" or a "<businesstable>.<businesscolumn>"
      if (fieldName.indexOf(".") < 0) { //$NON-NLS-1$
        
        // expecting <PHYSICAL COLUMN>
        
        if (tables == null) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0009_FIELDNAME_ERROR_NO_BUSINESS_TABLE", fieldName)); //$NON-NLS-1$
        }
        
        // note, this column name is the "physical column name" vs. the "business column name"
        // look through all the business columns and verify the column name matches an existing
        // business column.
        for (BusinessTable businessTable : tables) {
	        for (BusinessColumn businessColumn : businessTable.getBusinessColumns()) {
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
        }
        
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, fieldName, getBusinessTableIDs().toString()));//$NON-NLS-1$
        
      } else {
      
        // expecting <BUSINESS TABLE ID>.<BUSINESS COLUMN ID>
        String tblcol[] = fieldName.split("\\."); //$NON-NLS-1$
        if (tblcol.length != 2) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0011_INVALID_FIELDNAME",fieldName)); //$NON-NLS-1$
        }
        
        // first lookup the business table that the column belongs to.
        // finally check to see if the column exists in its parent, if no column is found, throw an exception.
        BusinessColumn column = null;
        BusinessTable businessTable = null;
        for (BusinessTable table : tables) {
	        if (table.getId().equalsIgnoreCase(tblcol[0])) {
	        	// This is the table involved...
	        	businessTable = table;
	        	break;
	        }
        }

        if (businessTable!= null) {
          // Find the column in that table...
          column = businessTable.findBusinessColumn(tblcol[1]);
          if (column == null) {
            throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0019_FIELDNAME_ERROR_CAT_COLUMN_NOT_FOUND", fieldName, tblcol[0], tblcol[1])); //$NON-NLS-1$
          }

        } else {
          
          // Look up the business table by ID
          // 
          BusinessTable bizTable = model.findBusinessTable(tblcol[0]);
          if (bizTable == null) {
        	  
        	// OK, now we try to look for the business category if someone was actually stupid enough to do that...
        	//
        	BusinessCategory businessCategory = model.getRootCategory().findBusinessCategory(tblcol[0]);
        	if (businessCategory==null) {
        		throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0012_FIELDNAME_ERROR_PARENT_NOT_FOUND", fieldName, tblcol[0])); //$NON-NLS-1$
        	}
        	
        	// What do you know, it worked.
        	// Now look up the column.
        	column = businessCategory.findBusinessColumn(tblcol[1]);
        	if (column==null) {
	            throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, tblcol[1], tblcol[0])); //$NON-NLS-1$
        	}
          }
          else {
	          column = bizTable.findBusinessColumn(tblcol[1]);
	          if (column == null) {
	            throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, tblcol[1], tblcol[0])); //$NON-NLS-1$
	          }
	          // This means that the business table is not in the list of used tables...
	          // Add it here...
	          // 
	          tables.add(bizTable);
          }
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
        if (!allowAggregateFunctions && tables == null && sqlDialect.isAggregateFunction(f.getFunctionName())) {
          throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0013_AGGREGATE_USAGE_ERROR", f.getFunctionName(), formulaString)); //$NON-NLS-1$
        }

        if (sqlDialect.isAggregateFunction(f.getFunctionName())) {
          hasAggregateFunction = true;
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
        throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0021_OPERATOR_NOT_SUPPORTED", val.toString())); //$NON-NLS-1$
      }
    } else {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString())); //$NON-NLS-1$
    }
  }
  
  /**
   * Determines whether or not child val needs to be wrapped with parens.
   * The determining factor is if both the current object and the parent are sql infix operators
   * @param parent object
   * @param val current object
   * @return true if parens are required
   */
  public boolean requiresParens(Object parent, Object val) {
    // first see if parent may required children parens
    boolean parentMatch = false;
    if (parent instanceof Term) {
      parentMatch = true;
    } else if (parent instanceof FormulaFunction) {
      FormulaFunction parentFunction = (FormulaFunction)parent;
      SQLFunctionGeneratorInterface parentGen = sqlDialect.getFunctionSQLGenerator(parentFunction.getFunctionName());
      parentMatch = (parentGen.getType() == SQLFunctionGeneratorInterface.INLINE_FUNCTION);
    }
    if (!parentMatch) {
      return false;
    }
    // second see if child needs parens
    if (val instanceof InfixOperator) {
      return true;
    } else if (val instanceof FormulaFunction) {
      FormulaFunction f = (FormulaFunction)val;
      SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator(f.getFunctionName());
      return (gen.getType() == SQLFunctionGeneratorInterface.INLINE_FUNCTION);
    } else {
      return false;
    }
  }
  
  /**
   * Recursive function that executes any preprocessing and generates the correct SQL
   * 
   * @param val the root of the formula object model
   * @param sb the string buffer to append the SQL to 
   * @param locale the current locale
   */
  public void generateSQL(Object parent, Object val, StringBuffer sb, String locale) throws PentahoMetadataException {
    if (val instanceof Term) {
      Term t = (Term)val;
      // parens are required if both parent and current are sql infix
      boolean addParens = (t.getOptimizedOperators().length > 1 || requiresParens(parent, t.getOptimizedOperators()[0]));
      if (addParens) {
        sb.append("("); //$NON-NLS-1$
      }
      generateSQL(t, t.getOptimizedHeadValue(), sb, locale);
      for (int i = 0; i < t.getOptimizedOperators().length; i++) {
        generateSQL(t, t.getOptimizedOperators()[i], sb, locale);
        generateSQL(t, t.getOptimizedOperands()[i], sb, locale);
      }
      if (addParens) {
        sb.append(")"); //$NON-NLS-1$
      }
    } else if (val instanceof ContextLookup) {
      ContextLookup l = (ContextLookup)val;
      renderContextLookup(sb, l.getName(), locale);
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
      // may need to be wrapped
      boolean addParens = requiresParens(parent, f);
      if (addParens) {
        sb.append("("); //$NON-NLS-1$
      }
      gen.generateFunctionSQL(this, sb, locale, f);
      if (addParens) {
        sb.append(")"); //$NON-NLS-1$
      }
    } else if (val instanceof InfixOperator) {
      if ( sqlDialect.isSupportedInfixOperator(val.toString())) {
        SQLOperatorGeneratorInterface gen = sqlDialect.getInfixOperatorSQLGenerator(val.toString());
        sb.append(" " + gen.getOperatorSQL() + " "); //$NON-NLS-1$ //$NON-NLS-2$
      }
    } else {
      throw new PentahoMetadataException(Messages.getErrorString("PMSFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString())); //$NON-NLS-1$
    }
  }

  protected void renderContextLookup(StringBuffer sb, String contextName, String locale) {
    BusinessColumn column = (BusinessColumn)businessColumnMap.get(contextName);
    if (column == null) {
      // we have a physical column function, we need to evaluate it
      // in a special way due to aggregations and such
      
      String tableColumn = ""; //$NON-NLS-1$
      sb.append(" "); //$NON-NLS-1$
      
      BusinessTable businessTable = findBusinessTableForContextName(contextName, locale);
      if (businessTable!=null) {
      
        // use a table alias if available
      
        String tableAlias = null;
        if (tableAliases != null) {
          tableAlias = tableAliases.get(businessTable);
        } else {
          tableAlias = businessTable.getId();
        }
	      sb.append(databaseMeta.quoteField(tableAlias));
	      sb.append("."); //$NON-NLS-1$
      }
      sb.append(databaseMeta.quoteField(contextName));
      sb.append(" "); //$NON-NLS-1$
      
    } else {
      // render the column sql
      sb.append(" "); //$NON-NLS-1$
      SQLAndTables sqlAndTables = SQLGenerator.getBusinessColumnSQL(model, column, tableAliases, databaseMeta, locale);
      sb.append(sqlAndTables.getSql());
      sb.append(" "); //$NON-NLS-1$
      
      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for (BusinessTable businessTable : sqlAndTables.getUsedTables()) {
    	  if (!tables.contains(businessTable)) tables.add(businessTable);
      }
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
    generateSQL(null, formulaObject.getRootReference(), sb, locale);
    return sb.toString();
  }
  
  /** 
   * retrieve the list of business columns
   * 
   * @return list of business columns referenced in the formula
   */
  public List<BusinessColumn> getBusinessColumns() {
    return businessColumnList;
  }
  

  /**
   * @return the IDs of the used business tables
   */
  public String[] getBusinessTableIDs() {
	  String[] names = new String[tables.size()];
	  for (int i=0;i<tables.size();i++) {
		  names[i] = tables.get(i).getId();
	  }
	  return names;
  }

  
  /**
   * returns true if pms formula contains agg functions.
   * run parseAndValidate() before running this method
   * 
   * @return hasAggregateFunction
   */
  public boolean hasAggregateFunction() {
    return hasAggregateFunction;
  }
  
  /**
   * allows overriding of default behavior, which doesn't allow
   * aggregate functions to be used.
   * 
   * @param allowAggregateFunctions
   */
  public void setAllowAggregateFunctions(boolean allowAggregateFunctions) {
    this.allowAggregateFunctions = allowAggregateFunctions;
  }
  
  /**
   * Find the business table associated with the context name.<br>
   * We look for it in the tables list by looking at the column IDs, the column display names, and by looking at the physical column formula<br>
   * @param contextName the context name
   * @param locale the locale to look in
   * @return the business table or null if nothing was found.
   */
  protected BusinessTable findBusinessTableForContextName(String contextName, String locale) {
      BusinessTable businessTable = null;
      for (BusinessTable table : getBusinessTables()) {
    	  // Search by column ID
    	  //
    	  BusinessColumn c = table.findBusinessColumn(contextName);
    	  // Search by column name
    	  //
    	  if (c==null) {
    		  c = table.findBusinessColumn(locale, contextName);
    	  }
    	  // Search by physical column name / formula...
    	  //
    	  for (BusinessColumn col : table.getBusinessColumns()) {
    		  if (col.getFormula()!=null && col.getFormula().equals(contextName)) {
    			  c=col;
    			  break;
    		  }
    	  }
    	  
    	  // If we found a valid column, we found the business table...
    	  //
    	  if (c!=null) businessTable = c.getBusinessTable();
      }
      return businessTable;
  }

}
