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

package org.pentaho.metadata.model.thin;

import org.apache.commons.lang.StringUtils;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;

public class Condition {

  private static final long serialVersionUID = 6382700024558898605L;
  private String elementId;
  private String parentId;
  private String operator = Operator.EQUAL.name();
  private String[] value;
  private String comboType = CombinationType.AND.name();
  private boolean parameterized;
  // private String defaultValue;
  private String selectedAggregation;

  public Condition() {

  }

  public String getElementId() {
    return this.elementId;
  }

  public String getCombinationType() {
    return this.comboType;
  }

  public String getOperator() {
    return this.operator;
  }

  public String[] getValue() {
    return this.value;
  }

  public void setElementId( String elementId ) {
    this.elementId = elementId;
  }

  public void setCombinationType( String combinationType ) {
    this.comboType = combinationType;
  }

  public void setOperator( String operator ) {
    this.operator = operator;
  }

  public void setValue( String[] value ) {
    this.value = value;
  }

  public boolean validate() {
    return true;
  }

  public String getCondition( String type ) {
    return getCondition( type, isParameterized() ? value[0] : null );
  }

  public String getCondition( String type, String paramName ) {
    String[] val = this.value;
    /*
     * if(val == null && defaultValue != null) { val = defaultValue; }
     */
    Operator theOperator = Operator.parse( operator );
    if ( type.equalsIgnoreCase( DataType.STRING.getName() ) && theOperator == Operator.EQUAL ) {
      theOperator = Operator.EXACTLY_MATCHES;
    }

    boolean enforceParameters = isParameterized() && paramName != null;

    if ( !enforceParameters && type.equalsIgnoreCase( DataType.STRING.getName() ) ) {
      for ( int idx = 0; idx < val.length; idx++ ) {
        val[idx] = "\"" + val[idx] + "\""; //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    String columnName =
        "[" + parentId + "." + elementId + ( StringUtils.isEmpty( selectedAggregation ) ? "" : "." + selectedAggregation ) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$;
    // Date is a special case where we craft a formula function.
    if ( type.equals( DataType.DATE.getName() ) ) {
      if ( enforceParameters ) {
        // Due to the fact that the value of a Date is a forumula function, the tokenizing of
        // the value needs to happen here instead of letting the Operator class handle it.
        for ( int idx = 0; idx < val.length; idx++ ) {
          val[idx] = "DATEVALUE(" + "[param:" + value[idx].replaceAll( "[\\{\\}]", "" ) + "]" + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        }
        return theOperator.formatCondition( columnName, paramName, val, false );
      } else {
        for ( int idx = 0; idx < val.length; idx++ ) {
          val[idx] = "DATEVALUE(\"" + val[idx] + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
    return theOperator.formatCondition( columnName, paramName, val, enforceParameters );
  }

  public boolean isParameterized() {
    return parameterized;
  }

  public void setParameterized( boolean parameterized ) {
    this.parameterized = parameterized;
  }

  /*
   * public void setDefaultValue(String val){ this.defaultValue = val; }
   * 
   * public String getDefaultValue(){ return this.defaultValue; }
   */
  public void setSelectedAggregation( String aggType ) {
    this.selectedAggregation = aggType;
  }

  public String getSelectedAggregation() {
    return this.selectedAggregation;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId( String parentId ) {
    this.parentId = parentId;
  }

}
