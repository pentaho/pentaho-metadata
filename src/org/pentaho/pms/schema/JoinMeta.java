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
import java.util.ArrayList;

import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.util.Const;
import org.w3c.dom.Node;

import be.ibridge.kettle.core.ChangedFlag;
import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.XMLHandler;
import be.ibridge.kettle.core.XMLInterface;

/*
 * Created on 28-jan-2004
 * 
 */
 
public class JoinMeta extends ChangedFlag implements Cloneable, XMLInterface, ChangedFlagInterface
{
	private PhysicalTable table_from, table_to;
	private int fieldnr_from, fieldnr_to;
	private int type;
	private String complex_join;
	
	public final static int TYPE_RELATIONSHIP_UNDEFINED = 0;
	public final static int TYPE_RELATIONSHIP_1_N       = 1;
	public final static int TYPE_RELATIONSHIP_N_1       = 2;
	public final static int TYPE_RELATIONSHIP_1_1       = 3;
	public final static int TYPE_RELATIONSHIP_0_N       = 4;
	public final static int TYPE_RELATIONSHIP_N_0       = 5;
	public final static int TYPE_RELATIONSHIP_0_1       = 6;
	public final static int TYPE_RELATIONSHIP_1_0       = 7;
	
	protected final static String type_relationship_desc[] = 
		{
			"undefined", "1:N", "N:1", "1:1", "0:N", "N:0", "0:1", "0:1" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		};
	
	public JoinMeta(PhysicalTable table_from, PhysicalTable table_to, int fieldnr_from, int fieldnr_to)
	{
		this.table_from   = table_from;
		this.table_to     = table_to;
		this.fieldnr_from = fieldnr_from;
		this.fieldnr_to   = fieldnr_to;
		type = TYPE_RELATIONSHIP_UNDEFINED;
	}
	
	public JoinMeta()
	{
		this(null, null, 0, 0);
	}

	public boolean readXML(Node joinnode, ArrayList tables)
	{
		try
		{
			String from = XMLHandler.getTagValue(joinnode, "table_from"); //$NON-NLS-1$
			table_from = findTable(tables, from);
			String to   = XMLHandler.getTagValue(joinnode, "table_to"); //$NON-NLS-1$
			table_to = findTable(tables, to);
					
			fieldnr_from = Const.toInt(XMLHandler.getTagValue(joinnode, "fieldnr_from"), -1); //$NON-NLS-1$
			fieldnr_to   = Const.toInt(XMLHandler.getTagValue(joinnode, "fieldnr_to"), -1); //$NON-NLS-1$
			type         = getType(XMLHandler.getTagValue(joinnode, "type")); //$NON-NLS-1$
			complex_join = XMLHandler.getTagValue(joinnode, "complex_join"); //$NON-NLS-1$
			
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
		retval+="        "+XMLHandler.addTagValue("fieldnr_from", fieldnr_from); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("fieldnr_to",   fieldnr_to); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("type",         getTypeDesc()); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        "+XMLHandler.addTagValue("complex_join", complex_join); //$NON-NLS-1$ //$NON-NLS-2$
		retval+="        </relationship>"+Const.CR; //$NON-NLS-1$
		
		return retval;
	}
	
	private PhysicalTable findTable(ArrayList tables, String name)
	{
		for (int x=0;x<tables.size();x++)
		{
			PhysicalTable tableinfo = (PhysicalTable)tables.get(x);
			if (tableinfo.getId().equalsIgnoreCase(name)) return tableinfo;
		}
		return null;
	}

	
	public Object clone()
	{
		try
		{
			JoinMeta retval   = (JoinMeta)super.clone();
			
			retval.setTableFrom((PhysicalTable)getTableFrom().clone());
			retval.setTableTo  ((PhysicalTable)getTableTo().clone());
			
			return retval;
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
	}

	public void setTableFrom(PhysicalTable table_from)
	{
		this.table_from = table_from; 
	}
	public PhysicalTable getTableFrom()
	{
		return table_from;
	}

	public void setTableTo(PhysicalTable table_to)
	{
		this.table_to = table_to; 
	}
	public PhysicalTable getTableTo()
	{
		return table_to;
	}
	
	public void setFieldnrFrom(int fieldnr_from)
	{
		this.fieldnr_from = fieldnr_from;
	}
	public void setFieldnrTo(int fieldnr_to)
	{
		this.fieldnr_to = fieldnr_to;
	}

	public int getFieldnrFrom()
	{
		return fieldnr_from;
	}
	public int getFieldnrTo()
	{
		return fieldnr_to;
	}

	public PhysicalColumn getFieldFrom()
	{
		return table_from.getPhysicalColumn(fieldnr_from);
	}
	public PhysicalColumn getFieldTo()
	{
		return table_to.getPhysicalColumn(fieldnr_to);
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
		return type_relationship_desc[i];
	}

	public static final int getType(String typedesc)
	{
		for (int i=0;i<type_relationship_desc.length;i++)
		{
			if (type_relationship_desc[i].equalsIgnoreCase(typedesc)) return i;
		}
		return TYPE_RELATIONSHIP_UNDEFINED;
	}
	
	public boolean isUsingTable(PhysicalTable table)
	{
		if (table==null) return false;
		return (table.equals(table_from) || table.equals(table_to));
	}
	
	// Swap from and to...
	public void flip()
	{
		PhysicalTable dummy = table_from;
		table_from = table_to;
		table_to = dummy;
		
		int dum = fieldnr_from;
		fieldnr_from = fieldnr_to;
		fieldnr_to = dum;
		
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
		if (fieldnr_from>=0 && fieldnr_to>=0)
		{
			return table_from.getId()+"."+table_from.getPhysicalColumn(fieldnr_from).getId()+  //$NON-NLS-1$
                   " - "+ //$NON-NLS-1$
				   table_to.getId()+"."+table_to.getPhysicalColumn(fieldnr_to).getId(); //$NON-NLS-1$
		}
		else
		{
			return table_from.getId()+" - "+table_to.getId(); //$NON-NLS-1$
		}
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public boolean equals(Object obj)
	{
		JoinMeta rel = (JoinMeta)obj;
		
		return toString().equalsIgnoreCase(rel.toString());
	}
	
	public String getJoin()
	{
		String join=""; //$NON-NLS-1$
		
		if (complex_join!=null)
		{
			join = complex_join;
		}
		else
		if (table_from!=null && table_to!=null && fieldnr_from>=0 && fieldnr_to>=0)
		{
			PhysicalColumn frf = getFieldFrom();
			PhysicalColumn tof = getFieldTo();
			join=frf.getTableColumn()+" = "+tof.getTableColumn(); //$NON-NLS-1$
		}
		
		return join;
	}
}
