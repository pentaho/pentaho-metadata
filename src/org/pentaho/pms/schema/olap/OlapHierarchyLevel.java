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
import java.util.List;

import org.pentaho.pms.schema.BusinessColumn;

import be.ibridge.kettle.core.ChangedFlag;

public class OlapHierarchyLevel extends ChangedFlag implements Cloneable
{
    private String name;
    private BusinessColumn referenceColumn; // Also has the business table of-course.
    private List businessColumns;
    private boolean havingUniqueMembers;
    
    private OlapHierarchy olapHierarchy;
    
    public OlapHierarchyLevel(OlapHierarchy olapHierarchy)
    {
        super();
        this.olapHierarchy = olapHierarchy;
        businessColumns = new ArrayList();
    }
    
    /**
     * @param name
     * @param referenceColumn
     * @param businessColumns
     */
    public OlapHierarchyLevel(OlapHierarchy olapHierarchy, String name, BusinessColumn referenceColumn, List businessColumns)
    {
        this(olapHierarchy);
        this.name = name;
        this.referenceColumn = referenceColumn;
        this.businessColumns = businessColumns;
    }

    public Object clone()
    {
        OlapHierarchyLevel hierarchyLevel = new OlapHierarchyLevel(olapHierarchy); // weak link again to the parent.
        
        hierarchyLevel.name = name;
        if (referenceColumn!=null) hierarchyLevel.referenceColumn = (BusinessColumn) referenceColumn.clone();
        for (int i=0;i<businessColumns.size();i++)
        {
            BusinessColumn businessColumn = (BusinessColumn) businessColumns.get(i);
            hierarchyLevel.businessColumns.add(businessColumn.clone());
        }
        hierarchyLevel.havingUniqueMembers = havingUniqueMembers;
        
        return hierarchyLevel;
    }

    public boolean equals(Object obj)
    {
        return name.equals(((OlapHierarchyLevel)obj).getName());
    }

    /**
     * @return the businessColumns
     */
    public List getBusinessColumns()
    {
        return businessColumns;
    }

    /**
     * @param businessColumns the businessColumns to set
     */
    public void setBusinessColumns(List businessColumns)
    {
        this.businessColumns = businessColumns;
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
     * @return the referenceColumn
     */
    public BusinessColumn getReferenceColumn()
    {
        return referenceColumn;
    }

    /**
     * @param referenceColumn the referenceColumn to set
     */
    public void setReferenceColumn(BusinessColumn referenceColumn)
    {
        this.referenceColumn = referenceColumn;
    }

    public BusinessColumn findBusinessColumn(String locale, String name)
    {
        if (referenceColumn!=null && referenceColumn.getDisplayName(locale).equalsIgnoreCase(name)) return referenceColumn;
        
        for (int i=0;i<businessColumns.size();i++)
        {
            BusinessColumn column = (BusinessColumn) businessColumns.get(i);
            if (column.getDisplayName(locale).equalsIgnoreCase(name)) return column;
        }
        return null;
    }

    /**
     * @return the olapHierarchy
     */
    public OlapHierarchy getOlapHierarchy()
    {
        return olapHierarchy;
    }

    /**
     * @param olapHierarchy the olapHierarchy to set
     */
    public void setOlapHierarchy(OlapHierarchy olapHierarchy)
    {
        this.olapHierarchy = olapHierarchy;
    }

    /**
     * @return the havingUniqueMembers
     */
    public boolean isHavingUniqueMembers()
    {
        return havingUniqueMembers;
    }

    /**
     * @param havingUniqueMembers the havingUniqueMembers to set
     */
    public void setHavingUniqueMembers(boolean havingUniqueMembers)
    {
        this.havingUniqueMembers = havingUniqueMembers;
    }
}
