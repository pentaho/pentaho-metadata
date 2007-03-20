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
/*
 * Created on 6-feb-04
 *
 */

public class SelectionField
{
	private String name; 
	private PhysicalColumn field;
	private SelectionGroup group;
	
	public SelectionField(String name, PhysicalColumn field, SelectionGroup group)
	{
		this.name = name;
		this.field = field;
		this.group = group;
	}

	public SelectionField(String name, PhysicalColumn field)
	{
		this(name, field, null);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setField(PhysicalColumn field)
	{
		this.field = field;
	}
	
	public PhysicalColumn getField()
	{
		return field;
	}
	
	public void setGroup(SelectionGroup group)
	{
		this.group = group;
	}
	
	public SelectionGroup getGroup()
	{
		return group;
	}
}
