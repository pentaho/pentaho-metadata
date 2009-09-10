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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.mql;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;


/**
 * Created on 30-jan-04
 *
 * @deprecated as of metadata 3.0.  Please use org.pentaho.metadata.query.model.impl.sql.Path
 */
public class Path
{
	private List<RelationshipMeta> path;  // contains Relationship objects
	
	public Path()
	{
		path=new ArrayList<RelationshipMeta>();
	}
	
	public void addRelationship(RelationshipMeta rel)
	{
		path.add(rel);
	}
	
	public void removeRelationship()
	{
		path.remove(size()-1);
	}
  
  public RelationshipMeta removeRelationship(int i)
  {
    return (RelationshipMeta)path.remove(i);
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
		return getUsedTables().size();
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
	
	public List<BusinessTable> getUsedTables()
	{
		Set<BusinessTable> treeSet = new TreeSet<BusinessTable>();
		for (int i=0;i<size();i++)
		{
			RelationshipMeta rel = getRelationship(i);
			treeSet.add(rel.getTableFrom()); //$NON-NLS-1$
			treeSet.add(rel.getTableTo()); //$NON-NLS-1$
		}
        return new ArrayList<BusinessTable>(treeSet);
	}

	public RelationshipMeta[] getUsedRelationships()
	{
		ArrayList<RelationshipMeta> list = new ArrayList<RelationshipMeta>();
		
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
