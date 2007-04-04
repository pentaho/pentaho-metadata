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
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;

import be.ibridge.kettle.core.database.DatabaseMeta;


public class PhysicalColumn extends ConceptUtilityBase implements ConceptUtilityInterface, Cloneable
{
	private PhysicalTable physicalTable;
		
	public PhysicalColumn(String id, String formula, FieldTypeSettings fieldType, AggregationSettings aggregationType, PhysicalTable tableinfo)
	{
    super(id);
		setFormula(formula);
		setFieldType(fieldType);
		setAggregationType(aggregationType);

   this.physicalTable       = tableinfo;
	}

    public PhysicalColumn(String id)
    {
        this(id, null, FieldTypeSettings.OTHER, AggregationSettings.NONE, null);
    }

    public PhysicalColumn()
	{
		this(null);
	}


    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription()
    {
        return Messages.getString("PhysicalColumn.USER_DESCRIPTION"); //$NON-NLS-1$
    }

    protected Object clone()
    {
        try
        {
            PhysicalColumn retval = (PhysicalColumn) super.clone();
            retval.setConcept((ConceptInterface) getConcept().clone()); // deep copy of the concepts
            return retval;
        }
        catch(CloneNotSupportedException e)
        {
            return null;
        }
    }
    
	public void setTable(PhysicalTable tableinfo)
	{
		this.physicalTable = tableinfo; 
	}
	
	public PhysicalTable getTable()
	{
		return physicalTable;
	}
	
	public String getTableColumn()
	{
		String retval;
		
		if (getFormula()!=null && getFormula().length()>0)
		{
			retval=getFormula();
			if (retval==null||retval.length()==0) retval=getId();
		}
		else
		{
			PhysicalTable table = getTable();
			retval=table.getId()+"."+getId(); //$NON-NLS-1$
		}
		
		return retval;
	}
	
	public String getAliasColumn(String tableAlias, String formula)
	{
	    // Database?
        DatabaseMeta databaseMeta = getTable().getDatabaseMeta();
        
		String retval;
		
		if (getTable()!=null && formula!=null)
		{
			if (!isExact())
			{
				retval = databaseMeta.quoteField(tableAlias)+"."+databaseMeta.quoteField(formula); //$NON-NLS-1$
			}
			else
			{
				retval = getFormula();
			}
		}
		else
		{
			retval = "??"; //$NON-NLS-1$
		}
		
		return retval;
	}

    public String getRenameAsColumn(DatabaseMeta dbinfo, int columnNr)
	{
		String retval=""; //$NON-NLS-1$
		
		if (hasAggregate() && !isExact())
		{
			retval+="F___"+columnNr;  //$NON-NLS-1$
		}
		else
		if (isExact())
		{
			retval+="E___"+columnNr; //$NON-NLS-1$
		}
		else
		{
			retval+=getFormula();
		}
	
		return retval;
	}
		
	public boolean equals(Object obj)
	{
		PhysicalColumn f = (PhysicalColumn)obj;
		if (!getId().equalsIgnoreCase(f.getId())) return false;
		if (!getFormula().equalsIgnoreCase(f.getFormula())) return false;
		if (getAggregationType()!=f.getAggregationType()) return false;
		if (getFieldType()!=f.getFieldType()) return false;
		if (physicalTable!=null)
		{
			if (f.physicalTable!=null)
			{
				if (!physicalTable.getId().equalsIgnoreCase(f.physicalTable.getId())) return false;
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
	
	public String toString()
	{
		return getId()==null?"NULL":getId(); //$NON-NLS-1$
	}

    /**
     * @param aggregationType the aggregationType to set
     */
    public void setAggregationType(AggregationSettings aggregationType)
    {
        getConcept().addProperty(new ConceptPropertyAggregation(DefaultPropertyID.AGGREGATION.getId(), aggregationType));
        setChanged();
    }

    public void setAggregationType(String aggregationTypeDesc)
    {
        setAggregationType( AggregationSettings.getType(aggregationTypeDesc));
        setChanged();
    }

    public void setFieldType(String fieldTypeDescription)
    {
        setFieldType( FieldTypeSettings.getType(fieldTypeDescription) );
        setChanged();
    }
 }
