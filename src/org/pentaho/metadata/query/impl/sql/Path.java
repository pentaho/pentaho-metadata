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
package org.pentaho.metadata.query.impl.sql;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.pentaho.metadata.model.LogicalRelationship;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlPhysicalTable;

/*
 * Created on 30-jan-04
 *
 */

public class Path {
	private List<LogicalRelationship> path;  // contains Relationship objects
	
	public Path() {
		path = new ArrayList<LogicalRelationship>();
	}
	
	public void addRelationship(LogicalRelationship rel)
	{
		path.add(rel);
	}
	
	public void removeRelationship()
	{
		path.remove(size()-1);
	}
  
  public LogicalRelationship removeRelationship(int i)
  {
    return (LogicalRelationship)path.remove(i);
  }
	
	public LogicalRelationship getLastRelationship()
	{
		return (LogicalRelationship)path.get(size()-1);
	}

	public int size()	{
		return path.size();
	}
	
	public int nrTables()	{
		return getUsedTables().size();
	}
	
	public int score() {
		int score=0;
		Integer relSize = null;
		for (int i=0;i<size();i++) {
		  LogicalRelationship rel = getRelationship(i);
		  LogicalTable from = rel.getFromTable();
		  relSize = (Integer)from.getProperty(SqlPhysicalTable.RELATIVE_SIZE);
		  if ( (relSize != null) && (relSize.intValue()>0) ) {
		    score+=relSize;
		  }
		}
		if (size()>0) {
			LogicalTable to = getLastRelationship().getToTable();
      relSize = (Integer)to.getProperty(SqlPhysicalTable.RELATIVE_SIZE); 
      if ( (relSize != null) && (relSize.intValue()>0) ) {
        score+=relSize;
      }
		}
		return score;
	}
	
	public LogicalRelationship getRelationship(int i)
	{
		return (LogicalRelationship)path.get(i);
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

	public boolean contains(LogicalRelationship rel) {
		if (rel==null) return false;
		for (int i=0;i<size();i++) {
		  LogicalRelationship check = getRelationship(i);
		  LogicalTable from = check.getFromTable();
		  LogicalTable to   = check.getToTable();
			if ((rel.getFromTable().equals(from) && rel.getToTable().equals(to)) ||
			    (rel.getFromTable().equals(to) && rel.getToTable().equals(from))) {
			  return true;
			}
		}
		return false;
	}

	public boolean contains(LogicalTable tab)	{
		if (tab==null) return false;
		for (int i=0;i<size();i++) {
			LogicalRelationship check = getRelationship(i);
			if (check.isUsingTable(tab)) return true;
		}
		return false;
	}
	
	public String toString() {
		String thisPath=""; //$NON-NLS-1$
		for (int i = 0; i < size(); i++) {
		  LogicalRelationship relationship = getRelationship(i);
		  if (i>0) {
		    thisPath+=", "; //$NON-NLS-1$
		  }
		  thisPath+="["+relationship.getFromTable().getId()+"-"+relationship.getToTable().getId()+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return thisPath;
	}
	
	// Compare two paths: first on the number of tables used!!!
	public int compare(Path thisPath)	{
		int diff = size() - thisPath.size();
		if (diff == 0) {
			diff = nrTables() - thisPath.nrTables();
			if (diff == 0) {
				diff = score() - thisPath.score();
			}
		}
		if (diff < 0) {
		  return -1;
		} else if (diff > 0) {
		  return 1;
		}	else {
		  return 0;
		}
	}
	
	public List<LogicalTable> getUsedTables() {
		Set<LogicalTable> treeSet = new TreeSet<LogicalTable>();
		for (int i=0;i<size();i++) {
			LogicalRelationship rel = getRelationship(i);
			treeSet.add(rel.getFromTable());
			treeSet.add(rel.getToTable());
		}
		return new ArrayList<LogicalTable>(treeSet);
	}
}
