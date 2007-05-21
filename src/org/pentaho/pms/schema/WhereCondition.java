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
 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/
 

/*
 * Created on 28-jan-2004
 * 
 */

package org.pentaho.pms.schema;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.Const;

public class WhereCondition extends ConceptUtilityBase implements ConceptUtilityInterface
{
    private String operator;       // AND
	private BusinessColumn field;  // customer_name
	private String condition;      // = 'Casters'

    public static final String[] operators = new String[] { "AND", "OR", "AND NOT", "OR NOT" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	public static final String[] comparators = new String[] { "=", "<>", "<", "<=", ">", ">=", "IS NULL", "IS NOT NULL", "IN", "NOT IN" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
	
	public WhereCondition(String operator, BusinessColumn field,  String condition)
	{
        this.operator  = operator;
        this.field     = field;
        this.condition = condition;
	}
	
	public WhereCondition()
	{
		this(null, null, null);
	}
    
    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription()
    {
        return Messages.getString("WhereCondition.USER_DESCRIPTION"); //$NON-NLS-1$
    }

	
	public String getWhereClause(String locale, boolean useOperator)
	{
		String retval = ""; //$NON-NLS-1$
		if (field!=null && condition!=null)
		{
            if (Const.isEmpty(operator) || !useOperator)
            {
                retval+=Const.rightPad(" ", 9)+" "; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                retval+=Const.rightPad(operator, 9)+" "; //$NON-NLS-1$
            }
            // The field : use the table alias, the business table actually. (including possible functions for having clause)
            retval += field.getFunctionTableAndColumnForSQL(locale);
            retval += " "; //$NON-NLS-1$
            
            // The condition: just put it in there for the time being...
            retval += condition;
		}
		return retval;
	}

	public String toString()
	{
		return getWhereClause("en_US", true); // TODO: take whatever default there is on the system later on. //$NON-NLS-1$
	}
	
	public int hashCode()
	{
		return getId().hashCode();
	}
	
	public boolean equals(Object obj)
	{
		WhereCondition rel = (WhereCondition)obj;
        
        if ( (operator==null && rel.operator!=null) || (operator!=null && rel.operator==null) || (operator!=null && rel.operator!=null && !operator.equals(rel.operator))) return false; 
        if ( (condition==null && rel.condition!=null) || (condition!=null && rel.condition==null) || (condition!=null && rel.condition!=null && !condition.equals(rel.condition))) return false; 
        if ( (field==null && rel.field!=null) || (field!=null && rel.field==null) || (field!=null && rel.field!=null && !field.equals(rel.field))) return false; 
        
		return true;
	}

    /**
     * @return the condition
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * @return the field
     */
    public BusinessColumn getField()
    {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(BusinessColumn field)
    {
        this.field = field;
    }

    /**
     * @return the operator
     */
    public String getOperator()
    {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    /**
     *  
     * @return true if the business column is an aggregate (sum() should go in the HAVING clause
     */
    public boolean hasAggregate()
    {
        return field.hasAggregate();
    }
    
    
}
