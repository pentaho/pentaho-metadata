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
package org.pentaho.pms.schema.olap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;

@SuppressWarnings("deprecation")
public class OlapHierarchy extends ChangedFlag implements Cloneable
{
    private String         name;
    private BusinessTable  businessTable;
    private BusinessColumn primaryKey;
    private List<OlapHierarchyLevel>           hierarchyLevels;
    private boolean        havingAll;
    
    private OlapDimension  olapDimension;
    
    // TODO: add DefaultMember Mondrian property too
    // TODO: add allMemberName property http://mondrian.pentaho.org/documentation/schema.php#The_all_member
    // TODO: add allLevelName property http://mondrian.pentaho.org/documentation/schema.php#The_all_member
    // 
    
    public OlapHierarchy(OlapDimension olapDimension)
    {
        super();
        this.olapDimension = olapDimension;
        hierarchyLevels = new ArrayList<OlapHierarchyLevel>();  
        havingAll = true; // Set the default to true, said Julian
    }
    
    /**
     * @param name
     * @param hierarchyLevels
     */
    public OlapHierarchy(OlapDimension olapDimension, String name, List<OlapHierarchyLevel> hierarchyLevels)
    {
        this(olapDimension);
        this.name = name;
        this.hierarchyLevels = hierarchyLevels;
    }

    public Object clone()
    {
        OlapHierarchy hierarchy = new OlapHierarchy(olapDimension); // weak reference, no hard copy
        
        hierarchy.name = name;
        if (businessTable!=null) hierarchy.businessTable = (BusinessTable) businessTable.clone();
        if (primaryKey!=null) hierarchy.primaryKey = (BusinessColumn) primaryKey.clone();
        for (int i=0;i<hierarchyLevels.size();i++)
        {
            OlapHierarchyLevel hierarchyLevel = (OlapHierarchyLevel) hierarchyLevels.get(i); 
            hierarchy.hierarchyLevels.add((OlapHierarchyLevel)hierarchyLevel.clone());
        }
        hierarchy.havingAll = havingAll;
        
        return hierarchy;
    }

    public boolean equals(Object obj)
    {
        return name.equals(((OlapHierarchy)obj).getName());
    }

    /**
     * @return the hierarchyLevels
     */
    public List<OlapHierarchyLevel> getHierarchyLevels()
    {
        return hierarchyLevels;
    }

    /**
     * @param hierarchyLevels the hierarchyLevels to set
     */
    public void setHierarchyLevels(List<OlapHierarchyLevel> hierarchyLevels)
    {
        this.hierarchyLevels = hierarchyLevels;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the havingAll
     */
    public boolean isHavingAll()
    {
        return havingAll;
    }

    /**
     * @param havingAll the havingAll to set
     */
    public void setHavingAll(boolean havingAll)
    {
        this.havingAll = havingAll;
    }

    /**
     * @return the primaryKey
     */
    public BusinessColumn getPrimaryKey()
    {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(BusinessColumn primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public OlapHierarchyLevel findOlapHierarchyLevel(String thisName)
    {
        for (int i=0;i<hierarchyLevels.size();i++)
        {
            OlapHierarchyLevel level = (OlapHierarchyLevel) hierarchyLevels.get(i);
            if (level.getName().equalsIgnoreCase(thisName)) return level;
        }
        return null;
    }

    /**
     * @return the olapDimension
     */
    public OlapDimension getOlapDimension()
    {
        return olapDimension;
    }

    /**
     * @param olapDimension the olapDimension to set
     */
    public void setOlapDimension(OlapDimension olapDimension)
    {
        this.olapDimension = olapDimension;
    }

    /**
     * @return the businessTable
     */
    public BusinessTable getBusinessTable()
    {
        return businessTable;
    }

    /**
     * @param businessTable the businessTable to set
     */
    public void setBusinessTable(BusinessTable businessTable)
    {
        this.businessTable = businessTable;
    }

    public String[] getUnusedColumnNames(String locale)
    {
        String[] allColumnNames = businessTable.getColumnNames(locale);
        List<String> names = new ArrayList<String>();
        names.addAll(Arrays.asList(allColumnNames));
        
        for (int i=names.size()-1;i>=0;i--)
        {
            String columnName = (String) names.get(i);
            if (findBusinessColumn(locale, columnName)!=null) names.remove(i);
        }
        
        return (String[]) names.toArray(new String[names.size()]);
    }
    
    public BusinessColumn findBusinessColumn(String locale, String columnName)
    {
        // Look in the levels
        for (int i=0;i<hierarchyLevels.size();i++)
        {
            OlapHierarchyLevel level = (OlapHierarchyLevel) hierarchyLevels.get(i);
            BusinessColumn businessColumn = level.findBusinessColumn(locale, columnName);
            if (businessColumn!=null) return businessColumn;
        }
        return null;
    }
    
    public boolean hasChanged()
    {
        for (int i=0;i<hierarchyLevels.size();i++)
        {
            if (((OlapHierarchyLevel)hierarchyLevels.get(i)).hasChanged()) return true;
        }
        return super.hasChanged();
    }
    
    public void clearChanged()
    {
        for (int i=0;i<hierarchyLevels.size();i++)
        {
            ((OlapHierarchyLevel)hierarchyLevels.get(i)).setChanged(false);
        }
        setChanged(false);       
    }
}
