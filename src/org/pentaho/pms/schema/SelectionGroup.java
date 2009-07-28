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
 * Created on 6-feb-04
 *
 */

package org.pentaho.pms.schema;
import java.util.ArrayList;


/**
 * @deprecated as of metadata 3.0.
 */
public class SelectionGroup
{
	private String         name; 
	private SelectionGroup parent;
	private String         description;
	private ArrayList      selectionGroups;
	private ArrayList      selectionFields;
	
	public SelectionGroup(String name, SelectionGroup parent)
	{
		clear();
		
		this.name  = name;
		this.parent = parent;		
	}

	public SelectionGroup(String name)
	{
		this(name, null);
	}
	
	public void clear()
	{
		name = ""; //$NON-NLS-1$
		parent = null;
		selectionGroups = new ArrayList();
		selectionFields = new ArrayList();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String desc)
	{
		this.description = desc;
	}
	
	public void setParent(SelectionGroup parent)
	{
		this.parent = parent;
	}
	
	public SelectionGroup getParent()
	{
		return parent;
	}

    public ArrayList getSelectionFields()
    {
        return selectionFields;
    }
    
    public ArrayList getSelectionGroups()
    {
        return selectionGroups;
    }
}
