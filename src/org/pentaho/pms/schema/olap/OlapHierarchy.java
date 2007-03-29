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
package org.pentaho.pms.schema.olap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessTable;

import be.ibridge.kettle.core.ChangedFlag;

public class OlapHierarchy extends ChangedFlag implements Cloneable
{
    private String         name;
    private BusinessTable  businessTable;
    private BusinessColumn primaryKey;
    private List           hierarchyLevels;
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
        hierarchyLevels = new ArrayList();  
        havingAll = true; // Set the default to true, said Julian
    }
    
    /**
     * @param name
     * @param hierarchyLevels
     */
    public OlapHierarchy(OlapDimension olapDimension, String name, List hierarchyLevels)
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
            hierarchy.hierarchyLevels.add(hierarchyLevel.clone());
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
    public List getHierarchyLevels()
    {
        return hierarchyLevels;
    }

    /**
     * @param hierarchyLevels the hierarchyLevels to set
     */
    public void setHierarchyLevels(List hierarchyLevels)
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
        List names = new ArrayList();
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
