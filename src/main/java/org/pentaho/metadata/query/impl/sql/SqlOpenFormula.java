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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.metadata.query.impl.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetColumnType;
import org.pentaho.metadata.query.model.Selection;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.mql.dialect.FormulaTraversalInterface;
import org.pentaho.pms.mql.dialect.SQLDialectFactory;
import org.pentaho.pms.mql.dialect.SQLDialectInterface;
import org.pentaho.pms.mql.dialect.SQLFunctionGeneratorInterface;
import org.pentaho.pms.mql.dialect.SQLOperatorGeneratorInterface;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.PrefixTerm;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.Term;
import org.pentaho.reporting.libraries.formula.operators.InfixOperator;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This class manages the two types of formulas which appear in the metadata system. Both of these types support the
 * conversion of open document formula syntax to RDBMS specific SQL.
 * 
 * The first formula type appears as a WhereCondition. WhereConditions may access logical columns via the syntax
 * "[<LOGICAL TABLE ID>.<LOGICAL COLUMN ID>]" within the defined formula.
 * 
 * The first formula type may appear in the "formula" property of physical columns if isExact is set to true. These
 * formulas allow for aggregates, and use the syntax "[<PHYSICAL COLUMN NAME>]" to refer to their fields. They may also
 * use the
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 * 
 * @see SqlGenerator
 */
public class SqlOpenFormula implements FormulaTraversalInterface {

  private static final String PARAM = "param:"; //$NON-NLS-1$

  private static final Log logger = LogFactory.getLog( SqlOpenFormula.class );

  /**
   * if a formula is provided with a logical table, the fields in the formula without a explicit table mentioned will be
   * mapped to this table.
   */
  private List<LogicalTable> tables;

  /** the model in which the formula will resolve logical tables and columns */
  private LogicalModel model = null;

  /** reference to kettle's database metadata object for converting to native SQL */
  private DatabaseMeta databaseMeta = null;

  /** libFormula formula object reference */
  private Formula formulaObject = null;

  /** cache of selections for lookup during SQL generation */
  private Map<String, Selection> selectionMap = new HashMap<String, Selection>();

  /** table alias map **/
  private Map<LogicalTable, String> tableAliases;

  /** list of selections, accessible by other classes */
  private List<Selection> selections = new ArrayList<Selection>();

  /** reference to formulaContext singleton */
  private SqlOpenFormulaContext formulaContext = SqlOpenFormulaContext.getInstance();
  private SQLDialectInterface sqlDialect = null;

  private boolean isValidated = false;

  private boolean allowAggregateFunctions = false;

  private boolean hasAggregateFunction = false;

  private Map<String, Object> parameters;

  /** the string to parse */
  private String formulaString;

  private boolean genAsPreparedStatement;

  /**
   * constructor, used for constraints, security, and complex joins
   * 
   * @param model
   *          logical model for logical column lookup
   * @param formulaString
   *          formula string
   * @throws PentahoMetadataException
   *           throws an exception if we're missing anything important
   */
  public SqlOpenFormula( LogicalModel model, DatabaseMeta databaseMeta, String formulaString,
      Map<LogicalTable, String> tableAliases, Map<String, Object> parameters, boolean genAsPreparedStatement )
    throws PentahoMetadataException {

    this.model = model;
    this.formulaString = formulaString;
    this.databaseMeta = databaseMeta;
    this.tableAliases = tableAliases;
    this.tables = new ArrayList<LogicalTable>();
    this.parameters = parameters;
    this.genAsPreparedStatement = genAsPreparedStatement;

    if ( model == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED" ) ); //$NON-NLS-1$
    }

    if ( databaseMeta == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0002_NO_DATABASE_META_PROVIDED" ) ); //$NON-NLS-1$
    }

    this.sqlDialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    if ( sqlDialect == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0018_DATABASE_DIALECT_NOT_FOUND", databaseMeta.getDatabaseTypeDesc() ) ); //$NON-NLS-1$
    }

    if ( formulaString == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0003_NO_FORMULA_STRING_PROVIDED" ) ); //$NON-NLS-1$
    }
  }

  /**
   * constructor, used for formula based physical columns
   * 
   * @param model
   *          business model for business column lookup
   * @param formulaString
   *          formula string
   * @throws PentahoMetadataException
   *           throws an exception if we're missing anything important
   */
  public SqlOpenFormula( LogicalModel model, LogicalTable table, DatabaseMeta databaseMeta, String formulaString,
      Map<LogicalTable, String> tableAliases, Map<String, Object> parameters, boolean genAsPreparedStatement )
    throws PentahoMetadataException {

    this.model = model;
    this.formulaString = formulaString;
    this.databaseMeta = databaseMeta;
    this.tableAliases = tableAliases;
    this.tables = new ArrayList<LogicalTable>();
    this.tables.add( table );
    this.parameters = parameters;
    this.genAsPreparedStatement = genAsPreparedStatement;

    if ( model == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0001_NO_BUSINESS_MODEL_PROVIDED" ) ); //$NON-NLS-1$
    }

    if ( databaseMeta == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0002_NO_DATABASE_META_PROVIDED" ) ); //$NON-NLS-1$
    }

    if ( table == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0004_NO_BUSINESS_TABLE_PROVIDED" ) ); //$NON-NLS-1$
    }

    this.sqlDialect = SQLDialectFactory.getSQLDialect( databaseMeta );

    if ( sqlDialect == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0015_DATABASE_DIALECT_NOT_FOUND", databaseMeta.getDatabaseTypeDesc() ) ); //$NON-NLS-1$
    }

    if ( formulaString == null ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0003_NO_FORMULA_STRING_PROVIDED" ) ); //$NON-NLS-1$
    }
  }

  public void setTableAliases( Map<LogicalTable, String> tableAliases ) {
    this.tableAliases = tableAliases;
  }

  protected DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }

  public List<LogicalTable> getLogicalTables() {
    return tables;
  }

  protected LogicalModel getLogicalModel() {
    return model;
  }

  protected Map getSelectionMap() {
    return selectionMap;
  }

  private String transformInCondition( String c ) {
    if ( c.contains( ";\"\"" ) ) {
      String field = c.substring( c.indexOf( "[" ), c.indexOf( ";" ) );
      StringBuilder sb = new StringBuilder();
      sb.append( "OR(" );
      sb.append( c );
      sb.append( ";" ).append( "ISNA(" ).append( field ).append( ")" );
      sb.append( ")" );
      c = sb.toString();
    }
    return c;
  }

  private String verifyNullInsideINcondition( String f ) {
    int pos = 0;
    int in_pos = 0;
    while ( ( in_pos = f.indexOf( "IN(", pos ) ) >= 0 ) {
      int pB = in_pos;
      int pE = f.indexOf( ")", in_pos ) + 1;
      String in_cond = f.substring( pB, pE );
      StringBuilder sb = new StringBuilder();
      sb.append( f.substring( 0, pB ) );
      String new_in_cond = transformInCondition( in_cond );
      sb.append( new_in_cond );
      sb.append( f.substring( pB + in_cond.length() ) );
      f = sb.toString();
      pos = pB + new_in_cond.length();
    }
    return f;
  }
  /**
   * parse and validate formula, including resolving all fields
   * 
   * @throws PentahoMetadataException
   */
  public void parseAndValidate() throws PentahoMetadataException {
    if ( !isValidated ) {
      // throws an error if failed to parse and validate condition
      try {
        formulaString = verifyNullInsideINcondition( formulaString );
        formulaObject = new Formula( formulaString );
        formulaObject.initialize( formulaContext );
        LValue val = formulaObject.getRootReference();
        validateAndResolveObjectModel( val );
        isValidated = true;
      } catch ( ParseException e ) {
        logger.debug( "an exception occurred", e ); //$NON-NLS-1$
        // is it possible to provide more detail in this exception to the user?
        throw new PentahoMetadataException( Messages.getErrorString(
            "SqlOpenFormula.ERROR_0005_FAILED_TO_PARSE_FORMULA", formulaString ) ); //$NON-NLS-1$
      } catch ( EvaluationException e ) {
        logger.debug( "an exception occurred", e ); //$NON-NLS-1$
        throw new PentahoMetadataException( Messages.getErrorString(
            "SqlOpenFormula.ERROR_0006_FAILED_TO_EVALUATE_FORMULA", formulaString ) ); //$NON-NLS-1$
      } catch ( Throwable e ) {
        if ( e instanceof PentahoMetadataException ) {
          throw (PentahoMetadataException) e;
        } else {
          logger.debug( "an exception occurred", e ); //$NON-NLS-1$
          throw new PentahoMetadataException( Messages.getErrorString(
              "SqlOpenFormula.ERROR_0007_UNKNOWN_ERROR", formulaString ) ); //$NON-NLS-1$
        }
      }
      // this should populate the fields object
    }
  }

  /**
   * We support unqualified logical columns if a logical table is provided. This allows physical columns to define a
   * formula which eventually gets used by logical table columns.
   * 
   * @param fieldName
   *          name of field, either "<LOGICAL TABLE ID>.<LOGICAL COLUMN ID>" or "<PHYSICAL COLUMN>"
   * 
   * @throws PentahoMetadataException
   *           if field cannot be resolved
   */
  protected void addField( String fieldName ) throws PentahoMetadataException {

    if ( fieldName == null ) {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0008_FIELDNAME_NULL", formulaString ) ); //$NON-NLS-1$
    }

    // we need to validate that "fieldName" actually maps to a field!
    if ( !selectionMap.containsKey( fieldName ) ) {

      // check to see if it's a parameter
      if ( fieldName.startsWith( PARAM ) ) {
        String paramName = fieldName.substring( 6 );
        if ( !parameters.containsKey( paramName ) ) {
          throw new PentahoMetadataException( Messages.getErrorString(
              "SqlOpenFormula.ERROR_00XX_PARAM_NOT_FOUND", paramName ) ); //$NON-NLS-1$
        }
        return;
      }

      // check if this is a "physicalcolumn" or a "<logicaltable>.<logicalcolumn>"
      if ( fieldName.indexOf( "." ) < 0 ) { //$NON-NLS-1$

        // expecting <PHYSICAL COLUMN>

        if ( tables == null ) {
          throw new PentahoMetadataException( Messages.getErrorString(
              "SqlOpenFormula.ERROR_0009_FIELDNAME_ERROR_NO_BUSINESS_TABLE", fieldName ) ); //$NON-NLS-1$
        }

        // note, this column name is the "physical column name" vs. the "logical column name"
        // look through all the logical columns and verify the column name matches an existing
        // logical column.
        for ( LogicalTable table : tables ) {
          for ( LogicalColumn column : table.getLogicalColumns() ) {
            // this matches how logical column renders it's sql, i'm not a big fan though.
            // instead i would prefer this:
            // if (logicalColumn.getPhysicalColumn().getId().equals(fieldName)) {
            // break;
            // }

            if ( ( column.getProperty( SqlPhysicalColumn.TARGET_COLUMN_TYPE ) == TargetColumnType.COLUMN_NAME )
                && fieldName.equals( column.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) ) ) {
              // we've found it, but we don't do anything due to aggregation issues later
              return;
            }
          }
        }

        throw new PentahoMetadataException(
            Messages
                .getErrorString(
                    "SqlOpenFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, fieldName, toString( getLogicalTableIDs() ) ) ); //$NON-NLS-1$

      } else {

        Category category = null;

        // expecting <LOGICAL TABLE ID>.<LOGICAL COLUMN ID>
        // or <LOGICAL TABLE ID>.<LOGICAL COLUMN ID>.<AGGREGATION>
        String[] tblcol = fieldName.split( "\\." ); //$NON-NLS-1$
        if ( tblcol.length != 2 && tblcol.length != 3 ) {
          throw new PentahoMetadataException( Messages.getErrorString(
              "SqlOpenFormula.ERROR_0011_INVALID_FIELDNAME", fieldName ) ); //$NON-NLS-1$
        }

        // first lookup the logical table that the column belongs to.
        // finally check to see if the column exists in its parent, if no column is found, throw an exception.
        LogicalColumn column = null;
        LogicalTable logicalTable = null;
        for ( LogicalTable table : tables ) {
          if ( table.getId().equalsIgnoreCase( tblcol[0] ) ) {
            // This is the table involved...
            logicalTable = table;
            break;
          }
        }

        if ( logicalTable != null ) {
          // Find the column in that table...
          column = logicalTable.findLogicalColumn( tblcol[1] );
          if ( column == null ) {
            throw new PentahoMetadataException( Messages.getErrorString(
                "SqlOpenFormula.ERROR_0019_FIELDNAME_ERROR_CAT_COLUMN_NOT_FOUND", fieldName, tblcol[0], tblcol[1] ) ); //$NON-NLS-1$
          }

        } else {

          // Look up the logical table by ID
          //
          logicalTable = model.findLogicalTable( tblcol[0] );
          if ( logicalTable == null ) {
            // OK, now we try to look for the category if someone was actually stupid enough to do that...
            //
            category = model.findCategory( tblcol[0] );
            if ( category == null ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "SqlOpenFormula.ERROR_0012_FIELDNAME_ERROR_PARENT_NOT_FOUND", fieldName, tblcol[0] ) ); //$NON-NLS-1$
            }

            // What do you know, it worked.
            // Now look up the column.
            column = category.findLogicalColumn( tblcol[1] );
            if ( column == null ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "SqlOpenFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, tblcol[1], tblcol[0] ) ); //$NON-NLS-1$
            }
          } else {
            column = logicalTable.findLogicalColumn( tblcol[1] );
            if ( column == null ) {
              throw new PentahoMetadataException( Messages.getErrorString(
                  "SqlOpenFormula.ERROR_0010_FIELDNAME_ERROR_COLUMN_NOT_FOUND", fieldName, tblcol[1], tblcol[0] ) ); //$NON-NLS-1$
            }
            // This means that the logical table is not in the list of used tables...
            // Add it here...
            //
            tables.add( logicalTable );
          }
        }

        AggregationType aggsetting = null;
        if ( tblcol.length == 3 ) {
          String aggregation = tblcol[2];
          if ( aggregation != null ) {
            AggregationType setting = AggregationType.valueOf( aggregation.toUpperCase() );
            if ( ( column.getAggregationType() == setting ) || column.getAggregationList() != null
                && column.getAggregationList().contains( setting ) ) {
              aggsetting = setting;
            }
          }
        }

        if ( category == null ) {
          logger.warn( Messages.getErrorString( "SqlOpenFormula.ERROR_0023_UNASSOCIATED_LOGICAL_COL", column.getId() ) ); //$NON-NLS-1$
          for ( Category cat : model.getCategories() ) {
            if ( cat.findLogicalColumn( column.getId() ) != null ) {
              category = cat;
              break;
            }
          }
        }

        Selection selection = new Selection( category, column, aggsetting );

        selectionMap.put( fieldName, selection );
        selections.add( selection );
      }
    }
  }

  public static String toString( Object[] arr ) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arr.length; i++ ) {
      if ( i != 0 ) {
        sb.append( "," ); //$NON-NLS-1$
      }
      sb.append( arr[i] );
    }
    return sb.toString();
  }

  /*
   * there should be 3 passes over the formula object model:
   * 
   * 1) a verification pass, which checks the functions for validity and resolves the business columns. see
   * validateAndResolveObjectModel()
   * 
   * 2) a preprocessing pass, which executes the necessary preprocessing items and generates the SQL see generateSQL()
   * 
   * Not implemented yet: 3) a post processing pass, which executes any necessary post processing items not SQL
   * compatible this is not implemented yet, it will require some rearchitecting of the metadata system currently all
   * the metadata system provides is raw SQL. eventually it will act as its own PentahoResultSet allowing post
   * processing
   */

  /**
   * Recursive function that traverses the formula object model, resolves the business columns, and validates the
   * functions and operators specified.
   * 
   * 
   * 
   * @param val
   *          the root of the formula object model
   */
  private void validateAndResolveObjectModel( Object val ) throws PentahoMetadataException {
    if ( val instanceof Term ) {
      Term t = (Term) val;
      validateAndResolveObjectModel( t.getHeadValue() );
      for ( int i = 0; i < t.getOperators().length; i++ ) {
        validateAndResolveObjectModel( t.getOperators()[i] );

        if ( t.getOperands()[i] instanceof ContextLookup ) {
          if ( paramContainsMultipleValues( (ContextLookup) t.getOperands()[i] ) ) {
            // no infix operators support multi-valued parameters
            throw new PentahoMetadataException( Messages.getErrorString(
                "SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", t.getOperators()[i].toString() ) ); //$NON-NLS-1$
          }
        }
        validateAndResolveObjectModel( t.getOperands()[i] );
      }
    } else if ( val instanceof ContextLookup ) {
      ContextLookup l = (ContextLookup) val;
      addField( l.getName() );
    } else if ( val instanceof StaticValue ) {
      // everything is fine
      return;
    } else if ( val instanceof FormulaFunction ) {

      FormulaFunction f = (FormulaFunction) val;
      if ( sqlDialect.isSupportedFunction( f.getFunctionName() ) ) {
        SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator( f.getFunctionName() );
        gen.validateFunction( f );

        // note, if aggregator function, we should make sure it is part of the table formula vs. conditional formula
        if ( !allowAggregateFunctions && tables == null && sqlDialect.isAggregateFunction( f.getFunctionName() ) ) {
          throw new PentahoMetadataException( Messages.getErrorString(
              "SqlOpenFormula.ERROR_0013_AGGREGATE_USAGE_ERROR", f.getFunctionName(), formulaString ) ); //$NON-NLS-1$
        }

        if ( sqlDialect.isAggregateFunction( f.getFunctionName() ) ) {
          hasAggregateFunction = true;
        }

        // validate functions parameters
        if ( f.getChildValues() != null && f.getChildValues().length > 0 ) {
          validateAndResolveObjectModel( f.getChildValues()[0] );

          for ( int i = 1; i < f.getChildValues().length; i++ ) {
            if ( f.getChildValues()[i] instanceof ContextLookup ) {
              if ( paramContainsMultipleValues( (ContextLookup) f.getChildValues()[i] )
                  && !gen.isMultiValuedParamAware() ) {
                throw new PentahoMetadataException( Messages.getErrorString(
                    "SqlOpenFormula.ERROR_0024_MULTIPLE_VALUES_NOT_SUPPORTED", f.getFunctionName() ) ); //$NON-NLS-1$
              }
            }
            validateAndResolveObjectModel( f.getChildValues()[i] );
          }
        }
      } else {
        throw new PentahoMetadataException( Messages.getErrorString(
            "SqlOpenFormula.ERROR_0014_FUNCTION_NOT_SUPPORTED", f.getFunctionName() ) ); //$NON-NLS-1$
      }
    } else if ( val instanceof InfixOperator ) {
      if ( sqlDialect.isSupportedInfixOperator( val.toString() ) ) {
        // everything is fine
        return;
      } else {
        throw new PentahoMetadataException( Messages.getErrorString(
            "SqlOpenFormula.ERROR_0021_OPERATOR_NOT_SUPPORTED", val.toString() ) ); //$NON-NLS-1$
      }
    } else if ( val instanceof PrefixTerm ) {
      return;
    } else {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString() ) ); //$NON-NLS-1$
    }
  }

  private boolean paramContainsMultipleValues( ContextLookup contextLookup ) {
    String fieldName = contextLookup.getName();
    // we need to validate that "fieldName" actually maps to a field!
    if ( !selectionMap.containsKey( fieldName ) ) {

      // check to see if it's a parameter
      if ( fieldName.startsWith( PARAM ) ) {
        String paramName = fieldName.substring( 6 );
        if ( parameters.containsKey( paramName ) ) {
          Object param = parameters.get( paramName );
          if ( param instanceof Object[] && ( (Object[]) param ).length > 1 ) {
            return true;
          } else {
            return false;
          }
        } else {
          return false;
        }
      }
    }
    return false;
  }

  /**
   * Determines whether or not child val needs to be wrapped with parens. The determining factor is if both the current
   * object and the parent are sql infix operators
   * 
   * @param parent
   *          object
   * @param val
   *          current object
   * @return true if parens are required
   */
  public boolean requiresParens( Object parent, Object val ) {
    // first see if parent may required children parens
    boolean parentMatch = false;
    if ( parent instanceof Term ) {
      parentMatch = true;
    } else if ( parent instanceof FormulaFunction ) {
      FormulaFunction parentFunction = (FormulaFunction) parent;
      SQLFunctionGeneratorInterface parentGen = sqlDialect.getFunctionSQLGenerator( parentFunction.getFunctionName() );
      parentMatch = ( parentGen.getType() == SQLFunctionGeneratorInterface.INLINE_FUNCTION );
    }
    if ( !parentMatch ) {
      return false;
    }
    // second see if child needs parens
    if ( val instanceof InfixOperator ) {
      return true;
    } else if ( val instanceof FormulaFunction ) {
      FormulaFunction f = (FormulaFunction) val;
      SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator( f.getFunctionName() );
      return ( gen.getType() == SQLFunctionGeneratorInterface.INLINE_FUNCTION );
    } else {
      return false;
    }
  }

  /**
   * Recursive function that executes any preprocessing and generates the correct SQL
   * 
   * @param val
   *          the root of the formula object model
   * @param sb
   *          the string buffer to append the SQL to
   * @param locale
   *          the current locale
   */
  public void generateSQL( Object parent, Object val, StringBuffer sb, String locale ) throws PentahoMetadataException {
    if ( val instanceof Term ) {
      Term t = (Term) val;
      // parens are required if both parent and current are sql infix
      boolean addParens = ( t.getOperators().length > 1 || requiresParens( parent, t.getOperators()[0] ) );
      if ( addParens ) {
        sb.append( "(" ); //$NON-NLS-1$
      }
      generateSQL( t, t.getHeadValue(), sb, locale );
      for ( int i = 0; i < t.getOperators().length; i++ ) {
        generateSQL( t, t.getOperators()[i], sb, locale );
        generateSQL( t, t.getOperands()[i], sb, locale );
      }
      if ( addParens ) {
        sb.append( ")" ); //$NON-NLS-1$
      }
    } else if ( val instanceof ContextLookup ) {
      ContextLookup l = (ContextLookup) val;
      renderContextLookup( sb, l.getName(), locale );
    } else if ( val instanceof StaticValue ) {
      StaticValue v = (StaticValue) val;

      if ( v.getValueType() instanceof TextType ) {
        sb.append( sqlDialect.quoteStringLiteral( v.getValue() ) );
      } else {
        sb.append( v.getValue() );
      }
    } else if ( val instanceof FormulaFunction ) {

      FormulaFunction f = (FormulaFunction) val;
      SQLFunctionGeneratorInterface gen = sqlDialect.getFunctionSQLGenerator( f.getFunctionName() );

      // note that generateFunctionSQL calls back into this function for children params if necessary
      // may need to be wrapped
      boolean addParens = requiresParens( parent, f );
      if ( addParens ) {
        sb.append( "(" ); //$NON-NLS-1$
      }
      gen.generateFunctionSQL( this, sb, locale, f );
      if ( addParens ) {
        sb.append( ")" ); //$NON-NLS-1$
      }
    } else if ( val instanceof InfixOperator ) {
      if ( sqlDialect.isSupportedInfixOperator( val.toString() ) ) {
        SQLOperatorGeneratorInterface gen = sqlDialect.getInfixOperatorSQLGenerator( val.toString() );
        sb.append( " " + gen.getOperatorSQL() + " " ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    } else if ( val instanceof PrefixTerm ) {
      PrefixTerm v = (PrefixTerm) val;
      sb.append( v.toString() );
    } else {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0016_CLASS_TYPE_NOT_SUPPORTED", val.getClass().toString() ) ); //$NON-NLS-1$
    }
  }

  public Object getParameterValue( ContextLookup param ) throws PentahoMetadataException {
    if ( param.getName().startsWith( PARAM ) ) {
      String paramName = param.getName().substring( 6 );
      return parameters.get( paramName );
    } else {
      throw new PentahoMetadataException( Messages.getErrorString(
          "SqlOpenFormula.ERROR_0022_INVALID_PARAM_REFERENCE", param.getName() ) ); //$NON-NLS-1$
    }
  }

  protected void renderContextLookup( StringBuffer sb, String contextName, String locale )
    throws PentahoMetadataException {
    Selection column = (Selection) selectionMap.get( contextName );
    if ( column == null ) {
      // either a physical column or parameter

      if ( contextName.startsWith( PARAM ) ) {
        String paramName = contextName.substring( 6 );
        if ( genAsPreparedStatement ) {
          // put a temporary placeholder in the SQL if this parameter will be used as part of a
          // prepared statement sql query.
          sb.append( "___PARAM[" + paramName + "]___" ); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          Object paramValue = parameters.get( paramName );
          if ( paramValue instanceof Boolean ) {
            // need to get and then render either true or false function.
            if ( ( (Boolean) paramValue ).booleanValue() ) {
              sqlDialect.getFunctionSQLGenerator( "TRUE" ).generateFunctionSQL( this, sb, locale, null ); //$NON-NLS-1$
            } else {
              sqlDialect.getFunctionSQLGenerator( "FALSE" ).generateFunctionSQL( this, sb, locale, null ); //$NON-NLS-1$
            }
          } else if ( paramValue instanceof Double ) {
            sb.append( paramValue.toString() );
          } else if ( paramValue instanceof Double[] ) {
            Double[] param = (Double[]) paramValue;
            for ( int i = 0; i < param.length; i++ ) {
              if ( i != 0 ) {
                sb.append( " , " );
              }
              sb.append( param[i].toString() );
            }
          } else if ( paramValue instanceof Object[] ) {
            Object[] param = (Object[]) paramValue;
            for ( int i = 0; i < param.length; i++ ) {
              if ( i != 0 ) {
                sb.append( " , " );
              }
              sb.append( sqlDialect.quoteStringLiteral( param[i].toString() ) );
            }
          } else {
            // assume a string, string literal quote
            sb.append( sqlDialect.quoteStringLiteral( paramValue.toString() ) );
          }
        }
        return;
      }

      // we have a physical column function, we need to evaluate it
      // in a special way due to aggregations and such

      String tableColumn = ""; //$NON-NLS-1$
      sb.append( " " ); //$NON-NLS-1$

      LogicalTable businessTable = findLogicalTableForContextName( contextName, locale );
      if ( businessTable != null ) {

        // use a table alias if available

        String tableAlias = null;
        if ( tableAliases != null ) {
          tableAlias = tableAliases.get( businessTable );
        } else {
          tableAlias = businessTable.getId();
        }
        sb.append( databaseMeta.quoteField( tableAlias ) );
        sb.append( "." ); //$NON-NLS-1$
      }
      sb.append( databaseMeta.quoteField( contextName ) );
      sb.append( " " ); //$NON-NLS-1$

    } else {
      // render the column sql
      sb.append( " " ); //$NON-NLS-1$
      SqlAndTables sqlAndTables =
          SqlGenerator.getBusinessColumnSQL( model, column, tableAliases, parameters, genAsPreparedStatement,
              databaseMeta, locale );
      sb.append( sqlAndTables.getSql() );
      sb.append( " " ); //$NON-NLS-1$

      // We need to make sure to add the used tables to this list (recursive use-case).
      // Only if they are not in there yet though.
      //
      for ( LogicalTable businessTable : sqlAndTables.getUsedTables() ) {
        if ( !tables.contains( businessTable ) ) {
          tables.add( businessTable );
        }
      }
    }
  }

  /**
   * wrapper for recursive generateSQL method
   * 
   * @param locale
   *          locale of user
   * 
   * @return sql string
   */
  public String generateSQL( String locale ) throws PentahoMetadataException {
    if ( !isValidated ) {
      throw new PentahoMetadataException( Messages
          .getErrorString( "SqlOpenFormula.ERROR_0017_STATE_ERROR_NOT_VALIDATED" ) ); //$NON-NLS-1$
    }
    StringBuffer sb = new StringBuffer();
    generateSQL( null, formulaObject.getRootReference(), sb, locale );
    return sb.toString();
  }

  /**
   * retrieve the list of business columns
   * 
   * @return list of business columns referenced in the formula
   */
  public List<Selection> getSelections() {
    return selections;
  }

  /**
   * @return the IDs of the used business tables
   */
  public String[] getLogicalTableIDs() {
    String[] names = new String[tables.size()];
    for ( int i = 0; i < tables.size(); i++ ) {
      names[i] = tables.get( i ).getId();
    }
    return names;
  }

  /**
   * returns true if pms formula contains agg functions. run parseAndValidate() before running this method
   * 
   * @return hasAggregateFunction
   */
  public boolean hasAggregateFunction() {
    return hasAggregateFunction;
  }

  /**
   * allows overriding of default behavior, which doesn't allow aggregate functions to be used.
   * 
   * @param allowAggregateFunctions
   */
  public void setAllowAggregateFunctions( boolean allowAggregateFunctions ) {
    this.allowAggregateFunctions = allowAggregateFunctions;
  }

  /**
   * Find the business table associated with the context name.<br>
   * We look for it in the tables list by looking at the column IDs, the column display names, and by looking at the
   * physical column formula<br>
   * 
   * @param contextName
   *          the context name
   * @param locale
   *          the locale to look in
   * @return the business table or null if nothing was found.
   */
  protected LogicalTable findLogicalTableForContextName( String contextName, String locale ) {
    LogicalTable businessTable = null;
    for ( LogicalTable table : getLogicalTables() ) {
      // Search by column ID
      //
      LogicalColumn c = table.findLogicalColumn( contextName );
      // Search by column name
      //
      if ( c == null ) {
        // this used to call
        // c = table.findLogicalColumn(locale, contextName);
        for ( LogicalColumn col : table.getLogicalColumns() ) {
          LocalizedString name = col.getName();
          if ( name != null && contextName.equals( name.getString( locale ) ) ) {
            c = col;
            break;
          }
        }
      }
      // Search by physical column name / formula...
      //
      for ( LogicalColumn col : table.getLogicalColumns() ) {
        if ( contextName.equals( col.getProperty( SqlPhysicalColumn.TARGET_COLUMN ) ) ) {
          c = col;
          break;
        }
      }

      // If we found a valid column, we found the business table...
      //
      if ( c != null ) {
        businessTable = c.getLogicalTable();
      }
    }
    return businessTable;
  }

  Boolean hasAgg;

  /**
   * Traverse the field list and see if any of the fields are aggregate fields. we cache hasAgg for future calls
   * 
   * @return true if aggregate
   */
  public boolean hasAggregate() {
    if ( hasAgg == null ) {
      hasAgg = Boolean.FALSE;
      for ( Selection col : getSelections() ) {
        if ( col.hasAggregate() ) {
          hasAgg = Boolean.TRUE;
          return hasAgg.booleanValue();
        }
      }
    }
    return hasAgg.booleanValue();
  }

}
