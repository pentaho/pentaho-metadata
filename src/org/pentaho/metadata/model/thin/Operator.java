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

import java.io.Serializable;

/**
 * Operator is used in the definition of a @see MqlCondition 
 */
public enum Operator implements Serializable{

  GREATER_THAN(">", 1, true),  //$NON-NLS-1$
  LESS_THAN("<", 1, true),  //$NON-NLS-1$
  EQUAL("=", 1, true),  //$NON-NLS-1$
  GREATOR_OR_EQUAL(">=", 1, true),  //$NON-NLS-1$
  LESS_OR_EQUAL("<=", 1, true),  //$NON-NLS-1$
  
  EXACTLY_MATCHES("EXACTLY MATCHES", 0, true),  //$NON-NLS-1$
  CONTAINS("CONTAINS", 0, true),  //$NON-NLS-1$
  DOES_NOT_CONTAIN("DOES NOT CONTAIN", 0, true),  //$NON-NLS-1$
  BEGINS_WITH("BEGINS WITH", 0, true),  //$NON-NLS-1$
  ENDS_WITH("ENDS WITH", 0, true), //$NON-NLS-1$
  
  IS_NULL("IS NULL", 2, false),  //$NON-NLS-1$
  IS_NOT_NULL("IS NOT NULL", 2, false); //$NON-NLS-1$

  
  private String strVal;
  // 0 = string
  // 1 = numeric
  // 2 = both
  private int operatorType;
  private boolean requiresValue;

  private Operator(String str, int operatorType, boolean requiresValue) {
    this.strVal = str;
    this.operatorType = operatorType;
    this.requiresValue = requiresValue;
  }

  public String toString() {
    return strVal;
  }
  
  public static Operator parse(String val){
    
    if(val == null || val.equals("")){ //$NON-NLS-1$
      return Operator.EQUAL;
    }
    val = val.toUpperCase();
    // These are the UI equivalents that are re-resolved. Note this needs to be i18n
    // @TODO i18n
    if(val.equals(">")){ //$NON-NLS-1$
      return Operator.GREATER_THAN;
    } else if(val.equals(">=")){ //$NON-NLS-1$
      return Operator.GREATOR_OR_EQUAL;
    } else if(val.equals("=")){ //$NON-NLS-1$
      return Operator.EQUAL;
    } else if(val.equals("<")){ //$NON-NLS-1$
      return Operator.LESS_THAN;
    } else if(val.equals("<=")){ //$NON-NLS-1$
      return Operator.LESS_OR_EQUAL;
    } else if(val.equals("EXACTLY MATCHES")){ //$NON-NLS-1$
      return Operator.EXACTLY_MATCHES;
    } else if(val.equals("CONTAINS")){ //$NON-NLS-1$
      return Operator.CONTAINS;
    } else if(val.equals("DOES NOT CONTAIN")){ //$NON-NLS-1$
      return Operator.DOES_NOT_CONTAIN;
    } else if(val.equals("BEGINS WITH")){ //$NON-NLS-1$
      return Operator.BEGINS_WITH;
    } else if(val.equals("ENDS WITH")){ //$NON-NLS-1$
      return Operator.ENDS_WITH;
    } else if(val.equals("IS NULL")){ //$NON-NLS-1$
      return Operator.IS_NULL;
    }  else if(val.equals("IS NOT NULL")){ //$NON-NLS-1$
      return Operator.IS_NOT_NULL;
    } 
    
    // Actual generated Open Formula formula name is passed in from deserialization routine. Try to match those here.
    if(val.equals("CONTAINS")){ //$NON-NLS-1$
      return Operator.CONTAINS;
    } else if(val.equals("BEGINSWITH")){ //$NON-NLS-1$
      return Operator.BEGINS_WITH;
    } else if(val.equals("ENDSWITH")){ //$NON-NLS-1$
      return Operator.ENDS_WITH;
    } else if(val.equals("ISNA")){ //$NON-NLS-1$
      return Operator.IS_NULL;
    }
    
    return Operator.EQUAL;
  }
  
  public boolean requiresValue(){
    return requiresValue;
  }
  
  /**
   * Returns an array of types separated by whether or not they're string types
   * @param stringType
   * @return array of Operators
   */
  /*
  public static Operator[] values(boolean stringType){
    Operator[] vals = Operator.values();
    List<Operator> ops = new ArrayList<Operator>();
    for(int i=0; i < vals.length; i++){
      if (vals[i].operatorType == 2) {
        ops.add(vals[i]); 
      } else if(vals[i].operatorType == 0 && stringType){
        ops.add(vals[i]); 
      } else if (vals[i].operatorType == 1 && !stringType) {
        ops.add(vals[i]); 
      }
    }
    return ops.toArray(new Operator[]{});
  }
  */
  
  public String formatCondition(String columnName, String paramName, String value[], boolean parameterized){
    
    if(parameterized){
     value = new String[] { "[param:"+paramName.replaceAll("[\\{\\}]","")+"]" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    } else if (this.operatorType == 0 || this.operatorType == 2) {
      for(int idx=0; idx<value.length; idx++) {
        if(!value[idx].startsWith("\"") && !value[idx].endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
          value[idx] = "\"" + value[idx] + "\""; //$NON-NLS-1$ //$NON-NLS-2$ 
        }
      }
    }
    String retVal = ""; //$NON-NLS-1$
    
    switch(this){
      case EXACTLY_MATCHES:
        if( value.length == 1 ) {
          retVal += columnName+" = " + value[0]; //$NON-NLS-1$          
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append("IN(") //$NON-NLS-1$
          .append(columnName)
          .append("; "); //$NON-NLS-1$
          for(int idx=0; idx<value.length; idx++) {
            if(idx>0) {
              sb.append(";"); //$NON-NLS-1$
            }
            sb.append(value[idx]);
          }
          sb.append(")"); //$NON-NLS-1$
          retVal = sb.toString();
        }
        break;
      case CONTAINS:
        retVal += "CONTAINS("+columnName+";"+value[0]+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        break;
      case DOES_NOT_CONTAIN:
        retVal += "NOT(CONTAINS("+columnName+";"+value[0]+"))"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        break;
      case BEGINS_WITH:
        retVal += "BEGINSWITH("+columnName+";"+value[0]+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        break;
      case ENDS_WITH:
        retVal += "ENDSWITH("+columnName+";"+value[0]+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        break;
      case IS_NULL:
        retVal += "ISNA("+columnName+")"; //$NON-NLS-1$ //$NON-NLS-2$
        break;
      case IS_NOT_NULL:
        retVal += "NOT(ISNA("+columnName+"))"; //$NON-NLS-1$ //$NON-NLS-2$
        break;
      default:
        retVal = columnName + " " + this.toString(); //$NON-NLS-1$
        if(this.requiresValue){
          retVal += value[0];
        }
        break;
    }
    return retVal;
    
  }
}
