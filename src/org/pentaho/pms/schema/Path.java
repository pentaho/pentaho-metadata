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
import java.util.Hashtable;


/*
 * Created on 30-jan-04
 *
 */

public class Path
{
	private ArrayList path;  // contains Relationship objects
	
	public Path()
	{
		path=new ArrayList();
	}
	
	public void addRelationship(RelationshipMeta rel)
	{
		path.add(rel);
	}
	
	public void removeRelationship()
	{
		path.remove(size()-1);
	}
	
	public RelationshipMeta getLastRelationship()
	{
		return (RelationshipMeta)path.get(size()-1);
	}

	public int size()
	{
		return path.size();
	}
	
	public int nrTables()
	{
		return getUsedTables().length;
	}
	
	public int score()
	{
		int score=0;
		for (int i=0;i<size();i++)
		{
			RelationshipMeta rel = getRelationship(i);
            BusinessTable from = rel.getTableFrom();
            int size = from.getRelativeSize(); 
			if (size>0) score+=size;
		}
		if (size()>0)
		{
			BusinessTable to = getLastRelationship().getTableTo();
            int size = to.getRelativeSize(); 
			if (size>0) score+=size;
		}
		return score;
	}
	
	public RelationshipMeta getRelationship(int i)
	{
		return (RelationshipMeta)path.get(i);
	}
		
	public boolean contains(Path in)
	{
		if (in.size()==0) return false;
		
		for (int i=0;i<size();i++)
		{
			int nr=0;
			while (getRelationship(i+nr).equals(in.getRelationship(nr)) && 
			       nr<in.size() &&
			       i+nr<size()
			      )
			{
				nr++;
			}
			if (nr==in.size()) return true;
		}
		return false;
	}

	public boolean contains(RelationshipMeta rel)
	{
		if (rel==null) return false;
		
		for (int i=0;i<size();i++)
		{
			RelationshipMeta check = getRelationship(i);
            BusinessTable from = check.getTableFrom();
            BusinessTable to   = check.getTableTo();
			if ( ( rel.getTableFrom().equals(from) && rel.getTableTo().equals(to) ) ||
                 ( rel.getTableFrom().equals(to) && rel.getTableTo().equals(from) )
              ) return true;
		}
		return false;
	}

	public boolean contains(BusinessTable tab)
	{
		if (tab==null) return false;
		
		for (int i=0;i<size();i++)
		{
			RelationshipMeta check = getRelationship(i);
			if (check.isUsingTable(tab)) return true;
		}
		return false;
	}

	public boolean contains(BusinessTable tabs[])
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.length && all;i++)
		{
			if (!contains(tabs[i])) all=false;
		}
		return all;
	}

	public boolean contains(ArrayList tabs)
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.size() && all;i++)
		{
			if (!contains((BusinessTable)tabs.get(i))) all=false;
		}
		return all;
	}

	public Object clone()
	{
		Path retval   = new Path();
		
		for (int i=0;i<size();i++)
		{
			RelationshipMeta rel = getRelationship(i);
			retval.addRelationship(rel);
		}
		
		return retval;
	}
	
	public String toString()
	{
		String thisPath=""; //$NON-NLS-1$
		for (int i=0;i<size();i++)
		{
            RelationshipMeta relationship = getRelationship(i);
            if (i>0) thisPath+=", "; //$NON-NLS-1$
            thisPath+="["+relationship.getTableFrom().getId()+"-"+relationship.getTableTo().getId()+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return thisPath;
	}
	
	// Compare two paths: first on the number of tables used!!!
	public int compare(Path thisPath)
	{
		int diff=size()-thisPath.size();
		if (diff==0)
		{
			diff=nrTables()-thisPath.nrTables();
			if (diff==0)
			{
				diff=score() - thisPath.score();
			}
		}
		if (diff<0) return -1;
		else if (diff>0) return 1;
		else return 0;
	}
	
	public BusinessTable[] getUsedTables()
	{
		Hashtable hash = new Hashtable();
		
		for (int i=0;i<size();i++)
		{
			RelationshipMeta rel = getRelationship(i);
			hash.put(rel.getTableFrom(), "OK"); //$NON-NLS-1$
			hash.put(rel.getTableTo(), "OK"); //$NON-NLS-1$
		}
        return (BusinessTable[]) hash.keySet().toArray(new BusinessTable[hash.keySet().size()]);
	}

	public RelationshipMeta[] getUsedRelationships()
	{
		ArrayList list = new ArrayList();
		
		for (int i=0;i<size();i++)
		{
			RelationshipMeta rel = getRelationship(i);
			boolean exists=false;
			for (int j=0;j<list.size() && !exists;j++)
			{
				RelationshipMeta check = (RelationshipMeta)list.get(j);
				if ( check.isUsingTable( rel.getTableFrom()) &&
				     check.isUsingTable( rel.getTableTo())
				   ) exists=true;
			}
			if (!exists) list.add(rel);
		}
		
		RelationshipMeta rels[] = new RelationshipMeta[list.size()];
		for (int i=0;i<list.size();i++) rels[i] = (RelationshipMeta)list.get(i);

		return rels;
	}
}
