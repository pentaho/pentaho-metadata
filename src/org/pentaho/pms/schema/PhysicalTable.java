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
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;

import be.ibridge.kettle.core.ChangedFlagInterface;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.list.ObjectAlreadyExistsException;
import be.ibridge.kettle.core.list.UniqueArrayList;
import be.ibridge.kettle.core.list.UniqueList;


/**
 * Represents a physical table on a physical database with physical columns.
 * 
 * @since 28-jan-2004
 */
public class PhysicalTable extends ConceptUtilityBase implements Cloneable, ChangedFlagInterface, ConceptUtilityInterface
{
	private DatabaseMeta databaseMeta;
	private UniqueList   physicalColumns;

	public PhysicalTable(String id, String targetSchema, String targetTable, DatabaseMeta databaseMeta, UniqueArrayList columns)
	{
        super(id);
		this.databaseMeta = databaseMeta;
		this.physicalColumns = columns;

        if (targetSchema!=null) setTargetSchema(targetSchema);
        setTargetTable(targetTable);
	}
	
	public PhysicalTable()
	{
		this(null, null, null, null, new UniqueArrayList());
	}	
    
    public PhysicalTable(String id)
    {
        this(id, null, null, null, new UniqueArrayList());
    }
    
    /**
     * @return the description of the model element 
     */
    public String getModelElementDescription()
    {
        return Messages.getString("PhysicalTable.USER_DESCRIPTION"); //$NON-NLS-1$
    }


	public Object clone()
	{
		try
		{
			PhysicalTable retval   = (PhysicalTable)super.clone();
            retval.setConcept((ConceptInterface) getConcept().clone()); // deep copy of all properties
            retval.setPhysicalColumns(new UniqueArrayList()); // clear all columns: deep copy as well.
            for (int i=0;i<nrPhysicalColumns();i++)
            {
                PhysicalColumn physicalColumn = getPhysicalColumn(i);
                try
                {
                    retval.addPhysicalColumn((PhysicalColumn) physicalColumn.clone()); // deep copy of the columns information.
                }
                catch(ObjectAlreadyExistsException e)
                {
                    // It's safe to say that if the original didn't have a uniqueness problem, this one has neither
                    // That being said, we still throw a Runtime :-)
                    // You never know.
                    throw new RuntimeException(e);
                }
            }
            retval.setDatabaseMeta(databaseMeta); // shallow copy of the database information
			
			return retval;
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	public void setDatabaseMeta(DatabaseMeta databaseMeta)
	{
		this.databaseMeta = databaseMeta;
	}
	
	public DatabaseMeta getDatabaseMeta()
	{
		return databaseMeta;
	}
	
	public void setPhysicalColumns(UniqueList physicalColumns)
	{
		this.physicalColumns = physicalColumns;
        setChanged();
	}
	
	public UniqueList getPhysicalColumns()
	{
		return physicalColumns;
	}
	
	public PhysicalColumn getPhysicalColumn(int i)
	{
		return (PhysicalColumn)physicalColumns.get(i);
	}
	
	public void addPhysicalColumn(PhysicalColumn column) throws ObjectAlreadyExistsException
	{
		physicalColumns.add(column);
        setChanged();
	}

	public void addPhysicalColumn(int i, PhysicalColumn column) throws ObjectAlreadyExistsException
	{
		physicalColumns.add(i, column);
        setChanged();
	}

	public int findPhysicalColumnNr(String columnName)
	{
		for (int i=0;i<physicalColumns.size();i++)
		{
			if (getPhysicalColumn(i).getId().equalsIgnoreCase(columnName)) return i;
		}
		return -1;
	}
	
    /**
     * Find a physical column using its ID
     * @param columnId the column ID to look out for
     * @return the physical column or null if nothing could be found.
     */
	public PhysicalColumn findPhysicalColumn(String columnId)
	{
		int idx = findPhysicalColumnNr(columnId);
		if (idx>=0) 
		{
			// System.out.println("Found column #"+idx);
			return getPhysicalColumn(idx);
		} 
		return null;
	}

    /**
     * Find a physical column using the localised name of the column (with a search for the ID as a fallback)
     * @param locale the locale to search in
     * @param columnName the column name
     * @return the physical column or null if nothing was found.
     */
    public PhysicalColumn findPhysicalColumn(String locale, String columnName)
    {
        for (int i=0;i<nrPhysicalColumns();i++)
        {
            PhysicalColumn physicalColumn = getPhysicalColumn(i);
            
            if (columnName.equalsIgnoreCase( physicalColumn.getConcept().getName(locale) ) ) return physicalColumn;
        }
        
        return findPhysicalColumn(columnName);
    }
    
	public int indexOfPhysicalColumn(PhysicalColumn f)
	{
		return physicalColumns.indexOf(f);
	}
	
	public void removePhysicalColumn(int i)
	{
		physicalColumns.remove(i);
        setChanged();
	}
	
	public void removeAllPhysicalColumns()
	{
		physicalColumns.clear();
        setChanged();
	}
	
	public int nrPhysicalColumns()
	{
		return physicalColumns.size();
	}

	public boolean equals(Object obj)
	{
		if (obj==null) return false;
		
		PhysicalTable inf = (PhysicalTable)obj;
		
		if (!getId().equalsIgnoreCase(inf.getId())) return false;

		return true; 
	}
	
	public String toString()
	{
		if (databaseMeta!=null) return databaseMeta.getName()+"-->"+getId(); //$NON-NLS-1$
		return getId();
	}
	
	public int hashCode()
	{
		return getId().hashCode();
	}

    /**
     * @return the IDs of all the physical columns
     */
    public String[] getColumnIDs()
    {
        String[] ids = new String[nrPhysicalColumns()];
        for (int i=0;i<nrPhysicalColumns();i++)
        {
            ids[i] = getPhysicalColumn(i).getId();
        }
        
        return ids;
    }

    /**
     * @return the names of all the physical columns
     * @param locale the locale to use
     */
    public String[] getColumnNames(String locale)
    {
        String[] names = new String[nrPhysicalColumns()];
        for (int i=0;i<nrPhysicalColumns();i++)
        {
            names[i] = getPhysicalColumn(i).getDisplayName(locale);
        }
        
        return names;
    }
}
