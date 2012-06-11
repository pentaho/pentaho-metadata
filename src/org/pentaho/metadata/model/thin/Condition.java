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
package org.pentaho.metadata.model.thin;

import org.apache.commons.lang.StringUtils;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.query.model.CombinationType;

public class Condition {

  private static final long serialVersionUID = 6382700024558898605L;
  private String column;
  private String category;
  private String operator = Operator.EQUAL.name();
  private String value[];
  private String comboType = CombinationType.AND.name();
  private boolean parameterized;
//  private String defaultValue;
  private String selectedAggType;

  public Condition(){
    
  }
  
  public String getColumn() {
    return this.column;    
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

  public void setColumn(String column) {
    this.column = column;  
  }

  public void setCombinationType(String combinationType) {
    this.comboType = combinationType;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public void setValue(String value[]) {
   this.value = value;   
  }

  public boolean validate() {
    return true;   
  }

  public String getCondition(String type) {
    return getCondition(type, isParameterized() ? value[0] : null);
  }

  public String getCondition(String type, String paramName){
    String val[] = this.value;
/*
    if(val == null && defaultValue != null) {
      val = defaultValue;
    }
*/
    Operator theOperator = Operator.parse(operator);
    if(type.equalsIgnoreCase(DataType.STRING.getName()) && theOperator == Operator.EQUAL ){
      theOperator = Operator.EXACTLY_MATCHES;
    }

    boolean enforceParameters = isParameterized() && paramName != null;

    if(!enforceParameters && type.equalsIgnoreCase(DataType.STRING.getName()) ){
      for(int idx=0; idx<val.length; idx++) {
        val[idx] = "\""+val[idx]+"\"";         //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    String columnName = "["+category+"."+column + (StringUtils.isEmpty(selectedAggType) ? "" : "." + selectedAggType) +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$;
    // Date is a special case where we craft a formula function.
    if(type.equals(DataType.DATE.getName())){
      if(enforceParameters){
        // Due to the fact that the value of a Date is a forumula function, the tokenizing of
        // the value needs to happen here instead of letting the Operator class handle it.
        for(int idx=0; idx<val.length; idx++) {
          val[idx] = "DATEVALUE("+"[param:"+value[idx].replaceAll("[\\{\\}]","")+"]"+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        }
        return theOperator.formatCondition(columnName, paramName, val, false);
      } else {  
        for(int idx=0; idx<val.length; idx++) {
        val[idx] = "DATEVALUE(\""+val[idx]+"\")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
    }
    return theOperator.formatCondition(columnName, paramName, val, enforceParameters);
  }

  public boolean isParameterized() {
    return parameterized;
  }

  public void setParameterized(boolean parameterized) {
   this.parameterized = parameterized;   
  }
/*
  public void setDefaultValue(String val){
    this.defaultValue = val;
  }
  
  public String getDefaultValue(){
    return this.defaultValue;
  }
*/
  public void setSelectedAggType(String aggType) {
    this.selectedAggType = aggType;
  }
  
  public String getSelectedAggType() {
    return this.selectedAggType;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

}
