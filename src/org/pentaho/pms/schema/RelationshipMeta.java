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
 
package org.pentaho.pms.schema;
import java.util.List;

import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.XMLInterface;
import be.ibridge.kettle.core.database.DatabaseMeta;

/*
 * Created on 28-jan-2004
 * 
 */
 
public class RelationshipMeta extends ChangedFlag implements Cloneable, XMLInterface, ChangedFlagInterface
{
	private BusinessTable table_from, table_to;
	private BusinessColumn field_from, field_to;
	private int type;
	private boolean complex;
	private String complex_join;
	
	public final static int TYPE_RELATIONSHIP_UNDEFINED = 0;
	public final static int TYPE_RELATIONSHIP_1_N       = 1;
	public final static int TYPE_RELATIONSHIP_N_1       = 2;
	public final static int TYPE_RELATIONSHIP_1_1       = 3;
	public final static int TYPE_RELATIONSHIP_0_N       = 4;
	public final static int TYPE_RELATIONSHIP_N_0       = 5;
	public final static int TYPE_RELATIONSHIP_0_1       = 6;
	public final static int TYPE_RELATIONSHIP_1_0       = 7;
	public final static int TYPE_RELATIONSHIP_N_N       = 8;
	
	public final static String typeRelationshipDesc[] = 
		{
			"undefined", "1:N", "N:1", "1:1", "0:N", "N:0", "0:1", "1:0", "N:N" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
		};
	
    public RelationshipMeta()
    {
        type = TYPE_RELATIONSHIP_UNDEFINED;
        complex = false;
        complex_join = ""; //$NON-NLS-1$
    }
    
	public RelationshipMeta(BusinessTable table_from, BusinessTable table_to, BusinessColumn field_from, BusinessColumn field_to)
	{
        this();
		this.table_from   = table_from;
		this.table_to     = table_to;
		this.field_from = field_from;
		this.field_to   = field_to;
	}

	public RelationshipMeta(BusinessTable table_from, BusinessTable table_to, String complex_join)
	{
		this.table_from   = table_from;
		this.table_to     = table_to;
		this.field_from   = null;
		this.field_to     = null;
		this.type         = TYPE_RELATIONSHIP_UNDEFINED;
		this.complex      = true;
		this.complex_join = complex_join;
	}

	public boolean loadXML(Node relnode, List tables)
	{
		try
		{
			String from = XMLHandler.getTagValue(relnode, "table_from"); //$NON-NLS-1$
			table_from  = findTable(tables, from);
			String to   = XMLHandler.getTagValue(relnode, "table_to"); //$NON-NLS-1$
			table_to = findTable(tables, to);
					
            if (table_from!=null)
            {
                field_from = table_from.findBusinessColumn( XMLHandler.getTagValue(relnode, "field_from") ); //$NON-NLS-1$
            }
            if (table_to!=null)
            {
                field_to   = table_to.findBusinessColumn( XMLHandler.getTagValue(relnode, "field_to") ); //$NON-NLS-1$
            }
			type         = getType(XMLHandler.getTagValue(relnode, "type")); //$NON-NLS-1$
			complex      = "Y".equalsIgnoreCase(XMLHandler.getTagValue(relnode, "complex")); //$NON-NLS-1$ //$NON-NLS-2$
			complex_join = XMLHandler.getTagValue(relnode, "complex_join"); //$NON-NLS-1$
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public String getXML()
	{
		String retval=""; //$NON-NLS-1$
		
		retval+="      <relationship>"+Const.CR; //$NON-NLS-1$
		retval+="        "+XMLHandler.addTagValue("table_from",   table_from.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("table_to",     table_to.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("field_from",   field_from!=null?field_from.getId():"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		retval+="        "+XMLHandler.addTagValue("field_to",     field_to!=null?field_to.getId():"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		retval+="        "+XMLHandler.addTagValue("type",         getTypeDesc()); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("complex",      complex); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("complex_join", complex_join); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        </relationship>"+Const.CR; //$NON-NLS-1$
		
		return retval;
	}
	
	private BusinessTable findTable(List tables, String name)
	{
		for (int x=0;x<tables.size();x++)
		{
            BusinessTable tableinfo = (BusinessTable)tables.get(x);
			if (tableinfo.getId().equalsIgnoreCase(name)) return tableinfo;
		}
		return null;
	}

	
	public Object clone()
	{
		try
		{
			RelationshipMeta retval   = (RelationshipMeta)super.clone();
			
			retval.setTableFrom((BusinessTable)getTableFrom().clone());
			retval.setTableTo  ((BusinessTable)getTableTo().clone());
			
			return retval;
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
	}

	public void setTableFrom(BusinessTable table_from)
	{
		this.table_from = table_from; 
	}
    
	public BusinessTable getTableFrom()
	{
		return table_from;
	}

	public void setTableTo(BusinessTable table_to)
	{
		this.table_to = table_to; 
	}
    
	public BusinessTable getTableTo()
	{
		return table_to;
	}
	
	public void setFieldFrom(BusinessColumn field_from)
	{
		this.field_from = field_from;
	}
	public void setFieldTo(BusinessColumn field_to)
	{
		this.field_to = field_to;
	}

	public BusinessColumn getFieldFrom()
	{
		return field_from;
	}
    
	public BusinessColumn getFieldTo()
	{
		return field_to;
	}
	
	public boolean isComplex()
	{
		return complex;
	}
	
	public boolean isRegular()
	{
		return !complex;
	}
	
	public void setComplex()
	{
		setComplex(true);
	}

	public void setRegular()
	{
		setComplex(false);
	}
	
	public void flipComplex()
	{
		setComplex(!isComplex());
	}

	public void setComplex(boolean c)
	{
		complex = c;
	}
	
	public String getComplexJoin()
	{
		return complex_join;
	}
	
	public void setComplexJoin(String cj)
	{
		complex_join = cj;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type=type;
	}
	
	public void setType(String tdesc)
	{
		this.type = getType(tdesc);
	}

	public String getTypeDesc()
	{
		return getType(type);
	}
	
	public static final String getType(int i)
	{
		return typeRelationshipDesc[i];
	}

	public static final int getType(String typedesc)
	{
		for (int i=0;i<typeRelationshipDesc.length;i++)
		{
			if (typeRelationshipDesc[i].equalsIgnoreCase(typedesc)) return i;
		}
		return TYPE_RELATIONSHIP_UNDEFINED;
	}
	
	public boolean isUsingTable(BusinessTable table)
	{
		if (table==null) return false;
		return (table.equals(table_from) || table.equals(table_to));
	}
	
	// Swap from and to...
	public void flip()
	{
        BusinessTable dummy = table_from;
		table_from = table_to;
		table_to = dummy;
		
		BusinessColumn dum = field_from;
		field_from = field_to;
		field_to = dum;
		
		switch(type)
		{
			case TYPE_RELATIONSHIP_UNDEFINED : break;
			case TYPE_RELATIONSHIP_1_N       : type = TYPE_RELATIONSHIP_N_1; break;
			case TYPE_RELATIONSHIP_N_1       : type = TYPE_RELATIONSHIP_1_N; break;
			case TYPE_RELATIONSHIP_1_1       : break;
			case TYPE_RELATIONSHIP_0_N       : type = TYPE_RELATIONSHIP_N_0; break;
			case TYPE_RELATIONSHIP_N_0       : type = TYPE_RELATIONSHIP_0_N; break;
			case TYPE_RELATIONSHIP_0_1       : type = TYPE_RELATIONSHIP_1_0; break;
			case TYPE_RELATIONSHIP_1_0       : type = TYPE_RELATIONSHIP_0_1; break;		
		}
	}
	
	public String toString()
	{
		if (field_from!=null && field_to!=null)
		{
			return table_from.getId()+"."+field_from.getId()+  //$NON-NLS-1$
                   " - "+ //$NON-NLS-1$
				   table_to.getId()+"."+field_to.getId(); //$NON-NLS-1$
		}
		else
		{
            try
            {
                return table_from.getId()+" - "+table_to.getId(); //$NON-NLS-1$
            }
            catch(Exception e)
            {
                return "??????????"; //$NON-NLS-1$
            }
		}
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public boolean equals(Object obj)
	{
        RelationshipMeta rel = (RelationshipMeta)obj;
		
        return  rel.table_from.equals(table_from) &&
                rel.table_to.equals(table_to)
                ;
	}

    public void clearChanged()
    {
        setChanged(false);
    }
}
