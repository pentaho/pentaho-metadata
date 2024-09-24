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
package org.pentaho.pms.mql;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.pms.core.exception.PentahoMetadataException;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.Const;

/**
 * @deprecated as of metadata 3.0. Please use org.pentaho.metadata.query.model.Constraint
 */
public class WhereCondition extends ConceptUtilityBase implements ConceptUtilityInterface {

  public static final String[] operators = new String[] { "AND", "OR", "AND NOT", "OR NOT" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

  public static final String[] comparators = new String[] { "=", "<>", "<", "<=", ">", ">=" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

  // we need to implement support for these functions in PMSFormula
  // , "IS NULL", "IS NOT NULL", "IN", "NOT IN"

  private String operator = null;

  private String condition = null;

  private Boolean hasAgg = null;

  private PMSFormula formula = null;

  /**
   * this method makes it possible to override the PMSFormula object with a subclass
   * 
   * @param formula
   * @param operator
   * @param condition
   * @throws PentahoMetadataException
   */
  public WhereCondition( PMSFormula formula, String operator, String condition ) throws PentahoMetadataException {
    this.operator = operator;
    this.condition = condition;
    this.formula = formula;
    formula.parseAndValidate();
  }

  /**
   * The WhereCondition now is based on LibFormula, so only a conditional string is necessary
   */
  public WhereCondition( BusinessModel model, DatabaseMeta databaseMeta, String operator, String condition )
    throws PentahoMetadataException {
    this.operator = operator;
    this.condition = condition;
    this.formula = new PMSFormula( model, databaseMeta, condition, null );
    formula.parseAndValidate();
  }

  /**
   * The WhereCondition now is based on LibFormula, so only a conditional string is necessary
   */
  public WhereCondition( BusinessModel model, String operator, String condition ) throws PentahoMetadataException {
    this.operator = operator;
    this.condition = condition;
    this.formula = new PMSFormula( model, condition, null );
    formula.parseAndValidate();
  }

  /**
   * return the condition operator for combining where conditions
   * 
   * @return the operator string
   */
  public String getOperator() {
    return operator;
  }

  /**
   * return the formula for this object
   * 
   * @return formula object
   */
  public PMSFormula getPMSFormula() {
    return formula;
  }

  /**
   * return the condition, ie "A = B"
   * 
   * @return the condition string
   */
  public String getCondition() {
    return condition;
  }

  /**
   * return a list of related business columns
   * 
   * @return list
   */
  public List<Selection> getBusinessColumns() {
    return formula.getBusinessColumns();
  }

  /**
   * Traverse the field list and see if any of the fields are aggregate fields. we cache hasAgg for future calls
   * 
   * @return true if aggregate
   */
  public boolean hasAggregate() {
    if ( hasAgg == null ) {
      hasAgg = Boolean.FALSE;
      for ( Selection col : formula.getBusinessColumns() ) {
        if ( col.hasAggregate() ) {
          hasAgg = Boolean.TRUE;
          return hasAgg.booleanValue();
        }
      }
    }
    return hasAgg.booleanValue();
  }

  /**
   * @return the description of the model element
   */
  public String getModelElementDescription() {
    return Messages.getString( "WhereCondition.USER_DESCRIPTION" ); //$NON-NLS-1$
  }

  /**
   * generate the SQL condition
   * 
   * @param locale
   *          locale for generating sql
   * @param useOperator
   *          appends operator if true
   * @return where clause
   */
  public String getWhereClause( String locale, boolean useOperator ) throws PentahoMetadataException {
    String retval = ""; //$NON-NLS-1$
    if ( condition != null ) {
      if ( Const.isEmpty( operator ) || !useOperator ) {
        retval += Const.rightPad( " ", 9 ) + " "; //$NON-NLS-1$ //$NON-NLS-2$
      } else {
        retval += Const.rightPad( operator, 9 ) + " "; //$NON-NLS-1$
      }

      retval += " ( " + formula.generateSQL( locale ) + " ) "; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return retval;
  }

}
